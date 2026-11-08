# 🍕 Break-app

![Platform](https://img.shields.io/badge/Platform-Android-green?style=flat-square&logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-2.2+-blue?style=flat-square&logo=kotlin)
![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=flat-square&logo=jetpackcompose)
![Architecture](https://img.shields.io/badge/Architecture-MVVM%20%7C%20Clean-orange?style=flat-square)
![Firebase](https://img.shields.io/badge/Backend-Firebase%20Suite-FFCA28?style=flat-square&logo=firebase)
![AI-Agent](https://img.shields.io/badge/AI%20Agent-BreakApp--AI%20Hybrid-purple?style=flat-square)

> Aplicación móvil nativa en **Android** desarrollada con **Kotlin** y **Jetpack Compose** para la comunidad universitaria de la **Universidad Jorge Tadeo Lozano (Bogotá)**. Permite descubrir restaurantes y cafeterías del campus, explorar menús, pedir comida en línea, recibir recomendaciones con un agente inteligente de IA, gestionar métodos de pago y realizar el seguimiento de pedidos en tiempo real.

---

## 📱 Módulos de la Aplicación

1. **Autenticación y Recuperación (`ui/login`, `ui/registro`, `ui/olvidocontrasena`, `ui/recuperacion`)**
   - Registro de usuarios con validación y confirmación.
   - Inicio de sesión conectado a Firebase Authentication y Cloud Firestore.
   - Flujo de recuperación de usuario y contraseña con PIN de seguridad.

2. **Perfil y Configuración (`ui/perfil`)**
   - Visualización y edición de datos del estudiante (nombre, correo, dirección de entrega).
   - Cambio de foto de perfil (cámara o galería).
   - Gestión de métodos de pago (tarjeta de crédito/débito y efectivo).
   - Ajustes de la aplicación: Modo oscuro, Idioma, Notificaciones, Privacidad y Ayuda/Soporte.

3. **Catálogo y Restaurantes (`main/home`, `main/restaurant`, `main/search`)**
   - Pantalla principal con ofertas, categorías y restaurantes recomendados (*Peoples Pizza*, *Catering Gourmet*, *Fruit Coffee*, *World Gourmet*).
   - Menús detallados con fotos, descripciones, precios en pesos colombianos (COP) y control de stock en tiempo real.
   - Búsqueda interactiva de productos y tiendas.

4. **Carrito, Pagos y Pedidos (`main/cart`, `main/orders`)**
   - Carrito de compras con cálculo automático de subtotales y tiempo de entrega.
   - Selección de pago: **Efectivo / Nequi** o **Tarjeta de crédito**.
   - Manejo de estados de pago (aprobado, fondos insuficientes, tarjeta rechazada).
   - Ticket de confirmación y pantalla de **Seguimiento del pedido en vivo** (*Recibido -> En preparación -> Listo -> Entregado*).

5. **Agente Inteligente de Recomendaciones (`BreakApp-AI` & `ui/ai`)**
   - Vinculación del agente inteligente de IA entrenado para la comunidad universitaria.
   - **Estrategia Dual-Engine (Alta disponibilidad)**:
     - **Microservicio Python (`ai_service/`)**: Servidor REST API local en puerto 8000 con endpoints `/api/chat` y `/api/recommend`, cargando el modelo híbrido serializado (`break_agent.pkl`).
     - **Motor Local On-Device (`BreakAiLocalEngine`)**: Algoritmo nativo en Kotlin con reglas de utilidad, franjas horarias (desayuno, almuerzo, snacks, cena), detección de presupuesto y maridajes, garantizando funcionamiento autónomo offline.
   - **Pantalla Interactiva (`AiRecommendationsScreen`)**: Chat con lenguaje natural, sugerencias rápidas (chips), tarjetas de productos recomendados con botón para añadir directamente al carrito y combos sugeridos destacados.
   - **Accesos directos**: Banner interactivo en la pantalla de Inicio (`HomeScreen`) y acceso desde la pantalla de Búsqueda (`SearchScreen`).

---

## 🛠️ Stack Tecnológico y Arquitectura

- **Lenguaje:** Kotlin 2.2+ / Python 3.11+ (para el servicio IA)
- **UI Framework:** Jetpack Compose (Material 3)
- **Arquitectura:** MVVM (Model-View-ViewModel) + Clean Architecture + UDF (Unidirectional Data Flow)
- **Navegación:** Jetpack Navigation Compose
- **Backend & Base de Datos:**
  - Firebase Authentication
  - Cloud Firestore
  - Firebase Storage
- **Red & API:** Retrofit 2 + Gson Converter
- **Inteligencia Artificial:** Agente Híbrido BreakApp-AI (Reglas + Clustering K-Means + Filtrado Colaborativo + Dual-Engine On-Device)
- **Gestión de Imágenes:** Coil Compose
- **Asincronía y Estado:** Kotlin Coroutines & StateFlow

---

## 📂 Estructura del Proyecto

```text
break-app-copy/
├── ai_service/                        # Microservicio Python del Agente Inteligente BreakApp-AI
│   ├── src/break_ia/                  # Algoritmos de clustering, híbrido y reglas
│   ├── data/                          # Modelos serializados (.pkl/.json) y catálogos
│   ├── server.py                      # Servidor REST API (/api/chat, /api/recommend)
│   ├── run_server.bat                 # Script de ejecución en 1 clic para Windows
│   └── README.md                      # Manual técnico del microservicio
├── app/                               # Aplicación móvil Android
│   ├── build.gradle.kts
│   ├── google-services.json
│   └── src/main/
│       ├── java/com/example/breakapp/
│       │   ├── data/
│       │   │   ├── ai/                # BreakAiLocalEngine y AiRepository (Dual-Engine)
│       │   │   └── network/           # RetrofitClient e IaApiService
│       │   ├── main/                  # Inicio, Restaurantes, Carrito y Pedidos
│       │   ├── ui/                    # Login, Registro, Perfil y Break IA (AiRecommendationsScreen)
│       │   ├── model/                 # DTOs de IA, pedidos y usuario
│       │   ├── navigation/            # BreakNavHost y rutas de la app
│       │   └── MainActivity.kt
│       └── res/                       # Recursos visuales (drawables, fuentes, strings)
├── gradle/                            # Versiones y dependencias (Version Catalog)
├── build.gradle.kts                   # Configuración Gradle raíz
├── settings.gradle.kts                # Configuración del proyecto
└── README.md                          # Documentación del proyecto
```

---

## 🚀 Cómo ejecutar el proyecto

1. **Clonar el repositorio**:
   ```bash
   git clone https://github.com/pepitaemy3535/break-app-copy.git
   ```

2. **(Opcional) Iniciar el microservicio de IA en Python**:
   - Haz doble clic en `ai_service/run_server.bat` o ejecuta en consola:
     ```bash
     cd ai_service
     python server.py 8000
     ```
   *(Nota: si no inicias el servidor Python, la app móvil activará automáticamente su motor inteligente On-Device sin interrumpir la experiencia del usuario).*

3. **Abrir y ejecutar en Android Studio**:
   - Abre la carpeta `break-app-copy` en Android Studio.
   - Sincroniza Gradle (`Sync Project with Gradle Files`).
   - Presiona **Run** (`Shift + F10`) en tu emulador o dispositivo físico.
