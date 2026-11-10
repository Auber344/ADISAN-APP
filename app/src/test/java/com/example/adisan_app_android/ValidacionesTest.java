package com.example.adisan_app_android;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Pruebas unitarias PU-46 a PU-52: validaciones compartidas de los formularios (REF-03, BUG-05).
 * Usa partición de equivalencia y valores límite.
 */
public class ValidacionesTest {

    @Test
    public void pu46_dniDeOchoDigitos() {
        assertTrue(Validaciones.dniValido("70000001"));
        assertFalse(Validaciones.dniValido("7000000"));     // 7 dígitos
        assertFalse(Validaciones.dniValido("700000012"));   // 9 dígitos
        assertFalse(Validaciones.dniValido("7000000A"));    // antes pasaba en el formulario del administrador
        assertFalse(Validaciones.dniValido(null));
    }

    @Test
    public void pu47_rucDeOnceDigitos() {
        assertTrue(Validaciones.rucValido("20123456789"));
        assertFalse(Validaciones.rucValido("2012345678"));    // 10 dígitos
        assertFalse(Validaciones.rucValido("201234567890"));  // 12 dígitos
        assertFalse(Validaciones.rucValido("2012345678X"));
    }

    @Test
    public void pu48_telefonoDeSieteAQuinceDigitos() {
        assertTrue(Validaciones.telefonoValido("987654321"));
        assertTrue(Validaciones.telefonoValido("+51 987-654-321"));
        assertTrue(Validaciones.telefonoValido("0761234"));     // 7 dígitos
        assertFalse(Validaciones.telefonoValido("076123"));     // 6 dígitos
        assertFalse(Validaciones.telefonoValido("98765432a"));
    }

    @Test
    public void pu49_correoConFormatoValido() {
        assertTrue(Validaciones.correoValido("cliente@adisan.pe"));
        assertFalse(Validaciones.correoValido("ana@correo"));    // sin dominio, igual que PF-03
        assertFalse(Validaciones.correoValido("anacorreo.pe"));
        assertFalse(Validaciones.correoValido(""));
    }

    @Test
    public void pu50_contrasenaMinimaDeOchoParaTodos() {
        assertEquals(8, Validaciones.MIN_PASSWORD);
        assertTrue(Validaciones.passwordValida("12345678"));
        assertFalse(Validaciones.passwordValida("1234567"));   // 7 caracteres: antes el autorregistro aceptaba 6
        assertFalse(Validaciones.passwordValida(null));
    }

    @Test
    public void pu51_usuarioDeAlMenosTresCaracteres() {
        assertTrue(Validaciones.usuarioValido("ana"));
        assertFalse(Validaciones.usuarioValido("an"));
        assertFalse(Validaciones.usuarioValido("  a "));
    }

    @Test
    public void pu52_mensajeDeContrasenaUsaLaConstante() {
        assertEquals("La contraseña debe tener al menos 8 caracteres", Validaciones.mensajePasswordCorta());
    }
}
