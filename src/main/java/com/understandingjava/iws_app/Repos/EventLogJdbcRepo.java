package com.understandingjava.iws_app.Repos;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Repository
@RequiredArgsConstructor
public class EventLogJdbcRepo {

    private final JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall simpleJdbcCall;

    @PostConstruct
    public void init() {
        simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("sp_insert_event_log")
                .declareParameters(
                        new SqlParameter("p_service", Types.VARCHAR),
                        new SqlParameter("p_message", Types.VARCHAR),
                        new SqlParameter("p_detail1", Types.VARCHAR),
                        new SqlParameter("p_detail2", Types.VARCHAR),
                        new SqlParameter("p_detail3", Types.VARCHAR)
                );
    }

    public void insertLog(String service, String message,
                          String detail1, String detail2, String detail3) {
        try {
            SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                    .withSchemaName("dbo")
                    .withProcedureName("sp_insert_event_log")
                    .declareParameters(
                            new org.springframework.jdbc.core.SqlParameter("p_service",  Types.VARCHAR),
                            new org.springframework.jdbc.core.SqlParameter("p_message",  Types.VARCHAR),
                            new org.springframework.jdbc.core.SqlParameter("p_detail1",  Types.VARCHAR),
                            new org.springframework.jdbc.core.SqlParameter("p_detail2",  Types.VARCHAR),
                            new org.springframework.jdbc.core.SqlParameter("p_detail3",  Types.VARCHAR)
                    );

            Map<String, Object> params = new HashMap<>();
            params.put("p_service", service);
            params.put("p_message", message);
            params.put("p_detail1", detail1);
            params.put("p_detail2", detail2);
            params.put("p_detail3", detail3);

            call.execute(params);

        } catch (Exception ex) {

            System.err.println("[LOG ERROR] Failed to insert log: " + ex.getMessage());
        }

    }
}
