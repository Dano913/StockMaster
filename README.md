# StockMaster S.L.
## Gestora de Fondos de Inversion - Proyecto Intermodular DAW 1

StockMaster es una SGIIC simulada que permite gestionar fondos de inversion,
clientes e inversiones a traves de un portal web HTML/CSS y una app Java+JDBC.

## Estructura del proyecto
- /web       -> Portal web corporativo (HTML5 + CSS3)
- /src       -> Aplicacion Java con arquitectura en capas
- /sql       -> Scripts de base de datos MySQL
- /docs      -> Documentacion tecnica y de empleabilidad

## Tecnologias
- Frontend:  HTML5, CSS3 (responsive)
- Backend:   Java 17, JDBC, MySQL 8.0
- Versiones: Git + GitHub
- BD:        MySQL 8.0

## Como ejecutar
1. Importar /sql/01_schema.sql y /sql/02_inserts.sql en MySQL
2. Compilar: javac -cp .:mysql-connector-j-8.x.jar src/com/stockmaster/**/*.java
3. Ejecutar: java -cp .:mysql-connector-j-8.x.jar com.stockmaster.controller.Main

## Fecha de entrega: Domingo 3 de mayo de 2026 a las 23:59
