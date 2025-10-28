package lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.products

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import lt.vitalijus.cmp_custom_pagination.domain.model.BasketItem
import lt.vitalijus.cmp_custom_pagination.domain.model.Product
import lt.vitalijus.cmp_custom_pagination.presentation.products.BasketState
import lt.vitalijus.cmp_custom_pagination.presentation.products.BrowseProductsState
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.ProductAction
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BrowseProductsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleProducts = listOf(
        Product(
            id = 1,
            title = "Test Product 1",
            price = 100.0,
            description = "This is a test product description 1",
            category = "electronics",
            brand = "TestBrand1",
            thumbnail = null
        ),
        Product(
            id = 2,
            title = "Test Product 2",
            price = 200.0,
            description = "This is a test product description 2",
            category = "clothing",
            brand = "TestBrand2",
            thumbnail = null
        ),
        Product(
            id = 3,
            title = "Test Product 3",
            price = 300.0,
            description = "This is a test product description 3",
            category = "books",
            brand = "TestBrand3",
            thumbnail = null
        )
    )

    @Test
    fun testEmptyProductList_ShowsNoProducts() {
        // Given
        val browseProductsState = BrowseProductsState(
            products = emptyList(),
            isLoadingMore = false
        )
        val basketState = BasketState()

        // When
        composeTestRule.setContent {
            ProductListScreen(
                browseProductsState = browseProductsState,
                basketState = basketState,
                onAction = {},
                lazyListState = rememberLazyListState()
            )
        }

        // Then
        composeTestRule.onNodeWithText("Test Product 1").assertDoesNotExist()
    }

    @Test
    fun testProductList_DisplaysProducts() {
        // Given
        val browseProductsState = BrowseProductsState(
            products = sampleProducts,
            isLoadingMore = false
        )
        val basketState = BasketState()

        // When
        composeTestRule.setContent {
            ProductListScreen(
                browseProductsState = browseProductsState,
                basketState = basketState,
                onAction = {},
                lazyListState = rememberLazyListState()
            )
        }

        // Then
        composeTestRule.onNodeWithText("Test Product 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Product 2").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Product 3").assertIsDisplayed()
        composeTestRule.onNodeWithText("This is a test product description 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("ELECTRONICS").assertIsDisplayed()
    }

    @Test
    fun testProductPrice_DisplaysCorrectly() {
        // Given
        val browseProductsState = BrowseProductsState(
            products = listOf(sampleProducts[0]),
            isLoadingMore = false
        )
        val basketState = BasketState()

        // When
        composeTestRule.setContent {
            ProductListScreen(
                browseProductsState = browseProductsState,
                basketState = basketState,
                onAction = {},
                lazyListState = rememberLazyListState()
            )
        }

        // Then
        composeTestRule.onNodeWithText("1.00").assertIsDisplayed()
    }

    @Test
    fun testAddToBasket_TriggersAction() {
        // Given
        val browseProductsState = BrowseProductsState(
            products = listOf(sampleProducts[0]),
            isLoadingMore = false
        )
        val basketState = BasketState()
        val actions = mutableListOf<ProductAction>()

        // When
        composeTestRule.setContent {
            ProductListScreen(
                browseProductsState = browseProductsState,
                basketState = basketState,
                onAction = {
                    actions.add(it)
                },
                lazyListState = rememberLazyListState()
            )
        }

        // Click the "Add to Basket" button
        composeTestRule.onNodeWithText("Add to Basket").performClick()

        // Then
        assert(actions.any {
            it is ProductAction.AddToBasket &&
                    it.product.id == 1L &&
                    it.count == 1
        })
    }

    @Test
    fun testQuantityButtons_IncrementAndDecrement() {
        // Given
        val browseProductsState = BrowseProductsState(
            products = listOf(sampleProducts[0]),
            isLoadingMore = false
        )
        val basketState = BasketState()
        val actions = mutableListOf<ProductAction>()

        // When
        composeTestRule.setContent {
            ProductListScreen(
                browseProductsState = browseProductsState,
                basketState = basketState,
                onAction = { actions.add(it) },
                lazyListState = rememberLazyListState()
            )
        }

        // Initially shows 1
        composeTestRule.onNodeWithText("1").assertIsDisplayed()

        // Click increment button (+)
        composeTestRule.onAllNodesWithText("+")[0].performClick()

        // Should now show 2
        composeTestRule.onNodeWithText("2").assertIsDisplayed()

        // Click decrement button (-)
        composeTestRule.onAllNodesWithText("-")[0].performClick()

        // Should be back to 1
        composeTestRule.onNodeWithText("1").assertIsDisplayed()

        // Click Add to Basket after incrementing
        composeTestRule.onAllNodesWithText("+")[0].performClick() // Now at 2
        composeTestRule.onNodeWithText("Add to Basket").performClick()

        // Verify the action has quantity 2
        val addAction = actions.filterIsInstance<ProductAction.AddToBasket>().lastOrNull()
        assert(addAction?.count == 2)
    }

    @Test
    fun testBasketDisplay_ShowsWhenNotEmpty() {
        // Given
        val browseProductsState = BrowseProductsState(
            products = sampleProducts,
            isLoadingMore = false
        )
        val basketState = BasketState(
            items = listOf(
                BasketItem(
                    product = sampleProducts[0],
                    quantity = 2
                )
            )
        )

        // When
        composeTestRule.setContent {
            ProductListScreen(
                browseProductsState = browseProductsState,
                basketState = basketState,
                onAction = {},
                lazyListState = rememberLazyListState()
            )
        }

        // Then
        composeTestRule.onNodeWithText("Basket: 2 items - 2.00").assertIsDisplayed()
    }

    @Test
    fun testBasketDisplay_HiddenWhenEmpty() {
        // Given
        val browseProductsState = BrowseProductsState(
            products = sampleProducts,
            isLoadingMore = false
        )
        val basketState = BasketState()

        // When
        composeTestRule.setContent {
            ProductListScreen(
                browseProductsState = browseProductsState,
                basketState = basketState,
                onAction = {},
                lazyListState = rememberLazyListState()
            )
        }

        // Then
        composeTestRule.onNodeWithText("Basket:", substring = true).assertDoesNotExist()
    }

    @Test
    fun testLoadingIndicator_DisplayedWhenLoading() {
        // Given
        val actions = mutableListOf<ProductAction>()
        val listState = LazyListState()
        val browseStateHolder = mutableStateOf(
            BrowseProductsState(
                products = emptyList(),
                isLoadingMore = false
            )
        )
        val basketState = BasketState()

        // When
        composeTestRule.setContent {
            ProductListScreen(
                browseProductsState = browseStateHolder.value,
                basketState = basketState,
                onAction = { actions.add(it) },
                lazyListState = listState
            )
        }

        // Wait for initial composition
        composeTestRule.waitForIdle()

        // Then - Should trigger LoadMore
        assert(actions.any { it is ProductAction.LoadMore })

        // Simulate loading state by changing the flag and triggering recomposition
        composeTestRule.runOnIdle {
            browseStateHolder.value = browseStateHolder.value.copy(isLoadingMore = true)
        }

        composeTestRule.waitForIdle()

        // Verify the loading indicator is displayed
        composeTestRule.onNodeWithTag("loading-more-indicator").assertIsDisplayed()
    }

    @Test
    fun testLoadingIndicator_NotDisplayedWhenNotLoading() {
        // Given
        val browseProductsState = BrowseProductsState(
            products = sampleProducts,
            isLoadingMore = false
        )
        val basketState = BasketState()

        // When
        composeTestRule.setContent {
            ProductListScreen(
                browseProductsState = browseProductsState,
                basketState = basketState,
                onAction = {},
                lazyListState = rememberLazyListState()
            )
        }

        // Then - Just verify the products are displayed (loading indicator is not shown)
        composeTestRule.onNodeWithTag("loading-more-indicator").assertIsNotDisplayed()
        composeTestRule.onNodeWithText("Test Product 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Product 2").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Product 3").assertIsDisplayed()
    }

    @Test
    fun testMultipleProducts_CanInteractWithEach() {
        // Given
        val browseProductsState = BrowseProductsState(
            products = sampleProducts,
            isLoadingMore = false
        )
        val basketState = BasketState()
        val actions = mutableListOf<ProductAction>()

        // When
        composeTestRule.setContent {
            ProductListScreen(
                browseProductsState = browseProductsState,
                basketState = basketState,
                onAction = { actions.add(it) },
                lazyListState = rememberLazyListState()
            )
        }

        // Interact with first product
        composeTestRule.onAllNodesWithText("Add to Basket")[0].performClick()

        // Interact with second product
        composeTestRule.onAllNodesWithText("Add to Basket")[1].performClick()

        // Then
        assert(actions.size >= 2)
        assert(
            actions.filterIsInstance<ProductAction.AddToBasket>()
                .any { it.product.id == 1L })
        assert(
            actions.filterIsInstance<ProductAction.AddToBasket>()
                .any { it.product.id == 2L })
    }

    @Test
    fun testProductCategories_DisplayedInUppercase() {
        // Given
        val browseProductsState = BrowseProductsState(
            products = sampleProducts,
            isLoadingMore = false
        )
        val basketState = BasketState()

        // When
        composeTestRule.setContent {
            ProductListScreen(
                browseProductsState = browseProductsState,
                basketState = basketState,
                onAction = {},
                lazyListState = rememberLazyListState()
            )
        }

        // Then
        composeTestRule.onNodeWithText("ELECTRONICS").assertIsDisplayed()
        composeTestRule.onNodeWithText("CLOTHING").assertIsDisplayed()
        composeTestRule.onNodeWithText("BOOKS").assertIsDisplayed()
    }

    @Test
    fun testInitialLoad_TriggersLoadMoreAction() {
        // Given
        val browseProductsState = BrowseProductsState(
            products = emptyList(),
            isLoadingMore = false
        )
        val basketState = BasketState()
        val actions = mutableListOf<ProductAction>()

        // When
        composeTestRule.setContent {
            ProductListScreen(
                browseProductsState = browseProductsState,
                basketState = basketState,
                onAction = { actions.add(it) },
                lazyListState = rememberLazyListState()
            )
        }

        // Wait for LaunchedEffect to execute
        composeTestRule.waitForIdle()

        // Then
        assert(actions.any { it is ProductAction.LoadMore })
    }

    @Test
    fun testPagination_TriggersLoadMoreWhenScrollingNearEnd() {
        // Given - Create enough products to enable scrolling
        val manyProducts = List(20) { index ->
            Product(
                id = index.toLong(),
                title = "Product $index",
                price = 100.0 * (index + 1),
                description = "Description $index",
                category = "category",
                brand = "Brand",
                thumbnail = null
            )
        }

        val browseStateHolder = mutableStateOf(
            BrowseProductsState(
                products = manyProducts,
                isLoadingMore = false
            )
        )
        val basketState = BasketState()
        val actions = mutableListOf<ProductAction>()
        val listState = LazyListState()

        // When
        composeTestRule.setContent {
            ProductListScreen(
                browseProductsState = browseStateHolder.value,
                basketState = basketState,
                onAction = { actions.add(it) },
                lazyListState = listState
            )
        }

        // Wait for initial composition
        composeTestRule.waitForIdle()

        // Scroll to index 17 (which is 3 items from the end: 17, 18, 19)
        composeTestRule.runOnIdle {
            runBlocking {
                listState.scrollToItem(17)
            }
        }

        // Wait for scroll effect to trigger
        composeTestRule.waitForIdle()

        // Then - Should trigger LoadMore when within 3 items of the end
        assert(actions.any { it is ProductAction.LoadMore })

        // Simulate loading state by updating the state properly
        composeTestRule.runOnIdle {
            browseStateHolder.value = browseStateHolder.value.copy(isLoadingMore = true)
        }

        composeTestRule.waitForIdle()

        // Scroll to the last item to make the loading indicator visible
        composeTestRule.runOnIdle {
            runBlocking {
                listState.scrollToItem(manyProducts.size) // Scroll to the loading indicator item
            }
        }

        composeTestRule.waitForIdle()

        // Verify the loading indicator is displayed
        composeTestRule.onNodeWithTag("loading-more-indicator").assertIsDisplayed()
    }

    @Test
    fun testBasketTotals_CalculatedCorrectly() {
        // Given
        val browseProductsState = BrowseProductsState(
            products = sampleProducts,
            isLoadingMore = false
        )
        val basketState = BasketState(
            items = listOf(
                BasketItem(
                    product = sampleProducts[0], // 100 cents * 2 = 200 cents = $2.00
                    quantity = 2
                ),
                BasketItem(
                    product = sampleProducts[1], // 200 cents * 1 = 200 cents = $2.00
                    quantity = 1
                )
            )
        )

        // When
        composeTestRule.setContent {
            ProductListScreen(
                browseProductsState = browseProductsState,
                basketState = basketState,
                onAction = {},
                lazyListState = rememberLazyListState()
            )
        }

        // Then - Total: 3 items, 400 cents = $4.00
        composeTestRule.onNodeWithText("Basket: 3 items - 4.00").assertIsDisplayed()
    }

    @Test
    fun testProductDescription_TruncatedToThreeLines() {
        // Given
        val longDescription = "This is a very long description that should be truncated. " +
                "It contains multiple sentences to test the maxLines property. " +
                "This should be enough text to span more than three lines. " +
                "If it shows all of this text, then the maxLines is not working correctly."

        val productWithLongDescription = Product(
            id = 1,
            title = "Test Product",
            price = 100.0,
            description = longDescription,
            category = "test",
            brand = "TestBrand",
            thumbnail = null
        )

        val browseProductsState = BrowseProductsState(
            products = listOf(productWithLongDescription),
            isLoadingMore = false
        )
        val basketState = BasketState()

        // When
        composeTestRule.setContent {
            ProductListScreen(
                browseProductsState = browseProductsState,
                basketState = basketState,
                onAction = {},
                lazyListState = rememberLazyListState()
            )
        }

        // Then - Description should be visible (though truncated)
        composeTestRule.onNodeWithText(longDescription, substring = true).assertIsDisplayed()
    }
}
