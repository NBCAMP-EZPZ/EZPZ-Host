package com.sparta.ezpzhost.domain.host.entity;

import com.sparta.ezpzhost.common.entity.Timestamped;
import com.sparta.ezpzhost.domain.host.dto.SignupRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Host extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "host_id")
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

    // 동시성 테스트용 생성자
    private Host(String username) {
        this.username = username;
    }

    public static Host createMockHost(String username) {
        return new Host(username);
    }

}
