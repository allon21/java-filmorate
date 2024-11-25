package ru.yandex.practicum.filmorate.exception;

public class EmptyIdException extends RuntimeException {
    public EmptyIdException() {
        super("id должен быть > ноля.");
    }
}