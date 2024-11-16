package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;

import jakarta.validation.constraints.*;
import lombok.Data;
/**
 * Film.
 */
@Data
public class Film {

    private long id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @NotNull
    @PastOrPresent
    private LocalDate releaseDate;
    @Positive
    private int duration;
}
