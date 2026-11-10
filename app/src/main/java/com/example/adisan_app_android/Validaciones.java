package com.example.adisan_app_android;

import java.util.regex.Pattern;

/**
 * Validaciones de formularios en un solo lugar. REF-03, corrige BUG-05.
 * Antes estaban repetidas, con reglas distintas, en DashboardActivity, RegistroActivity,
 * LoginActivity y FirebaseApiService. Es Java puro para poder probarla con JUnit.
 */
public final class Validaciones {

    /** Longitud mínima de contraseña para todos los formularios (RN, informe del proyecto). */
    public static final int MIN_PASSWORD = 8;
    public static final int MIN_USUARIO = 3;

    private static final Pattern CORREO = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private Validaciones() {
    }

    /** DNI peruano: exactamente 8 dígitos. */
    public static boolean dniValido(String dni) {
        return dni != null && dni.trim().matches("\\d{8}");
    }

    /** RUC peruano: exactamente 11 dígitos. */
    public static boolean rucValido(String ruc) {
        return ruc != null && ruc.trim().matches("\\d{11}");
    }

    /** Teléfono de 7 a 15 dígitos; se aceptan espacios, guiones y el signo +. */
    public static boolean telefonoValido(String telefono) {
        return telefono != null && telefono.replaceAll("[\\s+-]", "").matches("\\d{7,15}");
    }

    public static boolean correoValido(String correo) {
        return correo != null && CORREO.matcher(correo.trim()).matches();
    }

    public static boolean passwordValida(String password) {
        return password != null && password.length() >= MIN_PASSWORD;
    }

    public static boolean usuarioValido(String usuario) {
        return usuario != null && usuario.trim().length() >= MIN_USUARIO;
    }

    public static String mensajePasswordCorta() {
        return "La contraseña debe tener al menos " + MIN_PASSWORD + " caracteres";
    }
}
