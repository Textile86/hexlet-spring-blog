package io.hexlet.spring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class PostCreateDTO {
    @NotBlank(message = "Заголовок не может быть пустым")
    @Size(min = 5, max = 100, message = "Заголовок должен быть от 3 до 100 символов")
    private String title;

    @NotBlank(message = "Содержание не может быть пустым")
    @Size(min = 10, max = 5000, message = "Содержание должно быть от 10 до 5000 символов")
    private String content;

    private boolean published = false;

    @NotNull(message = "Необходимо указать пользователя")
    private Long userId;

    private List<Long> tagIds = new ArrayList<>();
}
