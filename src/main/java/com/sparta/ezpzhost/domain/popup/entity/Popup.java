package com.sparta.ezpzhost.domain.popup.entity;

import com.sparta.ezpzhost.common.entity.Timestamped;
import com.sparta.ezpzhost.common.exception.CustomException;
import com.sparta.ezpzhost.common.exception.ErrorType;
import com.sparta.ezpzhost.domain.host.entity.Host;
import com.sparta.ezpzhost.domain.popup.dto.ImageResponseDto;
import com.sparta.ezpzhost.domain.popup.dto.PopupRequestDto;
import com.sparta.ezpzhost.domain.popup.enums.ApprovalStatus;
import com.sparta.ezpzhost.domain.popup.enums.PopupStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Popup extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "popup_id")
    private Long id;

    private String name;

    private String description;

    private String thumbnailUrl;

    private String thumbnailName;

    private String address;

    private String managerName;

    private String phoneNumber;

    private int likeCount;

    private int reviewCount;

    private double ratingAvg;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private PopupStatus popupStatus;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id")
    private Host host;

    @OneToMany(mappedBy = "popup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> imageList = new ArrayList<>();

    /**
     * 생성자
     */
    private Popup(Host host, PopupRequestDto requestDto, ImageResponseDto thumbnail,
            PopupStatus popupStatus, ApprovalStatus approvalStatus) {
        this.host = host;
        this.name = requestDto.getName();
        this.description = requestDto.getDescription();
        this.address = requestDto.getAddress();
        this.managerName = requestDto.getManagerName();
        this.phoneNumber = requestDto.getPhoneNumber();
        this.likeCount = 0;
        this.startDate = requestDto.getStartDate();
        this.endDate = requestDto.getEndDate();
        this.thumbnailUrl = thumbnail.getUrl();
        this.thumbnailName = thumbnail.getName();
        this.popupStatus = popupStatus;
        this.approvalStatus = approvalStatus;
    }

    public static Popup of(Host host, PopupRequestDto requestDto, ImageResponseDto thumbnail,
            PopupStatus popupStatus, ApprovalStatus approvalStatus) {
        return new Popup(host, requestDto, thumbnail, popupStatus, approvalStatus);
    }

    // 동시성 테스트용 생성자
    private Popup(Host host, LocalDateTime startDate, LocalDateTime endDate) {
        this.host = host;
        this.startDate = startDate;
        this.endDate = endDate;
        this.approvalStatus = ApprovalStatus.APPROVED;
    }

    public static Popup createMockPopup(Host host, LocalDateTime startDate, LocalDateTime endDate) {
        return new Popup(host, startDate, endDate);
    }

    /**
     * 수정 가능 여부 확인
     */
    public void checkPossibleUpdateStatus() {
        if (this.approvalStatus.equals(ApprovalStatus.REJECTED)) {
            throw new CustomException(ErrorType.POPUP_NOT_APPROVAL);
        } else if (this.popupStatus.equals(PopupStatus.CANCELED) ||
                this.popupStatus.equals(PopupStatus.COMPLETED)) {
            throw new CustomException(ErrorType.POPUP_STATUS_IMPASSIBLE);
        }
    }

    /**
     * 썸네일 수정
     *
     * @param updateThumbnail 썸네일 수정 정보
     */
    public void updateThumbnail(ImageResponseDto updateThumbnail) {
        this.thumbnailUrl = updateThumbnail.getUrl();
        this.thumbnailName = updateThumbnail.getName();
    }

    /**
     * 팝업 정보 수정
     *
     * @param requestDto 팝업 수정 정보
     */
    public void update(PopupRequestDto requestDto) {
        this.name = requestDto.getName();
        this.description = requestDto.getDescription();
        this.address = requestDto.getAddress();
        this.managerName = requestDto.getManagerName();
        this.phoneNumber = requestDto.getPhoneNumber();
        this.startDate = requestDto.getStartDate();
        this.endDate = requestDto.getEndDate();
    }

    /**
     * 취소 가능한 팝업 상태인지 확인
     */
    public void checkCancellationPossible() {
        if (!this.popupStatus.equals(PopupStatus.SCHEDULED)) {
            throw new CustomException(ErrorType.POPUP_CANCEL_FORBIDDEN);
        }
    }

    /**
     * 팝업 취소
     */
    public void cancelPopup() {
        this.popupStatus = PopupStatus.CANCELED;
        this.approvalStatus = ApprovalStatus.REJECTED;
    }

    /**
     * 상품 등록 가능 여부 확인
     */
    public void checkItemCanBeRegistered() {
        if (this.approvalStatus.equals(ApprovalStatus.REVIEWING) ||
                this.approvalStatus.equals(ApprovalStatus.REJECTED)) {
            throw new CustomException(ErrorType.ITEM_REGISTRATION_IMPOSSIBLE);
        }
    }

    /**
     * 팝업 종료
     */
    public void completePopup() {
        this.popupStatus = PopupStatus.COMPLETED;
    }
}
