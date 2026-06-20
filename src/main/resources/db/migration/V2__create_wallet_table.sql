CREATE TABLE wallet
(
    id              UUID         NOT NULL,
    account_number  VARCHAR(255) NOT NULL,
    user_account_id UUID         NOT NULL,
    balance         DECIMAL(19,2) NOT NULL,
    currency        VARCHAR(255) NOT NULL ,
    version         INTEGER,
    created_at      TIMESTAMP WITHOUT TIME ZONE,
    updated_at      TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_wallet PRIMARY KEY (id)
);

ALTER TABLE wallet
    ADD CONSTRAINT uc_wallet_accountnumber UNIQUE (account_number);

ALTER TABLE wallet
    ADD CONSTRAINT uc_wallet_user_account UNIQUE (user_account_id);

ALTER TABLE wallet
    ADD CONSTRAINT FK_WALLET_ON_USER_ACCOUNT
        FOREIGN KEY (user_account_id)
            REFERENCES user_account (id);