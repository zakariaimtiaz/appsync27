-- Initial data for the application

-- Default admin user
-- MD5 (12345 = 827ccb0eea8a706c4c34a16891f84e7b)
INSERT OR IGNORE INTO user_info (login_id, password, user_name, email, state) 
VALUES ('admin', '827ccb0eea8a706c4c34a16891f84e7b', 'SUPER ADMIN', 'admin@friendship.ngo', 1);

-- Application properties
INSERT OR IGNORE INTO app_properties (ID, NAME, CODE, TYPE, STATE) VALUES 
(1, 'SERVER_1', 'S001', 'SERVER', 1);

-- Default client
INSERT OR IGNORE INTO client (NAME, CODE, STATE)
VALUES ('CLIENT_1', 'C001', 1);

-- Default server configuration
INSERT OR IGNORE INTO server_config (SERVER_NAME, SERVER_ADDRESS, STATE) 
VALUES ('SERVER_1', 'http://localhost:8080/appSync27', 1);
