package com.viscosity.productexplorer

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.viscosity.productexplorer.ui.theme.ProductExplorerTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ProductCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun productCard_displaysProductDetails() {
        val product = Product(
            id = 1,
            title = "Test Product",
            description = "Testing product",
            price = 100.0,
            thumbnail = "test-image"
        )

        composeTestRule.setContent {
            ProductExplorerTheme {
                ProductCard(
                    product = product, onClick = {})
            }
        }

        composeTestRule.onNodeWithText("Test Product").assertIsDisplayed()

        composeTestRule.onNodeWithText(
            text = "Price:", substring = true
        ).assertIsDisplayed()

    }

    @Test
    fun productCard_clickTriggersCallback() {
        var wasClicked = false

        val product = Product(
            id = 1,
            title = "Test Product",
            description = "Testing product",
            price = 100.0,
            thumbnail = "test-image"
        )

        composeTestRule.setContent {
            ProductExplorerTheme {
                ProductCard(
                    product = product, onClick = {
                        wasClicked = true
                    })
            }
        }

        composeTestRule.onNode(hasClickAction()).performClick()

        assertTrue(wasClicked)
    }

}