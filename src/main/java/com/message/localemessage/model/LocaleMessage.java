package com.message.localemessage.model;


import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "locale_messages")
public class LocaleMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Hidden
    private Long id;

    @Column(length = 5200)
    private String placeholder;

    private String lang;

    @Hidden
    @Column(name = "local_message", length = 5200)
    private String localMessage;

    @Hidden
    @Column(name = "local_code")
    private String localCode;
}
