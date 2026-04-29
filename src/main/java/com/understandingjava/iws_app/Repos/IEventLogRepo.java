package com.understandingjava.iws_app.Repos;


import com.understandingjava.iws_app.Models.EventLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IEventLogRepo extends JpaRepository<EventLog, Long> {


    Page<EventLog> findAll(Pageable pageable);

    Page<EventLog> findByService(String service, Pageable pageable);
}