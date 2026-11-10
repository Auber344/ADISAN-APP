package com.example.adisan_app_android;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.example.adisan_app_android.Espera.conError;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Pruebas funcionales PF-09 a PF-12: validaciones del autorregistro de clientes.
 * Verifican la refactorización REF-03 (clase Validaciones, contraseña mínima de 8 caracteres).
 * No crean cuentas: la app detiene el registro antes de llamar a Firebase cuando un dato no es válido.
 */
@RunWith(AndroidJUnit4.class)
public class RegistroValidacionTest {

    private ActivityScenario<RegistroActivity> pantalla;

    @Before
    public void abrirRegistro() {
        pantalla = ActivityScenario.launch(RegistroActivity.class);
    }

    @After
    public void cerrar() {
        pantalla.close();
    }

    /** Completa el formulario con los datos indicados y pulsa "Crear cuenta". */
    private void registrar(String nombres, String dni, String password, String confirmar) {
        escribir(R.id.etNombresReg, nombres);
        escribir(R.id.etApellidosReg, "Prueba");
        escribir(R.id.etDniReg, dni);
        escribir(R.id.etTelefonoReg, "987654321");
        escribir(R.id.etCorreoReg, "registro.prueba@adisan.pe");
        escribir(R.id.etPasswordReg, password);
        escribir(R.id.etConfirmarReg, confirmar);
        closeSoftKeyboard();
        onView(withId(R.id.btnCrearCuenta)).perform(scrollTo(), click());
    }

    private void escribir(int id, String texto) {
        onView(withId(id)).perform(scrollTo(), replaceText(texto));
    }

    /** Sigue en la pantalla de registro: no se envió nada a Firebase. */
    private void sigueEnRegistro() {
        onView(withId(R.id.btnCrearCuenta)).perform(scrollTo()).check(matches(isDisplayed()));
    }

    @Test
    @Trazabilidad(caso = "PF-09", rf = "RF-03", hu = "HU-02", ca = "CA-02.2")
    public void pf09_dniConSieteDigitos() {
        registrar("Ana", "7123456", "12345678", "12345678");
        onView(withId(R.id.tilDniReg)).check(matches(conError("El DNI tiene 8 dígitos")));
        sigueEnRegistro();
    }

    @Test
    @Trazabilidad(caso = "PF-10", rf = "RF-03", hu = "HU-02", ca = "CA-02.4")
    public void pf10_contrasenaDeSieteCaracteres() {
        // Antes de REF-03 el autorregistro aceptaba 6 caracteres; ahora exige 8
        registrar("Ana", "71234567", "1234567", "1234567");
        onView(withId(R.id.tilPasswordReg)).check(matches(conError("Debe tener al menos 8 caracteres")));
        sigueEnRegistro();
    }

    @Test
    @Trazabilidad(caso = "PF-11", rf = "RF-03", hu = "HU-02", ca = "CA-02.4")
    public void pf11_contrasenasDistintas() {
        registrar("Ana", "71234567", "12345678", "87654321");
        onView(withId(R.id.tilConfirmarReg)).check(matches(conError("Las contraseñas no coinciden")));
        sigueEnRegistro();
    }

    @Test
    @Trazabilidad(caso = "PF-12", rf = "RF-03", hu = "HU-02", ca = "CA-02.1")
    public void pf12_nombresVacios() {
        registrar("", "71234567", "12345678", "12345678");
        onView(withId(R.id.tilNombresReg)).check(matches(conError("Campo obligatorio")));
        sigueEnRegistro();
    }
}
