package io.hexlet.spring.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class PostParamsDTO {
    private String titleCont;
    private Long userId;
    private LocalDateTime createdAtGt;
    private LocalDateTime createdAtLt;
}
