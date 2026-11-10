package com.example.adisan_app_android;

import static com.example.adisan_app_android.DatosPrueba.DELTA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.google.gson.Gson;

import org.junit.Test;

/** Pruebas unitarias PU-27 a PU-37 del catálogo de pruebas: Producto, Oferta, Pedido y ApiClient.gson(). */
public class ModelosTest {

    private final Gson gson = ApiClient.gson();

    // ---------- Producto ----------

    @Test
    public void pu27_idUsaProductoIdCuandoIdEsCero() {
        Producto p = new Producto();
        p.setId(0);
        p.setProductoId(7);
        assertEquals(7, p.getId());
        assertEquals(7, p.getProductoId());
    }

    @Test
    public void pu28_nombresVaciosMuestranTextoPorDefecto() {
        Producto sinDatos = new Producto();
        assertEquals("Sin categoría", sinDatos.getCategoriaNombre());
        assertEquals("Sin proveedor", sinDatos.getProveedorNombre());

        Producto vacios = gson.fromJson("{\"categoria_nombre\":\"\",\"proveedor_nombre\":\"\"}", Producto.class);
        assertEquals("Sin categoría", vacios.getCategoriaNombre());
        assertEquals("Sin proveedor", vacios.getProveedorNombre());
    }

    @Test
    public void pu29_unidadesPorPaqueteCeroSeTomaComoUno() {
        Producto p = new Producto();
        p.setUnidadesPorPaquete(0);
        assertEquals(1, p.getUnidadesPorPaqueteVal());
    }

    @Test
    public void pu30_precioNoNumericoDevuelveCero() {
        Producto p = new Producto();
        p.setPrecioVentaPaquete("abc");
        assertEquals(0.0, p.getPrecioVentaDouble(), DELTA);
    }

    // ---------- Oferta ----------

    @Test
    public void pu31_activoAceptaDistintosFormatos() {
        assertEquals(1, gson.fromJson("{\"activo\":true}", Oferta.class).getActivo());
        assertEquals(0, gson.fromJson("{\"activo\":false}", Oferta.class).getActivo());
        assertEquals(0, gson.fromJson("{\"activo\":0}", Oferta.class).getActivo());
        assertEquals(1, gson.fromJson("{\"activo\":\"1\"}", Oferta.class).getActivo());
        assertEquals(1, gson.fromJson("{\"activo\":null}", Oferta.class).getActivo());
        assertEquals(1, gson.fromJson("{\"activo\":\"abc\"}", Oferta.class).getActivo());
    }

    @Test
    public void pu32_precioFinalInvalidoDevuelveCero() {
        Oferta o = new Oferta();
        o.setPrecioFinal("15.90");
        assertEquals(15.9, o.getPrecioFinalDouble(), DELTA);
        o.setPrecioFinal(null);
        assertEquals(0.0, o.getPrecioFinalDouble(), DELTA);
        o.setPrecioFinal("abc");
        assertEquals(0.0, o.getPrecioFinalDouble(), DELTA);
    }

    @Test
    public void pu33_descuentoNuloDevuelveCero() {
        Oferta o = new Oferta();
        o.setDescuento(null);
        assertEquals("0.00", o.getDescuento());
    }

    // ---------- Pedido ----------

    @Test
    public void pu34_clienteDeVentaDirecta() {
        Pedido sinCliente = new Pedido();
        sinCliente.setClienteId(null);
        sinCliente.setCliente("Ana Ruiz");
        assertEquals("Venta directa", sinCliente.getCliente());

        Pedido mostrador = new Pedido();
        mostrador.setClienteId(5);
        mostrador.setCliente("Venta Mostrador");
        assertEquals("Venta directa", mostrador.getCliente());

        Pedido conCliente = new Pedido();
        conCliente.setClienteId(5);
        conCliente.setCliente("Ana Ruiz");
        assertEquals("Ana Ruiz", conCliente.getCliente());
    }

    @Test
    public void pu35_totalNuloOInvalido() {
        Pedido p = new Pedido();
        p.setTotal(null);
        assertEquals("0.00", p.getTotal());
        p.setTotal("abc");
        assertEquals(0.0, p.getTotalDouble(), DELTA);
    }

    // ---------- ApiClient.gson() ----------

    @Test
    public void pu36_numeroEnCampoDeTextoSeFormateaConDosDecimales() {
        Producto p = gson.fromJson("{\"precio_venta_paquete\":12.5}", Producto.class);
        assertEquals("12.50", p.getPrecioVentaPaquete());
    }

    @Test
    public void pu37_booleanoYNuloEnCampoDeTexto() {
        assertEquals("true", gson.fromJson("true", String.class));
        assertNull(gson.fromJson("null", String.class));
    }
}
