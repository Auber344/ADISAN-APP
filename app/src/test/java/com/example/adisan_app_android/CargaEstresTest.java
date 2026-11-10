package com.example.adisan_app_android;

import static com.example.adisan_app_android.DatosPrueba.DELTA;
import static com.example.adisan_app_android.DatosPrueba.oferta;
import static com.example.adisan_app_android.DatosPrueba.producto;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Pruebas de carga y estrés PC-01 a PC-03 sobre la lógica que se ejecuta en el teléfono
 * (carrito en memoria, validaciones y reglas de estado). No incluyen Firestore: las pruebas de carga
 * contra la base de datos requieren Firebase Local Emulator Suite para no consumir la cuota del plan Spark.
 * Los tiempos medidos se escriben en la salida estándar del reporte de pruebas.
 */
public class CargaEstresTest {

    private CarritoManager carrito;

    @Before
    public void setUp() {
        carrito = CarritoManager.getInstance();
        carrito.limpiar();
    }

    private static long ms(long inicioNanos) {
        return (System.nanoTime() - inicioNanos) / 1_000_000;
    }

    @Test
    public void pc01_cargaCarritoConDiezMilProductos() {
        int n = 10_000;
        long inicio = System.nanoTime();
        for (int i = 1; i <= n; i++) carrito.agregarProducto(producto(i), "PAQUETE", 1, 0);
        double total = carrito.obtenerTotal();
        long tiempo = ms(inicio);
        System.out.println("PC-01 | productos=" + n + " | tiempo_ms=" + tiempo + " | total=" + total);

        assertEquals(n, carrito.getItems().size());
        assertEquals(n * 24.00, total, DELTA);
        assertTrue("Agregar 10 000 productos debe tardar menos de 3 s (tardó " + tiempo + " ms)", tiempo < 3000);
    }

    @Test
    public void pc02_estresOperacionesMixtasDelCarrito() {
        int operaciones = 100_000;
        long inicio = System.nanoTime();
        for (int i = 0; i < operaciones; i++) {
            int productoId = (i % 50) + 1;
            switch (i % 4) {
                case 0: carrito.agregarProducto(producto(productoId), "PAQUETE", 1, 0); break;
                case 1: carrito.agregarProducto(producto(productoId), "UNIDAD", 0, 2); break;
                case 2: carrito.agregarOferta(oferta(900 + (i % 5), "15.90"), 1); break;
                default: if (carrito.getItems().size() > 80) carrito.eliminarItem(0); break;
            }
        }
        long tiempo = ms(inicio);
        double suma = 0;
        for (ItemCarrito item : carrito.getItems()) {
            assertTrue(item.getCantidadPaquetes() >= 0 && item.getCantidadUnidades() >= 0 && item.getCantidadOfertas() >= 0);
            suma += item.getSubtotal();
        }
        System.out.println("PC-02 | operaciones=" + operaciones + " | items_finales=" + carrito.getItems().size()
                + " | tiempo_ms=" + tiempo);

        assertEquals(suma, carrito.obtenerTotal(), DELTA);
        assertTrue("100 000 operaciones deben tardar menos de 3 s (tardó " + tiempo + " ms)", tiempo < 3000);
    }

    @Test
    public void pc03_estresValidacionesYEstados() {
        int repeticiones = 100_000;
        String[] estados = TransicionesPedido.ESTADOS.toArray(new String[0]);
        int permitidas = 0;
        long inicio = System.nanoTime();
        for (int i = 0; i < repeticiones; i++) {
            Validaciones.dniValido(i % 2 == 0 ? "70000001" : "7000000A");
            Validaciones.correoValido("cliente" + (i % 100) + "@adisan.pe");
            Validaciones.passwordValida("clave" + i);
            if (TransicionesPedido.puedeCambiar(estados[i % 5], estados[(i / 5) % 5])) permitidas++;
        }
        long tiempo = ms(inicio);
        System.out.println("PC-03 | repeticiones=" + repeticiones + " | transiciones_permitidas=" + permitidas
                + " | tiempo_ms=" + tiempo);

        // De las 25 combinaciones de estados, 9 están permitidas (6 transiciones y 3 repeticiones no finales)
        assertEquals(repeticiones / 25 * 9, permitidas);
        assertTrue("100 000 validaciones deben tardar menos de 3 s (tardó " + tiempo + " ms)", tiempo < 3000);
    }
}
