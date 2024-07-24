package com.sparta.ezpzhost.domain.host.dto;

import com.sparta.ezpzhost.domain.host.entity.Host;
import lombok.Getter;

@Getter
public class HostResponseDto {

    private final Long id;
    private final String username;
    private final String email;
    private final String companyName;
    private final String businessNumber;

    private HostResponseDto(Host host) {
        this.id = host.getId();
        this.username = host.getUsername();
        this.email = host.getEmail();
        this.companyName = host.getCompanyName();
        this.businessNumber = host.getBusinessNumber();
    }

    public static HostResponseDto of(Host host) {
        return new HostResponseDto(host);
    }

}