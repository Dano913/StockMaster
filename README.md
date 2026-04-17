# 📊 StockMaster

StockMaster es un sistema distribuido de simulación y gestión de fondos de inversión ficticios compuesto por varios módulos desacoplados:

- 🧠 Core de lógica financiera en Java
- 🖥️ Panel de gestores en JavaFX
- 🔌 API REST en Spring Boot
- 🌐 Landing web en Node.js
- 🗄️ Base de datos relacional

---

## 🏗️ Arquitectura del sistema

```
StockMaster/
│
├── src/          → Java (Core + JavaFX)
├── api/          → Spring Boot REST API
├── web/          → Node.js + frontend
├── database/     → scripts SQL
└── docs/         → documentación del sistema
```

---

## 🔄 Flujo del sistema

```
Core Java (simulación de fondos)
        ↓
Spring Boot API (exposición de datos)
        ↓
┌──────────────────────────┐
│                          │
JavaFX Panel           Web (Node.js)
(gestores)             (landing pública)
        ↓                      ↓
        └────── HTTP JSON ─────┘
```

---

## 🧠 Módulo Core (Java)

📁 Ubicación: `src/main/java/com/stockmaster/core`

### Responsabilidades

- Simulación de mercados financieros
- Gestión de fondos de inversión
- Cálculo de rendimiento
- Modelado de activos
- Reglas de negocio

### Conceptos clave

- Fondos con rendimiento dinámico
- Activos con características propias
- Motor de simulación recursiva de tendencias

---

## 🖥️ Módulo JavaFX (Panel de gestores)

📁 Ubicación: `src/main/java/com/stockmaster/javafx`

### Responsabilidades

- Interfaz interna de la empresa
- Visualización de fondos
- CRUD de entidades
- Dashboards internos

### Comunicación

- Consume la API REST mediante HTTP
- No accede directamente a la base de datos

---

## 🔌 API (Spring Boot)

📁 Ubicación: `api/`

### Responsabilidades

- Exponer la lógica del core como servicios REST
- Gestionar acceso a datos
- Transformar entidades en DTOs
- Servir datos a JavaFX y Web

### Endpoints principales

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/funds/performance` | Rendimiento de todos los fondos |
| `GET` | `/api/funds/{id}` | Detalle de un fondo concreto |
| `POST` | `/api/simulate` | Lanzar simulación |

---

## 🌐 Web (Node.js)

📁 Ubicación: `web/`

### Responsabilidades

- Servidor web ligero
- Renderizado de HTML/CSS/JS
- Consumo de la API REST
- Filtrado y visualización de datos

### Tecnologías

- Node.js
- HTML / CSS / JavaScript (Fetch API)

---

## 🗄️ Base de datos

📁 Ubicación: `database/`

### Contiene

- Schema SQL
- Datos iniciales (seed)
- Diagramas de entidad-relación

---

## 🚀 Tecnologías utilizadas

|        Capa         |       Tecnología        |
|---------------------|-------------------------|
| Core lógica         | Java 17+                |
| Interfaz escritorio | JavaFX                  |
| Build tool          | Maven                   |
| API backend         | Spring Boot             |
| Servidor web        | Node.js                 |
| Frontend            | HTML / CSS / JavaScript |
| Persistencia        | SQL                     |

---

## 🔌 Comunicación entre módulos

```
JavaFX  ─┐
         ├── HTTP → Spring Boot API → Database
Node.js ─┘
```

---

## ⚠️ Reglas de arquitectura

|                       Regla                    |     Estado      |
|------------------------------------------------|-----------------|
| JavaFX accede a la base de datos directamente  | ❌ Prohibido   |
| Node.js accede a la base de datos directamente | ❌ Prohibido   |
| La lógica de negocio vive en la API            | ❌ Prohibido   |
| Todo acceso a datos pasa por Spring Boot       | ✅ Obligatorio |
| El core contiene toda la lógica financiera     | ✅ Obligatorio |

---

## 🧠 Objetivo del sistema

Simular un entorno financiero donde:

- Los fondos evolucionan dinámicamente
- Los activos afectan el rendimiento
- Las tendencias se calculan mediante simulación recursiva
- Los datos se exponen a múltiples clientes en tiempo real
- El panel de inversion interna únicamente es gestionado por los inversores de la empresa

---

## 👨‍💻 Enfoque

Proyecto personal enfocado en:

- Arquitectura de software distribuido
- Java + sistemas backend
- Simulación financiera
- Separación de responsabilidades