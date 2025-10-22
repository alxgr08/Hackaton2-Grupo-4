# 🍪 GUÍA COMPLETA PARA PROBAR EL CHECKPOINT 1 EN POSTMAN

## ✅ Estado del Proyecto

- ✅ Compilación exitosa
- ✅ JWT Service configurado y funcionando
- ✅ Servidor corriendo en puerto **8082**
- ✅ Base de datos H2 en memoria
- ✅ Seguridad JWT implementada

---

## 🚀 PASO 1: Verificar que el Servidor Está Corriendo

Ejecuta en cmd:
```cmd
test_endpoints.bat
```

Esto probará todos los endpoints y te mostrará los tokens generados.

---

## 📮 PASO 2: Configurar Postman

### Crear una Nueva Colección

1. Abre Postman
2. Click en **"New"** → **"Collection"**
3. Nombre: `Oreo Insight Factory - Checkpoint 1`

### Configurar Variables de Entorno

1. Click en el ícono de **engranaje** (⚙️) en la esquina superior derecha
2. Click en **"Add"** para crear un nuevo Environment
3. Nombre: `Oreo Dev`
4. Agregar estas variables:

| Variable | Initial Value | Current Value |
|----------|--------------|---------------|
| baseUrl | http://localhost:8082 | http://localhost:8082 |
| token | (dejar vacío) | (dejar vacío) |
| userId | (dejar vacío) | (dejar vacío) |
| branchToken | (dejar vacío) | (dejar vacío) |

5. Click en **"Save"**
6. Selecciona el environment **"Oreo Dev"** en el dropdown superior derecho

---

## 📝 PASO 3: Crear las Peticiones en Postman

### 🔐 Request 1: Register CENTRAL

**Configuración:**
- Método: `POST`
- URL: `{{baseUrl}}/auth/register`
- Headers:
  - `Content-Type: application/json`
- Body (raw, JSON):
```json
{
  "username": "oreo.admin",
  "email": "admin@oreo.com",
  "password": "Oreo1234",
  "role": "CENTRAL"
}
```

**Tests** (pestaña Tests):
```javascript
pm.test("Status 201 Created", function () {
    pm.response.to.have.status(201);
});

pm.test("Response has id", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.id).to.exist;
    pm.environment.set("userId", jsonData.id);
});

pm.test("Response has correct role", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.role).to.eql("CENTRAL");
});
```

**Respuesta Esperada (201):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "oreo.admin",
  "email": "admin@oreo.com",
  "role": "CENTRAL",
  "branch": null,
  "createdAt": "2025-10-21T19:10:00Z"
}
```

---

### 🔐 Request 2: Register BRANCH (Miraflores)

**Configuración:**
- Método: `POST`
- URL: `{{baseUrl}}/auth/register`
- Headers:
  - `Content-Type: application/json`
- Body (raw, JSON):
```json
{
  "username": "miraflores.user",
  "email": "mira@oreo.com",
  "password": "Oreo1234",
  "role": "BRANCH",
  "branch": "Miraflores"
}
```

**Tests:**
```javascript
pm.test("Status 201 Created", function () {
    pm.response.to.have.status(201);
});

pm.test("Response has branch", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.branch).to.eql("Miraflores");
});
```

---

### 🔑 Request 3: Login CENTRAL

**Configuración:**
- Método: `POST`
- URL: `{{baseUrl}}/auth/login`
- Headers:
  - `Content-Type: application/json`
- Body (raw, JSON):
```json
{
  "username": "oreo.admin",
  "password": "Oreo1234"
}
```

**Tests (IMPORTANTE - Guarda el token):**
```javascript
pm.test("Status 200 OK", function () {
    pm.response.to.have.status(200);
});

pm.test("Response has token", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.token).to.exist;
    pm.environment.set("token", jsonData.token);
});

pm.test("Token expires in 1 hour", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.expiresIn).to.eql(3600);
});

pm.test("Role is CENTRAL", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.role).to.eql("CENTRAL");
});
```

**Respuesta Esperada (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJvcmVvLmFkbWluIiwiaWF0IjoxNjk0NDY...",
  "expiresIn": 3600,
  "role": "CENTRAL",
  "branch": null
}
```

---

### 🔑 Request 4: Login BRANCH

**Configuración:**
- Método: `POST`
- URL: `{{baseUrl}}/auth/login`
- Headers:
  - `Content-Type: application/json`
- Body (raw, JSON):
```json
{
  "username": "miraflores.user",
  "password": "Oreo1234"
}
```

**Tests:**
```javascript
pm.test("Status 200 OK", function () {
    pm.response.to.have.status(200);
});

pm.test("Response has token", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.token).to.exist;
    pm.environment.set("branchToken", jsonData.token);
});

pm.test("Role is BRANCH", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.role).to.eql("BRANCH");
    pm.expect(jsonData.branch).to.eql("Miraflores");
});
```

---

### 🔒 Request 5: Probar Endpoint Protegido (GET /users)

**Configuración:**
- Método: `GET`
- URL: `{{baseUrl}}/users`
- Headers:
  - `Authorization: Bearer {{token}}`

**Tests:**
```javascript
pm.test("Status 200 OK for CENTRAL", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is array", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.be.an('array');
});
```

**Respuesta Esperada (200):**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "username": "oreo.admin",
    "email": "admin@oreo.com",
    "role": "CENTRAL",
    "branch": null,
    "createdAt": "2025-10-21T19:10:00Z"
  },
  {
    "id": "660e8400-e29b-41d4-a716-446655440001",
    "username": "miraflores.user",
    "email": "mira@oreo.com",
    "role": "BRANCH",
    "branch": "Miraflores",
    "createdAt": "2025-10-21T19:11:00Z"
  }
]
```

---

### 🚫 Request 6: Probar Endpoint Protegido con Token BRANCH (Debe Fallar)

**Configuración:**
- Método: `GET`
- URL: `{{baseUrl}}/users`
- Headers:
  - `Authorization: Bearer {{branchToken}}`

**Tests:**
```javascript
pm.test("Status 403 Forbidden for BRANCH", function () {
    pm.response.to.have.status(403);
});
```

**Respuesta Esperada (403):**
```json
{
  "error": "FORBIDDEN",
  "message": "Access denied: insufficient permissions",
  "timestamp": "2025-10-21T19:15:00Z",
  "path": "/users"
}
```

---

## 🎯 PASO 4: Ejecutar las Pruebas

### Orden de Ejecución:

1. **Register CENTRAL** → Debe devolver 201
2. **Register BRANCH** → Debe devolver 201
3. **Login CENTRAL** → Debe devolver 200 + token (se guarda automáticamente)
4. **Login BRANCH** → Debe devolver 200 + token (se guarda automáticamente)
5. **GET /users con token CENTRAL** → Debe devolver 200 + lista de usuarios
6. **GET /users con token BRANCH** → Debe devolver 403

### Ejecutar Toda la Colección:

1. Click derecho en la colección
2. Click en **"Run collection"**
3. Verifica que todos los tests pasen (verde ✅)

---

## 🔍 Verificar el Token JWT

Para inspeccionar el contenido del token:

1. Copia el token que obtuviste en el login
2. Ve a https://jwt.io
3. Pega el token en el campo "Encoded"
4. Verifica que contenga:
   - `sub`: username del usuario
   - `iat`: timestamp de emisión
   - `exp`: timestamp de expiración (1 hora después)

**⚠️ IMPORTANTE:** Nunca subas tokens reales a servicios públicos en producción.

---

## 🐛 Troubleshooting

### Si obtienes "Connection Refused":

El servidor no está corriendo. Ejecuta:
```cmd
cd C:\Users\USER\Desktop\Hackaton2\Hackaton2-Grupo-4
java -Dserver.port=8082 -Djwt.secret=DevSecretKeyForOreo1234567890123456 -jar target\insight-factory-1.0.0.jar
```

### Si obtienes 401 Unauthorized:

El token expiró o no se envió correctamente. Haz login nuevamente para obtener un nuevo token.

### Si obtienes 403 Forbidden:

El usuario no tiene permisos para ese endpoint. Verifica que estés usando el token correcto (CENTRAL para /users).

### Si obtienes 400 Bad Request:

Revisa que el JSON del body esté bien formado y cumpla las validaciones:
- Username: 3-30 caracteres alfanuméricos
- Email: formato válido
- Password: mínimo 8 caracteres
- Role: debe ser "CENTRAL" o "BRANCH"
- Branch: obligatorio si role es "BRANCH"

---

## ✅ Checklist Final del Checkpoint 1

- [ ] Servidor corriendo en puerto 8082
- [ ] Puede registrar usuario CENTRAL (201)
- [ ] Puede registrar usuario BRANCH con sucursal (201)
- [ ] Login devuelve JWT válido (200)
- [ ] Token se puede usar para endpoints protegidos
- [ ] Usuario CENTRAL puede acceder a /users
- [ ] Usuario BRANCH NO puede acceder a /users (403)
- [ ] Tokens expiran en 1 hora (3600 segundos)

---

## 📊 Resumen del Checkpoint 1

**✅ Implementado:**
- Autenticación JWT con Spring Security
- Dos roles: CENTRAL y BRANCH
- Registro de usuarios con validaciones
- Login que devuelve JWT
- Endpoints protegidos con autorización por rol
- Manejo de errores con formato estándar
- Base de datos H2 en memoria

**🎯 Funciona correctamente:**
- POST /auth/register (público)
- POST /auth/login (público)
- GET /users (solo CENTRAL)
- Validaciones de username, email, password
- Control de acceso por rol

---

## 🚀 Siguiente Paso

Una vez que hayas verificado que todos los tests del Checkpoint 1 pasan, estarás listo para continuar con el **Checkpoint 2: Gestión de Ventas**.

**¿Todo funcionó correctamente? ¡Avísame para continuar con el siguiente checkpoint!** 🍪✨

