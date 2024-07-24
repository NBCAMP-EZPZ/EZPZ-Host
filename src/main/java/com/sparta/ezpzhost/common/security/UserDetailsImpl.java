package com.sparta.ezpzhost.common.security;

import com.sparta.ezpzhost.domain.host.entity.Host;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private final Host host;

    @Override
    public String getPassword() {
        return host.getPassword();
    }

    @Override
    public String getUsername() {
        return host.getUsername();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

}
