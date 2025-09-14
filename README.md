# 📌 Social Network - Java Spring Boot

## 📖 Introduction
The **Social Network** project is built with **Java Spring Boot**, providing core social media features:  
- User registration & login (JWT + OAuth2)  
- User profile management  
- Friendships, follow system 
- Posts, comments, reactions
- Group Organization.
- Newsfeed (timeline)  
- Real-time chat (WebSocket + STOMP)  
- Notifications  
- Role-based access & violation management  

---

## 🛠️ Technology Stack
- **Architecture**: Layer Architecture, Restful APIs, STOMP.
- **Backend**: Spring boot, Spring Security, Spring Data JPA. 
- **Database**: MySQL 
- **Cache & Session**: Redis  
- **Authentication & Authorization**: Spring Security + JWT, OAuth2  
- **Real-time Communication**: WebSocket (STOMP protocol)  
- **Build Tool**: Maven 
- **Logging**: SLF4J + Logback  
- **Containerization & Deployment**: Docker.
- **Other Technologies**:Java Mail, Cloundinary APIs, Spring OAuth2-client dependency.
---

## ⚙️ Setup & Run

### 1️⃣ Requirements
- **Java JDK 17+**  
- **Maven 3.9+** (or Gradle)  
- **Docker - Docker Desktop** (optional, for MySQL/Redis/Run Project) 
- **Git**  


### 2️⃣ Clone Project
```bash
git clone https://github.com/yourusername/social-network.git
cd social-network
```

### 3️⃣ Configure Environment
Create application-secret.properties in src/main/resources or .env file in the project root:
```.env
# Database configuration
db.username=your_db_username
db.password=your_db_password
db.url=jdbc:mysql://${mysql.host:localhost}:3306/social-network

# JWT configuration
jwt.access-key=your_jwt_access_key
jwt.refresh-key=your_jwt_refresh_key

# OAuth2 Google configuration
oauth2.google.client-id=your_google_client_id
oauth2.google.client-secret=your_google_client_secret

# OAuth2 Facebook configuration
oauth2.facebook.client-id=your_facebook_client_id
oauth2.facebook.client-secret=your_facebook_client_secret

# OAuth2 GitHub configuration
oauth2.github.client-id=your_github_client_id
oauth2.github.client-secret=your_github_client_secret

# Redis configuration
redis.host=localhost
redis.port=6379

# Cloudinary configuration
cloudinary.cloud-name=your_cloud_name
cloudinary.cloud-key=your_cloud_key
cloudinary.cloud-secret=your_cloud_secret

# Mail configuration
mail.from=your_email@example.com
mail.host=smtp.gmail.com
mail.port=587
mail.username=your_email@example.com
mail.password=your_mail_app_password

```
### 4️⃣ Running the service
Run via Maven (`mvn spring-boot:run`), your IDE, or a Docker container.
