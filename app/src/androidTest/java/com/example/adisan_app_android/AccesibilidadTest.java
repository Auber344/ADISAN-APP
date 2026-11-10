package com.example.adisan_app_android;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Pruebas de accesibilidad PAC (inspección automática, equivalente a WAVE/AXE/Lighthouse para Android).
 * Aplica las reglas del Accessibility Test Framework de Google a las pantallas que no requieren sesión.
 * Los hallazgos se escriben en Logcat con la etiqueta ADISAN_A11Y; la prueba no falla, porque mide el estado actual.
 * Las pantallas con sesión (Dashboard, Productos y Carrito) se revisan dentro de FlujoPrincipalTest.
 */
@RunWith(AndroidJUnit4.class)
public class AccesibilidadTest {

    @Before
    public void sinSesion() {
        Context context = ApplicationProvider.getApplicationContext();
        context.getSharedPreferences("AdisanPrefs", Context.MODE_PRIVATE).edit().clear().commit();
    }

    @Test
    @Trazabilidad(caso = "PAC-AUTO-01", rf = "RNF-08", hu = "ODS 10", ca = "Reglas ATF sin errores")
    public void pac_pantallaLogin() {
        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class)) {
            scenario.onActivity(a -> RevisorAccesibilidad.revisar(a, "Login"));
        }
    }

    @Test
    @Trazabilidad(caso = "PAC-AUTO-02", rf = "RNF-08", hu = "ODS 10", ca = "Reglas ATF sin errores")
    public void pac_pantallaRegistro() {
        try (ActivityScenario<RegistroActivity> scenario = ActivityScenario.launch(RegistroActivity.class)) {
            scenario.onActivity(a -> RevisorAccesibilidad.revisar(a, "Registro"));
        }
    }
}
