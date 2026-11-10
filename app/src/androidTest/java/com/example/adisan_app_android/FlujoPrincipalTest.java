package com.example.adisan_app_android;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assume.assumeTrue;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Prueba funcional automatizada del flujo principal (PF-01 + PF-24 sin confirmar el pedido):
 * iniciar sesión (cliente, vendedor o administrador), abrir el catálogo, agregar un producto al carrito
 * y verificar el carrito y su total.
 *
 * Datos que recibe como argumentos de instrumentación (no se guardan en el código):
 *   correo    cuenta de prueba, por ejemplo cliente@adisan.pe
 *   password  su contraseña (firebase/seed/credenciales-prueba.txt)
 *   producto  opcional, por defecto "Coca Cola 1.5L"
 *   precio    opcional, precio del paquete esperado, por defecto "S/ 43.00"
 *
 * La prueba no confirma el pedido para no registrar ventas en la base de datos real.
 */
@RunWith(AndroidJUnit4.class)
public class FlujoPrincipalTest {

    private static final int ESPERA_FIREBASE = 20;

    private String correo;
    private String password;
    private String producto;
    private String precio;
    private ActivityScenario<LoginActivity> pantalla;

    @Before
    public void prepararDatos() {
        Bundle args = InstrumentationRegistry.getArguments();
        correo = args.getString("correo");
        password = args.getString("password");
        producto = args.getString("producto", "Coca Cola 1.5L");
        precio = args.getString("precio", "S/ 43.00");

        // Empieza sin sesión ni carrito
        Context context = ApplicationProvider.getApplicationContext();
        context.getSharedPreferences("AdisanPrefs", Context.MODE_PRIVATE).edit().clear().commit();
        FirebaseAuth.getInstance().signOut();
        CarritoManager.getInstance().limpiar();
    }

    @After
    public void cerrar() {
        if (pantalla != null) pantalla.close();
        CarritoManager.getInstance().limpiar();
        FirebaseAuth.getInstance().signOut();
    }

    @Test
    @Trazabilidad(caso = "PF-FLUJO-01", rf = "RF-01, RF-06, RF-23", hu = "HU-01, HU-08, HU-09", ca = "CA-01.1, CA-08.3, CA-09.1")
    public void flujoPrincipal_loginCatalogoYCarrito() {
        assumeTrue("Faltan los argumentos 'correo' y 'password'",
                !TextUtils.isEmpty(correo) && !TextUtils.isEmpty(password));

        // Paso 1: abrir la pantalla de inicio de sesión
        pantalla = ActivityScenario.launch(LoginActivity.class);

        // Paso 2: ingresar las credenciales e iniciar sesión
        onView(withId(R.id.etCorreo)).perform(replaceText(correo), closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(replaceText(password), closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());

        // Paso 3: se abre el Dashboard
        Espera.hastaQueSeVea(withId(R.id.bottomNavigation), ESPERA_FIREBASE);
        RevisorAccesibilidad.revisarPantallaActual("Dashboard");   // PAC-AUTO-03 (Logcat ADISAN_A11Y)

        // Paso 4: ir al catálogo de productos. El título depende del rol guardado al iniciar sesión:
        // "Comprar productos" para el cliente y "Vender productos" para vendedor o administrador.
        onView(withId(R.id.nav_productos)).perform(click());
        Espera.hastaQueSeVea(withId(R.id.rvProductos), ESPERA_FIREBASE);
        String rol = ApplicationProvider.getApplicationContext()
                .getSharedPreferences("AdisanPrefs", Context.MODE_PRIVATE).getString("rol", "");
        String tituloEsperado = "cliente".equalsIgnoreCase(rol) ? "Comprar productos" : "Vender productos";
        onView(withId(R.id.tvHeaderTitle)).check(matches(withText(tituloEsperado)));

        // Paso 5: buscar el producto y esperar a que cargue desde Firestore
        onView(withId(R.id.etBuscarProducto)).perform(replaceText(producto), closeSoftKeyboard());
        Espera.hastaQueSeVea(allOf(withId(R.id.tvNombreProducto), withText(producto)), ESPERA_FIREBASE);
        RevisorAccesibilidad.revisarPantallaActual("Productos");   // PAC-AUTO-04

        // Paso 6: agregar el producto al carrito (1 paquete)
        onView(allOf(withId(R.id.btnAgregarCarrito),
                isDescendantOfA(allOf(withId(R.id.cardProductoItem), hasDescendant(withText(producto))))))
                .perform(click());
        onView(withId(R.id.tvBadgeCarritoHeader)).check(matches(allOf(isDisplayed(), withText("1"))));

        // Paso 7: abrir el carrito
        onView(withId(R.id.btnVerCarrito)).perform(click());
        Espera.hastaQueSeVea(withId(R.id.rvCarrito), 10);

        // Paso 8: verificar el producto y el total
        onView(allOf(withId(R.id.tvNombreItemCarrito), withText(producto))).check(matches(isDisplayed()));
        onView(withId(R.id.tvTotalCarritoCalculado)).check(matches(withText(precio)));
        onView(withId(R.id.layoutCarritoVacio)).check(matches(not(isDisplayed())));
        RevisorAccesibilidad.revisarPantallaActual("Carrito");     // PAC-AUTO-05
    }
}
