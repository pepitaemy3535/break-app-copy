package com.example.breakapp.main.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Gestor del inventario/stock de los alimentos.
 *
 * Simula un stock inicial de 30 unidades para cada producto.
 * Guarda en SharedPreferences las unidades restantes para que persistan
 * cada vez que un usuario realiza una compra ("se van restando").
 */
object StockRepository {

    const val DEFAULT_STOCK = 30
    private const val PREFS_NAME = "break_app_stock_prefs"
    private const val PREF_PREFIX = "stock_"

    private var prefs: SharedPreferences? = null

    private val _stockMap = MutableStateFlow<Map<String, Int>>(emptyMap())
    val stockMap: StateFlow<Map<String, Int>> = _stockMap.asStateFlow()

    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            loadSavedStock()
        }
    }

    private fun loadSavedStock() {
        val sp = prefs ?: return
        val current = mutableMapOf<String, Int>()
        for ((key, value) in sp.all) {
            if (key.startsWith(PREF_PREFIX) && value is Int) {
                val productId = key.removePrefix(PREF_PREFIX)
                current[productId] = value
            }
        }
        _stockMap.value = current
    }

    /**
     * Retorna el stock actual disponible de un producto.
     * Si no se ha modificado nunca, retorna 30 por defecto.
     */
    fun getStock(productId: String): Int {
        _stockMap.value[productId]?.let { return it }
        val sp = prefs
        if (sp != null) {
            val stock = sp.getInt(PREF_PREFIX + productId, DEFAULT_STOCK)
            _stockMap.update { it + (productId to stock) }
            return stock
        }
        return DEFAULT_STOCK
    }

    /**
     * Valida si se puede agregar la cantidad deseada según el stock disponible.
     */
    fun canAdd(productId: String, currentQtyInCart: Int): Boolean {
        val available = getStock(productId)
        return currentQtyInCart < available
    }

    /**
     * Descuenta del stock los productos comprados y persiste los cambios.
     */
    fun decrementStock(items: List<CartItem>) {
        val sp = prefs
        val editor = sp?.edit()
        val current = _stockMap.value.toMutableMap()

        for (item in items) {
            val currentStock = getStock(item.product.id)
            val newStock = (currentStock - item.quantity).coerceAtLeast(0)
            editor?.putInt(PREF_PREFIX + item.product.id, newStock)
            current[item.product.id] = newStock
        }

        editor?.apply()
        _stockMap.value = current
    }

    /**
     * Permite restablecer el stock a 30 (útil para pruebas o reseteo).
     */
    fun resetStock(productId: String? = null) {
        val sp = prefs
        val editor = sp?.edit()
        if (productId != null) {
            editor?.putInt(PREF_PREFIX + productId, DEFAULT_STOCK)
            _stockMap.update { it + (productId to DEFAULT_STOCK) }
        } else {
            editor?.clear()
            _stockMap.value = emptyMap()
        }
        editor?.apply()
    }
}
