package com.sparta.ezpzhost.domain.host.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Host {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "host_id", nullable = false)
	private Long id;
}
