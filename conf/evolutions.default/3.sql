# --- !Ups
CREATE TABLE employee (
  employee_id INT PRIMARY KEY,
  employee_name VARCHAR(255),
  employee_mobile VARCHAR(20),
  employee_email VARCHAR(255),
  employee_address VARCHAR(255),
  employee_username VARCHAR(255),
  employee_password VARCHAR(255)
);
# --- !Downs
drop table "employee" if exists;