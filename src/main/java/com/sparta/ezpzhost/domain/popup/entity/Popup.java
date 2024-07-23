package com.sparta.ezpzhost.domain.popup.entity;

import java.time.LocalDateTime;

import com.sparta.ezpzhost.domain.host.entity.Host;
import com.sparta.ezpzhost.domain.popup.enums.ApprovalStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

@Entity
@Getter
public class Popup {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "popup_id", nullable = false, unique = true)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "host_id", nullable = false)
	private Host host;
	
	@Column(name = "start_date", nullable = false)
	private LocalDateTime startDate;
	
	@Column(name = "end_date", nullable = false)
	private LocalDateTime endDate;
	
	@Column(name = "approval_status", nullable = false)
	@Enumerated(EnumType.STRING)
	private ApprovalStatus approvalStatus;
}
