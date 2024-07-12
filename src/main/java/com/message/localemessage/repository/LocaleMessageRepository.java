package com.message.localemessage.repository;

import com.message.localemessage.model.LocaleMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LocaleMessageRepository extends JpaRepository<LocaleMessage, Long> {

    Optional<LocaleMessage> findByPlaceholderAndLang(String code, String locale);

}
