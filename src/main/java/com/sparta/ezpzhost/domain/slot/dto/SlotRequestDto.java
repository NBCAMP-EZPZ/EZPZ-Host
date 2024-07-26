package com.sparta.ezpzhost.domain.slot.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SlotRequestDto {
	@NotNull(message = "예약 시작 날짜를 입력해주세요.")
	private LocalDate startDate;
	
	@NotNull(message = "예약 종료 날짜를 입력해주세요.")
	private LocalDate endDate;
	
	@NotNull(message = "예약 시작 시간을 입력해주세요.")
	private LocalTime startTime;
	
	@NotNull(message = "예약 종료 시간을 입력해주세요.")
	private LocalTime endTime;
	
	@Range(min = 1, max = 2, message = "1인 최대 예약 가능 인원 수는 1~2명입니다.")
	private int availableCount;
	
	@Range(min = 1, message = "전체 예약 가능 인원은 1명 이상입니다.")
	private int totalCount;
}
