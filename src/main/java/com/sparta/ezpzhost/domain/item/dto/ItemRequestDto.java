package com.sparta.ezpzhost.domain.item.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class ItemRequestDto {

    @NotBlank(message = "상품명을 입력해주세요.")
    private String name;

    @NotBlank(message = "상품 설명을 입력해주세요.")
    private String description;

    @NotNull(message = "가격을 입력해주세요.")
    @Min(value = 100, message = "최소 가격 100원 이상으로 입력해주세요.")
    private int price;

    @NotNull(message = "재고를 입력해주세요.")
    @Min(value = 1, message = "최소 1개 이상의 제고를 등록해주세요.")
    private int stock;

    @NotNull(message = "상품 사진을 등록해주세요.")
    private MultipartFile image;
}
