package Manager_vnd.Manager.feature.payslip;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Manager_vnd.Manager.config.ActorResolver;
import Manager_vnd.Manager.dto.PaginatedResult;
import Manager_vnd.Manager.dto.PaginationMeta;
import Manager_vnd.Manager.exception.ConflictException;
import Manager_vnd.Manager.exception.InvalidRequestException;
import Manager_vnd.Manager.exception.ResourceNotFoundException;
import Manager_vnd.Manager.feature.cashbook.CashDirection;
import Manager_vnd.Manager.feature.cashbook.CashLedgerWriter;
import Manager_vnd.Manager.feature.cashbook.CashRefType;
import Manager_vnd.Manager.feature.debt.DebtEntryService;
import Manager_vnd.Manager.feature.debt.DebtEntryType;
import Manager_vnd.Manager.feature.debt.DebtRefType;
import Manager_vnd.Manager.feature.debt.LedgerDirection;
import Manager_vnd.Manager.feature.payslip.dto.CreatePayslipRequest;
import Manager_vnd.Manager.feature.payslip.dto.PayslipResponse;
import Manager_vnd.Manager.feature.payslip.dto.UpdatePayslipRequest;
import Manager_vnd.Manager.feature.wage.WageEntry;
import Manager_vnd.Manager.feature.wage.WageEntryRepository;
import Manager_vnd.Manager.feature.worker.Worker;
import Manager_vnd.Manager.feature.worker.WorkerRepository;
import jakarta.persistence.criteria.Predicate;

@Service
public class PayslipServiceImpl implements PayslipService {

    private final PayslipRepository payslipRepository;
    private final WorkerRepository workerRepository;
    private final WageEntryRepository wageEntryRepository;
    private final ActorResolver actorResolver;
    private final CashLedgerWriter cashLedgerWriter;
    private final DebtEntryService debtEntryService;

    public PayslipServiceImpl(
            PayslipRepository payslipRepository,
            WorkerRepository workerRepository,
            WageEntryRepository wageEntryRepository,
            ActorResolver actorResolver,
            CashLedgerWriter cashLedgerWriter,
            DebtEntryService debtEntryService) {
        this.payslipRepository = payslipRepository;
        this.workerRepository = workerRepository;
        this.wageEntryRepository = wageEntryRepository;
        this.actorResolver = actorResolver;
        this.cashLedgerWriter = cashLedgerWriter;
        this.debtEntryService = debtEntryService;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResult<PayslipResponse> getAll(
            int page, int size, String sort,
            Long workerId, PayslipStatus status,
            LocalDate periodFrom, LocalDate periodTo) {
        Pageable pageable = toPageable(page, size, sort);
        Page<Payslip> result = payslipRepository.findAll(
                buildSpec(workerId, status, periodFrom, periodTo), pageable);
        List<PayslipResponse> items = result.getContent().stream()
                .map(PayslipResponse::fromEntity)
                .toList();
        PaginationMeta meta = new PaginationMeta(
                result.getNumber() + 1,
                result.getSize(),
                result.getTotalPages(),
                result.getTotalElements());
        return new PaginatedResult<>(meta, items);
    }

    @Override
    @Transactional(readOnly = true)
    public PayslipResponse getById(long id) {
        return PayslipResponse.fromEntity(findPayslip(id));
    }

    @Override
    @Transactional
    public PayslipResponse create(CreatePayslipRequest request) {
        if (request.periodEnd().isBefore(request.periodStart())) {
            throw new InvalidRequestException("periodEnd phải >= periodStart");
        }
        Worker worker = workerRepository.findById(request.workerId())
                .orElseThrow(() -> new ResourceNotFoundException("Worker", "id", request.workerId()));

        List<WageEntry> wages = wageEntryRepository.findUnpaidInPeriod(
                worker.getId(), request.periodStart(), request.periodEnd());
        if (wages.isEmpty()) {
            throw new InvalidRequestException("Không có công chưa quyết toán trong kỳ");
        }

        BigDecimal gross = wages.stream()
                .map(WageEntry::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal advance = request.advanceDeducted() != null ? request.advanceDeducted() : BigDecimal.ZERO;
        BigDecimal other = request.otherDeduction() != null ? request.otherDeduction() : BigDecimal.ZERO;
        validateDeductions(worker, gross, advance, other);

        Payslip payslip = new Payslip();
        payslip.setWorker(worker);
        payslip.setPeriodStart(request.periodStart());
        payslip.setPeriodEnd(request.periodEnd());
        payslip.setGrossAmount(gross);
        payslip.setAdvanceDeducted(advance);
        payslip.setOtherDeduction(other);
        payslip.setNetAmount(calcNet(gross, advance, other));
        payslip.setStatus(PayslipStatus.DRAFT);
        payslip.setNote(blankToNull(request.note()));
        payslip.setCreatedBy(actorResolver.requireActor());
        Payslip saved = payslipRepository.save(payslip);

        for (WageEntry wage : wages) {
            wage.setPayslip(saved);
        }
        wageEntryRepository.saveAll(wages);

        return PayslipResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public PayslipResponse update(UpdatePayslipRequest request) {
        Payslip payslip = findPayslip(request.id());
        assertDraft(payslip);

        if (request.advanceDeducted() != null) {
            payslip.setAdvanceDeducted(request.advanceDeducted());
        }
        if (request.otherDeduction() != null) {
            payslip.setOtherDeduction(request.otherDeduction());
        }
        if (request.note() != null) {
            payslip.setNote(blankToNull(request.note()));
        }
        validateDeductions(
                payslip.getWorker(),
                payslip.getGrossAmount(),
                payslip.getAdvanceDeducted(),
                payslip.getOtherDeduction());
        payslip.setNetAmount(calcNet(
                payslip.getGrossAmount(),
                payslip.getAdvanceDeducted(),
                payslip.getOtherDeduction()));
        return PayslipResponse.fromEntity(payslipRepository.save(payslip));
    }

    @Override
    @Transactional
    public PayslipResponse confirm(long id) {
        Payslip payslip = findPayslip(id);
        assertDraft(payslip);
        payslip.setStatus(PayslipStatus.CONFIRMED);
        return PayslipResponse.fromEntity(payslipRepository.save(payslip));
    }

    @Override
    @Transactional
    public PayslipResponse pay(long id) {
        Payslip payslip = findPayslip(id);
        if (payslip.getStatus() != PayslipStatus.DRAFT && payslip.getStatus() != PayslipStatus.CONFIRMED) {
            throw new ConflictException("Chỉ thanh toán phiếu DRAFT hoặc CONFIRMED");
        }
        validateDeductions(
                payslip.getWorker(),
                payslip.getGrossAmount(),
                payslip.getAdvanceDeducted(),
                payslip.getOtherDeduction());

        payslip.setStatus(PayslipStatus.PAID);
        payslip.setPaidAt(Instant.now());
        Payslip saved = payslipRepository.save(payslip);

        if (saved.getNetAmount().compareTo(BigDecimal.ZERO) > 0) {
            cashLedgerWriter.postByCategoryCode(
                    saved.getPeriodEnd(),
                    CashDirection.OUT,
                    saved.getNetAmount(),
                    "WAGE",
                    "Tra luong #" + saved.getId(),
                    CashRefType.PAYSLIP,
                    saved.getId());
        }

        if (saved.getAdvanceDeducted().compareTo(BigDecimal.ZERO) > 0) {
            debtEntryService.createInternal(
                    null,
                    saved.getWorker().getId(),
                    DebtEntryType.PAYMENT,
                    LedgerDirection.DECREASE,
                    saved.getAdvanceDeducted(),
                    saved.getPeriodEnd(),
                    "Tru ung phieu luong #" + saved.getId(),
                    DebtRefType.PAYSLIP,
                    saved.getId(),
                    false);
        }

        return PayslipResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public PayslipResponse cancel(long id) {
        Payslip payslip = findPayslip(id);
        if (payslip.getStatus() == PayslipStatus.CANCELLED) {
            throw new ConflictException("Phiếu lương đã hủy");
        }

        if (payslip.getStatus() == PayslipStatus.PAID) {
            cashLedgerWriter.reverseAll(CashRefType.PAYSLIP, payslip.getId(), "Huy tra luong");
            if (payslip.getAdvanceDeducted().compareTo(BigDecimal.ZERO) > 0) {
                debtEntryService.createInternal(
                        null,
                        payslip.getWorker().getId(),
                        DebtEntryType.CHARGE,
                        LedgerDirection.INCREASE,
                        payslip.getAdvanceDeducted(),
                        payslip.getPeriodEnd(),
                        "Hoan ung huy phieu luong #" + payslip.getId(),
                        DebtRefType.PAYSLIP,
                        payslip.getId(),
                        false);
            }
        }

        List<WageEntry> linked = wageEntryRepository.findByPayslipId(payslip.getId());
        for (WageEntry wage : linked) {
            wage.setPayslip(null);
        }
        wageEntryRepository.saveAll(linked);

        payslip.setStatus(PayslipStatus.CANCELLED);
        return PayslipResponse.fromEntity(payslipRepository.save(payslip));
    }

    private void validateDeductions(Worker worker, BigDecimal gross, BigDecimal advance, BigDecimal other) {
        if (advance.compareTo(worker.getCurrentAdvance()) > 0) {
            throw new InvalidRequestException("advanceDeducted vượt quá currentAdvance của thợ");
        }
        if (calcNet(gross, advance, other).compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidRequestException("netAmount không được âm");
        }
    }

    private BigDecimal calcNet(BigDecimal gross, BigDecimal advance, BigDecimal other) {
        return gross.subtract(advance).subtract(other);
    }

    private void assertDraft(Payslip payslip) {
        if (payslip.getStatus() != PayslipStatus.DRAFT) {
            throw new ConflictException("Chỉ sửa/confirm phiếu DRAFT");
        }
    }

    private Specification<Payslip> buildSpec(
            Long workerId, PayslipStatus status, LocalDate periodFrom, LocalDate periodTo) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (workerId != null) {
                predicates.add(cb.equal(root.get("worker").get("id"), workerId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (periodFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("periodStart"), periodFrom));
            }
            if (periodTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("periodEnd"), periodTo));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private Payslip findPayslip(long id) {
        return payslipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payslip", "id", id));
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private Pageable toPageable(int page, int size, String sort) {
        int zeroBased = Math.max(page - 1, 0);
        return PageRequest.of(zeroBased, size, parseSort(sort));
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by("id").descending();
        }
        String[] parts = sort.split(",");
        if (parts.length == 2) {
            return Sort.by(Sort.Direction.fromString(parts[1].trim()), parts[0].trim());
        }
        return Sort.by("id").descending();
    }
}
