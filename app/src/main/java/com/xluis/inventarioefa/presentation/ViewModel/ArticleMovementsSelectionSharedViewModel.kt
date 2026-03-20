package com.xluis.inventarioefa.presentation.ViewModel

import androidx.lifecycle.ViewModel
import com.xluis.inventarioefa._domain.model.DataClass.ArticleMovement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ArticleMovementsSelectionSharedViewModel : ViewModel() {

    private val _selectedMovementsByScreen =
        MutableStateFlow<Map<String, List<ArticleMovement>>>(emptyMap())

    val selectedMovementsByScreen: StateFlow<Map<String, List<ArticleMovement>>> =
        _selectedMovementsByScreen

    /**
     * Añade movimientos a una pantalla.
     * Si existe un movimiento con mismo articleId, zoneId y actionType,
     * se acumula la cantidad.
     */
    fun addMovements(screenId: String, newMovements: List<ArticleMovement>) {
        _selectedMovementsByScreen.value =
            _selectedMovementsByScreen.value.toMutableMap().apply {

                val currentMovements = get(screenId).orEmpty().toMutableList()

                newMovements.forEach { newMovement ->
                    val index = currentMovements.indexOfFirst {
                        it.articleId == newMovement.articleId &&
                                it.zoneId == newMovement.zoneId &&
                                it.actionType == newMovement.actionType
                    }

                    if (index != -1) {
                        val existing = currentMovements[index]
                        currentMovements[index] =
                            existing.copy(count = existing.count + newMovement.count)
                    } else {
                        currentMovements.add(newMovement)
                    }
                }

                put(screenId, currentMovements)
            }
    }

    /**
     * Obtiene los movimientos seleccionados de una pantalla
     */
    fun getSelectedMovements(screenId: String): List<ArticleMovement> =
        _selectedMovementsByScreen.value[screenId] ?: emptyList()

    /**
     * Elimina todos los movimientos de una pantalla
     */
    fun clearMovementsByScreenId(screenId: String) {
        _selectedMovementsByScreen.value =
            _selectedMovementsByScreen.value.toMutableMap().apply {
                remove(screenId)
            }
    }

    /**
     * Elimina un movimiento concreto por su id
     */
    fun removeMovementById(screenId: String, movementId: String) {
        _selectedMovementsByScreen.value =
            _selectedMovementsByScreen.value.toMutableMap().apply {
                val currentMovements = get(screenId).orEmpty()
                put(
                    screenId,
                    currentMovements.filter { it.id != movementId }
                )
            }
    }

    /**
     * Elimina todos los movimientos asociados a un artículo
     */
    fun removeMovementsByArticleId(screenId: String, articleId: String) {
        _selectedMovementsByScreen.value =
            _selectedMovementsByScreen.value.toMutableMap().apply {
                val currentMovements = get(screenId).orEmpty()
                put(
                    screenId,
                    currentMovements.filter { it.articleId != articleId }
                )
            }
    }

    fun updateMovementCount(screenId: String, movementId: String, newCount: Int) {
        _selectedMovementsByScreen.value =
            _selectedMovementsByScreen.value.toMutableMap().apply {
                val currentMovements = get(screenId).orEmpty().toMutableList()
                val index = currentMovements.indexOfFirst { it.id == movementId }

                if (index != -1) {
                    if (newCount > 0) {
                        // Actualiza el count
                        val existing = currentMovements[index]
                        currentMovements[index] = existing.copy(count = newCount)
                    } else {
                        // Si el count es 0 o negativo, eliminamos el movimiento
                        currentMovements.removeAt(index)
                    }
                }

                put(screenId, currentMovements)
            }
    }


    /**
     * Devuelve el total de unidades movidas en una pantalla
     */
    fun getTotalCountByScreen(screenId: String): Int =
        getSelectedMovements(screenId).sumOf { it.count }
}
