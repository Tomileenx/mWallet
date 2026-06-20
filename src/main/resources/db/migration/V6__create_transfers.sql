CREATE TABLE transfers
(
    id                    UUID           NOT NULL,
    from_wallet_id        UUID           NOT NULL,
    to_wallet_id          UUID           NOT NULL,
    amount                DECIMAL(19, 2) NOT NULL,
    transaction_reference VARCHAR(255)   NOT NULL,
    transaction_status    VARCHAR(255)   NOT NULL,
    created_at            TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_transfers PRIMARY KEY (id)
);

ALTER TABLE transfers
    ADD CONSTRAINT uc_transfers_transactionreference UNIQUE (transaction_reference);

ALTER TABLE transfers
    ADD CONSTRAINT FK_TRANSFERS_FROM_WALLET FOREIGN KEY (from_wallet_id) REFERENCES wallet (id);

ALTER TABLE transfers
    ADD CONSTRAINT FK_TRANSFERS_TO_WALLET FOREIGN KEY (to_wallet_id) REFERENCES wallet (id);

CREATE INDEX idx_transfer_from_wallet_created_at ON transfers (from_wallet_id, created_at DESC);

CREATE INDEX idx_transfer_to_wallet_created_at ON transfers (to_wallet_id, created_at DESC);