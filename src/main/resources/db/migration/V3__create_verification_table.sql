CREATE TABLE verification_token
(
    id            UUID NOT NULL,
    token         VARCHAR(255) NOT NULL,
    user_account_id       UUID,
    expires_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_verification_token PRIMARY KEY (id)
);

ALTER TABLE verification_token
    ADD CONSTRAINT uc_verification_token_token UNIQUE (token);

ALTER TABLE verification_token
    ADD CONSTRAINT fk_verification_token_user_account
        FOREIGN KEY (user_account_id)
            REFERENCES user_account(id)
            ON DELETE CASCADE;
