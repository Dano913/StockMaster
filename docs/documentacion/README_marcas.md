# 📊 StockMaster

StockMaster es un sistema distribuido de simulación y gestión de fondos de inversión ficticios compuesto por varios módulos desacoplados:

- 🧠 Core de lógica financiera en Java
- 🖥️ Panel de gestores en JavaFX
- 🌐 Landing web en Node.js
- 🗄️ Base de datos relacional

---

## 🏗️ Arquitectura del sistema

```
StockMaster/
│
├── src/          → Java (Core + JavaFX)
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

📁 Ubicación: `src/main/java/org/example/paneljavafx`

### Responsabilidades

- Simulación de mercados financieros
- Gestión de fondos de inversión
- Cálculo de rendimiento
- Modelado de activos
- Reglas de negocio

### Conceptos clave

- Fondos con rendimiento dinámico
- Activos con características propias
- Motor de simulación recursiva de tendencias basado en Fibonacci

---

## 🖥️ Módulo JavaFX (Panel de gestores)

📁 Ubicación: `src/main/java/org/example/paneljavafx`

### Responsabilidades

- Interfaz interna de la empresa
- Visualización de fondos
- CRUD de entidades
- Dashboards internos

### Comunicación

- El codigo java pide los datosa la BBDD y muestra la informacion en las views
---

## 🌐 Web (Node.js)

📁 Ubicación: `web/`

### Responsabilidades

- Servidor web ligero
- Renderizado de HTML/CSS/JS
- Visualización de informacion

### Tecnologías

- Node.js
- HTML / Tailwind / JavaScript 

---

## 🗄️ Base de datos

📁 Ubicación: `database/`

### Contiene

- Schema SQL
- Datos iniciales (seed)
- Diagramas de entidad-relación

---

## 🚀 Tecnologías utilizadas

|        Capa         | Tecnología                   |
|---------------------|------------------------------|
| Core lógica         | Java 17+                     |
| Interfaz escritorio | JavaFX                       |
| Build tool          | Maven                        |
| Servidor web        | Node.js                      |
| Frontend            | HTML / Tailwind / JavaScript |
| Persistencia        | MySQL                        |

---

## ⚠️ Reglas de arquitectura

|                       Regla                    |
|------------------------------------------------|
| JavaFX accede a la base de datos directamente  |
| El core contiene toda la lógica financiera     |

---

## 🧠 Objetivo del sistema

Simular un entorno financiero donde:

- Los fondos evolucionan dinámicamente
- Los activos afectan el rendimiento
- Las tendencias se calculan mediante simulación recursiva
- Los datos se exponen a múltiples clientes en tiempo real
- El panel de inversion es gestionado por clientes y gestores de la empresa

---

## 👨‍💻 Enfoque

Proyecto personal enfocado en:

- Arquitectura de software distribuido
- Java 17 + Maven
- Simulación financiera
- Separación de responsabilidades