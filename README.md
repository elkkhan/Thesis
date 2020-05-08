# ELTE Bsc thesis project
## A class management system
A user can register as a Student or a Teacher.
Teachers can create/delete courses, start classes, track attendance, set grades for students.
Students can take/drop courses, mark attendance for classes and check their grades.
Users can use messaging within the program.

## Building
Repository includes the Gradle Wrapper.
### Steps for building:
```
git clone https://github.com/elkkhan/Thesis/
cd Thesis
```
Linux/Mac:
```
./gradlew build
```
Windows:
```
gradle.bat build
```
An executable Java file will be generated under **build/libs/Thesis-1.0-SNAPSHOT.jar**

### To run the program:
```
java -jar build/libs/Thesis-1.0-SNAPSHOT.jar
```
## Credentials
Predefined users in the database that can be used for testing:
### Students:
1) Login: **CRIS01**
   Password: **crismoltesanti@1A**
2) Login: **JACKY01**
   Password: **jackyjunior@1A**
### Teachers:
1) Login: **ROBA01**
   Password: **robertaaron@1A**
2) Login: **BOBT01**
   Password: **bobthomas@1A**
3) Login: **PHIL01**
   Password: **philleotardo@1A**

 ## Backend
MySQL is used for all of the backend.
It is stored on a free-tier Azure MYSQL server, so the connection might be slow.
To change the backend database, edit JDBC related entries in **resources/META-INF/persistence.xml**

**com.thesis.neptun.main.DatabaseFiller** class can be used to initialize tables with sample data
