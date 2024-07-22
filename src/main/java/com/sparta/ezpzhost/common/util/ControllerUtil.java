package com.sparta.ezpzhost.common.util;

import com.sparta.ezpzhost.common.dto.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Slf4j
public final class ControllerUtil {

    /**
     * 응답 데이터가 있는 ResponseEntity
     *
     * @param response 응답 데이터
     * @param msg 응답 메시지
     * @return ResponseEntity
     */
    public static ResponseEntity<CommonResponse<?>> getResponseEntity(Object response, String msg) {
        return ResponseEntity.ok().body(CommonResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .msg(msg)
                .data(response)
                .build());
    }

    /**
     * 응답 데이터가 없는 ResponseEntity
     *
     * @param msg 응답 메시지
     * @return ResponseEntity
     */
    public static ResponseEntity<CommonResponse<?>> getResponseEntity(String msg) {
        return ResponseEntity.ok().body(CommonResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .msg(msg)
                .build());
    }

    /**
     * PathVariable의 Id와 RequestBody의 Id와 일치하는지 확인
     *
     * @param pathId PathVariable의 Id
     * @param bodyId RequestBody의 Id
     */
    public static void verifyPathIdWithBody(Long pathId, Long bodyId) {
        if (!pathId.equals(bodyId)) {
            throw new IllegalArgumentException("PathVariable의 Id가 RequestBody의 Id와 일치하지 않습니다.");
        }
    }

}
