CREATE TABLE check_ins (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id        UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    week_start     DATE        NOT NULL,
    wins           TEXT,
    friction       TEXT,
    energy_rating  SMALLINT    CHECK (energy_rating BETWEEN 1 AND 10),
    signal_moment  TEXT,
    intentions     TEXT,
    completed      BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_check_ins_user_week UNIQUE (user_id, week_start)
);
CREATE INDEX idx_check_ins_user_id ON check_ins (user_id);
CREATE INDEX idx_check_ins_user_week ON check_ins (user_id, week_start DESC);
