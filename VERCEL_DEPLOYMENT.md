# SchedBank - Vercel Deployment Guide

## Full-Stack Architecture Overview

### Frontend Deployment (Vercel)
- **Platform**: Vercel (optimal for Vue.js SPA)
- **Technology**: Vue 3 + Vite
- **Build Output**: Static files in `frontend/dist`
- **Domain**: Auto-generated `.vercel.app` or custom domain

### Backend Deployment (Railway/Render)
- **Platform**: Railway or Render (Java Spring Boot support)
- **Technology**: Spring Boot 3.2.12 + Java 17
- **Database**: PostgreSQL (production) or H2 (development)
- **API Endpoints**: RESTful with OpenAPI documentation

---

## Deployment Steps

### 1. Frontend Deployment to Vercel

#### Prerequisites
- GitHub repository pushed with latest changes
- Vercel account connected to GitHub

#### Automated Deployment
1. **Connect Repository**
   ```bash
   # Push current state
   git add .
   git commit -m "feat: add vercel deployment configuration"
   git push origin main
   ```

2. **Vercel Dashboard Setup**
   - Go to [vercel.com](https://vercel.com)
   - Import GitHub repository: `Sched_Banck_Beta`
   - Framework: **Vue.js** (auto-detected)
   - Root Directory: **Leave empty** (uses vercel.json config)

3. **Environment Variables**
   ```
   VITE_API_BASE_URL=https://your-backend-url.railway.app/api
   ```

#### Manual CLI Deployment
```powershell
# Install Vercel CLI
npm i -g vercel

# Deploy from project root
cd "c:\Users\Cesar\OneDrive\Área de Trabalho\Sched_Banck_Beta"
vercel --prod
```

---

### 2. Backend Deployment Options

#### Option A: Railway (Recommended)
```yaml
# railway.toml (create in project root)
[build]
  builder = "NIXPACKS"
  
[deploy]
  numReplicas = 1
  
[[services]]
  name = "schedbank-api"
  source = "."
  
[services.variables]
  SPRING_PROFILES_ACTIVE = "prod"
  SERVER_PORT = "8080"
```

#### Option B: Render
```yaml
# render.yaml (create in project root)
services:
  - type: web
    name: schedbank-api
    env: java
    buildCommand: "./mvnw clean package -DskipTests"
    startCommand: "java -jar target/*.jar"
    plan: free
    envVars:
      - key: SPRING_PROFILES_ACTIVE
        value: prod
      - key: SERVER_PORT
        value: 8080
```

---

### 3. Production Configuration

#### Backend Environment Variables
```properties
# application-prod.properties
server.port=${SERVER_PORT:8080}
spring.profiles.active=prod

# Database (Railway PostgreSQL)
spring.datasource.url=${DATABASE_URL}
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

# CORS Configuration
cors.allowed-origins=${FRONTEND_URL:https://schedbank.vercel.app}
cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
cors.allowed-headers=*
cors.allow-credentials=true

# Security
jwt.secret=${JWT_SECRET:your-super-secure-secret-key}
jwt.expiration=86400000

# Monitoring
management.endpoints.web.exposure.include=health,metrics,prometheus
management.endpoint.health.show-details=always
```

#### Frontend Environment Variables (.env.production)
```env
VITE_API_BASE_URL=https://schedbank-api.railway.app/api
VITE_APP_TITLE=SchedBank
VITE_APP_VERSION=1.0.0
```

---

### 4. Integration Setup

#### CORS Configuration (SecurityConfiguration.java)
```java
@CrossOrigin(
    origins = {
        "https://schedbank.vercel.app",
        "https://*.vercel.app",
        "http://localhost:5173"
    },
    allowCredentials = "true"
)
```

#### API Base URL (frontend/src/infrastructure/http/HttpClient.js)
```javascript
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';
```

---

### 5. Monitoring and Observability

#### Health Checks
- **Frontend**: `https://schedbank.vercel.app/`
- **Backend**: `https://schedbank-api.railway.app/actuator/health`
- **API Docs**: `https://schedbank-api.railway.app/swagger-ui.html`

#### Metrics Endpoints
- **Prometheus**: `/actuator/prometheus`
- **Custom Metrics**: `/actuator/metrics`
- **Performance**: Built-in latency tracking

---

### 6. Production Checklist

#### Security
- [ ] JWT secret configured
- [ ] CORS properly restricted
- [ ] HTTPS enforced
- [ ] Environment variables secured

#### Performance
- [ ] Gzip compression enabled
- [ ] CDN configured (Vercel automatic)
- [ ] Database connection pooling
- [ ] Caching headers set

#### Monitoring
- [ ] Health checks responding
- [ ] Metrics collection active
- [ ] Error tracking configured
- [ ] Logging properly formatted

---

### 7. Custom Domain Setup (Optional)

#### Vercel Custom Domain
1. Add domain in Vercel dashboard
2. Configure DNS records:
   ```
   Type: CNAME
   Name: schedbank (or @)
   Value: cname.vercel-dns.com
   ```

#### Railway Custom Domain
1. Add custom domain in Railway dashboard
2. Configure DNS records:
   ```
   Type: CNAME
   Name: api
   Value: your-service.railway.app
   ```

---

## Next Steps

1. **Deploy Frontend**: Push to GitHub → Import to Vercel
2. **Deploy Backend**: Configure Railway/Render → Deploy Spring Boot
3. **Connect Services**: Update environment variables for integration
4. **Test End-to-End**: Verify complete application functionality
5. **Monitor Performance**: Set up alerts and observability

The application will be accessible at:
- **Frontend**: `https://schedbank.vercel.app`
- **Backend API**: `https://schedbank-api.railway.app/api`
- **API Documentation**: `https://schedbank-api.railway.app/swagger-ui.html`