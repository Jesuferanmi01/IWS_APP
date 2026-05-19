package com.understandingjava.iws_app.Repos;

import com.understandingjava.iws_app.Models.IpAddress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.util.List;
import java.util.Optional;

@Repository
public interface IpAddressRepo extends JpaRepository<IpAddress, Integer> {

    Optional<IpAddress> findByIpAddress(String ipAddress);

    @Query(value = "EXEC dbo.sp_get_ip_time :userCode, :ipAddress", nativeQuery = true)
    Time findTimeByIpAddress(@Param("userCode") String userCode, @Param("ipAddress") String ipAddress);

    @Modifying
    @Query("UPDATE IpAddress i SET i.isFlagged = true WHERE i.ipAddress = :ip")
    int flagByIpAddress(@Param("ip") String ip);

    @Modifying
    @Query(value = """
        INSERT INTO ip_address (ip_address, is_flagged) VALUES (:ip, true)  ON CONFLICT (ip_address)    DO UPDATE SET is_flagged = true
        """, nativeQuery = true)
    void upsertFlagged(@Param("ip") String ip);

//    List<IpAddress> findByIsFlaggedTrue();

    Page<IpAddress> findByIsFlaggedTrue(Pageable pageable);

    // Keep the non-paginated version only if used elsewhere (e.g. CacheService)
    List<IpAddress> findByIsFlaggedTrue();

}
