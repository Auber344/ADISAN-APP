package com.example.adisan_app_android;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckPreset;
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckResult.AccessibilityCheckResultType;
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityHierarchyCheck;
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityHierarchyCheckResult;
import com.google.android.apps.common.testing.accessibility.framework.uielement.AccessibilityHierarchyAndroid;

import java.util.Collection;
import java.util.Locale;
import java.util.Set;

/**
 * Aplica las reglas del Accessibility Test Framework de Google (las mismas de Accessibility Scanner)
 * a la pantalla indicada y escribe los hallazgos en Logcat con la etiqueta ADISAN_A11Y.
 * Equivale, para Android, a WAVE, AXE o Lighthouse en la web.
 */
final class RevisorAccesibilidad {

    static final String TAG = "ADISAN_A11Y";

    private RevisorAccesibilidad() {
    }

    /** Revisa una Activity. Debe llamarse en el hilo principal. */
    static int revisar(Activity activity, String pantalla) {
        View raiz = activity.getWindow().getDecorView();
        AccessibilityHierarchyAndroid jerarquia = AccessibilityHierarchyAndroid.newBuilder(raiz).build();
        Set<AccessibilityHierarchyCheck> reglas =
                AccessibilityCheckPreset.getAccessibilityHierarchyChecksForPreset(AccessibilityCheckPreset.LATEST);

        int errores = 0;
        int advertencias = 0;
        for (AccessibilityHierarchyCheck regla : reglas) {
            for (AccessibilityHierarchyCheckResult r : regla.runCheckOnHierarchy(jerarquia)) {
                AccessibilityCheckResultType tipo = r.getType();
                if (tipo != AccessibilityCheckResultType.ERROR && tipo != AccessibilityCheckResultType.WARNING) continue;
                if (tipo == AccessibilityCheckResultType.ERROR) errores++; else advertencias++;
                String vista = r.getElement() != null && r.getElement().getResourceName() != null
                        ? r.getElement().getResourceName() : "";
                Log.i(TAG, pantalla + " | " + tipo + " | " + r.getSourceCheckClass().getSimpleName()
                        + " | " + vista + " | " + r.getMessage(Locale.getDefault()));
            }
        }
        Log.i(TAG, pantalla + " | RESUMEN | reglas=" + reglas.size() + " | hallazgos=" + (errores + advertencias)
                + " | errores=" + errores + " | advertencias=" + advertencias);
        return errores + advertencias;
    }

    /** Revisa la pantalla que está visible en este momento (útil dentro de un flujo con sesión iniciada). */
    static void revisarPantallaActual(String pantalla) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            Collection<Activity> activas = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
            if (activas.isEmpty()) {
                Log.i(TAG, pantalla + " | RESUMEN | sin pantalla visible");
                return;
            }
            revisar(activas.iterator().next(), pantalla);
        });
    }
}
