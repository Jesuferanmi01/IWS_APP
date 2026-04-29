package com.understandingjava.iws_app.Models;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "card_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cards {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "card_no", nullable = false, unique = true)
    private String cardNo;

    @Column(name = "bank_name", nullable = false)
    private String bankName;

    // ACTIVE | BLOCKED
    @Column(nullable = false)
    private String status;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

}
