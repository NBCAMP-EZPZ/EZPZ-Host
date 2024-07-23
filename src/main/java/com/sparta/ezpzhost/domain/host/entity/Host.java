package com.sparta.ezpzhost.domain.host.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Host {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "host_id", nullable = false, unique = true)
    private Long id;

    public Host(Long id) {
        this.id = id;
    }
}
