# --- !Ups
CREATE TABLE student (
                         studentId bigint NOT NULL,
                         fullName varchar(255) NOT NULL,
                         collegeName varchar(255),
                         department varchar(255),
                         PRIMARY KEY (studentId)
);
