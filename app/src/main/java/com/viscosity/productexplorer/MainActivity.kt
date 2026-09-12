package com.viscosity.productexplorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.viscosity.productexplorer.ui.theme.ProductExplorerTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ProductExplorerTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun ProductScreen(
    onProductClick: (Product) -> Unit,
    productViewModel: ProductViewModel = viewModel()
)
 {
    val state = productViewModel.uiState

    when {
        state.isLoading -> {
            LoadingScreen()
        }

        state.errorMessage != null -> {
            ErrorScreen(
                message = state.errorMessage, onRetry = {
                    productViewModel.retry()
                })
        }

        else -> {
            ProductList(
                products = state.products,
                onProductClick = onProductClick,
                onLoadMore = {
                    productViewModel.loadNextPage()
                },
                isLoadingMore = productViewModel.isLoadingMore
            )

        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(
    message: String, onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(message)

        Button(
            onClick = onRetry, modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Retry")
        }
    }
}

@Composable
fun ProductList(
    products: List<Product>,
    onProductClick: (Product) -> Unit,
    onLoadMore: () -> Unit,
    isLoadingMore: Boolean
) {
    var searchQuery by remember {
        mutableStateOf("")
    }

    val filteredProducts = products.filter { product ->
        product.title.contains(
            other = searchQuery, ignoreCase = true
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery, onValueChange = {
                searchQuery = it
            }, label = {
                Text("Search products")
            }, modifier = Modifier.fillMaxWidth(), singleLine = true
        )

        if (filteredProducts.isEmpty()) {
            Text(
                text = "No products found", modifier = Modifier.padding(top = 20.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f)
            ) {
                items(filteredProducts) { product ->
                    ProductCard(
                        product = product,
                        onClick = {
                            onProductClick(product)
                        }
                    )
                }

            }
            Button(
                onClick = onLoadMore,
                enabled = !isLoadingMore,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(
                    if (isLoadingMore) {
                        "Loading..."
                    } else {
                        "Load More"
                    }
                )
            }




        }
    }
}

@Composable
fun ProductCard(
    product: Product, onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable {
                onClick()
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            AsyncImage(
                model = product.thumbnail,
                contentDescription = product.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(100.dp)
            )

            Spacer(
                modifier = Modifier.size(12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.title
                )

                Text(
                    text = product.description, modifier = Modifier.padding(top = 8.dp)
                )

                Text(
                    text = "Price: \\(\\){product.price}", modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "products"
    ) {
        composable("products") {
            ProductScreen(
                onProductClick = { product ->
                    navController.navigate(
                        "product_detail/${product.id}"
                    )
                }
            )
        }

        composable(
            route = "product_detail/{productId}",
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val productId =
                backStackEntry.arguments?.getInt("productId") ?: -1

            ProductDetailScreen(
                productId = productId,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
@Composable
fun ProductDetailScreen(
    productId: Int,
    onBack: () -> Unit,
    productDetailViewModel: ProductDetailViewModel = viewModel()
) {
    LaunchedEffect(productId) {
        productDetailViewModel.loadProduct(productId)
    }

    val state = productDetailViewModel.uiState

    when {
        state.isLoading -> {
            LoadingScreen()
        }

        state.errorMessage != null -> {
            ErrorScreen(
                message = state.errorMessage,
                onRetry = {
                    productDetailViewModel.loadProduct(productId)
                }
            )
        }

        state.product != null -> {
            ProductDetailContent(
                product = state.product,
                onBack = onBack
            )
        }
    }
}
@Composable
fun ProductDetailContent(
    product: Product,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = onBack
        ) {
            Text("Go Back")
        }

        AsyncImage(
            model = product.thumbnail,
            contentDescription = product.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        )

        Text(
            text = product.title,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = "Price: \\(\\){product.price}",
            modifier = Modifier.padding(top = 8.dp)
        )

        Text(
            text = product.description,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}




