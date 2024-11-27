CREATE TABLE IF NOT EXISTS genre (
    genre_id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS mpa_rating (
    rating_id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS film (
    film_id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    release_date DATE NOT NULL,
    duration INT NOT NULL,
    rating_id INT NOT NULL,
    FOREIGN KEY (rating_id) REFERENCES mpa_rating (rating_id)
);

CREATE TABLE IF NOT EXISTS user_info (
    user_id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    login VARCHAR(255) NOT NULL,
    birthday DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genre (
    film_id INT,
    genre_id INT,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES film (film_id),
    FOREIGN KEY (genre_id) REFERENCES genre (genre_id)
);

CREATE TABLE IF NOT EXISTS film_like (
    film_id INT,
    user_id INT,
    PRIMARY KEY (film_id, user_id),
    FOREIGN KEY (film_id) REFERENCES film (film_id),
    FOREIGN KEY (user_id) REFERENCES user_info (user_id)
);

CREATE TABLE IF NOT EXISTS friendship (
    user_id INT,
    friend_id INT,
    status BOOLEAN NOT NULL,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES user_info (user_id),
    FOREIGN KEY (friend_id) REFERENCES user_info (user_id)
);