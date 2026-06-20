CREATE TABLE deposits
(
    id                    UUID           NOT NULL,
    to_wallet_id          UUID           NOT NULL,
    amount                DECIMAL(19, 2) NOT NULL,
    transaction_reference VARCHAR(255)   NOT NULL,
    transaction_status    VARCHAR(255)   NOT NULL,
    created_at            TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_deposits PRIMARY KEY (id)
);

ALTER TABLE deposits
    ADD CONSTRAINT uc_deposits_transactionreference UNIQUE (transaction_reference);

ALTER TABLE deposits
    ADD CONSTRAINT FK_DEPOSITS_TO_WALLET FOREIGN KEY (to_wallet_id) REFERENCES wallet (id);

CREATE INDEX idx_deposit_wallet_created_at ON deposits (to_wallet_id, created_at DESC);