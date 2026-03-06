# Justina AI Engine - Motor de Análisis Quirúrgico

<p align="center">
  <img src="https://img.shields.io/badge/Python-3.10+-blue.svg" alt="Python 3.10+">
  <img src="https://img.shields.io/badge/Pandas-2.0+-green.svg" alt="Pandas">
  <img src="https://img.shields.io/badge/WebSocket-STOMP-orange.svg" alt="WebSocket">
  <img src="https://img.shields.io/badge/Architecture-Clean-lightgrey.svg" alt="Architecture Clean">
</p>

## 🧠 Descripción General

El **Motor de IA de Justina** es un componente especializado diseñado para el análisis biomecánico y técnico de procedimientos quirúrgicos simulados. Utiliza datos de telemetría de alta frecuencia para evaluar la destreza del cirujano, identificar riesgos intraoperatorios y proporcionar retroalimentación constructiva basada en patrones clínicos.

---

## 🏛️ Arquitectura del Sistema

El motor opera bajo un modelo de procesamiento asíncrono, integrándose con el backend a través de WebSockets para recibir notificaciones en tiempo real y APIs REST para la sincronización de datos pesados.

### Pipeline de Análisis de 5 Pasos

El núcleo de la inteligencia reside en `analysis_pipeline.py`, que implementa una arquitectura secuencial de procesamiento:

1.  **Ingesta y Normalización**: Limpieza de datos raw, sincronización temporal y cálculo de diferenciales espaciales.
2.  **Métricas de Destreza**: Cálculo de cinemática (Velocidad, Aceleración) y **Jerk** (fluidez del movimiento). Evalúa la economía de movimiento.
3.  **Gold Pattern Alignment**: Benchmarking comparativo contra trayectorias ideales para determinar la precisión del abordaje.
4.  **Evaluación de Riesgo**: Detección de eventos críticos (Hemorragias, contacto con tejido sano) y zonificación de riesgos por cuadrantes.
5.  **Generación de Feedback**: Motor de reglas que sintetiza los hallazgos en un Score (0-100) y recomendaciones clínicas personalizadas.

---

## 🚀 Componentes Principales

| Archivo | Responsabilidad |
|---------|-----------------|
| `websocket_client.py` | Cliente de larga duración que escucha eventos de nuevas cirugías. |
| `analysis_pipeline.py` | Lógica central del motor de análisis biomecánico. |
| `client.py` | Cliente HTTP/REST para comunicación con la API de Justina. |
| `config.py` | Gestión de variables de entorno y constantes del sistema. |
| `main.py` | Orquestador para ejecución manual y pruebas de lote. |

---

## 🛠️ Instalación y Configuración

### Requisitos
- Python 3.10 o superior
- Entorno virtual (recomendado)

### Pasos
1. **Crear entorno virtual**:
   ```bash
   python -m venv venv
   source venv/bin/activate  # En Windows: venv\Scripts\activate
   ```

2. **Instalar dependencias**:
   ```bash
   pip install -r requirements.txt
   ```

3. **Variables de Entorno**:
   Configura un archivo `.env` o variables de sistema:
   - `BASE_URL`: URL del backend de Justina.
   - `IA_USER`: Email de la cuenta con rol `ROLE_IA`.
   - `IA_PASSWORD`: Contraseña de la cuenta.

---

## 📡 Modos de Operación

### 1. Modo Servicio (Tiempo Real)
Este es el modo principal de producción donde el motor actúa como un daemon escuchando al backend.
```bash
python websocket_client.py
```

### 2. Modo Manual / Batch
Para análisis bajo demanda o pruebas de regresión.
```bash
python main.py
```

---

## 📊 Métricas Evaluadas

- **Fluidez (Jerk Analysis)**: Mide la estabilidad de las manos del cirujano. Un jerk alto indica indecisión o temblor.
- **Economía de Movimiento**: Ratio entre la trayectoria recorrida y la distancia óptima.
- **Zonificación de Riesgo**: Identificación de cuadrantes (Sup-Izq, Inf-Der, etc.) donde se producen eventos adversos.
- **Score Multivariante**: Algoritmo que penaliza riesgos y bonifica técnica quirúrgica.

---

## 🛡️ Seguridad e Integración
El motor utiliza **Autenticación JWT** para todas las operaciones. El flujo de trabajo con el backend es el siguiente:
1. El backend notifica vía `/ws/ai` sobre una nueva cirugía.
2. El motor descarga la trayectoria vía REST `GET /surgeries/{id}/trajectory`.
3. El motor procesa y envía el análisis vía REST `POST /surgeries/{id}/analysis`.

---

> **Profesional de Arquitectura de Sistemas:** "Este motor ha sido diseñado para ser escalable, permitiendo la adición de nuevos modelos de visión por computadora o redes neuronales en el Pipeline de Paso 3 sin afectar la infraestructura de comunicación."
