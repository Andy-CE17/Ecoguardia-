/*
 * EcoGuardia del Ruido - Firmware ESP32
 *
 * Hardware:
 *   KY-038  -> AO en GPIO 34, DO en GPIO 23
 *   LCD I2C -> SDA 21, SCL 22 (dirección 0x27)
 *   LEDs    -> Rojo 19, Amarillo 18, Verde 15
 */

#include <WiFi.h>
#include <HTTPClient.h>
#include <ArduinoJson.h>
#include <Wire.h>
#include <LiquidCrystal_I2C.h>

// ===================== CONFIGURACIÓN =====================

const char* WIFI_SSID      = "A56 de Piero";
const char* WIFI_PASSWORD   = "123456789";

const char* SERVER_URL = "http://10.14.9.71:8081/api/mediciones";
const char* CODIGO_ESP32 = "ESP32-A1B2C3";

// Intervalo entre envíos al backend (milisegundos)
const unsigned long INTERVALO_ENVIO = 5000;

// ==========================================================

#define LED_ROJO     19
#define LED_AMARILLO 18
#define LED_VERDE    15
#define SENSOR_AO    34
#define SENSOR_DO    23

LiquidCrystal_I2C lcd(0x27, 16, 2);

unsigned long ultimoEnvio = 0;
int ultimaAmplitud = 0;

void setup() {
  Serial.begin(115200);
  Wire.begin(21, 22);

  lcd.init();
  lcd.backlight();

  pinMode(LED_ROJO, OUTPUT);
  pinMode(LED_AMARILLO, OUTPUT);
  pinMode(LED_VERDE, OUTPUT);
  pinMode(SENSOR_DO, INPUT);

  lcd.setCursor(0, 0);
  lcd.print("  EcoGuardia  ");
  lcd.setCursor(0, 1);
  lcd.print("Conectando WiFi");

  conectarWiFi();

  lcd.clear();
}

void loop() {
  if (WiFi.status() != WL_CONNECTED) {
    conectarWiFi();
  }

  // Ventana de 250ms para capturar picos de sonido
  int valorMaximo = 0;
  int valorMinimo = 4095;

  unsigned long tiempoInicio = millis();
  while (millis() - tiempoInicio < 250) {
    int lectura = analogRead(SENSOR_AO);
    if (lectura > valorMaximo) valorMaximo = lectura;
    if (lectura < valorMinimo) valorMinimo = lectura;
  }

  int amplitudRuido = valorMaximo - valorMinimo;
  int estadoDO = digitalRead(SENSOR_DO);
  ultimaAmplitud = amplitudRuido;

  // Serial Monitor
  Serial.print("Amplitud: ");
  Serial.print(amplitudRuido);
  Serial.print(" | DO: ");
  Serial.println(estadoDO);

  // LCD fila 1: nivel de ruido
  lcd.setCursor(0, 0);
  lcd.print("Ruido: ");
  lcd.print(amplitudRuido);
  lcd.print("    ");

  // Apagar LEDs
  digitalWrite(LED_ROJO,     LOW);
  digitalWrite(LED_AMARILLO, LOW);
  digitalWrite(LED_VERDE,    LOW);

  // Semáforo + LCD fila 2
  lcd.setCursor(0, 1);
  if (amplitudRuido < 90) {
    digitalWrite(LED_VERDE, HIGH);
    lcd.print("BAJO  | DO:");
  }
  else if (amplitudRuido < 250) {
    digitalWrite(LED_AMARILLO, HIGH);
    lcd.print("MEDIO | DO:");
  }
  else {
    digitalWrite(LED_ROJO, HIGH);
    lcd.print("ALTO  | DO:");
  }
  lcd.print(estadoDO);
  lcd.print(" ");

  // Enviar al backend cada INTERVALO_ENVIO ms
  unsigned long ahora = millis();
  if (ahora - ultimoEnvio >= INTERVALO_ENVIO) {
    ultimoEnvio = ahora;
    float nivelDb = amplitudADb(amplitudRuido);
    enviarMedicion(nivelDb);
  }

  delay(100);
}

// Convierte amplitud del KY-038 a decibeles aproximados
// Mapeo: 0→30dB, 90→60dB, 250→80dB, 4095→120dB
float amplitudADb(int amplitud) {
  float db;
  if (amplitud < 90) {
    db = 30.0 + (amplitud / 90.0) * 30.0;
  } else if (amplitud < 250) {
    db = 60.0 + ((amplitud - 90.0) / 160.0) * 20.0;
  } else {
    db = 80.0 + ((amplitud - 250.0) / 3845.0) * 40.0;
  }
  if (db < 30.0) db = 30.0;
  if (db > 120.0) db = 120.0;
  return db;
}

void conectarWiFi() {
  WiFi.disconnect(true);
  delay(100);
  WiFi.mode(WIFI_STA);
  delay(100);

  Serial.printf("Conectando a %s", WIFI_SSID);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);

  int intentos = 0;
  while (WiFi.status() != WL_CONNECTED && intentos < 30) {
    delay(500);
    Serial.print(".");
    intentos++;
  }

  if (WiFi.status() == WL_CONNECTED) {
    Serial.printf("\nConectado! IP: %s\n", WiFi.localIP().toString().c_str());
  } else {
    Serial.println("\nNo se pudo conectar. Reintentando en 5s...");
    WiFi.disconnect(true);
    delay(5000);
  }
}

void enviarMedicion(float nivelDb) {
  if (WiFi.status() != WL_CONNECTED) {
    Serial.println("Sin WiFi, saltando envio");
    return;
  }

  HTTPClient http;
  http.begin(SERVER_URL);
  http.addHeader("Content-Type", "application/json");
  http.setTimeout(5000);

  JsonDocument doc;
  doc["nivelDb"] = round(nivelDb * 10.0) / 10.0;
  doc["codigoEsp32"] = CODIGO_ESP32;

  String payload;
  serializeJson(doc, payload);

  Serial.printf("Enviando: %s\n", payload.c_str());

  int httpCode = http.POST(payload);

  if (httpCode == 201) {
    Serial.printf("OK: %s\n", http.getString().c_str());
  } else if (httpCode > 0) {
    Serial.printf("Error HTTP %d\n", httpCode);
  } else {
    Serial.printf("Error conexion: %s\n", http.errorToString(httpCode).c_str());
  }

  http.end();
}
