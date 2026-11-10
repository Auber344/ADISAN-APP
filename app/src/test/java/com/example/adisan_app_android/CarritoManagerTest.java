package com.example.adisan_app_android;

import static com.example.adisan_app_android.DatosPrueba.DELTA;
import static com.example.adisan_app_android.DatosPrueba.oferta;
import static com.example.adisan_app_android.DatosPrueba.producto;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/** Pruebas unitarias PU-14 a PU-26 del catálogo de pruebas: clase CarritoManager. */
public class CarritoManagerTest {

    private CarritoManager carrito;

    @Before
    public void setUp() {
        // CarritoManager es un Singleton: se vacía antes de cada prueba.
        carrito = CarritoManager.getInstance();
        carrito.limpiar();
    }

    @Test
    public void pu14_getInstanceDevuelveSiempreLaMismaInstancia() {
        assertSame(CarritoManager.getInstance(), CarritoManager.getInstance());
    }

    @Test
    public void pu15_mismoProductoYTipoAcumulaCantidades() {
        carrito.agregarProducto(producto(1), "PAQUETE", 1, 0);
        carrito.agregarProducto(producto(1), "PAQUETE", 2, 0);
        assertEquals(1, carrito.getItems().size());
        assertEquals(3, carrito.getItems().get(0).getCantidadPaquetes());
    }

    @Test
    public void pu16_mismoProductoConDistintoTipoCreaDosItems() {
        carrito.agregarProducto(producto(1), "PAQUETE", 1, 0);
        carrito.agregarProducto(producto(1), "UNIDAD", 0, 3);
        assertEquals(2, carrito.getItems().size());
    }

    @Test
    public void pu17_mismaOfertaAcumulaCantidad() {
        carrito.agregarOferta(oferta(9, "15.90"), 1);
        carrito.agregarOferta(oferta(9, "15.90"), 2);
        assertEquals(1, carrito.getItems().size());
        assertEquals(3, carrito.getItems().get(0).getCantidadOfertas());
    }

    @Test
    public void pu18_ofertaConCantidadCeroONegativaNoSeAgrega() {
        carrito.agregarOferta(oferta(9, "15.90"), 0);
        carrito.agregarOferta(oferta(9, "15.90"), -1);
        assertTrue(carrito.getItems().isEmpty());
    }

    @Test
    public void pu19_productoNuloNoSeAgrega() {
        carrito.agregarProducto(null, "PAQUETE", 1, 0);
        assertTrue(carrito.getItems().isEmpty());
    }

    @Test
    public void pu20_totalSumaProductosYOfertas() {
        carrito.agregarProducto(producto(1), "PAQUETE", 2, 0);
        carrito.agregarOferta(oferta(9, "15.90"), 2);
        assertEquals(79.80, carrito.obtenerTotal(), DELTA);
    }

    @Test
    public void pu21_totalDeCarritoVacioEsCero() {
        assertEquals(0.0, carrito.obtenerTotal(), DELTA);
    }

    @Test
    public void pu22_eliminarItemSoloConPosicionValida() {
        carrito.agregarProducto(producto(1), "UNIDAD", 0, 3);
        carrito.eliminarItem(-1);
        carrito.eliminarItem(1);
        assertEquals(1, carrito.getItems().size());
        carrito.eliminarItem(0);
        assertEquals(0, carrito.getItems().size());
    }

    @Test
    public void pu23_cantidadTotalDeItems() {
        carrito.agregarProducto(producto(1), "MIXTA", 2, 3);
        carrito.agregarOferta(oferta(9, "15.90"), 2);
        assertEquals(7, carrito.obtenerCantidadTotalItems());
    }

    @Test
    public void pu24_contieneProductoIgnoraOfertas() {
        carrito.agregarProducto(producto(1), "UNIDAD", 0, 1);
        carrito.agregarOferta(oferta(9, "15.90"), 1);
        assertTrue(carrito.contieneProducto(1));
        assertFalse(carrito.contieneProducto(2));
        assertFalse(carrito.contieneProducto(9));
    }

    @Test
    public void pu25_limpiarVaciaElCarrito() {
        carrito.agregarProducto(producto(1), "UNIDAD", 0, 1);
        carrito.agregarProducto(producto(2), "UNIDAD", 0, 1);
        carrito.agregarOferta(oferta(9, "15.90"), 1);
        carrito.limpiar();
        assertTrue(carrito.getItems().isEmpty());
        assertEquals(0.0, carrito.obtenerTotal(), DELTA);
    }

    @Test
    public void pu26_metodoHeredadoAgregaPorUnidad() {
        carrito.agregarProducto(producto(1), 4);
        ItemCarrito item = carrito.getItems().get(0);
        assertEquals("UNIDAD", item.getTipoVenta());
        assertEquals(4, item.getCantidadUnidades());
    }
}
