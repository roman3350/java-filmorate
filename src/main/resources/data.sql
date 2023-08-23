INSERT INTO MPA (mpa_name)
VALUES('G'),('PG'),('PG-13'),('R'),('NC-17')
ON CONFLICT DO NOTHING;
INSERT INTO GENRE (genre_name)
VALUES('Комедия'),('Драма'),('Мультфильм'),('Триллер'),('Документальный'),('Боевик')
ON CONFLICT DO NOTHING;