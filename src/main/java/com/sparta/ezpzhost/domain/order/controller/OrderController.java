package com.sparta.ezpzhost.domain.order.controller;

import static com.sparta.ezpzhost.common.util.ControllerUtil.getResponseEntity;

import com.sparta.ezpzhost.common.dto.CommonResponse;
import com.sparta.ezpzhost.common.security.UserDetailsImpl;
import com.sparta.ezpzhost.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 주문 목록 조회
     *
     * @param pageable        페이지네이션 조건
     * @param orderRequestDto 조회 조건
     * @param userDetails     사용자 정보
     * @return 조건별 주문 목록
     */
    @GetMapping
    public ResponseEntity<CommonResponse<?>> findAllOrders(
            Pageable pageable,
            @RequestBody OrderRequestDto orderRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return getResponseEntity(
                orderService.findAllOrders(orderRequestDto, pageable, userDetails.getHost()),
                "주문 목록 조회 성공");

    }

    /**
     * 주문 상세 조회
     *
     * @param orderId     조회할 주문 id
     * @param userDetails 사용자 정보
     * @return 주문 상세 조회 데이터
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<CommonResponse<?>> findOrder(
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return getResponseEntity(orderService.findOrder(orderId, userDetails.getHost()),
                "주문 상세 조회 성공");
    }
}
