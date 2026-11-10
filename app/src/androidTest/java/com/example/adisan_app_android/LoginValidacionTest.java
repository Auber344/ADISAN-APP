package com.example.adisan_app_android;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.example.adisan_app_android.Espera.conError;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Pruebas funcionales PF-01 a PF-04 (validaciones de la pantalla de inicio de sesión).
 * No se conectan a Firebase: la app detiene el envío cuando los datos no son válidos.
 */
@RunWith(AndroidJUnit4.class)
public class LoginValidacionTest {

    private ActivityScenario<LoginActivity> pantalla;

    @Before
    public void abrirLogin() {
        Context context = ApplicationProvider.getApplicationContext();
        context.getSharedPreferences("AdisanPrefs", Context.MODE_PRIVATE).edit().clear().commit();
        pantalla = ActivityScenario.launch(LoginActivity.class);
    }

    @After
    public void cerrar() {
        pantalla.close();
    }

    @Test
    @Trazabilidad(caso = "PF-02", rf = "RF-01", hu = "HU-01", ca = "CA-01.3")
    public void pf02_correoVacioMuestraError() {
        onView(withId(R.id.etCorreo)).perform(clearText());
        onView(withId(R.id.etPassword)).perform(typeText("cualquiera"), closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());

        onView(withId(R.id.tilCorreo)).check(matches(conError("Ingresa tu correo")));
    }

    @Test
    @Trazabilidad(caso = "PF-03", rf = "RF-01", hu = "HU-01", ca = "CA-01.3")
    public void pf03_correoConFormatoInvalidoMuestraError() {
        onView(withId(R.id.etCorreo)).perform(typeText("ana@correo"), closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText("cualquiera"), closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());

        onView(withId(R.id.tilCorreo)).check(matches(conError("Ingresa un correo válido")));
    }

    @Test
    @Trazabilidad(caso = "PF-04", rf = "RF-01", hu = "HU-01", ca = "CA-01.3")
    public void pf04_contrasenaVaciaMuestraError() {
        onView(withId(R.id.etCorreo)).perform(typeText("cliente@adisan.pe"), closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());

        onView(withId(R.id.tilPassword)).check(matches(conError("Ingresa tu contraseña")));
        // Sigue en la pantalla de inicio de sesión
        onView(withId(R.id.btnLogin)).check(matches(isDisplayed()));
    }
}
