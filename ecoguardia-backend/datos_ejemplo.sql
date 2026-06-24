-- Datos de ejemplo para EcoGuardia
-- Ejecutar DESPUES de que la aplicacion haya arrancado al menos una vez
-- (Hibernate crea las tablas en el primer arranque).

-- Aulas
INSERT INTO ubicaciones (nombre, descripcion) VALUES
  ('Aula 101', 'Pabellon A, piso 1'),
  ('Aula 102', 'Pabellon A, piso 1'),
  ('Laboratorio A', 'Pabellon C, planta baja'),
  ('Biblioteca', 'Edificio central');

-- Dispositivos ESP32 (asignados a las aulas de arriba)
INSERT INTO dispositivos (nombre, codigo_esp32, ubicacion_id, conectado) VALUES
  ('Sensor ESP32-01', 'ESP32-A1B2C3', 1, true),
  ('Sensor ESP32-02', 'ESP32-D4E5F6', 2, true),
  ('Sensor ESP32-03', 'ESP32-G7H8I9', 3, true),
  ('Sensor ESP32-04', 'ESP32-J0K1L2', 4, false);

-- Las mediciones se generan solas cuando los ESP32 (o pruebas con Postman)
-- envien datos al endpoint POST /api/mediciones
