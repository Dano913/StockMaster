# Itinerario Personal para la Empleabilidad I
**Módulo 1709 · Proyecto Intermodular · Curso 2025–2026**

---

## 1. Perfil Profesional Personal

Soy estudiante de primer curso de Desarrollo de Aplicaciones Multiplataforma (DAM). Durante este curso he trabajado en **StockMaster**, una plataforma para la gestión de fondos de inversión con arquitectura multicapa: web con html, base de datos MySQL y panel de escritorio en JavaFX.

Me interesa el **manejo de datos y la conexion APIs**. Lo que más me ha enganchado del proyecto ha sido entender cómo funciona la arquitectura por capas —controllers, services, repositorios, entidades— y ver cómo cada pieza encaja con las demás. Hay algo satisfactorio en construir un sistema que responde bien, que es coherente por dentro y que escala sin romperse.

Las tecnologías que estoy aprendiendo este curso son Java, JavaFX, JavaScript, Python, JDBC, MySQL y Git. También he tenido contacto con Tailwind CSS y Node.js en la parte de landing page.

Me gustaría especializarme en **Data Analysis**, y con el tiempo profundizar en sistemas de trading o análisis de bolsa. Me motiva entender el fondo de las cosas: no solo que algo funcione, sino por qué funciona.

---

## 2. Perfil Profesional en GitHub

> 📍 El perfil de GitHub está disponible en: `https://github.com/Dano913/StockMaster`


### Organización del repositorio

El repositorio del proyecto sigue la siguiente estructura de nombrado y documentación:

- **Nombre del repo:** - StockMaster
- **Descripción:** *Plataforma de gestión de fondos de inversión · Java + JavaFX + MySQL*

### Buenas prácticas aplicadas

Los commits del proyecto siguen una convención propia con prefijos `+` (inicio de tarea) y `-` (cierre de tarea), lo que permite reconstruir el historial de trabajo de forma legible. Se han realizado más de 50 commits documentados a lo largo del desarrollo.

---

## 3. Presentación Profesional del Proyecto

### StockMaster — Plataforma de gestión de fondos de inversión

**¿Qué es?**
StockMaster es una aplicación multicapa para la gestión interna de fondos de inversión. Cuenta con un backend desarrollado en Java, una base de datos relacional en MySQL, un panel de escritorio para gestores desarrollado en JavaFX y una landing page pública con HTML Tailwind y JavaScript.

**¿Qué problema resuelve?**
Las empresas de gestión de fondos necesitan centralizar su información financiera y separar lo que ven los clientes de lo que gestionan internamente. StockMaster resuelve esa separación con una arquitectura clara: JBDC actúa como único punto de acceso a los datos, el panel interno da herramientas a los gestores y clientes con posiciones y la landing page ofrece una vista pública para futuros clientes.

**¿Para quién está pensado?**
- **Clientes externos:** acceden al panel JavaFX para consultar información de fondos y activos
- **Gestores internos:** utilizan el panel JavaFX para administrar el sistema
- **Administradores:** tienen acceso completo a todo el personal, clientes y base de datos

**Tecnologías utilizadas**

| Capa                | Tecnología |
|---------------------|---|
| Programacion        | Java 17, Maven |
| Base de datos       | MySQL, JDBC, DAO pattern |
| Panel de escritorio | JavaFX |
| Landing page        | HTML, CSS (Tailwind), JavaScript |
| Infraestructura     | Podman, DBeaver, Git |

**¿Qué sé hacer gracias a este proyecto?**
- Diseñar e implementar una arquitectura por capas (controller → service → repository → model)
- Conectar una aplicación Java a una base de datos mediante JDBC y el patrón DAO
- Construir interfaces de escritorio con JavaFX y FXML
- Gestionar el ciclo de vida de un proyecto con Git, incluyendo ramas y merges
- Desplegar servicios en contenedores con Podman

---

## 4. Portfolio Básico

### Capturas del sistema

El sistema incluye los siguientes módulos visuales desarrollados y funcionales:

- **Landing page** — diseño con Tailwind CSS, información pública de fondos y activos
- **Panel de login** — autenticación con múltiples roles (cliente, gestor, administrador)
- **Vista de clientes** — listado y detalle de clientes del fondo
- **Vista de gestores** — panel de administración para usuarios internos
- **Vista de activos y fondos** — motor de precios dinámico con gráficos en tiempo real
- **Global view** — vista general del estado del sistema

### Lo que he aprendido

Este proyecto me ha enseñado que construir software real es muy diferente a hacer ejercicios. Algunos aprendizajes concretos:

- La arquitectura importa desde el principio. Cuando no está clara, refactorizar cuesta el doble.
- JDBC y el patrón DAO tienen mucha más profundidad de lo que parece en teoría.
- Git no es solo guardar código: es documentar decisiones y proteger el trabajo.
- Un sistema que "funciona" no siempre está bien hecho. La diferencia está en cómo está organizado por dentro.

---

## 5. Reflexión Personal

### ¿Qué he aprendido?

He aprendido a construir una aplicación real de principio a fin. No un ejercicio aislado, sino un sistema con varias capas que se comunican entre sí. Entender cómo fluye una petición desde la interfaz hasta la base de datos y vuelve ha sido el aprendizaje más valioso del curso.

También he aprendido a trabajar con Git de forma seria: hacer commits con sentido, nombrarlos bien, usar ramas. El historial del proyecto refleja el proceso real de desarrollo, con sus avances y sus correcciones.

### ¿Qué se me ha dado mejor?

El diseño de la arquitectura backend y la implementación de la capa de acceso a datos. Una vez que entendí el patrón DAO y cómo separar responsabilidades entre capas, el código empezó a tener más sentido. También me resultó natural el trabajo con Spring Boot una vez superada la curva inicial.

### ¿Qué me ha costado más?

Refactorizar. Es fácil hacer que algo funcione; es difícil hacerlo bien. En varios momentos del proyecto tuve que volver atrás, reorganizar clases, limpiar métodos que hacían demasiadas cosas y separar lógica que estaba mezclada. No es un proceso visible desde fuera, pero es donde más he aprendido sobre cómo escribir código mantenible.

### ¿Qué mejoraría?

Empezaría con una arquitectura más definida antes de escribir la primera línea de código. Muchos de los refactors que hice a mitad del proyecto se habrían evitado con un mejor diseño inicial. También dedicaría más tiempo a la documentación técnica en paralelo al desarrollo, en lugar de dejarlo para el final.

---