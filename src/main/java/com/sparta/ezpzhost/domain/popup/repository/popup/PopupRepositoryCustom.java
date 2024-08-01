package com.sparta.ezpzhost.domain.popup.repository.popup;

import com.sparta.ezpzhost.domain.host.entity.Host;
import com.sparta.ezpzhost.domain.popup.dto.PopupCondition;
import com.sparta.ezpzhost.domain.popup.entity.Popup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PopupRepositoryCustom {

    Page<Popup> findAllPopupsByStatus(Host host, Pageable pageable, PopupCondition cond);

}
