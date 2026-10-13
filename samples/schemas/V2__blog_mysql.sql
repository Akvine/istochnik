-- Миграция для MySQL: имена в обратных апострофах, AUTO_INCREMENT, enum(...), tinyint(1), индексы внутри CREATE TABLE.
-- По ней POST /v1/schemas/convert возвращает три запроса на генерацию.

CREATE TABLE `authors` (
    `id`         INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `login`      VARCHAR(32)  NOT NULL,
    `email`      VARCHAR(255) NOT NULL,
    `rating`     FLOAT        NOT NULL DEFAULT 0,
    `is_blocked` TINYINT(1)   NOT NULL DEFAULT 0,
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_authors_login` (`login`),
    UNIQUE KEY `uk_authors_email` (`email`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE `posts` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT,
    `author_id`    INT UNSIGNED NOT NULL,
    `title`        VARCHAR(200) NOT NULL,
    `body`         LONGTEXT     NOT NULL,
    `status`       ENUM('draft', 'published', 'archived') NOT NULL DEFAULT 'draft',
    `views`        MEDIUMINT UNSIGNED NOT NULL DEFAULT 0,
    `cover`        BLOB,
    `published_at` TIMESTAMP    NULL,
    PRIMARY KEY (`id`),
    -- Обычный индекс на генерацию не влияет
    KEY `idx_posts_status` (`status`),
    CONSTRAINT `fk_posts_authors` FOREIGN KEY (`author_id`) REFERENCES `authors` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE `comments` (
    `id`         BIGINT     NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `post_id`    BIGINT     NOT NULL,
    `author_id`  INT UNSIGNED,
    `text`       TEXT       NOT NULL,
    `likes`      SMALLINT   NOT NULL DEFAULT 0,
    `created_on` DATE       NOT NULL,
    `created_at` TIME       NOT NULL,
    FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB;

-- Изменения после создания таблиц тоже учитываются
ALTER TABLE `posts` ADD COLUMN `slug` VARCHAR(100) NOT NULL;
ALTER TABLE `posts` ADD UNIQUE (`slug`);
