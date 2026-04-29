
CREATE TABLE event_logs (
                            id         BIGINT IDENTITY(1,1) PRIMARY KEY,
                            service    VARCHAR(50)   NOT NULL,
                            message    VARCHAR(500)  NOT NULL,
                            detail1    VARCHAR(500)  NULL,
                            detail2    VARCHAR(500)  NULL,
                            detail3    VARCHAR(500)  NULL,
                            created_at DATETIME2     NOT NULL DEFAULT SYSDATETIME()
);
GO


CREATE PROCEDURE dbo.sp_insert_event_log
    @p_service  VARCHAR(50),
    @p_message  VARCHAR(500),
    @p_detail1  VARCHAR(500) = NULL,
    @p_detail2  VARCHAR(500) = NULL,
    @p_detail3  VARCHAR(500) = NULL
AS
BEGIN
    SET NOCOUNT ON;

INSERT INTO event_logs (service, message, detail1, detail2, detail3, created_at)
VALUES (@p_service, @p_message, @p_detail1, @p_detail2, @p_detail3, SYSDATETIME());
END
GO

-- ── 3. sp_flag_transaction ────────────────────────────────────────────────────
-- OUTPUT params removed — ends with SELECT so JPA nativeQuery can map the result set.
CREATE PROCEDURE dbo.sp_flag_transaction
    @p_card_no       VARCHAR(20),
    @p_amount        DECIMAL(15,2),
    @p_merchant_code VARCHAR(50),
    @p_ip_address    VARCHAR(50),
    @p_trans_ref     VARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @flagCount  INT;
    DECLARE @otp        VARCHAR(10);
    DECLARE @p_status   VARCHAR(15);
    DECLARE @newId      UNIQUEIDENTIFIER = NEWID();

    -- Count existing flagged / blacklisted transactions for this user
SELECT @flagCount = COUNT(*)
FROM   transactions
WHERE  merchant_code = @p_merchant_code
  AND  status    IN ('FLAGGED', 'BLACKLISTED');

-- Generate 6-digit OTP
SET @otp = RIGHT('000000' + CAST(ABS(CHECKSUM(NEWID())) % 1000000 AS VARCHAR), 6);

    IF @flagCount > 2
BEGIN
        SET @p_status = 'BLACKLISTED';
UPDATE users
SET    status = 'BLACKLISTED'
WHERE  user_code = @p_merchant_code;
END
ELSE
BEGIN
        SET @p_status = 'FLAGGED';
END

    -- Insert the transaction record
INSERT INTO transactions (id, trans_ref, user_code, card_no, amount,
                          merchant_code, ip_address, status, token, created_at)
VALUES (@newId, @p_trans_ref, @p_merchant_code, @p_card_no, @p_amount,
        @p_merchant_code, @p_ip_address, @p_status, @otp, GETDATE());

-- Log the event
EXEC dbo.sp_insert_event_log
        'SP_FLAG', 'sp_flag_transaction executed',
        @p_trans_ref, @p_status, @p_merchant_code;

    -- Return result set — JPA nativeQuery maps this to FlagResult projection
SELECT @p_status AS status,
       @otp      AS token;
END
GO

-- ── 4. sp_approve_transaction ─────────────────────────────────────────────────
-- OUTPUT params removed — ends with SELECT so JPA nativeQuery can map the result set.
CREATE PROCEDURE dbo.sp_approve_transaction
    @p_card_no       VARCHAR(20),
    @p_amount        DECIMAL(15,2),
    @p_merchant_code VARCHAR(50),
    @p_ip_address    VARCHAR(50),
    @p_trans_ref     VARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @newId UNIQUEIDENTIFIER = NEWID();

    -- Insert the approved transaction record
INSERT INTO transactions (id, trans_ref, user_code, card_no, amount,
                          merchant_code, ip_address, status, token, created_at)
VALUES (@newId, @p_trans_ref,@p_merchant_code, @p_card_no, @p_amount,
        @p_merchant_code, @p_ip_address, 'PASSED', NULL, GETDATE());

-- Log the event
EXEC dbo.sp_insert_event_log
        'SP_APPROVE', 'sp_approve_transaction executed',
        @p_trans_ref, 'PASSED', @p_merchant_code;

    -- Return result set
SELECT 'PASSED' AS status,
       NULL     AS token;
END
GO


ALTER TABLE ip_address
    ADD is_flagged BIT NOT NULL DEFAULT 0;
GO

-- Index for the GET /flagged-ips endpoint — filters on is_flagged = 1
CREATE INDEX IX_ip_address_is_flagged
    ON ip_address (is_flagged)
    WHERE is_flagged = 1;
GO