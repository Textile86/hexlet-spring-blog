package io.hexlet.spring.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "users")
@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Email должен быть валидным")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Имя не может быть пустым")
    private String firstName;

    @NotBlank(message = "Фамилия не может быть пустой")
    private String lastName;

    private LocalDate birthday;
}
