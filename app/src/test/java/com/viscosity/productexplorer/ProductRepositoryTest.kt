package com.viscosity.productexplorer

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ProductRepositoryTest {

    @Test
    fun getProducts_returns_products_from_api() = runBlocking {
        val fakeApi = FakeProductApi()
        val repository = ProductRepository(fakeApi)

        val result = repository.getProducts(
            limit = 10, skip = 0
        )

        assertEquals(1, result.products.size)
        assertEquals("Test Product", result.products[0].title)
    }
}

private class FakeProductApi : ProductApi {

    override suspend fun getProducts(
        limit: Int, skip: Int
    ): ProductResponse {
        return ProductResponse(
            products = listOf(
                Product(
                    id = 1,
                    title = "Test Product",
                    description = "Testing product",
                    price = 100.0,
                    thumbnail = "test-image"
                )
            ), total = 1, skip = skip, limit = limit
        )
    }

    override suspend fun getProduct(
        productId: Int
    ): Product {
        return Product(
            id = productId,
            title = "Test Product",
            description = "Testing product",
            price = 100.0,
            thumbnail = "test-image"
        )
    }
}
