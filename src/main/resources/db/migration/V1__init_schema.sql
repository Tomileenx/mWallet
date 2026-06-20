CREATE TABLE refresh_token
(
    id            UUID NOT NULL,
    token_hash         VARCHAR(255) NOT NULL,
    user_account_id       UUID,
    expiry_date   TIMESTAMP WITHOUT TIME ZONE,
    last_activity TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_refresh_token PRIMARY KEY (id)
);

CREATE TABLE user_account
(
    id            UUID NOT NULL,
    email         VARCHAR(255) NOT NULL,
    full_name     VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(255) NOT NULL,
    verified      BOOLEAN   NOT NULL  DEFAULT FALSE,
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_user_account PRIMARY KEY (id)
);

ALTER TABLE refresh_token
    ADD CONSTRAINT uc_refresh_token_tokenhash UNIQUE (token_hash);

ALTER TABLE refresh_token
    ADD CONSTRAINT fk_refresh_token_user_account
        FOREIGN KEY (user_account_id)
            REFERENCES user_account(id)
            ON DELETE CASCADE;

ALTER TABLE user_account
    ADD CONSTRAINT uc_user_account_email UNIQUE (email);
