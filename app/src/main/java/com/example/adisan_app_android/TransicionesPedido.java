package com.example.adisan_app_android;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Reglas de cambio de estado de un pedido (RN-12). REF-02, corrige BUG-01.
 *
 *   pendiente  -> aceptado, cancelado
 *   aceptado   -> en_camino, cancelado
 *   en_camino  -> entregado, cancelado
 *   entregado  -> (final: ya es una venta)
 *   cancelado  -> (final: las unidades ya volvieron al stock)
 *
 * Es Java puro (sin Android ni Firebase) para poder probarla con JUnit.
 */
public final class TransicionesPedido {

    public static final String PENDIENTE = "pendiente";
    public static final String ACEPTADO = "aceptado";
    public static final String EN_CAMINO = "en_camino";
    public static final String ENTREGADO = "entregado";
    public static final String CANCELADO = "cancelado";

    public static final List<String> ESTADOS = Collections.unmodifiableList(
            Arrays.asList(PENDIENTE, ACEPTADO, EN_CAMINO, ENTREGADO, CANCELADO));

    private static final Map<String, List<String>> SIGUIENTES = new HashMap<>();

    static {
        SIGUIENTES.put(PENDIENTE, Arrays.asList(ACEPTADO, CANCELADO));
        SIGUIENTES.put(ACEPTADO, Arrays.asList(EN_CAMINO, CANCELADO));
        SIGUIENTES.put(EN_CAMINO, Arrays.asList(ENTREGADO, CANCELADO));
        SIGUIENTES.put(ENTREGADO, Collections.<String>emptyList());
        SIGUIENTES.put(CANCELADO, Collections.<String>emptyList());
    }

    private TransicionesPedido() {
    }

    /** Unifica el formato: minúsculas, "en camino" como "en_camino" y "completado" (dato antiguo) como "entregado". */
    public static String normalizar(String estado) {
        if (estado == null) return "";
        String e = estado.trim().toLowerCase(Locale.ROOT).replace(' ', '_');
        if ("completado".equals(e)) return ENTREGADO;
        return e;
    }

    public static boolean esEstadoValido(String estado) {
        return ESTADOS.contains(normalizar(estado));
    }

    public static boolean esFinal(String estado) {
        String e = normalizar(estado);
        return ENTREGADO.equals(e) || CANCELADO.equals(e);
    }

    /**
     * Indica si un pedido puede pasar de {@code anterior} a {@code nuevo}.
     * Un pedido sin estado registrado se trata como pendiente.
     * Repetir el mismo estado solo se permite si no es final (por ejemplo, para cambiar la fecha estimada).
     */
    public static boolean puedeCambiar(String anterior, String nuevo) {
        String n = normalizar(nuevo);
        if (!ESTADOS.contains(n)) return false;
        String a = normalizar(anterior);
        if (!ESTADOS.contains(a)) a = PENDIENTE;
        if (a.equals(n)) return !esFinal(a);
        return SIGUIENTES.get(a).contains(n);
    }

    /** Mensaje para el usuario cuando {@link #puedeCambiar} es falso. */
    public static String mensajeRechazo(String anterior, String nuevo) {
        if (!esEstadoValido(nuevo)) return "Estado de pedido no válido";
        String a = normalizar(anterior);
        if (CANCELADO.equals(a)) return "El pedido ya fue cancelado";
        if (ENTREGADO.equals(a)) return "El pedido ya fue entregado y no puede cambiar de estado";
        if (!ESTADOS.contains(a)) a = PENDIENTE;
        return "No se puede pasar un pedido de " + etiqueta(a) + " a " + etiqueta(normalizar(nuevo));
    }

    private static String etiqueta(String estado) {
        return estado.replace('_', ' ');
    }
}
