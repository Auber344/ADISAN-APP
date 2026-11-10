package com.example.adisan_app_android;

/**
 * Datos de prueba compartidos por las pruebas unitarias.
 * Producto base: paquete S/ 24.00, unidad S/ 2.50, 12 unidades por paquete y 100 unidades en stock.
 */
final class DatosPrueba {

    static final double DELTA = 0.001;

    private DatosPrueba() {
    }

    static Producto producto(int id) {
        Producto p = new Producto();
        p.setProductoId(id);
        p.setNombre("Gaseosa 500 ml");
        p.setUnidadesPorPaquete(12);
        p.setPrecioVentaPaquete("24.00");
        p.setPrecioVentaUnidad("2.50");
        p.setStockUnidades(100);
        return p;
    }

    static Oferta oferta(int id, String precioFinal) {
        Oferta o = new Oferta();
        o.setId(id);
        o.setNombre("Combo");
        o.setPrecioFinal(precioFinal);
        return o;
    }
}
