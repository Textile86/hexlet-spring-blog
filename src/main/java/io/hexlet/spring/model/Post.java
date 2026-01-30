package io.hexlet.spring.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@NoArgsConstructor
@Setter
@Getter
public class Post {
    private Long id;

    @NotBlank(message = "Заголовок не может быть пустым")
    private String title;

    @NotBlank(message = "Содержание не может быть пустым")
    private String content;

    @NotBlank(message = "Автор не может быть пустым")
    private String author;

    private LocalDateTime createdAt;
}
