package com.viscosity.productexplorer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

data class ProductUiState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val total: Int = 0,
    val errorMessage: String? = null
)

class ProductViewModel : ViewModel() {

    private val repository = ProductRepository()

    private val pageSize = 10
    private var currentSkip = 0

    var uiState by mutableStateOf(ProductUiState())
        private set

    var isLoadingMore by mutableStateOf(false)
        private set

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            uiState = ProductUiState(isLoading = true)
            currentSkip = 0

            try {
                val response = repository.getProducts(
                    limit = pageSize,
                    skip = currentSkip
                )

                currentSkip += response.products.size

                uiState = ProductUiState(
                    products = response.products,
                    total = response.total
                )
            } catch (exception: Exception) {
                uiState = ProductUiState(
                    errorMessage = "Something went wrong. Check your internet."
                )
            }
        }
    }

    fun loadNextPage() {
        if (isLoadingMore) return
        if (uiState.products.size >= uiState.total) return

        viewModelScope.launch {
            isLoadingMore = true

            try {
                val response = repository.getProducts(
                    limit = pageSize,
                    skip = currentSkip
                )

                currentSkip += response.products.size

                uiState = uiState.copy(
                    products = uiState.products + response.products
                )
            } finally {
                isLoadingMore = false
            }
        }
    }

    fun retry() {
        loadProducts()
    }
}


