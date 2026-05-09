CREATE TABLE user_wallet (
   id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
   user_id INT NOT NULL UNIQUE,
   balance NUMERIC(19,4) NOT NULL,
   currency VARCHAR(10) NOT NULL,
   version INT DEFAULT 0
);

CREATE INDEX idx_user_wallet_user_id ON user_wallet(user_id);