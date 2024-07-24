package com.sparta.ezpzhost.domain.host.entity;

import com.sparta.ezpzhost.common.entity.Timestamped;
import com.sparta.ezpzhost.domain.host.dto.SignupRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Host extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "host_id", nullable = false, unique = true)
    private Long id;

    private String username;

    private String password;

    private String email;

    private String companyName;

    private String businessNumber;

    private Host(SignupRequestDto dto, String encodedPassword) {
        this.username = dto.getUsername();
        this.password = encodedPassword;
        this.email = dto.getEmail();
        this.companyName = dto.getCompanyName();
        this.businessNumber = dto.getBusinessNumber();
    }

    public static Host of(SignupRequestDto dto, String encodedPassword) {
        return new Host(dto, encodedPassword);
    }
}
