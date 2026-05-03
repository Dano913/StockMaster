
-- =====================================================
-- ASSET QUERIES
-- =====================================================
SELECT * FROM asset;

SELECT * FROM asset WHERE id = ?;

-- =====================================================
-- FUND QUERIES
-- =====================================================
SELECT * FROM fund WHERE id_fondo = ?;
SELECT * FROM fund;

-- =====================================================
-- CLIENT QUERIES
-- =====================================================
SELECT * FROM client;

SELECT id, gestor_id, name, surname, email,
       national_id, join_date, country, user_id
FROM client
WHERE user_id = ?;

UPDATE client SET
    gestor_id=?,
    name=?,
    surname=?,
    email=?,
    national_id=?,
    join_date=?,
    country=?
WHERE id=?;

DELETE FROM client WHERE id = ?;

-- =====================================================
-- GESTOR QUERIES
-- =====================================================
SELECT * FROM gestor;
SELECT * FROM gestor WHERE id = ?;

SELECT id, company_id, fund_id,
       national_id,
       name, surname,
       years_of_experience,
       risk_profile,
       email, phone,
       user_id
FROM gestor
WHERE user_id = ?;

UPDATE gestor
SET company_id=?, fund_id=?, national_id=?, name=?, surname=?,
    years_of_experience=?, risk_profile=?, email=?, phone=?, user_id=?
WHERE id=?;

DELETE FROM gestor WHERE id=?;

-- =====================================================
-- USERS QUERIES
-- =====================================================
SELECT * FROM users
WHERE email = ? AND password = ?
LIMIT 1;

SELECT * FROM users WHERE id = ?;

UPDATE users
SET email = ?, password = ?, role = ?, status = ?
WHERE id = ?;

UPDATE users SET status = ? WHERE id = ?;
UPDATE users SET role = ? WHERE id = ?;

-- =====================================================
-- POSITION & RELATION TABLES
-- =====================================================
SELECT * FROM fund_position_asset;
SELECT * FROM client_fund_position WHERE id_client = ?;
SELECT * FROM client_fund_position;
SELECT * FROM client_fund_position WHERE id = ?;

-- =====================================================
-- TRANSACTION QUERIES
-- =====================================================
SELECT id, position_id, type, amount, date
FROM transaction
WHERE position_id = ?
ORDER BY fecha DESC;

DELETE FROM transaction WHERE id = ?;

-- =====================================================
-- CANDLE & POSITION QUERIES
-- =====================================================
SELECT * FROM candle WHERE asset_id = ? ORDER BY timestamp ASC;

UPDATE position
SET quantity = ?, actual_value = ?
WHERE id = ?;

DELETE FROM position WHERE id = ?;