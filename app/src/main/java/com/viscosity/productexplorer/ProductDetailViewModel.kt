package com.viscosity.productexplorer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

data class ProductDetailUiState(
    val isLoading: Boolean = false,
    val product: Product? = null,
    val errorMessage: String? = null
)

class ProductDetailViewModel : ViewModel() {

    private val repository = ProductRepository()

    var uiState by mutableStateOf(ProductDetailUiState())
        private set

    private var loadedProductId: Int? = null

    fun loadProduct(productId: Int) {
        if (loadedProductId == productId && uiState.product != null) {
            return
        }

        loadedProductId = productId

        viewModelScope.launch {
            uiState = ProductDetailUiState(isLoading = true)

            try {
                val product = repository.getProduct(productId)

                uiState = ProductDetailUiState(
                    product = product
                )
            } catch (exception: Exception) {
                uiState = ProductDetailUiState(
                    errorMessage = "Unable to load product details."
                )
            }
        }
    }
}

