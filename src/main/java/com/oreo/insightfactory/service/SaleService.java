package com.oreo.insightfactory.service;

import com.oreo.insightfactory.dto.sale.CreateSaleRequest;
import com.oreo.insightfactory.dto.sale.SaleResponse;
import com.oreo.insightfactory.dto.sale.UpdateSaleRequest;
import com.oreo.insightfactory.exception.ForbiddenException;
import com.oreo.insightfactory.exception.ResourceNotFoundException;
import com.oreo.insightfactory.exception.ValidationException;
import com.oreo.insightfactory.model.Role;
import com.oreo.insightfactory.model.Sale;
import com.oreo.insightfactory.model.User;
import com.oreo.insightfactory.repository.SaleRepository;
import com.oreo.insightfactory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final UserRepository userRepository;

    @Transactional
    public SaleResponse createSale(CreateSaleRequest request) {
        User currentUser = getCurrentUser();

        // BRANCH users can only create sales for their branch
        if (currentUser.getRole() == Role.BRANCH) {
            if (!currentUser.getBranch().equals(request.getBranch())) {
                throw new ForbiddenException("You can only create sales for your branch: " + currentUser.getBranch());
            }
        }

        Sale sale = Sale.builder()
                .branch(request.getBranch())
                .sku(request.getSku())
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .saleDate(request.getSaleDate() != null ? request.getSaleDate() : LocalDateTime.now())
                .createdBy(currentUser.getUsername())
                .build();

        Sale savedSale = saleRepository.save(sale);
        return mapToResponse(savedSale);
    }

    @Transactional(readOnly = true)
    public List<SaleResponse> getAllSales(String branch, String sku, LocalDateTime startDate, LocalDateTime endDate) {
        User currentUser = getCurrentUser();

        // BRANCH users can only see sales from their branch
        if (currentUser.getRole() == Role.BRANCH) {
            branch = currentUser.getBranch();
        }

        List<Sale> sales = saleRepository.findByFilters(branch, sku, startDate, endDate);
        return sales.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SaleResponse getSaleById(String id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id: " + id));

        User currentUser = getCurrentUser();

        // BRANCH users can only see sales from their branch
        if (currentUser.getRole() == Role.BRANCH) {
            if (!currentUser.getBranch().equals(sale.getBranch())) {
                throw new ForbiddenException("You can only view sales from your branch");
            }
        }

        return mapToResponse(sale);
    }

    @Transactional
    public SaleResponse updateSale(String id, UpdateSaleRequest request) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id: " + id));

        User currentUser = getCurrentUser();

        // BRANCH users can only update sales from their branch
        if (currentUser.getRole() == Role.BRANCH) {
            if (!currentUser.getBranch().equals(sale.getBranch())) {
                throw new ForbiddenException("You can only update sales from your branch");
            }
        }

        // Update fields if provided
        if (request.getBranch() != null) {
            // BRANCH users cannot change the branch
            if (currentUser.getRole() == Role.BRANCH && !request.getBranch().equals(currentUser.getBranch())) {
                throw new ForbiddenException("You cannot change the branch of a sale");
            }
            sale.setBranch(request.getBranch());
        }
        if (request.getSku() != null) {
            sale.setSku(request.getSku());
        }
        if (request.getQuantity() != null) {
            sale.setQuantity(request.getQuantity());
        }
        if (request.getPrice() != null) {
            sale.setPrice(request.getPrice());
        }
        if (request.getSaleDate() != null) {
            sale.setSaleDate(request.getSaleDate());
        }

        Sale updatedSale = saleRepository.save(sale);
        return mapToResponse(updatedSale);
    }

    @Transactional
    public void deleteSale(String id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id: " + id));

        User currentUser = getCurrentUser();

        // BRANCH users can only delete sales from their branch
        if (currentUser.getRole() == Role.BRANCH) {
            if (!currentUser.getBranch().equals(sale.getBranch())) {
                throw new ForbiddenException("You can only delete sales from your branch");
            }
        }

        saleRepository.delete(sale);
    }

    @Transactional(readOnly = true)
    public List<SaleResponse> getSalesByBranch(String branch) {
        User currentUser = getCurrentUser();

        // BRANCH users can only see sales from their own branch
        if (currentUser.getRole() == Role.BRANCH) {
            if (!currentUser.getBranch().equals(branch)) {
                throw new ForbiddenException("You can only view sales from your branch: " + currentUser.getBranch());
            }
        }

        List<Sale> sales = saleRepository.findByBranch(branch);
        return sales.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ValidationException("User not found: " + username));
    }

    private SaleResponse mapToResponse(Sale sale) {
        return SaleResponse.builder()
                .id(sale.getId())
                .branch(sale.getBranch())
                .sku(sale.getSku())
                .quantity(sale.getQuantity())
                .price(sale.getPrice())
                .totalAmount(sale.getTotalAmount())
                .saleDate(sale.getSaleDate())
                .createdAt(sale.getCreatedAt())
                .createdBy(sale.getCreatedBy())
                .build();
    }
}

