CREATE TABLE IF NOT EXISTS rating (
  rating_id integer generated by default as identity primary key,
  name varchar(30) NOT NULL
);

 CREATE TABLE IF NOT EXISTS genre (
  genre_id integer generated by default as identity primary key,
  name VARCHAR(30) NOT NULL
);

CREATE TABLE IF NOT EXISTS film (
  film_id integer generated by default as identity primary key,
  name VARCHAR(30) NOT NULL,
  description VARCHAR(200) NOT NULL,
  release_date DATE NOT NULL,
  duration integer NOT NULL,
  rating_id integer NOT NULL REFERENCES rating(rating_id)
);

CREATE TABLE IF NOT EXISTS genre_film (
  film_id integer NOT NULL REFERENCES film(film_id),
  genre_id integer NOT NULL REFERENCES genre(genre_id),
  PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS users (
  user_id integer generated by default as identity primary key,
  email VARCHAR(30) NOT NULL,
  login VARCHAR(30) NOT NULL,
  name VARCHAR(30) NOT NULL,
  birthday DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS film_likes (
  film_id integer NOT NULL REFERENCES film(film_id),
  user_id integer NOT NULL REFERENCES users(user_id),
  PRIMARY KEY(film_id, user_id)
);

CREATE TABLE IF NOT EXISTS friendship (
  friendship_id integer generated by default as identity primary key,
  user_id integer NOT NULL REFERENCES users(user_id),
  friend_user_id integer NOT NULL REFERENCES users(user_id),
  confirmed boolean NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS director (
  director_id integer generated by default as identity primary key,
  name VARCHAR(30) NOT NULL
);

DELETE FROM FRIENDSHIP ;
ALTER TABLE FRIENDSHIP ALTER COLUMN friendship_id RESTART WITH 1;
DELETE FROM FILM_LIKES ;
DELETE FROM USERS ;
ALTER TABLE USERS ALTER COLUMN user_id RESTART WITH 1;
DELETE FROM GENRE_FILM ;
DELETE FROM GENRE ;
ALTER TABLE GENRE ALTER COLUMN genre_id RESTART WITH 1;
DELETE FROM FILM ;
ALTER TABLE FILM ALTER COLUMN film_id RESTART WITH 1;
DELETE FROM RATING ;
ALTER TABLE RATING ALTER COLUMN rating_id RESTART WITH 1;
DELETE FROM DIRECTOR ;
ALTER TABLE DIRECTOR ALTER COLUMN director_id RESTART WITH 1;

INSERT INTO RATING (name) values('G'), ('PG'), ('PG-13'), ('R'), ('NC-17');

INSERT INTO GENRE (name) values('�������'), ('�����'), ('����������'), ('�������'), ('��������������'), ('������');
