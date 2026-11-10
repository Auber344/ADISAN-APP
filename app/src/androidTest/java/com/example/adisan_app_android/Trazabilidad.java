package com.example.adisan_app_android;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marca cada prueba automatizada con el caso de prueba, el requisito, la historia de usuario
 * y el criterio de aceptación que verifica. Permite armar la matriz de trazabilidad
 * directamente desde los scripts de Espresso.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Trazabilidad {
    /** Identificador del caso de prueba (por ejemplo, PF-09). */
    String caso();

    /** Requisito funcional o no funcional (por ejemplo, RF-03). */
    String rf();

    /** Historia de usuario (por ejemplo, HU-02). */
    String hu() default "";

    /** Criterio de aceptación (por ejemplo, CA-02.2). */
    String ca() default "";
}
