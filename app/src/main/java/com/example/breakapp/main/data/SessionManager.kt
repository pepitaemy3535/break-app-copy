package com.example.breakapp.main.data

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth

/**
 * Administra la persistencia de sesión local del usuario.
 * Permite recordar la sesión para entrar directamente a la pantalla principal
 * y limpiarla cuando el usuario cierra sesión desde su perfil.
 */
object SessionManager {
    private const val PREFS_NAME = "break_app_session"
    private const val KEY_RECORDAR_SESION = "key_recordar_sesion"
    private const val KEY_SESION_INICIADA = "key_sesion_iniciada"
    private const val KEY_ULTIMO_USUARIO = "key_ultimo_usuario"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Guarda el estado de la sesión activa y si el usuario eligió ser recordado.
     */
    fun guardarSesion(context: Context, recordar: Boolean, nombreUsuario: String? = null) {
        getPrefs(context).edit().apply {
            putBoolean(KEY_RECORDAR_SESION, recordar)
            putBoolean(KEY_SESION_INICIADA, true)
            if (!nombreUsuario.isNullOrBlank()) {
                putString(KEY_ULTIMO_USUARIO, nombreUsuario)
            }
            apply()
        }
    }

    /**
     * Retorna si el usuario tiene la opción de recordar sesión activa.
     */
    fun isRecordarSesionHabilitado(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_RECORDAR_SESION, true)
    }

    /**
     * Retorna el último usuario ingresado (si fue guardado).
     */
    fun getUltimoUsuario(context: Context): String {
        return getPrefs(context).getString(KEY_ULTIMO_USUARIO, "") ?: ""
    }

    /**
     * Determina si existe una sesión válida que permita entrar directo a la pantalla principal.
     */
    fun haySesionActiva(context: Context): Boolean {
        val recordar = isRecordarSesionHabilitado(context)
        val sesionIniciada = getPrefs(context).getBoolean(KEY_SESION_INICIADA, false)
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        return recordar && sesionIniciada && firebaseUser != null
    }

    /**
     * Cierra la sesión activa en Firebase Auth y borra las marcas de sesión recordada.
     * Al volver a ingresar, se requerirá obligatoriamente autenticarse con cuenta y contraseña.
     */
    fun cerrarSesion(context: Context) {
        getPrefs(context).edit().apply {
            putBoolean(KEY_SESION_INICIADA, false)
            putBoolean(KEY_RECORDAR_SESION, false)
            apply()
        }
        FirebaseAuth.getInstance().signOut()
    }
}
