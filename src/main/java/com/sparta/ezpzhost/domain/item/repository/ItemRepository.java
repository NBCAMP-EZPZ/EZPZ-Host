package com.sparta.ezpzhost.domain.item.repository;

import com.sparta.ezpzhost.domain.host.entity.Host;
import com.sparta.ezpzhost.domain.item.entity.Item;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {

    boolean existsByName(String name);

    Optional<Item> findByIdAndPopup_Host(Long itemId, Host host);

    @Query("SELECT COUNT(i) > 0 FROM Item i JOIN i.popup p JOIN p.host h WHERE i.id = :itemId AND h.id = :hostId")
    boolean isItemSoldByHost(@Param("itemId") Long itemId, @Param("hostId") Long hostId);

    boolean existsByModifiedAtAfter(LocalDateTime localDateTime);
}
