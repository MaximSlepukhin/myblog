create table if not exists posts(
    id bigserial PRIMARY KEY,
    title varchar(255) NOT NULL,
    text varchar NOT NULL,
    likesCount INT DEFAULT 0,
    tags varchar(255)
);

create table if not exists comments(
    id bigserial PRIMARY KEY,
    text varchar(255) NOT NULL,
    postId BIGINT NOT NULL,
    FOREIGN KEY (postId) REFERENCES posts(id) ON DELETE CASCADE
);