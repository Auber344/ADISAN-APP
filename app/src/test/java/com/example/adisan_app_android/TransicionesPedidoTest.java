package com.example.adisan_app_android;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Pruebas unitarias PU-38 a PU-45: reglas de cambio de estado del pedido (REF-02, BUG-01).
 * Usa tabla de decisión y transición de estados.
 */
public class TransicionesPedidoTest {

    @Test
    public void pu38_transicionesPermitidas() {
        assertTrue(TransicionesPedido.puedeCambiar("pendiente", "aceptado"));
        assertTrue(TransicionesPedido.puedeCambiar("pendiente", "cancelado"));
        assertTrue(TransicionesPedido.puedeCambiar("aceptado", "en_camino"));
        assertTrue(TransicionesPedido.puedeCambiar("aceptado", "cancelado"));
        assertTrue(TransicionesPedido.puedeCambiar("en_camino", "entregado"));
        assertTrue(TransicionesPedido.puedeCambiar("en_camino", "cancelado"));
    }

    @Test
    public void pu39_pedidoEntregadoNoCambia() {
        assertFalse(TransicionesPedido.puedeCambiar("entregado", "pendiente"));
        assertFalse(TransicionesPedido.puedeCambiar("entregado", "aceptado"));
        assertFalse(TransicionesPedido.puedeCambiar("entregado", "en_camino"));
        assertFalse(TransicionesPedido.puedeCambiar("entregado", "cancelado"));
        assertFalse(TransicionesPedido.puedeCambiar("entregado", "entregado"));
    }

    @Test
    public void pu40_pedidoCanceladoNoCambia() {
        assertFalse(TransicionesPedido.puedeCambiar("cancelado", "pendiente"));
        assertFalse(TransicionesPedido.puedeCambiar("cancelado", "entregado"));
        assertFalse(TransicionesPedido.puedeCambiar("cancelado", "cancelado"));
    }

    @Test
    public void pu41_noSePuedeRetrocederNiSaltarEstados() {
        assertFalse(TransicionesPedido.puedeCambiar("aceptado", "pendiente"));
        assertFalse(TransicionesPedido.puedeCambiar("en_camino", "aceptado"));
        assertFalse(TransicionesPedido.puedeCambiar("en_camino", "pendiente"));
        assertFalse(TransicionesPedido.puedeCambiar("pendiente", "en_camino"));
        assertFalse(TransicionesPedido.puedeCambiar("pendiente", "entregado"));
    }

    @Test
    public void pu42_normalizaFormatosAntiguos() {
        assertEquals("en_camino", TransicionesPedido.normalizar("En Camino"));
        assertEquals("entregado", TransicionesPedido.normalizar("completado"));
        assertEquals("", TransicionesPedido.normalizar(null));
        assertTrue(TransicionesPedido.puedeCambiar("en camino", "entregado"));
        assertFalse(TransicionesPedido.puedeCambiar("completado", "cancelado"));
    }

    @Test
    public void pu43_estadoNoValidoSeRechaza() {
        assertFalse(TransicionesPedido.esEstadoValido("enviado"));
        assertFalse(TransicionesPedido.puedeCambiar("pendiente", "enviado"));
        assertEquals("Estado de pedido no válido", TransicionesPedido.mensajeRechazo("pendiente", "enviado"));
    }

    @Test
    public void pu44_mensajesDeRechazo() {
        assertEquals("El pedido ya fue cancelado", TransicionesPedido.mensajeRechazo("cancelado", "pendiente"));
        assertEquals("El pedido ya fue entregado y no puede cambiar de estado",
                TransicionesPedido.mensajeRechazo("entregado", "cancelado"));
        assertEquals("No se puede pasar un pedido de en camino a aceptado",
                TransicionesPedido.mensajeRechazo("en_camino", "aceptado"));
    }

    @Test
    public void pu45_mismoEstadoYEstadoDesconocido() {
        // Repetir un estado no final se permite (por ejemplo, para cambiar la fecha estimada)
        assertTrue(TransicionesPedido.puedeCambiar("pendiente", "pendiente"));
        // Un pedido sin estado se trata como pendiente
        assertTrue(TransicionesPedido.puedeCambiar(null, "aceptado"));
        assertFalse(TransicionesPedido.puedeCambiar(null, "entregado"));
    }
}
