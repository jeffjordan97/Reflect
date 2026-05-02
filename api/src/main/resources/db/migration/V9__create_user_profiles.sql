CREATE TABLE user_profiles (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id        UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    profession     VARCHAR(200),
    industry       VARCHAR(100),
    role_level     VARCHAR(50),
    focus_areas    TEXT[],
    bio_context    TEXT,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_user_profiles_user_id UNIQUE (user_id)
);
