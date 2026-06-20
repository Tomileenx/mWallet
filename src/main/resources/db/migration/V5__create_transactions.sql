CREATE TABLE transactions
(
    id                    UUID           NOT NULL,
    amount                DECIMAL(19, 2) NOT NULL,
    transaction_type      VARCHAR(255)   NOT NULL,
    balance_type          VARCHAR(255)   NOT NULL,
    status                VARCHAR(255)   NOT NULL,
    wallet_id             UUID           NOT NULL,
    transaction_reference VARCHAR(255)   NOT NULL,
    created_at            TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_transactions PRIMARY KEY (id)
);

ALTER TABLE transactions
    ADD CONSTRAINT FK_TRANSACTIONS_ON_WALLET FOREIGN KEY (wallet_id) REFERENCES wallet (id);

CREATE INDEX idx_transaction_wallet_created_at ON transactions (wallet_id, created_at DESC);

CREATE INDEX idx_transaction_type_created_at ON transactions (transaction_type, created_at DESC);

CREATE INDEX idx_balance_type_created_at ON transactions (balance_type, created_at DESC);