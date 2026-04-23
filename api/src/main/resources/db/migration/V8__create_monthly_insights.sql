CREATE TABLE monthly_insights (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id        UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    content        TEXT         NOT NULL,
    model          VARCHAR(100) NOT NULL,
    input_tokens   INTEGER,
    output_tokens  INTEGER,
    period_start   DATE         NOT NULL,
    period_end     DATE         NOT NULL,
    check_in_count INTEGER      NOT NULL,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_monthly_insights_user_id ON monthly_insights (user_id);
CREATE INDEX idx_monthly_insights_user_created ON monthly_insights (user_id, created_at DESC);
