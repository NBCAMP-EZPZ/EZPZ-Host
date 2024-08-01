package com.sparta.ezpzhost.domain.popup.entity;

import com.sparta.ezpzhost.common.entity.Timestamped;
import com.sparta.ezpzhost.common.exception.CustomException;
import com.sparta.ezpzhost.common.exception.ErrorType;
import com.sparta.ezpzhost.domain.host.entity.Host;
import com.sparta.ezpzhost.domain.popup.dto.ImageResponseDto;
import com.sparta.ezpzhost.domain.popup.dto.PopupRequestDto;
import com.sparta.ezpzhost.domain.popup.enums.ApprovalStatus;
import com.sparta.ezpzhost.domain.popup.enums.PopupStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Popup extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "popup_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
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

    @Column(name = "like_count", nullable = false)
    private int likeCount;

    @Column(name = "review_count", nullable = false)
    private int reviewCount;

    @Column(name = "rating_avg", nullable = false)
    private float ratingAvg;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @OneToMany(mappedBy = "popup")
    private List<Image> imageList;

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

}
