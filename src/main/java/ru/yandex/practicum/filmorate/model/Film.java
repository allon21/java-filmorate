package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
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
    @Builder.Default
    private Set<Integer> idUsersLikedFilm = new HashSet<>();

    public static Integer getFilmsLikes(Film film) {
        return film.getIdUsersLikedFilm().size();
    }
}
