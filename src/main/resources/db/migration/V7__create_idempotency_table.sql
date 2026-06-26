CREATE TABLE idempotency_records
(
    id                    UUID           NOT NULL,
    idempotency_key       VARCHAR(255)   NOT NULL,
    user_account_id       UUID           NOT NULL,
    transaction_type      VARCHAR (255)  NOT NULL,
    transaction_reference VARCHAR(255)   NOT NULL,
    created_at            TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_idempotency_records PRIMARY KEY (id)
);

ALTER TABLE idempotency_records
        ADD CONSTRAINT Fk_IDEMPOTENCY_USER_ACCOUNT FOREIGN KEY (user_account_id)
        REFERENCES user_account(id)
        ON DELETE CASCADE;

ALTER TABLE idempotency_records
        ADD CONSTRAINT uc_idempotency_unique
        UNIQUE (user_account_id, transaction_type, idempotency_key);