package Manager_vnd.Manager.feature.customer.dto;

import java.math.BigDecimal;
import java.time.Instant;

import Manager_vnd.Manager.feature.customer.Customer;

public record CustomerResponse(
        long id,
        String name,
        String phone,
        String address,
        String note,
        boolean active,
        BigDecimal currentDebt,
        Instant createdAt,
        Instant updatedAt
) {
    public static CustomerResponse fromEntity(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getPhone(),
                customer.getAddress(),
                customer.getNote(),
                customer.isActive(),
                customer.getCurrentDebt(),
                customer.getCreatedAt(),
                customer.getUpdatedAt());
    }
}
