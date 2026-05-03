# Informe Técnico
## Entorno de Ejecución de la Aplicación

La empresa **StockMaster**, para la que se desarrolla el sistema, se dedica a la gestión y análisis de fondos de inversión, ofreciendo servicios tanto a clientes externos como a equipos internos de gestión. En este contexto, se requiere una herramienta que permita centralizar la información financiera, facilitar la toma de decisiones y ofrecer una interfaz accesible para los distintos perfiles de usuario dentro de la organización.

La aplicación desarrollada consiste en una plataforma para la gestión de un fondo de inversión, compuesta por una interfaz pública para clientes y una herramienta interna para gestores contratados por la empresa.

---

## 1. Tipo de sistema donde se ejecuta

- **Panel interno para gestores:** Aplicación de escritorio utilizada por los gestores para la administración del sistema y para clientes, desplegada en los equipos de la empresa.
- **Landing page:** Publicada en un servidor web alojado en una máquina virtual en la nube, lo que permite su acceso público a través de internet.
- **Base de datos:** Sistema de almacenamiento central del sistema, alojado en un servidor local de la empresa.

---

## 2. Requisitos de Hardware

Para asegurar un rendimiento adecuado de la aplicación StockMaster, se establecen los siguientes requisitos de hardware, diferenciando entre configuraciones mínimas y recomendadas.

### Requisitos Mínimos

| Componente | Procesador | RAM | Otros |
|---|---|---|---|
| Landing Page | Dual-core (Intel i3 o equivalente) | 4 GB | Navegador moderno · Conexión a internet estable |
| Panel de gestores | Intel i3 / Ryzen 3 | 4–8 GB | Resolución mínima 1366×768 · Java Runtime instalado |
| Servidor (BD) | 2–4 cores | 8 GB | Almacenamiento: 50–100 GB SSD |

### Requisitos Recomendados

| Componente        | Procesador | RAM | Otros |
|-------------------|---|---|---|
| Landing Page      | Intel i5 / Ryzen 5 o superior | 8 GB o más | Navegador actualizado · Fibra óptica (mín. 100 Mbps) |
| Panel de gestores | Intel i5 / Ryzen 5 | 8–16 GB | SSD · Monitor, teclado, ratón |
| Servidor (BD)     | 4–8 cores | 16 GB o más | SSD rápido (NVMe recomendado) · Red estable con baja latencia |

---

## 3. Sistema operativo recomendado

### Landing page

- **Sistema operativo:** Cualquiera (Windows, Linux, macOS)
- **Versión recomendada:** Windows 10/11 o distribuciones Linux modernas (Ubuntu 22.04+)
- **Justificación:** Al tratarse de una aplicación web, la landing page se ejecuta en el navegador del usuario, por lo que no depende del sistema operativo. Su funcionamiento está garantizado en cualquier sistema que disponga de un navegador moderno y conexión a internet.

### Base de datos

- **Sistema operativo:** Linux Server
- **Versión recomendada:** Ubuntu Server 22.04 LTS o superior
- **Justificación:** La lógica de negocio del sistema desarrollada en Java, junto con la base de datos, se despliegan habitualmente en sistemas operativos Linux debido a su alta estabilidad, seguridad y eficiencia en entornos de servidor.

### Panel de gestores

- **Sistema operativo:** Windows
- **Versión recomendada:** Windows 10 o Windows 11
- **Justificación:** El panel de gestión está orientado a usuarios internos de la empresa, por lo que se recomienda su ejecución en sistemas Windows debido a su amplia compatibilidad, facilidad de uso y soporte nativo para aplicaciones de escritorio basadas en Java.

---

## 4. Instalación del Entorno

La instalación del entorno de desarrollo y ejecución de StockMaster sigue un orden específico para asegurar la correcta configuración de todas las dependencias.

### Componentes por entorno

| Entorno | Componentes |
|---|---|
| Backend | Java Development Kit (JDK) 17+, Spring Boot (Maven), MySQL |
| Frontend | Node.js, Navegador web moderno |
| Panel de gestores | JavaFX, JDK 17+ |
| Base de datos | Podman (contenedor), DBeaver (cliente de administración) |

### Orden de instalación

1. **Java Development Kit (JDK) 17+** — Permite compilar y ejecutar el backend y la aplicación JavaFX.
2. **Git** — Control de versiones del código fuente del proyecto.
3. **IntelliJ IDEA** — Entorno de desarrollo para programar el sistema.
4. **Apache Maven** — Gestión de dependencias y construcción del backend.
5. **Podman** — Ejecución de servicios en contenedores (base de datos y entorno aislado).
6. **MySQL** — Sistema de base de datos para almacenar la información del sistema.
7. **DBeaver** — Herramienta para administrar y visualizar la base de datos.
9. **Node.js** — Ejecución del frontend web o capa BFF.
10. **JavaFX** — Desarrollo del panel de gestores como aplicación de escritorio.

---

## 5. Usuarios, Permisos y Estructura

El sistema StockMaster define roles de usuario con permisos específicos y una estructura de datos organizada para facilitar su gestión.

### Tipos de usuarios

| Rol | Acceso                      | Capacidades                                                                            |
|---|-----------------------------|----------------------------------------------------------------------------------------|
| **Cliente** (externo) | Landing page y Panel JavaFX | Consulta información de fondos y activos. Con acceso a sus operaciones                 |
| **Gestor** (interno) | Panel JavaFX                | Consulta, crea y modifica fondos, activos y operaciones. Acceso restringido según rol. |
| **Administrador** | Backend completo            | Gestión de usuarios, permisos y configuración. Control total sobre la base de datos.   |

### Permisos

| Rol | Tipo de acceso                           | Alcance |
|---|------------------------------------------|---|
| Cliente | Solo lectura y gestion de sus posiciones | Datos públicos filtrados |
| Gestor | Lectura y escritura controlada           | Fondos, activos y panel JavaFX |
| Administrador | Acceso total                             | Usuarios, sistema y datos críticos |

### Estructura de carpetas del proyecto

**Backend (Spring Boot)**
```
/controller   → endpoints API
/service      → lógica de negocio
/dao          → acceso a datos
/model        → entidades
/config       → configuración del sistema
```

**Frontend (landing)**
```
/pages        → páginas web
/css          → estilos
/js           → lógica frontend
/assets       → imágenes y recursos
```

**Panel JavaFX**
```
/controllers  → lógica de interfaz
/views        → pantallas FXML
/models       → modelos de datos
```

**Base de datos**
- Tablas gestionadas dentro del sistema MySQL
- Estructura lógica organizada en esquemas y tablas (no en carpetas)

### Almacenamiento y copias de seguridad

- Base de datos relacional en **MySQL**, almacenada en un servidor local o contenedor (Podman)
- Copias de seguridad almacenadas en el directorio de backups del servidor o en almacenamiento externo (disco o nube)

---

## 6. Mantenimiento Básico

El mantenimiento de StockMaster es crucial para asegurar su estabilidad y rendimiento a largo plazo.

### Qué debe actualizarse

- **Java Development Kit (JDK)** — versiones LTS para compatibilidad
- **MySQL** — mejoras de rendimiento y seguridad
- **Dependencias del frontend** (Node.js si aplica)
- **Panel JavaFX** — librerías y compatibilidad

### Frecuencia de actualización

| Componente | Frecuencia |
|---|---|
| Dependencias del backend | Cada 1–3 meses o ante parches críticos |
| Base de datos | Revisiones mensuales de rendimiento y seguridad |
| Sistema operativo del servidor | Mensual / trimestral |
| Aplicaciones cliente (JavaFX y landing) | Al añadir nuevas funcionalidades o mejoras |

### Qué revisar periódicamente

- Estado del servidor (CPU, RAM, carga)
- Conexión entre API y base de datos
- Logs de errores del backend
- Tiempo de respuesta de la API
- Correcto funcionamiento de endpoints
- Integridad de los datos almacenados
- Funcionamiento del panel JavaFX y la landing page

### Protocolo ante fallos

1. Comprobar conexión con la base de datos MySQL
2. Reiniciar servicios del servidor (contenedores Podman)
3. Verificar configuración de red (puertos y acceso)
4. Restaurar copia de seguridad en caso de corrupción de datos
5. Actualizar dependencias si el fallo proviene de incompatibilidades

---

## 7. Evidencias

### Ejecución del sistema

El sistema ha sido desplegado en el entorno de desarrollo y se ha verificado su correcta ejecución en sus distintos componentes:

- La base de datos MySQL se conecta sin errores al JBDC
- La landing page se ejecuta en un servidor web en la máquina virtual
- El panel de gestores desarrollado en JavaFX se inicia correctamente en entorno de escritorio

### Arranque del sistema

Se ha comprobado que:

- La base de datos está operativa
- La landing page carga correctamente desde el navegador
- El panel JavaFX se ejecuta

### Funcionamiento general

Durante las pruebas realizadas:

- Se obtiene información filtrada de fondos y activos
- La comunicación entre frontend, backend y base de datos es funcional
- El sistema mantiene coherencia en la lectura y escritura de datos
- No se detectan errores críticos en los flujos principales del sistema

### Validación del sistema

El sistema completo ha sido verificado como funcional dentro del entorno configurado, confirmando la correcta integración entre los distintos módulos: **frontend**, **backend** y **base de datos**.