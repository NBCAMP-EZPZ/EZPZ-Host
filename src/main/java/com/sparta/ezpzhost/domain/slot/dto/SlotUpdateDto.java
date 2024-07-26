package com.sparta.ezpzhost.domain.slot.dto;

import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SlotUpdateDto {
	@Range(min = 1, max = 2, message = "1인 최대 예약 가능 인원 수는 1~2명입니다.")
	private int availableCount;
	
	@Range(min = 1, message = "전체 예약 가능 인원은 1명 이상입니다.")
	private int totalCount;
	
	@NotBlank(message = "슬롯 상태를 입력해주세요.")
	private String slotStatus;
}
