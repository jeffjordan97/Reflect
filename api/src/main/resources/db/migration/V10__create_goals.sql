CREATE TABLE goals (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title           VARCHAR(300) NOT NULL,
    description     TEXT,
    horizon         VARCHAR(20)  NOT NULL CHECK (horizon IN ('SHORT', 'MEDIUM', 'LONG')),
    status          VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'PAUSED', 'COMPLETED', 'RELEASED')),
    target_date     DATE,
    sort_order      INTEGER      NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    completed_at    TIMESTAMPTZ,
    released_at     TIMESTAMPTZ
);

CREATE INDEX idx_goals_user_id ON goals (user_id);
CREATE INDEX idx_goals_user_active ON goals (user_id) WHERE status = 'ACTIVE';
