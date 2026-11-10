# ADISAN Móvil

Aplicación Android nativa para la gestión comercial de **ADISAN**, distribuidora de abarrotes en Cajamarca. Permite administrar productos, inventario, pedidos, ventas de mostrador, compras a proveedores, ofertas y reportes financieros, con una interfaz que se adapta al rol del usuario (`admin`, `vendedor`, `chofer`, `cliente`).

Proyecto del curso **Calidad y Pruebas de Software** — Universidad Privada del Norte (UPN), Cajamarca, 2026-2.

## Tecnologías

| Área | Tecnología |
| :--- | :--- |
| Plataforma | Android nativo (Min SDK 24, Target SDK 37) |
| Lenguaje e interfaz | Java 11 + layouts XML con Material Design 3 |
| Backend | Firebase Authentication y Cloud Firestore (sin servidor propio) |
| Pruebas | JUnit 4, Espresso, Accessibility Test Framework |
| Calidad | JaCoCo (cobertura) y Android Lint (análisis estático) |

## Funcionalidades

- Inicio de sesión y registro con Firebase Authentication.
- Panel de indicadores según el rol del usuario.
- Catálogo de productos con precios por paquete y por unidad.
- Carrito de compras, pedidos y seguimiento de estados (`pendiente` → `aceptado` → `en_camino` → `entregado`, o `cancelado`).
- Venta directa de mostrador.
- Compras a proveedores e ingreso de mercadería.
- Control de inventario con historial de movimientos (transacciones de Firestore).
- Ofertas y combos.
- Reportes financieros.

## Estructura

```
app/src/main/java/...      Código de la aplicación (Activities, adaptadores, modelos, servicios)
app/src/main/res/          Layouts XML, estilos y recursos
app/src/test/java/...      Pruebas unitarias y de carga (JUnit 4)
app/src/androidTest/java/  Pruebas funcionales y de accesibilidad (Espresso)
firebase/                  Reglas de Firestore, índices y script de datos de prueba
```

## Cómo ejecutar

1. Abrir el proyecto en Android Studio (requiere JDK 17 o superior para Gradle).
2. Colocar el archivo `app/google-services.json` del proyecto de Firebase.
3. Compilar e instalar en un emulador o teléfono:

```bash
./gradlew app:assembleDebug
```

## Pruebas

| Tipo | Cantidad | Comando |
| :--- | :--- | :--- |
| Unitarias (carrito, modelos, estados del pedido, validaciones) | 52 | `./gradlew testDebugUnitTest` |
| Carga y estrés (lógica local) | 3 | incluidas en el comando anterior |
| Funcionales de interfaz (Espresso) | 8 | `./gradlew connectedDebugAndroidTest` |
| Accesibilidad automatizada (ATF) | 2 | incluidas en el comando anterior |

- Reporte de cobertura: `./gradlew createDebugUnitTestCoverageReport`
- Análisis estático: `./gradlew lintDebug`

Las pruebas instrumentadas necesitan un emulador o dispositivo conectado. La prueba del flujo principal (inicio de sesión → producto → carrito) recibe la cuenta de prueba como argumentos y se omite si no se le pasan.

Los casos de prueba y los defectos están registrados en los [issues del repositorio](https://github.com/Auber344/ADISAN-MOVIL/issues), enlazados con su requisito.

## Autores

- Díaz Sánchez, Auber
