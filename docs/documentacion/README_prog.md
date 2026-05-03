# StockMaster - Aplicación de Gestión de Fondos de Inversión

## 📌 Qué hace la aplicación

StockMaster es una aplicación de gestión financiera que simula el funcionamiento de una **empresa de fondos de inversión**. 
Permite administrar clientes, fondos y activos financieros, así como registrar y consultar las operaciones y transacciones de inversión realizadas.

El sistema centraliza toda la información del negocio en una base de datos. 
Expone funcionalidades para consultar carteras, calcular valoraciones y gestionar posiciones de inversión.

---

## 🚀 Cómo se ejecuta

El proyecto tiene dos partes:

- La web en html con datos estáticos para presentación básica. Lo ejecuta VITE con npm run dev.
- El panel interno en java con la logica de negocio y motor de generacion de precios basado en recursividad y fibonacci.
- Se ejecuta con mvn javafx:run en bash o con el metodo de MainApp de IntelliJ.


---

## ⚙️ Funcionalidades principales

- Gestión de clientes (alta, consulta y seguimiento de inversiones)
- Gestión de fondos de inversión
- Gestión de activos financieros
- Registro de posiciones de clientes en fondos
- Cálculo del valor total de cartera (NAV)
- Consulta de datos históricos de inversión
- Registro de transacciones de clientes
- Visualización de composición de fondos

---

## 🧩 Entidades que gestiona

La aplicación trabaja con las siguientes entidades principales:

- **Cliente**: usuarios que invierten su capital.
- **Gestor**: gestiona el fondo y sus clientes.
- **Fondo de inversión**: producto financiero donde invierten los clientes.
- **Activo**: instrumentos financieros (acciones, bonos, etc.).
- **Posición de cliente en fondo**: relación entre cliente y fondo con su inversión.
- **Posición de fondo en activo**: composición de cada fondo.
- **Transacción de cliente**: movimientos de inversión (aportaciones o reembolsos).
- **Vela (Candle)**: datos históricos de precios de activos.
- **Usuario**: Permite el login al panel de java.

---

## 🗄️ Uso de la base de datos

La base de datos es el núcleo del sistema y se utiliza para:

- Almacenar clientes, fondos y activos.
- Guardar relaciones entre entidades (posiciones y transacciones).
- Registrar datos históricos de mercado (velas).
- Permitir consultas complejas para cálculo de rendimiento y análisis de cartera.

Todas las operaciones de la aplicación (consultas, cálculos y actualizaciones) se realizan directamente sobre la base de datos mediante consultas SQL.