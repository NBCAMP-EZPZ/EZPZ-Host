package com.sparta.ezpzhost.domain.popup.entity;

import com.sparta.ezpzhost.common.entity.Timestamped;
import com.sparta.ezpzhost.domain.host.entity.Host;
import com.sparta.ezpzhost.domain.popup.dto.ImageResponseDto;
import com.sparta.ezpzhost.domain.popup.dto.PopupRequestDto;
import com.sparta.ezpzhost.domain.popup.enums.ApprovalStatus;
import com.sparta.ezpzhost.domain.popup.enums.PopupStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name="Popup")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Popup extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "popup_id", nullable = false, unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "host_id", nullable = false)
    private Host host;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(name = "thumbnail_url", nullable = false)
    private String thumbnailUrl;

    @Column(name = "thumbnail_name", nullable = false)
    private String thumbnailName;

    @Column(nullable = false)
    private String address;

    @Column(name = "manager_name", nullable = false)
    private String managerName;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "popup_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PopupStatus popupStatus;

    @Column(name = "approval_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    private Popup(Host host, String name, String description, String address, String managerName, String phoneNumber, LocalDateTime startDate, LocalDateTime endDate, String thumbnailUrl, String thumbnailName, PopupStatus popupStatus, ApprovalStatus approvalStatus) {
        this.host = host;
        this.name = name;
        this.description = description;
        this.address = address;
        this.managerName = managerName;
        this.phoneNumber = phoneNumber;
        this.startDate = startDate;
        this.endDate = endDate;

        this.thumbnailUrl = thumbnailUrl;
        this.thumbnailName = thumbnailName;
        this.popupStatus = popupStatus;
        this.approvalStatus = approvalStatus;
    }

    public static Popup of(Host host, PopupRequestDto requestDto, ImageResponseDto thumbnail, PopupStatus popupStatus, ApprovalStatus approvalStatus) {
        return new Popup(
                host,
                requestDto.getName(), requestDto.getDescription(), requestDto.getAddress(),
                requestDto.getManagerName(), requestDto.getPhoneNumber(),
                requestDto.getStartDate(), requestDto.getEndDate(),
                thumbnail.getUrl(), thumbnail.getName(), popupStatus, approvalStatus
        );
    }
}
