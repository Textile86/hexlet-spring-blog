package io.hexlet.spring.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.LocalDate;

@Setter
@Getter
public class UserPatchDTO {

    @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
    private JsonNullable<String> firstName = JsonNullable.undefined();

    @Size(min = 2, max = 50, message = "Фамилия должна быть от 2 до 50 символов")
    private JsonNullable<String> lastName = JsonNullable.undefined();

    @Email(message = "Email должен быть валидным")
    private JsonNullable<String> email = JsonNullable.undefined();

    private JsonNullable<LocalDate> birthday = JsonNullable.undefined();
}