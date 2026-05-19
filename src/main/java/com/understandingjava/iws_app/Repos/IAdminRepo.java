package com.understandingjava.iws_app.Repos;

import com.understandingjava.iws_app.Models.Transactions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface IAdminRepo extends JpaRepository<Transactions, UUID> {

    Page<Transactions> findAll(Pageable pageable);

    Page<Transactions> findByStatus(String status, Pageable pageable);

    @Query("SELECT t FROM Transactions t WHERE t.status IN :statuses")
    Page<Transactions> findByStatusIn(
            @Param("statuses") List<String> statuses, Pageable pageable);
}
