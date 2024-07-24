package com.sparta.ezpzhost.domain.item.repository;

import com.sparta.ezpzhost.common.util.PageUtil;
import com.sparta.ezpzhost.domain.host.entity.Host;
import com.sparta.ezpzhost.domain.item.dto.ItemCondition;
import com.sparta.ezpzhost.domain.item.entity.Item;
import org.springframework.data.domain.Page;

public interface ItemRepositoryCustom {
    Page<Item> findAllItemsByPopupAndStatus(Host host, PageUtil pageUtil, ItemCondition cond);
}
