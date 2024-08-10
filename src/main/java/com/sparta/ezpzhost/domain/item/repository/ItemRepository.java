package com.sparta.ezpzhost.domain.item.repository;

import com.sparta.ezpzhost.domain.host.entity.Host;
import com.sparta.ezpzhost.domain.item.entity.Item;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {

    boolean existsByName(String name);

    Optional<Item> findByIdAndHost(Long itemId, Host host);

    boolean existsByIdAndHostId(Long itemId, Long hostId);

    boolean existsByModifiedAtAfter(LocalDateTime localDateTime);
}
