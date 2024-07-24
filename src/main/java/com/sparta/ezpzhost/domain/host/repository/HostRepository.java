package com.sparta.ezpzhost.domain.host.repository;

import com.sparta.ezpzhost.domain.host.entity.Host;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HostRepository extends JpaRepository<Host, Long> {

    boolean existsByUsername(String username);

    Optional<Host> findByUsername(String username);

}
