package com.sparta.ezpzhost.common.resolver;

import com.sparta.ezpzhost.common.exception.CustomException;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static com.sparta.ezpzhost.common.exception.ErrorType.*;


@Component
public class CustomPageableHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 50;

    /**
     * Pageable 타입의 파라미터를 지원하도록 설정합니다.
     *
     * @param parameter 체크할 파라미터
     * @return Pageable 타입의 파라미터인 경우 true 반환
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(Pageable.class);
    }

    /**
     * Pageable 객체를 생성하기 위해 page와 size 파라미터를 파싱하고, 검증합니다.
     *
     * @param parameter     the method parameter to resolve. This parameter must
     *                      have previously been passed to {@link #supportsParameter} which must
     *                      have returned {@code true}.
     * @param mavContainer  the ModelAndViewContainer for the current request
     * @param webRequest    the current request
     * @param binderFactory a factory for creating {@link WebDataBinder} instances
     * @return Pageable 객체
     */
    @Override
    public Pageable resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            org.springframework.web.bind.support.WebDataBinderFactory binderFactory) {

        String pageParameter = webRequest.getParameter("page");
        String sizeParameter = webRequest.getParameter("size");

        int page = parsePageParameter(pageParameter);
        int size = parseSizeParameter(sizeParameter);

        validatePageAndSize(page, size);

        return PageRequest.of(page, size);
    }

    /**
     * page 파라미터를 파싱하며, 없을 경우 기본값인 0을 반환합니다.
     *
     * @param pageParameter String 페이지 number
     * @return int 페이지 number
     */
    private int parsePageParameter(String pageParameter) {
        try {
            return (pageParameter != null) ? Integer.parseInt(pageParameter) : 0;
        } catch (NumberFormatException e) {
            throw new CustomException(INVALID_PAGE_NUMBER_FORMAT);
        }
    }

    /**
     * size 파라미터를 파싱하며, 없을 경우 기본값을 사용합니다.
     *
     * @param sizeParameter String 페이지 size
     * @return int 페이지 size
     */
    private int parseSizeParameter(String sizeParameter) {
        try {
            return (sizeParameter != null) ? Integer.parseInt(sizeParameter) : DEFAULT_PAGE_SIZE;
        } catch (NumberFormatException e) {
            throw new CustomException(INVALID_PAGE_SIZE_FORMAT);
        }
    }

    /**
     * page와 size 파라미터가 유효한지 검증합니다.
     *
     * @param page 페이지 number
     * @param size 페이지 size
     */
    private void validatePageAndSize(int page, int size) {
        if (page < 0) {
            throw new CustomException(INVALID_PAGE_NUMBER);
        }
        if (size <= 0) {
            throw new CustomException(INVALID_PAGE_SIZE);
        }
        if (size > MAX_PAGE_SIZE) {
            throw new CustomException(EXCEED_MAX_PAGE_SIZE);
        }
    }

}