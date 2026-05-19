package com.understandingjava.iws_app.DTOs;

import lombok.*;

import java.sql.Time;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IpaddressHelpers {

    Time storedTime;
    Boolean transactionExist;
}
