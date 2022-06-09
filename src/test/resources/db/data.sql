DELETE
FROM user;

INSERT INTO user (id, name, encrypt_name, age, email)
VALUES (1, 'yb', '"obnuy"', 18, 'zhuyb0614@qq.com');

INSERT INTO user_auth (user_id, identity_no, encrypt_identity_no)
VALUES (1, 'dd', 'ZHVuZHVu');

