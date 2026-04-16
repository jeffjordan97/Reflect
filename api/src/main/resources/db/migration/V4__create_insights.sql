CREATE TABLE insights (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id       UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    check_in_id   UUID         NOT NULL REFERENCES check_ins(id) ON DELETE CASCADE,
    content       TEXT         NOT NULL,
    model         VARCHAR(100) NOT NULL,
    input_tokens  INTEGER,
    output_tokens INTEGER,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_insights_check_in UNIQUE (check_in_id)
);

CREATE INDEX idx_insights_user_id ON insights (user_id);
CREATE INDEX idx_insights_check_in_id ON insights (check_in_id);
