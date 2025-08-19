# 📱 Roaming Assistant

A full-stack web application that simulates international roaming usage and recommends the most cost-effective roaming packs for travelers.  

Built with **Spring Boot (Java, PostgreSQL)** for backend and **React (TypeScript)** for frontend.  

---

## 🚀 Features

- 🌍 **Trip Planner** – Plan trips by country and travel dates.  
- 👤 **Usage Profile** – Define typical usage (data, voice, SMS).  
- 💰 **Simulation** – Compare roaming packs vs pay-as-you-go.  
- 🧠 **Recommendation** – Get top 3 best roaming pack options.  
- 🛒 **Checkout** – Mock purchase flow for selected packs.  

---

## 🛠️ Tech Stack

**Backend**
- Java 17  
- Spring Boot  
- Spring Data JPA + Hibernate  
- PostgreSQL  
- Lombok  
- Maven  

**Frontend**
- React  
- TypeScript  
- Material UI (MUI)  
- Axios  

---

## ⚙️ Backend Setup

### 1. Clone the repository
```bash
git clone https://github.com/<your-username>/<repo-name>.git
cd <repo-name>/demo

server.port=8000

spring.datasource.url=jdbc:postgresql://localhost:5432/roaming_db
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true

mvn spring-boot:run
