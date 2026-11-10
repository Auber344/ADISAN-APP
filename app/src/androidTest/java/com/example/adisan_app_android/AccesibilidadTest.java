package com.example.adisan_app_android;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckPreset;
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckResult.AccessibilityCheckResultType;
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityHierarchyCheck;
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityHierarchyCheckResult;
import com.google.android.apps.common.testing.accessibility.framework.uielement.AccessibilityHierarchyAndroid;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Pruebas de accesibilidad PAC (inspección automática).
 * Aplica las reglas del Accessibility Test Framework de Google (las mismas de Accessibility Scanner)
 * a las pantallas que no requieren sesión y escribe los hallazgos en Logcat con la etiqueta ADISAN_A11Y.
 * La prueba registra los hallazgos sin fallar, porque su objetivo es medir el estado actual.
 */
@RunWith(AndroidJUnit4.class)
public class AccesibilidadTest {

    private static final String TAG = "ADISAN_A11Y";

    @Before
    public void sinSesion() {
        Context context = ApplicationProvider.getApplicationContext();
        context.getSharedPreferences("AdisanPrefs", Context.MODE_PRIVATE).edit().clear().commit();
    }

    @Test
    public void pac_pantallaLogin() {
        revisar(LoginActivity.class, "Login");
    }

    @Test
    public void pac_pantallaRegistro() {
        revisar(RegistroActivity.class, "Registro");
    }

    private <A extends Activity> void revisar(Class<A> pantalla, String nombre) {
        try (ActivityScenario<A> scenario = ActivityScenario.launch(pantalla)) {
            scenario.onActivity(activity -> {
                View raiz = activity.getWindow().getDecorView();
                AccessibilityHierarchyAndroid jerarquia = AccessibilityHierarchyAndroid.newBuilder(raiz).build();
                Set<AccessibilityHierarchyCheck> reglas =
                        AccessibilityCheckPreset.getAccessibilityHierarchyChecksForPreset(AccessibilityCheckPreset.LATEST);

                List<AccessibilityHierarchyCheckResult> hallazgos = new ArrayList<>();
                for (AccessibilityHierarchyCheck regla : reglas) {
                    for (AccessibilityHierarchyCheckResult r : regla.runCheckOnHierarchy(jerarquia)) {
                        AccessibilityCheckResultType tipo = r.getType();
                        if (tipo == AccessibilityCheckResultType.ERROR || tipo == AccessibilityCheckResultType.WARNING) {
                            hallazgos.add(r);
                        }
                    }
                }

                int errores = 0;
                for (AccessibilityHierarchyCheckResult r : hallazgos) {
                    if (r.getType() == AccessibilityCheckResultType.ERROR) errores++;
                    String vista = "";
                    if (r.getElement() != null && r.getElement().getResourceName() != null) {
                        vista = r.getElement().getResourceName();
                    }
                    Log.i(TAG, nombre + " | " + r.getType() + " | " + r.getSourceCheckClass().getSimpleName()
                            + " | " + vista + " | " + r.getMessage(Locale.getDefault()));
                }
                Log.i(TAG, nombre + " | RESUMEN | reglas=" + reglas.size() + " | hallazgos=" + hallazgos.size()
                        + " | errores=" + errores + " | advertencias=" + (hallazgos.size() - errores));
            });
        }
    }
}
