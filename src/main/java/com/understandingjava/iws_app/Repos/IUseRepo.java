package com.understandingjava.iws_app.Repos;

import com.understandingjava.iws_app.DTOs.UserResponseDTO;
import com.understandingjava.iws_app.Models.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface IUseRepo extends JpaRepository<Users, UUID> {

    @Query("SELECT u.userCode FROM Users u WHERE u.status = 'BLACKLISTED'")
    List<String> findBlacklistedMerchantCodes();

    Page<UserResponseDTO> findByStatus(String status, Pageable pageable);

    @Query("SELECT u.userCode FROM Users u WHERE u.status = 'BLACKLISTED'")
    List<String> findBlacklistedMerchantCodess();

    Page<Users> findByStatusIgnoreCase(String status, Pageable pageable);
}

