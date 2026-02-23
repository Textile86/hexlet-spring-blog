package io.hexlet.spring.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Setter
@Getter
public class PostPatchDTO {

    @Size(min = 3, max = 100, message = "Заголовок должен быть от 3 до 100 символов")
    private JsonNullable<String> title = JsonNullable.undefined();

    @Size(min = 10, max = 5000, message = "Содержание должно быть от 10 до 5000 символов")
    private JsonNullable<String> content = JsonNullable.undefined();

    private JsonNullable<Boolean> published = JsonNullable.undefined();
}
