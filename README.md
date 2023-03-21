# eventmanagement
PLay scala project demo event management

# Student CRUD

## Setup Instructions

### Prerequisites

##### 1.scala 2.13.6

##### 2. sbt 1.3.8

##### 3. play Framework

##### 4. MySQL database

###  Configuration

##### 1.  Clone the repository to your local machine.

##### 2.  Navigate to the project directory.

##### 3.  Copy `conf/application.conf.example` to `conf/application.conf`.

##### 4.  Edit `conf/application.conf` with your MySQL database credentials.

```bash

db.default.driver=com.mysql.jdbc.Driver

db.default.url="jdbc:mysql://localhost:3306/<your_database_name>"

db.default.username=<your_database_username>

db.default.password=<your_database_password>

```

###  Running the Application

##### 5.  Navigate to the project directory.

##### 6.  Run `sbt run` command to start the application.

##### 7.  Open `http://localhost:9000` in your browser to view the application.

###  Running Tests

##### 8.  Navigate to the project directory.

##### 9.  Run `sbt test` command to run the tests

###  Contributing

##### Please follow the standard git flow process when contributing to this project:

##### 10.  Fork the repository.

##### 11.  Create a feature branch off of `develop`.

##### 12.  Make changes and commit to your feature branch.

##### 13.  Push your feature branch to your forked repository.

##### 14.  Open a pull request to the `develop` branch of the main repository.
