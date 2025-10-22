# 🚀 Guía de Ejecución y Testing - Oreo Insight Factory

## 📋 Comandos para Ejecutar el Proyecto

### 1. Compilar el Proyecto
```cmd
cd C:\Users\USER\Desktop\Hackaton2\Hackaton2-Grupo-4
mvn clean install -DskipTests
```

### 2. Ejecutar la Aplicación
```cmd
mvn spring-boot:run
```

**O alternativamente:**
```cmd
java -jar target\insight-factory-1.0.0.jar
```

### 3. Verificar que está corriendo
La aplicación estará disponible en: **http://localhost:8080**

Verás un mensaje similar a:
```
Started OreoInsightFactoryApplication in X.XXX seconds
```

---

## 🧪 Testing con cURL (Windows CMD)

### Test 1: Registrar Usuario CENTRAL
```cmd
curl -X POST http://localhost:8080/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"oreo.admin\",\"email\":\"admin@oreo.com\",\"password\":\"Oreo1234\",\"role\":\"CENTRAL\"}"
```

**Respuesta esperada (201):**
```json
{
  "id": "uuid-generado",
  "username": "oreo.admin",
  "email": "admin@oreo.com",
  "role": "CENTRAL",
  "branch": null,
  "createdAt": "2025-10-21T..."
}
```

### Test 2: Login Usuario CENTRAL
```cmd
curl -X POST http://localhost:8080/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"oreo.admin\",\"password\":\"Oreo1234\"}"
```

**Respuesta esperada (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600,
  "role": "CENTRAL",
  "branch": null
}
```

### Test 3: Registrar Usuario BRANCH
```cmd
curl -X POST http://localhost:8080/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"miraflores.user\",\"email\":\"mira@oreo.com\",\"password\":\"Oreo1234\",\"role\":\"BRANCH\",\"branch\":\"Miraflores\"}"
```

### Test 4: Login Usuario BRANCH
```cmd
curl -X POST http://localhost:8080/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"miraflores.user\",\"password\":\"Oreo1234\"}"
```

### Test 5: Verificar Autenticación (guardar token del login anterior)
```cmd
curl -X GET http://localhost:8080/api/test ^
  -H "Authorization: Bearer TU_TOKEN_AQUI"
```

---

## 🎯 Testing con Postman (RECOMENDADO)

### Paso 1: Importar Colección
1. Abrir Postman
2. Click en **Import**
3. Seleccionar archivo: `Oreo_Insight_Factory.postman_collection.json`
4. Click en **Import**

### Paso 2: Configurar Environment
1. Click en **Environments** (icono de ojo)
2. Click en **Add**
3. Nombre: `Oreo Local`
4. Agregar variable:
   - Variable: `baseUrl`
   - Initial Value: `http://localhost:8080`
   - Current Value: `http://localhost:8080`
5. Guardar y seleccionar el environment

### Paso 3: Ejecutar Tests
1. Abrir la colección **Oreo Insight Factory API**
2. Expandir carpeta **Authentication**
3. Ejecutar en orden:
   - ✅ **Register CENTRAL User**
   - ✅ **Login CENTRAL User**
   - ✅ **Register BRANCH User**
   - ✅ **Login BRANCH User**

### Paso 4: Ejecutar Toda la Colección
- Click derecho en la colección → **Run collection**
- Click en **Run Oreo Insight Factory API**
- Verificar que todos los tests pasen ✅

---

## 🗄️ Acceder a la Base de Datos H2 Console

### Paso 1: Abrir H2 Console
URL: **http://localhost:8080/h2-console**

### Paso 2: Configurar Conexión
- **JDBC URL**: `jdbc:h2:mem:oreo_db`
- **User Name**: `sa`
- **Password**: *(dejar vacío)*
- Click en **Connect**

### Paso 3: Consultar Datos
```sql
-- Ver todos los usuarios
SELECT * FROM USERS;

-- Ver usuarios CENTRAL
SELECT * FROM USERS WHERE ROLE = 'CENTRAL';

-- Ver usuarios BRANCH
SELECT * FROM USERS WHERE ROLE = 'BRANCH';

-- Contar usuarios por role
SELECT ROLE, COUNT(*) FROM USERS GROUP BY ROLE;
```

---

## 🔍 Verificación Completa del Sistema

### Script de Verificación Automática (CMD)

Crea un archivo `test-api.cmd` con este contenido:

```cmd
@echo off
echo ========================================
echo Testing Oreo Insight Factory API
echo ========================================
echo.

echo [1/4] Registrando Usuario CENTRAL...
curl -X POST http://localhost:8080/auth/register -H "Content-Type: application/json" -d "{\"username\":\"oreo.admin\",\"email\":\"admin@oreo.com\",\"password\":\"Oreo1234\",\"role\":\"CENTRAL\"}"
echo.
echo.

echo [2/4] Login Usuario CENTRAL...
curl -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" -d "{\"username\":\"oreo.admin\",\"password\":\"Oreo1234\"}"
echo.
echo.

echo [3/4] Registrando Usuario BRANCH...
curl -X POST http://localhost:8080/auth/register -H "Content-Type: application/json" -d "{\"username\":\"miraflores.user\",\"email\":\"mira@oreo.com\",\"password\":\"Oreo1234\",\"role\":\"BRANCH\",\"branch\":\"Miraflores\"}"
echo.
echo.

echo [4/4] Login Usuario BRANCH...
curl -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" -d "{\"username\":\"miraflores.user\",\"password\":\"Oreo1234\"}"
echo.
echo.

echo ========================================
echo Testing completado!
echo ========================================
pause
```

Luego ejecutar:
```cmd
test-api.cmd
```

---

## ✅ Checklist de Verificación

- [ ] Compilación exitosa sin errores
- [ ] Aplicación inicia en el puerto 8080
- [ ] H2 Console accesible
- [ ] Registro de usuario CENTRAL retorna 201
- [ ] Login de usuario CENTRAL retorna 200 con token
- [ ] Registro de usuario BRANCH con branch retorna 201
- [ ] Login de usuario BRANCH retorna 200 con token
- [ ] Registro con username duplicado retorna 409
- [ ] Login con credenciales inválidas retorna 401
- [ ] Endpoints protegidos requieren token JWT

---

## 🐛 Solución de Problemas

### Error: "Port 8080 already in use"
```cmd
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### Error: "JAVA_HOME not set"
```cmd
set JAVA_HOME=C:\Path\To\Java21
set PATH=%JAVA_HOME%\bin;%PATH%
```

### Error: "mvn command not found"
```cmd
set M2_HOME=C:\Path\To\Maven
set PATH=%M2_HOME%\bin;%PATH%
```

### Ver logs en tiempo real
Los logs aparecen en la consola donde ejecutaste `mvn spring-boot:run`

---

## 📊 Resultados Esperados

### Respuesta Exitosa (Status 2xx)
```json
{
  "id": "...",
  "username": "...",
  ...
}
```

### Respuesta de Error (Status 4xx/5xx)
```json
{
  "error": "BAD_REQUEST",
  "message": "Validation failed: ...",
  "timestamp": "2025-10-21T...",
  "path": "/auth/register"
}
```

---

## 🎓 Próximos Pasos

Una vez verificado el Checkpoint 1:
1. Confirmar que todos los tests pasan ✅
2. Revisar la documentación en `IMPLEMENTATION.md`
3. Solicitar avanzar al Checkpoint 2: Gestión de Ventas

---

*Última actualización: 2025-10-21*

