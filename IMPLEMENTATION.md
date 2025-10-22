# 🍪 Oreo Insight Factory - Documentación de Implementación

## 📋 Información del Equipo
*[Agregar nombres completos y códigos UTEC de los integrantes]*

---

## 🚀 Instrucciones de Ejecución

### Prerrequisitos
- Java 21+
- Maven 3.6+
- Git

### Pasos para Ejecutar

1. **Clonar el repositorio**
```bash
git clone <repository-url>
cd Hackaton2-Grupo-4
```

2. **Configurar variables de entorno**
Copiar `.env.example` y configurar tus valores:
```bash
copy .env.example .env
```

Variables requeridas:
- `JWT_SECRET`: Clave secreta para JWT (mínimo 256 bits)
- `GITHUB_TOKEN`: Token de acceso a GitHub Models
- `MAIL_USERNAME`: Email para envío de reportes
- `MAIL_PASSWORD`: App password del email

3. **Compilar el proyecto**
```bash
mvn clean install
```

4. **Ejecutar la aplicación**
```bash
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

5. **Acceder a H2 Console (opcional)**
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:oreo_db`
- User: `sa`
- Password: *(dejar vacío)*

---

## 📌 Checkpoints Implementados

### ✅ Checkpoint 1: Autenticación JWT

**Estado**: ✅ COMPLETADO

#### Componentes Implementados

1. **Modelo de Datos**
   - `User.java`: Entidad JPA con UserDetails
   - `Role.java`: Enum con roles CENTRAL y BRANCH
   - `UserRepository.java`: Repositorio JPA

2. **Seguridad JWT**
   - `JwtService.java`: Generación y validación de tokens JWT
   - `JwtAuthenticationFilter.java`: Filtro de autenticación
   - `SecurityConfig.java`: Configuración de Spring Security
   - `UserDetailsServiceImpl.java`: Servicio de carga de usuarios

3. **DTOs**
   - `RegisterRequest.java`: DTO para registro con validaciones
   - `RegisterResponse.java`: DTO de respuesta de registro
   - `LoginRequest.java`: DTO para login
   - `LoginResponse.java`: DTO de respuesta con token

4. **Servicios y Controladores**
   - `AuthService.java`: Lógica de negocio de autenticación
   - `AuthController.java`: Endpoints REST

5. **Manejo de Errores**
   - `GlobalExceptionHandler.java`: Manejo centralizado de excepciones
   - `ErrorResponse.java`: DTO estándar de errores
   - `ConflictException.java`: Excepción 409
   - `ValidationException.java`: Excepción 400

#### Endpoints Disponibles

##### 1. POST `/auth/register` - Registrar Usuario

**Request Body**:
```json
{
  "username": "oreo.admin",
  "email": "admin@oreo.com",
  "password": "Oreo1234",
  "role": "CENTRAL"
}
```

Para usuario BRANCH:
```json
{
  "username": "miraflores.user",
  "email": "mira@oreo.com",
  "password": "Oreo1234",
  "role": "BRANCH",
  "branch": "Miraflores"
}
```

**Response** (201 Created):
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "oreo.admin",
  "email": "admin@oreo.com",
  "role": "CENTRAL",
  "branch": null,
  "createdAt": "2025-10-21T10:30:00"
}
```

**Validaciones**:
- Username: 3-30 caracteres, alfanumérico + `_` y `.`
- Email: formato válido
- Password: mínimo 8 caracteres
- Role: debe ser "CENTRAL" o "BRANCH"
- Branch: obligatorio para BRANCH, null para CENTRAL

**Errores**:
- `400 BAD_REQUEST`: Validación fallida
- `409 CONFLICT`: Username o email ya existe

##### 2. POST `/auth/login` - Iniciar Sesión

**Request Body**:
```json
{
  "username": "oreo.admin",
  "password": "Oreo1234"
}
```

**Response** (200 OK):
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600,
  "role": "CENTRAL",
  "branch": null
}
```

**Errores**:
- `401 UNAUTHORIZED`: Credenciales inválidas

#### Flujo de Autenticación

1. **Registro**:
   - Valida datos de entrada
   - Verifica que username/email no existan
   - Encripta password con BCrypt
   - Crea usuario en base de datos
   - Retorna datos del usuario

2. **Login**:
   - Valida credenciales con AuthenticationManager
   - Genera token JWT firmado con clave secreta
   - Token válido por 3600 segundos (1 hora)
   - Retorna token y datos del usuario

3. **Autenticación de Requests**:
   - Cliente envía token en header: `Authorization: Bearer <token>`
   - `JwtAuthenticationFilter` intercepta la petición
   - Valida y decodifica el token
   - Carga el usuario y sus autoridades
   - Permite acceso a endpoints protegidos

#### Seguridad Implementada

- ✅ Passwords encriptados con BCrypt
- ✅ Tokens JWT firmados con HS256
- ✅ Validación de expiración de tokens
- ✅ Roles implementados como GrantedAuthority
- ✅ Endpoints públicos: `/auth/**`, `/h2-console/**`, `/error`
- ✅ Resto de endpoints requieren autenticación

#### Archivos Afectados

**Configuración**:
- `pom.xml` - Dependencias del proyecto
- `application.properties` - Configuración de Spring
- `.gitignore` - Archivos ignorados
- `.env.example` - Template de variables de entorno

**Código Principal** (21 archivos):
- `OreoInsightFactoryApplication.java`
- Model: `User.java`, `Role.java`
- Repository: `UserRepository.java`
- Security: `JwtService.java`, `JwtAuthenticationFilter.java`, `SecurityConfig.java`, `UserDetailsServiceImpl.java`
- DTOs: `RegisterRequest.java`, `RegisterResponse.java`, `LoginRequest.java`, `LoginResponse.java`
- Service: `AuthService.java`
- Controller: `AuthController.java`
- Exceptions: `GlobalExceptionHandler.java`, `ErrorResponse.java`, `ConflictException.java`, `ValidationException.java`

**Testing**:
- `Oreo_Insight_Factory.postman_collection.json` - Colección de Postman con tests

---

## 🧪 Pruebas con Postman

### Importar Colección

1. Abrir Postman
2. Importar el archivo `Oreo_Insight_Factory.postman_collection.json`
3. Configurar variable de entorno `baseUrl` = `http://localhost:8080`

### Tests Incluidos en Checkpoint 1

✅ **Register CENTRAL User**
- Verifica status 201
- Valida estructura de respuesta
- Guarda userId en variable de entorno

✅ **Login CENTRAL User**
- Verifica status 200
- Valida presencia de token
- Guarda token en variable `centralToken`

✅ **Register BRANCH User**
- Verifica status 201
- Valida role y branch
- Guarda userId

✅ **Login BRANCH User**
- Verifica status 200
- Valida token y branch
- Guarda token en variable `branchToken`

---

## 🔍 Verificación del Checkpoint 1

Para verificar que todo funciona correctamente:

1. **Ejecutar la aplicación**
2. **Importar colección de Postman**
3. **Ejecutar carpeta "Authentication"** en Postman
4. **Verificar que todos los tests pasen** ✅

Todos los endpoints deben responder correctamente con los códigos HTTP esperados.

---

## 📝 Próximos Checkpoints

- [ ] Checkpoint 2: Gestión de Ventas
- [ ] Checkpoint 3: Resumen Semanal Asíncrono con LLM y Email
- [ ] Checkpoint 4: Gestión de Usuarios
- [ ] Checkpoint 5: Testing Unitario
- [ ] Checkpoint 6: (Opcional) Reto Extra - Email Premium

---

## 🛠️ Tecnologías Utilizadas

- **Java 21**
- **Spring Boot 3.2.0**
- **Spring Security** con JWT
- **Spring Data JPA**
- **H2 Database** (en memoria)
- **JWT (jjwt 0.12.3)**
- **Lombok**
- **Maven**

---

*Generado automáticamente - Checkpoint 1 completado*

