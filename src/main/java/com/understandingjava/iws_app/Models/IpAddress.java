package com.understandingjava.iws_app.Models;


import jakarta.persistence.*;
import lombok.*;

import java.sql.Time;
import java.util.UUID;

@Entity
@Table(name = "ip_address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IpAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "ip", nullable = false)
    private String ipAddress;

    @Column(nullable = false)
    private Time time;

    @Column(name = "is_flagged", nullable = false)
    private boolean isFlagged = false;
}
