package nl.itqaanconsulting.orderflow.order.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateOrderRequest(
        @NotBlank @Size(max = 80) String externalReference,
        @NotBlank @Email @Size(max = 160) String customerEmail,
        @NotNull @DecimalMin("0.01") BigDecimal totalAmount,
        @NotBlank @Size(min = 3, max = 3) String currency
) {
}
