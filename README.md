# java-filmorate
Template repository for Filmorate project.

![DB flow chart](DB_flow_chart_v2.jpg)

TESTING USING MYSQL
-------------------

-> Database creation & filling in

~~~~~~~~~
CREATE TABLE IF NOT EXISTS rating (
  rating_id INT UNSIGNED NOT NULL AUTO_INCREMENT, 
  name VARCHAR(30) NOT NULL, 
  PRIMARY KEY(rating_id)
);

INSERT INTO rating 
VALUES 
  (1, 'PG-16');
  
CREATE TABLE IF NOT EXISTS genre (
  genre_id INT UNSIGNED NOT NULL AUTO_INCREMENT, 
  name VARCHAR(30) NOT NULL, 
  PRIMARY KEY(genre_id)
);

INSERT INTO genre 
VALUES 
  (1, 'Action');
  
CREATE TABLE IF NOT EXISTS film (
  film_id INT UNSIGNED NOT NULL AUTO_INCREMENT, 
  name VARCHAR(30) NOT NULL, 
  description VARCHAR(200) NOT NULL, 
  release_date DATE NOT NULL, 
  duration INT UNSIGNED NOT NULL, 
  rating_id INT UNSIGNED NOT NULL, 
  PRIMARY KEY(film_id), 
  FOREIGN KEY(rating_id) REFERENCES rating(rating_id)
);

INSERT INTO film 
VALUES 
  (
    1, 'Terminator', 'I will be back', 
    '1984-10-26', 108, 1
  );
  
INSERT INTO film 
VALUES 
  (
    2, 'Star wars 5', 'I am your father', 
    '1980-05-17', 124, 1
  );
  
INSERT INTO film 
VALUES 
  (
    null, 'Terminator 5', 'Genisys', '2015-07-02', 
    126, 1
  );
  
CREATE TABLE IF NOT EXISTS genre_film (
  film_id INT UNSIGNED NOT NULL, 
  genre_id INT UNSIGNED NOT NULL, 
  PRIMARY KEY (film_id, genre_id), 
  FOREIGN KEY (film_id) REFERENCES film(film_id), 
  FOREIGN KEY (genre_id) REFERENCES genre(genre_id)
);

INSERT INTO genre_film 
VALUES 
  (1, 1);
  
CREATE TABLE IF NOT EXISTS user (
  user_id INT UNSIGNED NOT NULL AUTO_INCREMENT, 
  email VARCHAR(30) NOT NULL, 
  login VARCHAR(30) NOT NULL, 
  name VARCHAR(30) NOT NULL, 
  birthday DATE NOT NULL, 
  PRIMARY KEY(user_id)
);

INSERT INTO user 
VALUES 
  (
    1, 'misha@gmail.com', 'misha', 'misha', 
    '1984-05-06'
  ), 
  (
    2, 'sasha@gmail.com', 'sasha', 'sasha', 
    '1990-04-01'
  ), 
  (
    3, 'masha@gmail.com', 'masha', 'masha', 
    '1998-06-17'
  );
  
INSERT INTO user 
VALUES 
  (
    null, 'roma@gmail.com', 'roma', 'roma', 
    '1994-04-04'
  );
  
INSERT INTO user 
VALUES 
  (
    null, 'moshe@gmail.com', 'moshe', 
    'moshe', '1990-05-05'
  );
  
CREATE TABLE IF NOT EXISTS film_likes (
  film_id INT UNSIGNED NOT NULL, 
  user_id INT UNSIGNED NOT NULL, 
  PRIMARY KEY(film_id, user_id), 
  FOREIGN KEY (film_id) REFERENCES film(film_id), 
  FOREIGN KEY (user_id) REFERENCES user(user_id)
);

INSERT INTO film_likes 
VALUES 
  (1, 1), 
  (1, 2);
  
INSERT INTO film_likes 
VALUES 
  (3, 3);
  
CREATE TABLE IF NOT EXISTS friendship (
  friendship_id INT UNSIGNED NOT NULL AUTO_INCREMENT, 
  user_id INT UNSIGNED NOT NULL, 
  friend_user_id INT UNSIGNED NOT NULL, 
  confirmed BOOL NOT NULL DEFAULT 0, 
  PRIMARY KEY(friendship_id), 
  FOREIGN KEY (user_id) REFERENCES user(user_id), 
  FOREIGN KEY (friend_user_id) REFERENCES user(user_id)
);

INSERT INTO friendship 
VALUES 
  (1, 1, 2, false), 
  (2, 2, 3, false);
  
INSERT INTO friendship 
VALUES 
  (null, 4, 1, false);
  
INSERT INTO friendship 
VALUES 
  (null, 1, 5, false), 
  (null, 3, 5, false);
~~~~~~~~~~~~~~~~~~~~~

-> Select queries from created database

1) Get friends of user 2
------------------------ 

~~~~~~~~
SELECT
*
FROM
user u
WHERE
u.user_id IN (
SELECT
f.friend_user_id
FROM
friendship f
WHERE
f.user_id = 2
UNION
SELECT
f.user_id
FROM
friendship f
WHERE
f.friend_user_id = 2
);
~~~~~~~~

2) Get common friends of users 1 and 3
--------------------------------------

~~~~
SELECT 
  * 
FROM 
  user u 
WHERE 
  u.user_id IN (
    SELECT 
      all_friends_for_pair.user_id 
    FROM 
      (
        SELECT 
          f.user_id 
        FROM 
          friendship f 
        WHERE 
          (
            f.friend_user_id = 1 
            OR f.friend_user_id = 3
          ) 
        UNION ALL 
        SELECT 
          f.friend_user_id 
        FROM 
          friendship f 
        WHERE 
          (
            f.user_id = 1 
            OR f.user_id = 3
          )
      ) all_friends_for_pair 
    GROUP BY 
      all_friends_for_pair.user_id 
    HAVING 
      COUNT(*) > 1
  );
~~~~

3) Get all users
----------------
~~~~
SELECT * FROM user;
~~~~

4) Get user with id 1
---------------------
~~~~
SELECT * FROM user WHERE user_id = 1;
~~~~

5) Get film with id 1
---------------------
~~~~
SELECT * FROM film WHERE film_id = 1;
~~~~

6) Find most popular films (show all films whether or not liked, ordered by amount of likes)
--------------------------
~~~~
SELECT 
  f.* 
FROM 
  film f 
  LEFT JOIN film_likes fl USING(film_id) 
GROUP BY 
  f.film_id 
ORDER BY 
  COUNT(DISTINCT fl.user_id) DESC 
LIMIT 
  10;
~~~~