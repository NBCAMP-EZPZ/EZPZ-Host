package com.sparta.ezpzhost.common.config;

import com.sparta.ezpzhost.common.resolver.CustomPageableHandlerMethodArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class PageConfig implements WebMvcConfigurer {

    private final CustomPageableHandlerMethodArgumentResolver customPageableResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(customPageableResolver);
    }

}