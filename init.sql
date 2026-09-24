CREATE USER lab_user WITH PASSWORD 'password';

GRANT CONNECT ON DATABASE lab_db TO lab_user;
GRANT USAGE ON SCHEMA public TO lab_user;