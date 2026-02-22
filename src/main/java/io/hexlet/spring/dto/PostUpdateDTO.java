package io.hexlet.spring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PostUpdateDTO {

    @NotBlank(message = "Заголовок не может быть пустым")
    @Size(min = 3, max = 100, message = "Заголовок должен быть от 3 до 100 символов")
    private String title;

    @NotBlank(message = "Содержание не может быть пустым")
    @Size(min = 10, max = 5000, message = "Содержание должно быть от 10 до 5000 символов")
    private String content;

    private boolean published = false;
}
