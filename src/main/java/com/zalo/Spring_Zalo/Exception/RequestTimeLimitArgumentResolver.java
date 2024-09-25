package com.zalo.Spring_Zalo.Exception;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class RequestTimeLimitArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasMethodAnnotation(RequestTimeLimit.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        RequestTimeLimit annotation = parameter.getMethodAnnotation(RequestTimeLimit.class);
        long maxRequestTimeMs = annotation.value();

        long startTime = System.currentTimeMillis();
        try {
            return parameter.getParameterType().newInstance();
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            if (duration > maxRequestTimeMs) {
                throw new RuntimeException("Request took too long to process: " + duration + "ms");
            }
        }
    }
}
