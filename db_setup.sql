create database venera_production;
create database venera_test;
create user 'venera_user'@'localhost' identified by 'veneraPWD';
grant all privileges on venera_production.* to 'venera_user'@'localhost';
grant all privileges on venera_test.* to 'venera_user'@'localhost';
