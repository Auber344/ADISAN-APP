package com.example.adisan_app_android;

import static com.example.adisan_app_android.DatosPrueba.DELTA;
import static com.example.adisan_app_android.DatosPrueba.oferta;
import static com.example.adisan_app_android.DatosPrueba.producto;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** Pruebas unitarias PU-01 a PU-13 del catálogo de pruebas: clase ItemCarrito. */
public class ItemCarritoTest {

    @Test
    public void pu01_subtotalVentaPorPaquete() {
        ItemCarrito item = new ItemCarrito(producto(1), "PAQUETE", 2, 0);
        assertEquals(48.00, item.getSubtotal(), DELTA);
    }

    @Test
    public void pu02_subtotalVentaPorUnidad() {
        ItemCarrito item = new ItemCarrito(producto(1), "UNIDAD", 0, 3);
        assertEquals(7.50, item.getSubtotal(), DELTA);
    }

    @Test
    public void pu03_subtotalVentaMixta() {
        ItemCarrito item = new ItemCarrito(producto(1), "MIXTA", 2, 3);
        assertEquals(55.50, item.getSubtotal(), DELTA);
    }

    @Test
    public void pu04_subtotalDeOferta() {
        ItemCarrito item = new ItemCarrito(oferta(9, "15.90"), 2);
        assertEquals(31.80, item.getSubtotal(), DELTA);
    }

    @Test
    public void pu05_totalUnidadesVentaPorPaquete() {
        ItemCarrito item = new ItemCarrito(producto(1), "PAQUETE", 2, 0);
        assertEquals(24, item.getCantidadTotalUnidades());
    }

    @Test
    public void pu06_totalUnidadesVentaMixta() {
        ItemCarrito item = new ItemCarrito(producto(1), "MIXTA", 1, 5);
        assertEquals(17, item.getCantidadTotalUnidades());
    }

    @Test
    public void pu07_unidadesPorPaqueteCeroSeTomaComoUno() {
        Producto p = producto(1);
        p.setUnidadesPorPaquete(0);
        ItemCarrito item = new ItemCarrito(p, "PAQUETE", 1, 0);
        assertEquals(1, item.getUnidadesPorPaquete());
    }

    @Test
    public void pu08_precioNoNumericoSeTomaComoCero() {
        Producto p = producto(1);
        p.setPrecioVentaPaquete("abc");
        ItemCarrito item = new ItemCarrito(p, "PAQUETE", 2, 0);
        assertEquals(0.0, item.getPrecioVentaPaquete(), DELTA);
        assertEquals(0.0, item.getSubtotal(), DELTA);
    }

    @Test
    public void pu09_cantidadDePaquetesNegativaQuedaEnCero() {
        ItemCarrito item = new ItemCarrito(producto(1), "PAQUETE", 2, 0);
        item.setCantidadPaquetes(-5);
        assertEquals(0, item.getCantidadPaquetes());
    }

    @Test
    public void pu10_tipoVentaNuloSeTomaComoUnidad() {
        ItemCarrito item = new ItemCarrito(producto(1), null, 0, 1);
        assertEquals("UNIDAD", item.getTipoVenta());
    }

    @Test
    public void pu11_itemDeOfertaTieneTipoOfertaYNoCambia() {
        ItemCarrito item = new ItemCarrito(oferta(9, "15.90"), 1);
        assertTrue(item.isOferta());
        assertEquals("OFERTA", item.getTipoVenta());
        assertEquals(0, item.getCantidadPaquetes());
        item.setTipoVenta("PAQUETE");
        assertEquals("OFERTA", item.getTipoVenta());
    }

    // Verifica la corrección REF-01 del defecto BUG-08 (antes fallaba y estaba omitida con @Ignore).
    @Test
    public void pu12_cantidadDeOfertasNoDebeSerNegativa() {
        ItemCarrito item = new ItemCarrito(oferta(9, "15.90"), 1);
        item.setCantidadOfertas(-1);
        assertTrue("La cantidad de ofertas no debe ser negativa", item.getCantidadOfertas() >= 0);
    }

    // Verifica la corrección REF-01 del defecto BUG-08 (antes fallaba y estaba omitida con @Ignore).
    @Test
    public void pu13_constructorNoDebeAceptarCantidadesNegativas() {
        ItemCarrito item = new ItemCarrito(producto(1), "UNIDAD", 0, -3);
        assertTrue("El subtotal no debe ser negativo", item.getSubtotal() >= 0);
    }
}
