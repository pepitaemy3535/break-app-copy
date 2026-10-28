# 🍕 Break-app

![Platform](https://img.shields.io/badge/Platform-Android-green?style=flat-square&logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-2.2+-blue?style=flat-square&logo=kotlin)
![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=flat-square&logo=jetpackcompose)
![Architecture](https://img.shields.io/badge/Architecture-MVVM%20%7C%20Clean-orange?style=flat-square)
![Firebase](https://img.shields.io/badge/Backend-Firebase%20Suite-FFCA28?style=flat-square&logo=firebase)

> Aplicación móvil nativa en **Android** desarrollada con **Kotlin** y **Jetpack Compose** para la comunidad universitaria de la **Universidad Jorge Tadeo Lozano (Bogotá)**. Permite descubrir restaurantes y cafeterías del campus, explorar menús, pedir comida en línea, gestionar métodos de pago y realizar el seguimiento de pedidos en tiempo real.

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

---

## 🛠️ Stack Tecnológico y Arquitectura

- **Lenguaje:** Kotlin 2.2+
- **UI Framework:** Jetpack Compose (Material 3)
- **Arquitectura:** MVVM (Model-View-ViewModel) + Clean Architecture + UDF (Unidirectional Data Flow)
- **Navegación:** Jetpack Navigation Compose
- **Backend & Base de Datos:**
  - Firebase Authentication
  - Cloud Firestore
  - Firebase Storage
- **Gestión de Imágenes:** Coil Compose
- **Asincronía y Estado:** Kotlin Coroutines & StateFlow

---

## 📂 Estructura del Proyecto

```text
break-app-copy/
├── app/                               # Módulo principal y único de la aplicación
│   ├── build.gradle.kts
│   ├── google-services.json
│   └── src/main/
│       ├── java/com/example/breakapp/
│       │   ├── main/                  # Inicio, Restaurantes, Carrito y Pedidos
│       │   ├── ui/                    # Login, Registro, Recuperación y Perfil
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

1. Clona el repositorio en tu máquina:
   ```bash
   git clone https://github.com/pepitaemy3535/break-app-copy.git
   ```
2. Abre la carpeta en **Android Studio** (versión Ladybug o superior recomendada).
3. Asegúrate de tener configurado **JDK 21** en `Settings > Build, Execution, Deployment > Build Tools > Gradle`.
4. Sincroniza Gradle (`Sync Project with Gradle Files`).
5. Presiona **Run** (`Shift + F10`) seleccionando un emulador o dispositivo físico con Android 7.0+ (API 24+).
