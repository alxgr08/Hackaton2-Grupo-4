package com.oreo.insightfactory.controller;

import com.oreo.insightfactory.dto.sale.CreateSaleRequest;
import com.oreo.insightfactory.dto.sale.SaleResponse;
import com.oreo.insightfactory.dto.sale.UpdateSaleRequest;
import com.oreo.insightfactory.service.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('CENTRAL', 'BRANCH')")
    public ResponseEntity<SaleResponse> createSale(@Valid @RequestBody CreateSaleRequest request) {
        SaleResponse response = saleService.createSale(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('CENTRAL', 'BRANCH')")
    public ResponseEntity<List<SaleResponse>> getAllSales(
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) String sku,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        List<SaleResponse> sales = saleService.getAllSales(branch, sku, startDate, endDate);
        return ResponseEntity.ok(sales);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CENTRAL', 'BRANCH')")
    public ResponseEntity<SaleResponse> getSaleById(@PathVariable String id) {
        SaleResponse response = saleService.getSaleById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CENTRAL', 'BRANCH')")
    public ResponseEntity<SaleResponse> updateSale(
            @PathVariable String id,
            @Valid @RequestBody UpdateSaleRequest request
    ) {
        SaleResponse response = saleService.updateSale(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CENTRAL', 'BRANCH')")
    public ResponseEntity<Void> deleteSale(@PathVariable String id) {
        saleService.deleteSale(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/branch/{branch}")
    @PreAuthorize("hasAnyAuthority('CENTRAL', 'BRANCH')")
    public ResponseEntity<List<SaleResponse>> getSalesByBranch(@PathVariable String branch) {
        List<SaleResponse> sales = saleService.getSalesByBranch(branch);
        return ResponseEntity.ok(sales);
    }
}

