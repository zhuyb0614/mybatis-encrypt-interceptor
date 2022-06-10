DELETE
FROM user;

INSERT INTO user (id, name, encrypt_name, age, encrypt_age, long_t, encrypt_long_t, email)
VALUES (1, 'yb', '"obnuy"', 18, 91, 10086, 'L68001', 'zhuyb0614@qq.com');

INSERT INTO user_auth (user_id, identity_no, encrypt_identity_no)
VALUES (1, 'dd', 'ZHVuZHVu');

