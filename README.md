# СourseLink 

**CourseLink** - is a system designed to automate the creation, management and organization of defense sessions for academic projects, such as theses or courseworks in universities. With its help teachers can create special slots that help to structure the students defence of their academic works.

---

## 📋 Key Features

- **Session Creation**: Add defense sessions and booking slots with specified dates, times, venues, and topics
- **Session Scheduling**: Ensure sessions are scheduled without overlapping time slots
- **User Management**: Ban/Unban users or change their roles with admin credentials
- **Localization**: The system includes such languages as English(Default), Ukrainian, Russian, Polish and German
- **CRUD for task categories**: creating, updating, deleting and retreaving task categories with admin credentials

--- 

## ⚙️ Technologies

- **Programming language**: Java 11
- **Framework**: Spring Boot
- **Authorization**: Spring Security, JWT
- **Database**: PostgeSQL
- **Migrations**: FlyWay
- **Testing**: JUnit, Mockito, Parametrized tests, Testcontainers, Postman
- **Test covarage**: JaCoCo
- **Logging**: SLF4J, Logstash, ElasticSearch, Kibana
- **API Documentation**: OpenAPI (Swagger)
- **Containerization**: Docker

---

## 🚀 Getting Started  

### Prerequisites  

1. Java 11 or newer.  
2. Docker (for running the database and containerized application).  
3. A REST client like Postman (optional, for testing).

### Installation Steps  

1. Clone the repository:  
   ```bash
   git clone https://github.com/VladislavSaliuk/CourseLink.git
   cd CourseLink
2. Run the application:
   ```bash
   ./mvnw spring-boot:run
3. Start Docker containers:
   ```bash
   docker-compose up -d   
4. Access the OpenAPI at:
   ```bash
   localhost:8080/swagger-ui/index.html

---

## 📂 Project Structure

- **config/**: Contains configuration classes, such as beans, CORS settings, or application profiles.  
- **controller/**: Handles HTTP requests and returns responses.  
- **dto/**: Contains classes used for transferring data between layers.  
- **entity/**: Defines entities mapped to database tables.  
- **repository/**: Provides database access methods.
- **security/**: Contains security configuration, filters, and authentication-related logic.  
- **service/**: Contains business logic and service layer code.
- **validation/**: Custom validators for ensuring data integrity and request validation.  
- **resources/**: Stores configuration files, templates, and migration scripts.
- **test/**: Contains test classes for unit and integration testing.  
- **docker-compose.yml**: Sets up the application and database containers.  
- **pom.xml**: Lists dependencies, plugins, and build configuration.

--- 

## 🛠 Contributing

If you want to contribute to this project, feel free to fork the repository, create a feature branch, and submit a pull request.

---

## 📜 License

This project is licensed under the [LICENSE](./LICENSE). You are free to use, modify, and distribute this software as described in the license.
