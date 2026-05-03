# 📊 Stockmaster – Análisis de Datos
## Empresa de Gestión de Fondos de Inversión

---

## 🏢 1. Tipo de empresa

**Stockmaster** es una gestora de fondos de inversión.

Su actividad principal consiste en administrar el capital de distintos clientes, invirtiéndolo en fondos que, a su vez, están compuestos por múltiples activos financieros.

---

## 📈 2. Información que maneja el negocio

El sistema gestiona información crítica y dinámica:

- 📊 Datos de mercado: precios históricos y actuales de activos financieros (velas o candlesticks)
- 💼 Composición de carteras: distribución de activos dentro de cada fondo
- 👤 Gestión de clientes: datos personales, financieros y posiciones en fondos
- 🔄 Operativa: registro de todas las transacciones de clientes
- 🧑‍💼 Estructura organizativa: asignación de gestores a clientes y fondos

---

## 🧩 3. Entidades identificadas

- **Empresa**: entidad principal que gestiona los fondos
- **Gestor**: profesional encargado de la gestión de un fondo
- **Fondo**: vehículo de inversión de los clientes
- **Activo**: instrumento financiero (acciones, bonos, cripto, etc.)
- **Vela (Candlestick)**: representación del precio de un activo en el tiempo
- **Posición de Fondo en Activo**: composición de un fondo
- **Cliente**: inversor del sistema
- **Posición de Cliente en Fondo**: inversión de un cliente en un fondo
- **Transacción de Cliente**: movimientos de capital realizados

---

## 🔗 4. Relaciones entre entidades

- Empresa – Gestor (1:N): una empresa tiene múltiples gestores
- Gestor – Fondo (1:1): cada gestor administra un único fondo
- Gestor – Cliente (1:N): un gestor puede gestionar varios clientes
- Fondo – Posición de Fondo en Activo (1:N): un fondo tiene múltiples activos
- Activo – Posición de Fondo en Activo (1:N): un activo puede estar en varios fondos
- Activo – Vela (1:N): un activo tiene histórico de precios
- Cliente – Posición de Cliente en Fondo (1:N): un cliente puede invertir en varios fondos
- Fondo – Posición de Cliente en Fondo (1:N): un fondo tiene múltiples inversores
- Cliente – Transacción de Cliente (1:N): un cliente realiza múltiples transacciones
- Fondo – Transacción de Cliente (1:N): cada transacción pertenece a un fondo
- Posición – Transacción (1:N): las transacciones modifican posiciones

---

## 🌐 5. La web como interfaz del sistema

La web es la interfaz principal del sistema y se divide en tres áreas:

### 🏠 Página pública
Portal informativo con:

- Filosofía de inversión
- Fondos disponibles
- Equipo de gestores
- Noticias y recursos educativos

---

### 👤 Área de clientes (portal del inversor)

Zona privada donde los clientes pueden:

- Visualizar su cartera
- Realizar suscripciones y reembolsos
- Descargar informes financieros

---

### 🧑‍💼 Área de gestores (back-office)

Panel interno para gestión:

- Monitorización de fondos y activos
- Gestión de clientes
- Ajuste de posiciones en fondos

---

## 🗄️ 6. Base de datos como núcleo del sistema

La base de datos es el núcleo operativo del negocio.

### 🧩 Entidades y atributos
Incluyen Cliente, Fondo, Activo, Gestor y Empresa con información clave como ISIN, tipo de activo, etc.

---

### 🔗 Relaciones
Las tablas intermedias representan la interacción del sistema:

- PosicionClienteEnFondo
- PosicionFondoEnActivo
- TransaccionCliente

---

### 🔒 Integridad y consistencia

Se garantiza mediante:

- Claves primarias
- Claves foráneas
- Restricciones (NOT NULL, UNIQUE)

---

### ⚙️ Consultas

Las consultas permiten:

- Recuperar información
- Analizar carteras
- Calcular rendimiento
- Explorar históricos de mercado

---

## 🎯 Conclusión

Stockmaster simula una empresa real de gestión de fondos mediante:

- 🌐 Web como interfaz de usuario
- 🗄️ Base de datos como núcleo del negocio
- 📊 Modelo de datos financiero estructurado

Esto permite un sistema coherente, escalable y realista.