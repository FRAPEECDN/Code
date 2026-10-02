CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100)
);

INSERT INTO users(username)
VALUES ('AdminUser');
