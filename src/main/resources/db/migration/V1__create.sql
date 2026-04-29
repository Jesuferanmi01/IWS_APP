IF OBJECT_ID('dbo.card_details', 'U') IS NULL
    BEGIN
        CREATE TABLE card_details (
                                      id          int IDENTITY(1,1) NOT NULL,
                                      card_no     varchar(255) NOT NULL,
                                      bank_name   varchar(255) NOT NULL,
                                      status      varchar(255) NOT NULL,
                                      expiry_date date NOT NULL,
                                      CONSTRAINT pk_card_details PRIMARY KEY (id)
        )
    END
GO

IF OBJECT_ID('dbo.ip_address', 'U') IS NULL
    BEGIN
        CREATE TABLE ip_address (
                                    id  int IDENTITY(1,1) NOT NULL,
                                    ip  varchar(255) NOT NULL,
                                    time time NOT NULL,
                                    CONSTRAINT pk_ip_address PRIMARY KEY (id)
        )
    END
GO

IF OBJECT_ID('dbo.transactions', 'U') IS NULL
    BEGIN
        CREATE TABLE transactions (
                                      id            uniqueidentifier NOT NULL,
                                      trans_ref     varchar(255) NOT NULL,
                                      user_code     varchar(255) NOT NULL,
                                      card_no       varchar(255) NOT NULL,
                                      amount        decimal(15,2) NOT NULL,
                                      merchant_code varchar(255) NOT NULL,
                                      ip_address    varchar(255) NOT NULL,
                                      status        varchar(255) NOT NULL,
                                      token         varchar(255),
                                      created_at    datetime2 NOT NULL,
                                      CONSTRAINT pk_transactions PRIMARY KEY (id)
        )

        -- Unique constraint inside the same guard block
        ALTER TABLE transactions
            ADD CONSTRAINT uc_transactions_trans_ref UNIQUE (trans_ref)
    END
GO