package com.sparta.ezpzhost.domain.item.repository;

import com.sparta.ezpzhost.domain.host.entity.Host;
import com.sparta.ezpzhost.domain.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {
    boolean existsByName(String name);

    Optional<Item> findByIdAndPopup_Host(Long itemId, Host host);
}
