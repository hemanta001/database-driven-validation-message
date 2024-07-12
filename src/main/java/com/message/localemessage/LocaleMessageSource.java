package com.message.localemessage;

import com.message.exception.LocaleMessageException;
import com.message.localemessage.model.LocaleMessage;
import com.message.localemessage.repository.LocaleMessageRepository;
import jakarta.validation.MessageInterpolator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocaleMessageSource implements MessageInterpolator {
    private final LocaleMessageRepository localeMessageRepository;
    private static final String MESSAGE_SOURCE_ERROR = "Message Source Not Available";

    @Override
    @Cacheable(value = "internalMessage", key = "#s")
    public String interpolate(String s, Context context) {
        return Objects.requireNonNull(getMessageInternal(s, Locale.getDefault()));
    }

    @Cacheable(value = "internalMessage", key = "#code+'-'+#locale")
    public String getMessage(@NonNull String code, Object[] args, String defaultMessage, @NonNull Locale locale) {
        return getMessageInternal(code, locale);
    }

    @Cacheable(value = "internalMessage", key = "#messageTemplate.replace('{', '').replace('}', '')+'-'+#locale")
    @Override
    public String interpolate(String messageTemplate, Context context, Locale locale) {
        if (messageTemplate.contains("{") && messageTemplate.contains("}")) {
            messageTemplate = messageTemplate.replace("{", "").replace("}", "");
            if (!StringUtils.hasText(locale.toString())) {
                locale = Locale.getDefault();
            }
            StringSubstitutor sub = new StringSubstitutor(context.getConstraintDescriptor().getAttributes(), "{", "}");
            return sub.replace(Objects.requireNonNull(getMessageInternal(messageTemplate, locale)));
        }
        return messageTemplate;
    }


    @NonNull
    @Cacheable(value = "internalMessage", key = "#code+'-'+#locale")
    public String getMessage(@NonNull String code, Object[] args, @NonNull Locale locale) {
        if (!StringUtils.hasText(locale.toString())) {
            locale = Locale.getDefault();
        }
        return Objects.requireNonNull(getMessageInternal(code, locale));
    }


    public String getMessageInternal(String code, Locale locale) {
        try {
            Optional<LocaleMessage> postgresMessageOptional = localeMessageRepository.findByPlaceholderAndLang(code, locale.toString());
            if (postgresMessageOptional.isPresent()) {
                return postgresMessageOptional.get().getLocalMessage();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        throw new LocaleMessageException(MESSAGE_SOURCE_ERROR, HttpStatus.BAD_REQUEST);
    }
}
