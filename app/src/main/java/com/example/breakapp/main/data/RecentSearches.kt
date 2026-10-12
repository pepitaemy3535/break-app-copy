package com.example.breakapp.main.data

import androidx.compose.runtime.mutableStateListOf

/**
 * Búsquedas recientes. Viven solo mientras la app está abierta (en memoria).
 * Si más adelante las quieres guardar en el teléfono, cambia solo este archivo (DataStore).
 */
object RecentSearches {
    private const val MAX_ITEMS = 5

    /** Valores iniciales iguales al diseño de Figma. Déjala vacía si prefieres empezar sin recientes. */
    val items = mutableStateListOf("Jugo hit", "Chocoramo", "Chocolatina")

    fun add(term: String) {
        val clean = term.trim()
        if (clean.isEmpty()) return
        items.removeAll { it.equals(clean, ignoreCase = true) }
        items.add(0, clean)
        while (items.size > MAX_ITEMS) items.removeAt(items.lastIndex)
    }
}
