package com.example.adisan_app_android;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import android.view.View;

import com.google.android.material.textfield.TextInputLayout;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Utilidades para las pruebas funcionales con Espresso.
 * Espresso no espera las respuestas de Firebase, por eso se consulta la vista
 * varias veces hasta que aparezca o se agote el tiempo.
 */
final class Espera {

    private Espera() {
    }

    /** Espera hasta que la vista esté visible en pantalla (máximo {@code segundos}). */
    static void hastaQueSeVea(Matcher<View> vista, int segundos) {
        long limite = System.currentTimeMillis() + segundos * 1000L;
        while (true) {
            try {
                onView(vista).check(matches(isDisplayed()));
                return;
            } catch (Throwable error) {
                if (System.currentTimeMillis() > limite) throw error;
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw error;
                }
            }
        }
    }

    /** Comprueba el mensaje de error que muestra un TextInputLayout. */
    static Matcher<View> conError(String mensaje) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                if (!(view instanceof TextInputLayout)) return false;
                CharSequence error = ((TextInputLayout) view).getError();
                return error != null && mensaje.contentEquals(error);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("TextInputLayout con el error: " + mensaje);
            }
        };
    }
}
