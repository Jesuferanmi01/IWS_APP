package com.understandingjava.iws_app.Services;

import com.understandingjava.iws_app.Repos.IpAddressRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IpFlagService {

    private final IpAddressRepo ipAddressRepo;

    public void flagIp(String ipAddress) {
        ipAddressRepo.upsertFlagged(ipAddress);
    }
}
