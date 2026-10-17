package com.example.breakapp.ui.home

import androidx.lifecycle.ViewModel
import com.example.breakapp.model.Tienda
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class HomeUiState(
    val tiendas: List<Tienda> = emptyList(),
    val isLoading: Boolean = false
)

class HomeViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        cargarTiendas()
    }

    fun cargarTiendas() {
        _uiState.update { it.copy(isLoading = true) }

        // Asegúrate de que tu colección en Firebase se llame "Tiendas" exactamente así
        db.collection("Tiendas")
            .get()
            .addOnSuccessListener { result ->
                val listaTiendas = result.toObjects(Tienda::class.java)
                _uiState.update {
                    it.copy(
                        tiendas = listaTiendas,
                        isLoading = false
                    )
                }
            }
            .addOnFailureListener {
                _uiState.update { it.copy(isLoading = false) }
            }
    }
}