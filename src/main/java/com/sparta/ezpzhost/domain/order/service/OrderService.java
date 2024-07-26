package com.sparta.ezpzhost.domain.order.service;

import com.sparta.ezpzhost.common.exception.CustomException;
import com.sparta.ezpzhost.common.exception.ErrorType;
import com.sparta.ezpzhost.common.util.PageUtil;
import com.sparta.ezpzhost.domain.host.entity.Host;
import com.sparta.ezpzhost.domain.item.entity.Item;
import com.sparta.ezpzhost.domain.item.repository.ItemRepository;
import com.sparta.ezpzhost.domain.order.dto.OrderCondition;
import com.sparta.ezpzhost.domain.order.dto.OrderFindAllResponseDto;
import com.sparta.ezpzhost.domain.order.dto.OrderRequestDto;
import com.sparta.ezpzhost.domain.order.dto.OrderResponseDto;
import com.sparta.ezpzhost.domain.order.entity.Order;
import com.sparta.ezpzhost.domain.order.enums.OrderSearchType;
import com.sparta.ezpzhost.domain.order.repository.OrderRepository;
import com.sparta.ezpzhost.domain.orderline.dto.OrderlineResponseDto;
import com.sparta.ezpzhost.domain.orderline.repository.OrderlineRepository;
import java.util.List;
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
    private final OrderlineRepository orderlineRepository;

    /**
     * 조건별 주문 목록 조회
     *
     * @param orderRequestDto 조회 조건
     * @param pageable        페이지네이션 조건
     * @param host            요청한 호스트
     * @return 조회 조건에 따른 주문 목록
     */
    public Page<OrderFindAllResponseDto> findAllOrders(OrderRequestDto orderRequestDto,
            Pageable pageable, Host host) {
        OrderCondition cond = OrderCondition.of(orderRequestDto);

        if (cond.getItemId() != -1 && cond.getSearchType().equals(OrderSearchType.BY_ITEM)) {
            Item item = itemRepository.findById(cond.getItemId())
                    .orElseThrow(() -> new CustomException(ErrorType.ITEM_ACCESS_FORBIDDEN));
            if (!item.getPopup().getHost().getId().equals(host.getId())) {
                throw new CustomException(ErrorType.ITEM_ACCESS_FORBIDDEN);
            }
        }
        Page<Order> orderPages = orderRepository.findOrdersAllByStatus(cond, pageable, host);
        PageUtil.validatePageableWithPage(pageable, orderPages);
        return orderPages.map(OrderFindAllResponseDto::of);
    }

    /**
     * 주문 상세 조회
     *
     * @param orderId 조회할 주문 id
     * @param host    요청한 호스트
     * @return 요청한 주문 상세 조회 데이터
     */
    public OrderResponseDto findOrder(Long orderId, Host host) {
        Order order = orderRepository.findOrderWithDetails(orderId, host.getId());

        if (order == null) {
            throw new CustomException(ErrorType.ORDER_NOT_FOUND_OR_ACCESS_DENIED);
        }

        List<OrderlineResponseDto> orderlineResponseDtoList = order.getOrderlineList().stream()
                .filter(orderline -> itemRepository.isItemSoldByHost(orderline.getItem().getId(),
                        host.getId()))
                .map(OrderlineResponseDto::of)
                .toList();
        return OrderResponseDto.of(order, orderlineResponseDtoList);
    }
}