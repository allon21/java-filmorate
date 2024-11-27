package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class Film {
    @Null
    @NotNull
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    @Size(max = 200)
    private String description;
    @NotNull
    private Mpa mpa;
    @NotNull
    @PastOrPresent
    private LocalDate releaseDate;
    @Positive
    private int duration;
    @JsonIgnore
    private Set<Long> idUsersLikedFilm = new HashSet<>();

    private List<Genre> genres;

    public static Long getFilmsLikes(Film film) {
        return (long) (film.getIdUsersLikedFilm() != null ? film.getIdUsersLikedFilm().size() : 0);
    }
}
