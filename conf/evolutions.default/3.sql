CREATE TABLE employee (
                         employeeId bigint NOT NULL,
                         employeeName varchar(255) NOT NULL,
                         employeeMobile varchar(255),
                         employeeEmail varchar(255) UNIQUE,
                         employeeAddress varchar(255),
                         employeeUsername varchar(255) UNIQUE,
                         employeePassword varchar(255),
                         PRIMARY KEY (employeeId)
);