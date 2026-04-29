IF OBJECT_ID('dbo.users', 'U') IS NULL
BEGIN
CREATE TABLE users (
                       id          UNIQUEIDENTIFIER    PRIMARY KEY DEFAULT NEWID(),
                       first_name  VARCHAR(100)        NOT NULL,
                       last_name   VARCHAR(100)        NOT NULL,
                       email       VARCHAR(150)        NOT NULL UNIQUE,
                       user_type   VARCHAR(20)         NOT NULL,
                       user_code   VARCHAR(50)         NOT NULL UNIQUE,
                       created_at  DATETIME2           NOT NULL DEFAULT GETDATE(),
                       status      VARCHAR(20)         NOT NULL DEFAULT 'ACTIVE',
                       password    VARCHAR(255)        NOT NULL,
                       phone_no    VARCHAR(20)         NULL,
                       ip_address  VARCHAR(50)         NOT NULL
);
CREATE INDEX idx_users_email    ON users(email);
CREATE INDEX idx_users_usercode ON users(user_code);
PRINT 'users table created.';
END
GO


IF NOT EXISTS (SELECT 1 FROM users)
BEGIN
INSERT INTO users (
    id, first_name, last_name, email, user_type, user_code,
    created_at, status, password, phone_no, ip_address
)
VALUES
('643D7F14-10CE-4ED4-8A3A-04301E41B78A','John','Smith','john.smith1@example.com','USER','USER-1','2026-03-16 22:52:03.807','ACTIVE','$2a$10$nr6dHXz/SV26GtFUJMYojOELGyO7P82ZKq4.3n8UB/Cth9pvEh9TO','08011111111','192.168.1.10'),

('17236B49-01DB-4F57-9EBF-15837A8A1AED','Grace','Adams','grace.adams@example.com','MERCHANT','MECT-4','2026-03-16 22:54:16.227','ACTIVE','$2a$10$tOSTRRu9XWr5dXnvEj3Xten6Q/FJZjPsbFBab081Ql7Y38OaiUIWW','08044444444','192.168.1.13'),

('125AAF03-E03D-4AEB-B91E-7272B6F80AE0','Michael','Brown','michael.brown@example.com','USER','USER-3','2026-03-16 22:52:44.990','ACTIVE','$2a$10$9uVj3zlGTO1RuDSWN.GWie4lDL7DJAqwzOo3nhYM/I6daKbrJ6jDK','08033333333','192.168.1.12'),

('5AED4680-C665-48A1-923F-9D46600DC342','Aisha','Bello','aisha.bello@example.com','MERCHANT','MECT-2','2026-03-16 22:52:29.037','ACTIVE','$2a$10$NiEfLuzgwnIfyOSSfxkoe.ijbuS5d8u7xITR5QAjaG1MV98RmG5ue','08022222222','192.168.1.11'),

('3AB81C94-BE29-4358-BA86-B144D351F543','Daniel','Okafor','daniel.okafor@example.com','USER','USER-5','2026-03-16 22:54:32.240','ACTIVE','$2a$10$PGMHoDkkDkaugeOgxOYrWOnyZt6hpEkX3Sd6b/cgqG0aJ2vwemjsK','08055555555','192.168.1.14'),

('37DD961D-EDBF-4D85-B63F-B6DBDB92DF58','fefe','ayo','fefe@gmail.com','ADMIN','USER-0','2026-03-16 22:32:30.443','ACTIVE','$2a$10$zNrAMuLKryPvcZmmK/11G.ig3w5NUI7BxxBRTVK/bOKRflWRHppBa','0909090909','123-123'),

('38940C92-70F3-4D08-8A0B-F472D1A55647','Fatima','Ibrahim','fatima.ibrahim@example.com','MERCHANT','MECT-6','2026-03-16 22:55:33.410','BLACKLISTED','$2a$10$lzZ.db/aRMfcbn0ix4NRsOFglwnj2LcbAxSSHOiybiUSUj8FHEsb6','08066666666','192.168.1.15'),

('E84ECA36-5001-427F-B5DE-FD262B00FA0C','Chinedu','Eze','chinedu.eze@example.com','MERCHANT','MECT-7','2026-03-16 22:55:53.480','ACTIVE','$2a$10$nzNHaumNOxqBLXug.wG4su44DJQy.AI6OxFiGyKcdDEdtKMAESxdO','08088888888','192.168.1.17');

PRINT 'users seeded.';
END
GO

PRINT 'IWSAPP init complete.';
GO