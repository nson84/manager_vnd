package Manager_vnd.Manager.feature.worker;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import Manager_vnd.Manager.feature.debt.DebtEntry;
import Manager_vnd.Manager.feature.payslip.Payslip;
import Manager_vnd.Manager.feature.wage.WageEntry;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(
        name = "workers",
        indexes = @Index(name = "idx_workers_phone", columnList = "phone", unique = true)
)
public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 20, unique = true)
    private String phone;

    @Column(length = 255)
    private String address;

    @Column(name = "job_title", length = 100)
    private String jobTitle;

    @Enumerated(EnumType.STRING)
    @Column(name = "wage_type", nullable = false, length = 20)
    private WageType wageType;

    @Column(name = "default_unit_rate", nullable = false, precision = 15, scale = 2)
    private BigDecimal defaultUnitRate;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(length = 500)
    private String note;

    @Column(name = "current_advance", nullable = false, precision = 15, scale = 2)
    private BigDecimal currentAdvance = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "worker", fetch = FetchType.LAZY)
    private List<WageEntry> wageEntries = new ArrayList<>();

    @OneToMany(mappedBy = "worker", fetch = FetchType.LAZY)
    private List<Payslip> payslips = new ArrayList<>();

    @OneToMany(mappedBy = "worker", fetch = FetchType.LAZY)
    private List<DebtEntry> debtEntries = new ArrayList<>();

    public Worker() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public WageType getWageType() {
        return wageType;
    }

    public void setWageType(WageType wageType) {
        this.wageType = wageType;
    }

    public BigDecimal getDefaultUnitRate() {
        return defaultUnitRate;
    }

    public void setDefaultUnitRate(BigDecimal defaultUnitRate) {
        this.defaultUnitRate = defaultUnitRate;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public BigDecimal getCurrentAdvance() {
        return currentAdvance;
    }

    public void setCurrentAdvance(BigDecimal currentAdvance) {
        this.currentAdvance = currentAdvance;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public List<WageEntry> getWageEntries() {
        return wageEntries;
    }

    public void setWageEntries(List<WageEntry> wageEntries) {
        this.wageEntries = wageEntries;
    }

    public List<Payslip> getPayslips() {
        return payslips;
    }

    public void setPayslips(List<Payslip> payslips) {
        this.payslips = payslips;
    }

    public List<DebtEntry> getDebtEntries() {
        return debtEntries;
    }

    public void setDebtEntries(List<DebtEntry> debtEntries) {
        this.debtEntries = debtEntries;
    }
}
