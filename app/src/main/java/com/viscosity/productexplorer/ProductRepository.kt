package com.viscosity.productexplorer

class ProductRepository(
    private val productApi: ProductApi = RetrofitInstance.api
) {
    suspend fun getProducts(
        limit: Int,
        skip: Int
    ): ProductResponse {
        return productApi.getProducts(
            limit = limit,
            skip = skip
        )
    }

    suspend fun getProduct(productId: Int): Product {
        return productApi.getProduct(productId)
    }
}
