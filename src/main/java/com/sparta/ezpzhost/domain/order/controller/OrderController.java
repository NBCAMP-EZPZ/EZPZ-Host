package com.sparta.ezpzhost.domain.order.controller;

import static com.sparta.ezpzhost.common.util.ControllerUtil.getResponseEntity;

import com.sparta.ezpzhost.common.dto.CommonResponse;
import com.sparta.ezpzhost.common.security.UserDetailsImpl;
import com.sparta.ezpzhost.domain.order.dto.OrderCondition;
import com.sparta.ezpzhost.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 주문 목록 조회
     *
     * @param pageable    페이지네이션 조건
     * @param searchType  조건별 조회 기준
     * @param itemId      상품별 조회할 때 조회할 상품 Id
     * @param orderStatus 주문 상태별 조회할 때 조회할 주문 상태
     * @param userDetails 사용자 정보
     * @return 조건별 주문 목록
     */
    @GetMapping
    public ResponseEntity<CommonResponse<?>> findOrdersAllByStatus(
            Pageable pageable,
            @RequestParam(defaultValue = "all") String searchType,
            @RequestParam(defaultValue = "-1") Long itemId,
            @RequestParam(defaultValue = "all") String orderStatus,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        OrderCondition cond = OrderCondition.of(searchType, itemId, orderStatus);
        return getResponseEntity(
                orderService.findOrdersAllByStatus(cond, pageable, userDetails.getHost()),
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
