package com.sparta.ezpzhost.domain.order.service;

import com.sparta.ezpzhost.common.exception.CustomException;
import com.sparta.ezpzhost.common.exception.ErrorType;
import com.sparta.ezpzhost.domain.host.entity.Host;
import com.sparta.ezpzhost.domain.item.entity.Item;
import com.sparta.ezpzhost.domain.item.repository.ItemRepository;
import com.sparta.ezpzhost.domain.order.dto.OrderCondition;
import com.sparta.ezpzhost.domain.order.dto.OrderFindAllResponseDto;
import com.sparta.ezpzhost.domain.order.entity.Order;
import com.sparta.ezpzhost.domain.order.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;

    /**
     * 조건별 주문 목록 조회
     *
     * @param cond     조회 조건
     * @param pageable 페이지네이션 조건
     * @param host     요청한 호스트
     * @return 조회 조건에 따른 주문 목록
     */
    public Page<OrderFindAllResponseDto> findOrdersAllByStatus(OrderCondition cond,
            Pageable pageable, Host host) {
        if (cond.getItemId() != -1) {
            Item item = itemRepository.findById(cond.getItemId())
                    .orElseThrow(() -> new CustomException(ErrorType.ITEM_ACCESS_FORBIDDEN));
            if (!item.getPopup().getHost().getId().equals(host.getId())) {
                throw new CustomException(ErrorType.ITEM_ACCESS_FORBIDDEN);
            }
        }
        Page<Order> orderPages = orderRepository.findOrdersAllByStatus(cond, pageable, host);
        return orderPages.map(OrderFindAllResponseDto::of);
    }
}
