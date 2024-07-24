package com.sparta.ezpzhost.domain.popup.repository.popup;

import com.sparta.ezpzhost.common.util.PageUtil;
import com.sparta.ezpzhost.domain.host.entity.Host;
import com.sparta.ezpzhost.domain.popup.entity.Popup;
import org.springframework.data.domain.Page;

public interface PopupRepositoryCustom {
    Page<Popup> findAllPopupsByStatus(Host host, PageUtil pageUtil);
}
