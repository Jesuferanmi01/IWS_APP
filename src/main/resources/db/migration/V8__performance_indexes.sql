
CREATE INDEX IX_transactions_status
    ON transactions (status);


CREATE INDEX IX_transactions_created_at
    ON transactions (created_at DESC);


CREATE INDEX IX_transactions_status_created
    ON transactions (status, created_at DESC);

CREATE INDEX IX_transactions_ip_merchant
    ON transactions (ip_address, merchant_code);


CREATE INDEX IX_transactions_merchant_created
    ON transactions (merchant_code, created_at DESC);

CREATE INDEX IX_transactions_merchant_amount
    ON transactions (merchant_code, amount);

CREATE INDEX IX_event_logs_created_at
    ON event_logs (created_at DESC);

CREATE INDEX IX_event_logs_service
    ON event_logs (service);

CREATE INDEX IX_event_logs_service_created
    ON event_logs (service, created_at DESC);

CREATE INDEX IX_ip_address_ip
    ON ip_address (ip);

CREATE INDEX IX_ip_address_flagged
    ON ip_address (is_flagged)
    WHERE is_flagged = 1;


CREATE INDEX IX_cards_card_no
    ON card_details (card_no);

CREATE INDEX IX_users_merchant_code
    ON users (user_code);
GO