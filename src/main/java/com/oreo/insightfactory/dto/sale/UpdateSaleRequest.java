package com.oreo.insightfactory.dto.sale;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSaleRequest {

    @Size(max = 100, message = "Branch name must not exceed 100 characters")
    private String branch;

    @Size(max = 50, message = "SKU must not exceed 50 characters")
    private String sku;

    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 10000, message = "Quantity must not exceed 10000")
    private Integer quantity;

    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @DecimalMax(value = "999999.99", message = "Price must not exceed 999999.99")
    private BigDecimal price;

    private LocalDateTime saleDate;
}

