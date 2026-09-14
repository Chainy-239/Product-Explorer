package com.viscosity.productexplorer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadProducts_updatesUiStateWithProducts() = runTest {
        val fakeApi = FakeProductApiForViewModelTest()
        val repository = ProductRepository(fakeApi)
        val viewModel = ProductViewModel(repository)

        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.products.size)
        assertEquals(
            "Test Product", viewModel.uiState.products[0].title
        )
        assertEquals(
            false, viewModel.uiState.isLoading
        )
    }

    @Test
    fun loadProducts_whenApiFails_updatesErrorState() = runTest {
        val fakeApi = FailingProductApi()
        val repository = ProductRepository(fakeApi)
        val viewModel = ProductViewModel(repository)

        advanceUntilIdle()

        assertEquals(
            "Something went wrong. Check your internet.", viewModel.uiState.errorMessage
        )

        assertEquals(
            false, viewModel.uiState.isLoading
        )
    }


}

private class FakeProductApiForViewModelTest : ProductApi {

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

private class FailingProductApi : ProductApi {

    override suspend fun getProducts(
        limit: Int, skip: Int
    ): ProductResponse {
        throw RuntimeException("Network failed")
    }

    override suspend fun getProduct(
        productId: Int
    ): Product {
        throw RuntimeException("Network failed")
    }
}


