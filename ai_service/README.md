# 🤖 BreakApp-AI Service (Microservicio del Agente Inteligente)

Servidor HTTP REST API que expone el agente de recomendaciones híbrido (reglas de utilidad, clustering K-Means y filtrado colaborativo) para la aplicación móvil **Break-app**.

---

## 🚀 Cómo ejecutar el servidor

### Opción 1: Doble clic en Windows
Ejecuta el archivo `run_server.bat` o `run_server.ps1`.

### Opción 2: Terminal
```bash
cd ai_service
python server.py 8000
```

El servidor quedará disponible en:
- `http://localhost:8000/`
- En el emulador de Android: `http://10.0.2.2:8000/api/chat`

---

## 📡 Endpoints Disponibles

### 1. `GET /api/health`
Verifica el estado del servicio y del agente.

### 2. `POST /api/chat`
Endpoint principal para consultas en lenguaje natural desde la app móvil.
- **Request:**
  ```json
  {
    "prompt": "¿Qué me recomiendas para el almuerzo con menos de 15000?",
    "userId": "usr_estudiante_01"
  }
  ```
- **Response:**
  Devuelve el texto conversacional formateado, el mensaje banner, el cluster asignado, el combo sugerido y la lista estructurada de productos recomendados con puntuación y justificación.

### 3. `POST /api/recommend`
Genera recomendaciones estructuradas directas para un usuario en una fecha/hora determinada.
