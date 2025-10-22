# 🍪 GUÍA RÁPIDA - CHECKPOINT 1 EN POSTMAN

## ✅ ESTADO ACTUAL
- ✅ Servidor corriendo en: **http://localhost:8081**
- ✅ Base de datos H2 en memoria inicializada
- ✅ JWT configurado y funcionando
- ✅ Consola H2: http://localhost:8081/h2-console

---

## 📮 CONFIGURACIÓN RÁPIDA EN POSTMAN (3 MINUTOS)

### 1️⃣ Crear Environment (Variables)

1. Click en **"Environments"** (izquierda)
2. Click en **"+"** para crear nuevo
3. Nombre: `Oreo Dev`
4. Agregar estas variables:

```
baseUrl = http://localhost:8081
token = (vacío - se llenará automáticamente)
```

5. **IMPORTANTE**: Selecciona el environment "Oreo Dev" en el dropdown superior derecho

---

### 2️⃣ Crear Collection con 4 Requests Principales

#### 📝 REQUEST 1: Register CENTRAL

**Método:** `POST`  
**URL:** `{{baseUrl}}/auth/register`  
**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "username": "oreo.admin",
  "email": "admin@oreo.com",
  "password": "Oreo1234",
  "role": "CENTRAL"
}
```

**Tests (opcional - pegar en pestaña Tests):**
```javascript
pm.test("Status 201", () => pm.response.to.have.status(201));
pm.test("Has user data", () => {
    const data = pm.response.json();
    pm.expect(data.id).to.exist;
    pm.expect(data.role).to.eql("CENTRAL");
});
```

**✅ Respuesta esperada:** Status **201 Created**
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

---

#### 📝 REQUEST 2: Register BRANCH

**Método:** `POST`  
**URL:** `{{baseUrl}}/auth/register`  
**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "username": "miraflores.user",
  "email": "mira@oreo.com",
  "password": "Oreo1234",
  "role": "BRANCH",
  "branch": "Miraflores"
}
```

**✅ Respuesta esperada:** Status **201 Created**
```json
{
  "id": "uuid-generado",
  "username": "miraflores.user",
  "email": "mira@oreo.com",
  "role": "BRANCH",
  "branch": "Miraflores",
  "createdAt": "2025-10-21T..."
}
```

---

#### 🔑 REQUEST 3: Login CENTRAL (⭐ MÁS IMPORTANTE)

**Método:** `POST`  
**URL:** `{{baseUrl}}/auth/login`  
**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "username": "oreo.admin",
  "password": "Oreo1234"
}
```

**Tests (⭐ IMPORTANTE - GUARDA EL TOKEN):**
```javascript
pm.test("Status 200", () => pm.response.to.have.status(200));
pm.test("Has token", () => {
    const data = pm.response.json();
    pm.expect(data.token).to.exist;
    pm.environment.set("token", data.token);
});
```

**✅ Respuesta esperada:** Status **200 OK**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJvcmVvLmFkbWluIi...",
  "expiresIn": 3600,
  "role": "CENTRAL",
  "branch": null
}
```

**🎯 El token se guardará automáticamente en la variable `{{token}}` si agregaste el script de Tests**

---

#### 🔒 REQUEST 4: Test Endpoint Protegido

**Método:** `GET`  
**URL:** `{{baseUrl}}/actuator/health`  
**Headers:**
```
Authorization: Bearer {{token}}
```

**✅ Respuesta esperada:** Status **200 OK**

---

## 🎯 FLUJO DE PRUEBA COMPLETO

### Orden de ejecución:
1. ✅ **Register CENTRAL** → Crea usuario admin
2. ✅ **Register BRANCH** → Crea usuario sucursal
3. ⭐ **Login CENTRAL** → Obtiene JWT (se guarda automáticamente)
4. ✅ **Test endpoint protegido** → Verifica que el token funciona

---

## 🔥 TIPS IMPORTANTES

### ✅ Cómo usar el token en cualquier request:

En cualquier petición protegida, ve a la pestaña **"Authorization"**:
1. Type: `Bearer Token`
2. Token: `{{token}}`

O en Headers manualmente:
```
Authorization: Bearer {{token}}
```

### ✅ Si el token expira (después de 1 hora):
- Simplemente vuelve a ejecutar el **Login** y obtendrás un nuevo token

### ✅ Para ver el contenido del token:
1. Copia el token de la respuesta del login
2. Ve a https://jwt.io
3. Pégalo en la sección "Encoded"
4. Verás el payload con: `sub` (username), `iat` (issued at), `exp` (expiration)

---

## 🐛 TROUBLESHOOTING

### ❌ Error "Connection refused"
**Problema:** El servidor no está corriendo  
**Solución:** 
```powershell
cd C:\Users\USER\Desktop\Hackaton2\Hackaton2-Grupo-4
java "-Djwt.secret=DevSecretKeyForOreo1234567890123456" -jar target\insight-factory-1.0.0.jar
```

### ❌ Error 401 Unauthorized
**Problema:** Token inválido o expirado  
**Solución:** Ejecuta el request de **Login** nuevamente

### ❌ Error 400 Bad Request
**Problema:** Datos inválidos en el body  
**Solución:** Verifica que:
- `username`: 3-30 caracteres, solo letras/números/guiones bajos
- `email`: formato válido
- `password`: mínimo 8 caracteres
- `role`: debe ser exactamente "CENTRAL" o "BRANCH"
- `branch`: obligatorio solo si `role` es "BRANCH"

### ❌ Error 409 Conflict
**Problema:** Usuario ya existe  
**Solución:** Usa otro username o email

---

## ✅ VALIDACIÓN FINAL DEL CHECKPOINT 1

Marca lo que funciona:

- [ ] **POST /auth/register** con role CENTRAL → Status 201
- [ ] **POST /auth/register** con role BRANCH → Status 201
- [ ] **POST /auth/login** → Status 200 + token JWT
- [ ] Token se guarda en variable de environment automáticamente
- [ ] Puedo usar el token en headers: `Authorization: Bearer {{token}}`
- [ ] El token tiene expiración de 3600 segundos (1 hora)

---

## 🎉 CUANDO TODO FUNCIONE

**¡Checkpoint 1 completado!** 

El servidor seguirá corriendo hasta que cierres la terminal o presiones CTRL+C.

---

## 🚀 LISTO PARA EL CHECKPOINT 2

Una vez que confirmes que todos los endpoints del Checkpoint 1 funcionan correctamente en Postman, avísame para implementar el **Checkpoint 2: Gestión de Ventas** (CRUD de ventas con permisos por sucursal).

---

## 📝 NOTAS ADICIONALES

### Endpoints disponibles actualmente:
- `POST /auth/register` - Registro de usuarios (público)
- `POST /auth/login` - Login y generación de JWT (público)
- `GET /h2-console` - Consola de base de datos H2 (público, solo desarrollo)

### Próximos endpoints (Checkpoint 2):
- `POST /sales` - Crear venta
- `GET /sales` - Listar ventas
- `GET /sales/{id}` - Ver detalle de venta
- `PUT /sales/{id}` - Actualizar venta
- `DELETE /sales/{id}` - Eliminar venta

---

**🍪 ¡Prueba todo en Postman y avísame cuando estés listo para continuar!**

