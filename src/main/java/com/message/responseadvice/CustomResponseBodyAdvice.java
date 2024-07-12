package com.message.responseadvice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.message.localemessage.LocaleMessageSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ControllerAdvice
@Order(2)
@Slf4j
@RequiredArgsConstructor
public class CustomResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    private final LocaleMessageSource localeMessageSource;
    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(@NotNull MethodParameter returnType, @NotNull Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            @NotNull MethodParameter returnType,
            @NotNull MediaType selectedContentType,
            @NotNull Class selectedConverterType,
            @NotNull ServerHttpRequest request,
            @NotNull ServerHttpResponse response) {

        String responseBody = null;
        try {
            responseBody = this.objectMapper.writeValueAsString(body);
            Pattern pattern = Pattern.compile("\\$\\{([^}]+)}");
            Matcher matcher = pattern.matcher(responseBody);
            StringBuilder result = new StringBuilder();
            while (matcher.find()) {
                this.setLocaleMessageSource(matcher, result);
            }
            matcher.appendTail(result);
            return this.objectMapper.readValue(result.toString(), Map.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return body;
    }

    private void setLocaleMessageSource(Matcher matcher, StringBuilder result) {
        String placeholder = matcher.group(1); // Placeholder with brackets (e.g., {0})
        try {
            String replacement = localeMessageSource.getMessage(placeholder, null, LocaleContextHolder.getLocale()); // Get message source value
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement)); // Quote replacement to avoid regex issues
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
