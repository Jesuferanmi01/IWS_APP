package com.understandingjava.iws_app.Repos;

import com.understandingjava.iws_app.Models.Transactions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface IAdminRepo extends JpaRepository<Transactions, UUID> {

    Page<Transactions> findAll(Pageable pageable);

    Page<Transactions> findByStatus(String status, Pageable pageable);
}
