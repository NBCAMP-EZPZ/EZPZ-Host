package com.sparta.ezpzhost.domain.popup.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class PopupRequestDto {

    @NotBlank(message = "팝업명을 입력해주세요.")
    private String name;

    @NotBlank(message = "팝업 설명을 입력해주세요.")
    private String description;

    @NotNull(message = "대표 사진을 등록해주세요.")
    private MultipartFile thumbnail;

    @NotBlank(message = "주소를 입력해주세요.")
    private String address;

    @NotBlank(message = "담당자 이름을 입력해주세요.")
    private String managerName;

    @NotBlank(message = "담당자 번호를 입력해주세요.")
    @Pattern(regexp = "^\\d{2,3}\\d{3,4}\\d{4}$", message = "휴대폰 번호 양식에 맞지 않습니다.")
    private String phoneNumber;

    @NotNull(message = "시작 일자를 입력해주세요.")
    private LocalDateTime startDate;

    @NotNull(message = "종료 일자를 입력해주세요.")
    private LocalDateTime endDate;

    @NotNull(message = "추가 사진을 최소 1개이상 등록해주세요.")
    @Size(min = 1, max = 3, message = "추가 사진은 최소 1개, 최대 3개까지 등록 가능합니다.")
    private List<MultipartFile> images;
}
