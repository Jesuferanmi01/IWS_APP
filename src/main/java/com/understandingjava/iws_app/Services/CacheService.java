package com.understandingjava.iws_app.Services;

import com.understandingjava.iws_app.Repos.ITransactionRepo;
import com.understandingjava.iws_app.Repos.IUseRepo;
import com.understandingjava.iws_app.Repos.IpAddressRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CacheService {
    private final IpAddressRepo ipAddressRepo;
    private final EventLogService log;
    private final ITransactionRepo  transactionRepo;
    private final IUseRepo repo;

    @Cacheable("flaggedIps")
    public List<String> getFlaggedIps() {

        log.action("CACHE", "flaggedIps cache miss — loading from DB");

        return ipAddressRepo.findByIsFlaggedTrue()
                .stream()
                .map(ip -> ip.getIpAddress())
                .toList();
    }

    @CacheEvict(value = "flaggedIps", allEntries = true)
    public void evict() {

        log.action("CACHE", "flaggedIps cache evicted");
    }

    @Cacheable("flaggedIps")
    public boolean isFlagged(String ipAddress) {

        return getFlaggedIps().contains(ipAddress);
    }

    @Cacheable("blacklistedMerchants")
    public List<String> getBlacklistedMerchants() {

        log.action("CACHE", "blacklistedMerchants cache miss — loading from DB");

        return repo.findBlacklistedMerchantCodes();
    }

    @CacheEvict(value = {"flaggedIps", "blacklistedMerchants"}, allEntries = true)
    public void evictAll() {
        log.action("CACHE", "All caches evicted");
    }

    @Cacheable("blacklistedMerchants")
    public Set<String> setBlacklistedMerchants() {

        log.action("CACHE", "blacklistedMerchants cache miss — loading from DB");

        return new HashSet<>(repo.findBlacklistedMerchantCodes());
    }

    public boolean isBlacklisted(String merchantCode) {
        return getBlacklistedMerchants().contains(merchantCode);
    }
}
