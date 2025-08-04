# Deployment Guide

This guide covers various deployment options for the CargoPro Backend System.

## 🐳 Docker Deployment

### 1. Create Dockerfile
Create a `Dockerfile` in the project root:

```dockerfile
FROM openjdk:11-jre-slim

# Set working directory
WORKDIR /app

# Copy the built JAR file
COPY build/libs/*.jar app.jar

# Expose port
EXPOSE 8080

# Set JVM options for production
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### 2. Create Docker Compose
Create `docker-compose.yml`:

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:13
    environment:
      POSTGRES_DB: cargopro_db
      POSTGRES_USER: cargopro
      POSTGRES_PASSWORD: cargopro123
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - cargopro-network

  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/cargopro_db
      SPRING_DATASOURCE_USERNAME: cargopro
      SPRING_DATASOURCE_PASSWORD: cargopro123
      SPRING_JPA_HIBERNATE_DDL_AUTO: update
    depends_on:
      - postgres
    networks:
      - cargopro-network

volumes:
  postgres_data:

networks:
  cargopro-network:
    driver: bridge
```

### 3. Build and Deploy
```bash
# Build the application
./gradlew build

# Build and start with Docker Compose
docker-compose up --build -d

# View logs
docker-compose logs -f app

# Stop services
docker-compose down
```

## ☁️ Cloud Deployment

### AWS Deployment

#### Using AWS Elastic Beanstalk

1. **Prepare the application**:
```bash
./gradlew build
```

2. **Create application.yml for production**:
```yaml
server:
  port: 5000

spring:
  datasource:
    url: ${RDS_HOSTNAME:jdbc:postgresql://localhost:5432/cargopro_db}
    username: ${RDS_USERNAME:cargopro}
    password: ${RDS_PASSWORD:cargopro123}
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect

logging:
  level:
    com.cargoAppService: INFO
    org.hibernate.SQL: WARN
```

3. **Deploy to Elastic Beanstalk**:
```bash
# Install EB CLI
pip install awsebcli

# Initialize EB application
eb init cargopro-backend

# Create environment
eb create production

# Deploy
eb deploy
```

#### Using AWS ECS (Fargate)

1. **Build and push Docker image**:
```bash
# Build image
docker build -t cargopro-backend .

# Tag for ECR
docker tag cargopro-backend:latest 123456789012.dkr.ecr.us-east-1.amazonaws.com/cargopro-backend:latest

# Push to ECR
docker push 123456789012.dkr.ecr.us-east-1.amazonaws.com/cargopro-backend:latest
```

2. **Create ECS Task Definition** (`task-definition.json`):
```json
{
  "family": "cargopro-backend",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "256",
  "memory": "512",
  "executionRoleArn": "arn:aws:iam::123456789012:role/ecsTaskExecutionRole",
  "containerDefinitions": [
    {
      "name": "cargopro-backend",
      "image": "123456789012.dkr.ecr.us-east-1.amazonaws.com/cargopro-backend:latest",
      "portMappings": [
        {
          "containerPort": 8080,
          "protocol": "tcp"
        }
      ],
      "environment": [
        {
          "name": "SPRING_DATASOURCE_URL",
          "value": "jdbc:postgresql://your-rds-endpoint:5432/cargopro_db"
        },
        {
          "name": "SPRING_DATASOURCE_USERNAME",
          "value": "cargopro"
        },
        {
          "name": "SPRING_DATASOURCE_PASSWORD",
          "value": "your-secure-password"
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/cargopro-backend",
          "awslogs-region": "us-east-1",
          "awslogs-stream-prefix": "ecs"
        }
      }
    }
  ]
}
```

### Google Cloud Platform

#### Using Google Cloud Run

1. **Create cloudbuild.yaml**:
```yaml
steps:
  - name: 'gcr.io/cloud-builders/gradle'
    args: ['build']
  
  - name: 'gcr.io/cloud-builders/docker'
    args: ['build', '-t', 'gcr.io/$PROJECT_ID/cargopro-backend', '.']
  
  - name: 'gcr.io/cloud-builders/docker'
    args: ['push', 'gcr.io/$PROJECT_ID/cargopro-backend']
  
  - name: 'gcr.io/cloud-builders/gcloud'
    args:
    - 'run'
    - 'deploy'
    - 'cargopro-backend'
    - '--image'
    - 'gcr.io/$PROJECT_ID/cargopro-backend'
    - '--region'
    - 'us-central1'
    - '--platform'
    - 'managed'
    - '--allow-unauthenticated'

images:
  - 'gcr.io/$PROJECT_ID/cargopro-backend'
```

2. **Deploy**:
```bash
gcloud builds submit --config cloudbuild.yaml
```

### Heroku Deployment

1. **Create Procfile**:
```
web: java -Dserver.port=$PORT $JAVA_OPTS -jar build/libs/*.jar
```

2. **Create system.properties**:
```
java.runtime.version=11
```

3. **Deploy**:
```bash
# Login to Heroku
heroku login

# Create app
heroku create cargopro-backend

# Add PostgreSQL addon
heroku addons:create heroku-postgresql:hobby-dev

# Set environment variables
heroku config:set SPRING_JPA_HIBERNATE_DDL_AUTO=update

# Deploy
git push heroku main
```

## 🔧 Production Configuration

### Environment Variables
Set these environment variables for production:

```bash
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://your-db-host:5432/cargopro_db
SPRING_DATASOURCE_USERNAME=your-username
SPRING_DATASOURCE_PASSWORD=your-secure-password

# JPA/Hibernate
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
SPRING_JPA_SHOW_SQL=false

# Logging
LOGGING_LEVEL_COM_CARGOAPPSERVICE=INFO
LOGGING_LEVEL_ORG_HIBERNATE_SQL=WARN

# Server
SERVER_PORT=8080
```

### Security Considerations

1. **Database Security**:
   - Use strong passwords
   - Enable SSL connections
   - Restrict database access to application servers only

2. **Application Security**:
   - Add authentication and authorization
   - Enable HTTPS
   - Implement rate limiting
   - Add input sanitization

3. **Infrastructure Security**:
   - Use VPC/private networks
   - Configure security groups/firewalls
   - Enable monitoring and logging

### Performance Tuning

1. **JVM Options**:
```bash
JAVA_OPTS="-Xmx1g -Xms512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
```

2. **Database Connection Pool**:
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

3. **Caching** (add to dependencies):
```gradle
implementation 'org.springframework.boot:spring-boot-starter-cache'
implementation 'org.ehcache:ehcache'
```

## 📊 Monitoring

### Health Checks
Add Spring Boot Actuator:

```gradle
implementation 'org.springframework.boot:spring-boot-starter-actuator'
```

Configure in `application.yml`:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always
```

### Logging
Configure structured logging for production:

```yaml
logging:
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: /var/log/cargopro/application.log
  level:
    com.cargoAppService: INFO
    org.springframework.web: INFO
    org.hibernate.SQL: WARN
```

## 🔄 CI/CD Pipeline

### GitHub Actions
Create `.github/workflows/deploy.yml`:

```yaml
name: Deploy to Production

on:
  push:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v2
    - name: Set up JDK 11
      uses: actions/setup-java@v2
      with:
        java-version: '11'
        distribution: 'adopt'
    - name: Run tests
      run: ./gradlew test

  deploy:
    needs: test
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v2
    - name: Deploy to production
      run: |
        # Add your deployment commands here
        echo "Deploying to production..."
```

This deployment guide provides multiple options for deploying your CargoPro Backend System to various platforms and environments.
