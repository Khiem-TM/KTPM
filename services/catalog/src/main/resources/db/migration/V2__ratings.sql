CREATE TABLE IF NOT EXISTS ratings (
    id BIGSERIAL PRIMARY KEY,
    book_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    points INT NOT NULL CHECK (points BETWEEN 1 AND 10),
    comment TEXT NULL,
    time TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_ratings_book_id ON ratings(book_id);
