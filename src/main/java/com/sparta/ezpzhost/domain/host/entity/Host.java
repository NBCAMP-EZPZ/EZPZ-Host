package com.sparta.ezpzhost.domain.host.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name="Host")
public class Host {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;
}
