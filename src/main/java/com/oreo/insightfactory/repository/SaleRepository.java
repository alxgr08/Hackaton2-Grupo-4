package com.oreo.insightfactory.repository;

import com.oreo.insightfactory.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, String> {

    // Find all sales by branch
    List<Sale> findByBranch(String branch);

    // Find sales by branch and date range
    List<Sale> findByBranchAndSaleDateBetween(String branch, LocalDateTime startDate, LocalDateTime endDate);

    // Find all sales by date range
    List<Sale> findBySaleDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Find sales by SKU
    List<Sale> findBySku(String sku);

    // Find sales by branch and SKU
    List<Sale> findByBranchAndSku(String branch, String sku);

    // Custom query for filtering with multiple optional parameters
    @Query("SELECT s FROM Sale s WHERE " +
           "(:branch IS NULL OR s.branch = :branch) AND " +
           "(:sku IS NULL OR s.sku = :sku) AND " +
           "(:startDate IS NULL OR s.saleDate >= :startDate) AND " +
           "(:endDate IS NULL OR s.saleDate <= :endDate) " +
           "ORDER BY s.saleDate DESC")
    List<Sale> findByFilters(
            @Param("branch") String branch,
            @Param("sku") String sku,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Get sales created by specific user
    List<Sale> findByCreatedBy(String username);
}

