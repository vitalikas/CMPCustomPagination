@file:OptIn(ExperimentalCoroutinesApi::class)

package lt.vitalijus.cmp_custom_pagination.presentation.products

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import lt.vitalijus.cmp_custom_pagination.core.utils.pager.ProductPager
import lt.vitalijus.cmp_custom_pagination.domain.model.Product
import lt.vitalijus.cmp_custom_pagination.domain.paging.PagingEvent
import lt.vitalijus.cmp_custom_pagination.domain.paging.PagingStrategy
import lt.vitalijus.cmp_custom_pagination.domain.paging.ProductPagingFactory
import lt.vitalijus.cmp_custom_pagination.domain.usecase.basket.AddToBasketUseCase
import lt.vitalijus.cmp_custom_pagination.presentation.products.mvi.ProductsIntent
import lt.vitalijus.cmp_custom_pagination.presentation.products.mvi.ProductsState
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ProductsViewModelMviTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: ProductsViewModelMvi
    private lateinit var mockPagingStrategy: FakePagingStrategy
    private lateinit var mockPagingFactory: ProductPagingFactory
    private lateinit var addToBasketUseCase: AddToBasketUseCase

    private val sampleProducts = listOf(
        Product(
            id = 1,
            title = "Product 1",
            price = 100.0,
            description = "Description 1",
            category = "electronics",
            brand = "Brand1",
            thumbnail = null
        ),
        Product(
            id = 2,
            title = "Product 2",
            price = 200.0,
            description = "Description 2",
            category = "clothing",
            brand = "Brand2",
            thumbnail = null
        ),
        Product(
            id = 3,
            title = "Product 3",
            price = 300.0,
            description = "Description 3",
            category = "books",
            brand = "Brand3",
            thumbnail = null
        ),
        Product(
            id = 4,
            title = "Product 4",
            price = 400.0,
            description = "Description 4",
            category = "electronics",
            brand = "Brand4",
            thumbnail = null
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockPagingStrategy = FakePagingStrategy(products = sampleProducts)
        mockPagingFactory = ProductPagingFactory(pagingStrategy = mockPagingStrategy)
        addToBasketUseCase = AddToBasketUseCase()
        viewModel = ProductsViewModelMvi(
            pagerFactory = mockPagingFactory,
            addToBasketUseCase = addToBasketUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialStateIsEmpty() = runTest {
        val state = viewModel.state.first()

        assertTrue(state.products.isEmpty())
        assertFalse(state.isLoadingMore)
        assertNull(state.error)
        assertTrue(state.isBasketEmpty)
    }

    @Test
    fun testLoadMoreIntent_LoadsProducts() = runTest {
        val states = mutableListOf<ProductsState>()
        val collectJob = launch(testDispatcher) {
            viewModel.state.collect { states.add(it) }
        }

        // Process LoadMore intent
        viewModel.processIntent(ProductsIntent.LoadMore)
        testDispatcher.scheduler.advanceUntilIdle()

        val loadedState = states.last()
        assertEquals(2, loadedState.products.size)
        assertEquals("Product 1", loadedState.products[0].title)
        assertEquals("Product 2", loadedState.products[1].title)

        collectJob.cancel()
    }

    @Test
    fun testLoadMoreIntent_AppendsProducts() = runTest {
        val states = mutableListOf<ProductsState>()
        val collectJob = launch(testDispatcher) {
            viewModel.state.collect { states.add(it) }
        }

        // First load
        viewModel.processIntent(ProductsIntent.LoadMore)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(2, states.last().products.size)

        // Second load
        viewModel.processIntent(ProductsIntent.LoadMore)
        testDispatcher.scheduler.advanceUntilIdle()

        val finalState = states.last()
        assertEquals(4, finalState.products.size)
        assertEquals("Product 1", finalState.products[0].title)
        assertEquals("Product 2", finalState.products[1].title)
        assertEquals("Product 3", finalState.products[2].title)
        assertEquals("Product 4", finalState.products[3].title)

        collectJob.cancel()
    }

    @Test
    fun testErrorIntent_UpdatesErrorState() = runTest {
        val states = mutableListOf<ProductsState>()
        val collectJob = launch(testDispatcher) {
            viewModel.state.collect { states.add(it) }
        }

        // Simulate an error -> set up error behavior before loading
        mockPagingStrategy.loadBehavior = { LoadBehavior.Error("Test error") }

        // Trigger load that should fail
        viewModel.processIntent(ProductsIntent.LoadMore)
        testDispatcher.scheduler.advanceUntilIdle()

        val errorState = states.last()
        assertTrue(errorState.products.isEmpty())
        assertFalse(errorState.isLoadingMore)
        assertEquals("Test error", errorState.error)

        collectJob.cancel()
    }

    @Test
    fun testErrorIntent_AfterSuccessfulLoad() = runTest {
        val states = mutableListOf<ProductsState>()
        val collectJob = launch(testDispatcher) {
            viewModel.state.collect { states.add(it) }
        }

        // First load succeeds
        viewModel.processIntent(ProductsIntent.LoadMore)
        testDispatcher.scheduler.advanceUntilIdle()

        val successState = states.last()
        assertEquals(2, successState.products.size)
        assertNull(successState.error)

        // Simulate network failure
        mockPagingStrategy.loadBehavior = { LoadBehavior.Error("Network timeout") }

        // Second load should fail
        viewModel.processIntent(ProductsIntent.LoadMore)
        testDispatcher.scheduler.advanceUntilIdle()

        val errorState = states.last()
        assertEquals(2, errorState.products.size) // Previous products still there
        assertFalse(errorState.isLoadingMore)
        assertEquals("Network timeout", errorState.error)

        collectJob.cancel()
    }

    @Test
    fun testAddToBasketIntent_AddsNewProduct() = runTest {
        viewModel.processIntent(ProductsIntent.AddToBasket(sampleProducts[0], 2))

        val state = viewModel.state.first()
        assertEquals(1, state.basketItems.size)
        assertEquals(sampleProducts[0].id, state.basketItems[0].product.id)
        assertEquals(2, state.basketItems[0].quantity)
        assertEquals(2, state.totalQuantity)
    }

    @Test
    fun testAddToBasketIntent_UpdatesExistingProduct() = runTest {
        // Add product first time
        viewModel.processIntent(ProductsIntent.AddToBasket(sampleProducts[0], 1))

        val firstState = viewModel.state.first()
        assertEquals(1, firstState.basketItems[0].quantity)
        assertEquals(sampleProducts[0].id, firstState.basketItems[0].product.id)

        // Add same product again
        viewModel.processIntent(ProductsIntent.AddToBasket(sampleProducts[0], 2))

        val secondState = viewModel.state.first()
        assertEquals(1, secondState.basketItems.size)
        assertEquals(3, secondState.basketItems[0].quantity)
        assertEquals(sampleProducts[0].id, secondState.basketItems[0].product.id)
    }

    @Test
    fun testAddToBasketIntent_CalculatesTotalPriceCorrectly() = runTest {
        viewModel.processIntent(ProductsIntent.AddToBasket(sampleProducts[0], 2))

        val state = viewModel.state.first()
        // Product price is 100.0, quantity is 2
        assertEquals(200.0, state.totalRetailPrice, 0.01)
        assertEquals(140.0, state.totalCostPrice, 0.01) // 70% of retail
    }

    @Test
    fun testUpdateQuantityIntent_ChangesProductQuantity() = runTest {
        // Add product
        viewModel.processIntent(ProductsIntent.AddToBasket(sampleProducts[0], 2))

        // Update quantity
        viewModel.processIntent(ProductsIntent.UpdateQuantity(1L, 5))

        val state = viewModel.state.first()
        assertEquals(1, state.basketItems.size)
        assertEquals(5, state.basketItems[0].quantity)
        assertEquals(5, state.totalQuantity)
    }

    @Test
    fun testUpdateQuantityIntent_WithZeroRemovesProduct() = runTest {
        // Add product
        viewModel.processIntent(ProductsIntent.AddToBasket(sampleProducts[0], 2))

        // Update quantity to 0
        viewModel.processIntent(ProductsIntent.UpdateQuantity(sampleProducts[0].id, 0))

        val state = viewModel.state.first()
        assertTrue(state.isBasketEmpty)
    }

    @Test
    fun testUpdateQuantityIntent_WithNegativeRemovesProduct() = runTest {
        // Add product
        viewModel.processIntent(ProductsIntent.AddToBasket(sampleProducts[0], 2))

        // Update quantity to negative
        viewModel.processIntent(ProductsIntent.UpdateQuantity(sampleProducts[0].id, -1))

        val state = viewModel.state.first()
        assertTrue(state.isBasketEmpty)
    }

    @Test
    fun testRemoveProductIntent_RemovesItemFromBasket() = runTest {
        // Add two products
        viewModel.processIntent(ProductsIntent.AddToBasket(sampleProducts[0], 1))
        viewModel.processIntent(ProductsIntent.AddToBasket(sampleProducts[1], 1))

        val stateWithTwo = viewModel.state.first()
        assertEquals(2, stateWithTwo.basketItems.size)

        // Remove first product
        viewModel.processIntent(ProductsIntent.RemoveProduct(sampleProducts[0].id))

        val finalState = viewModel.state.first()
        assertEquals(1, finalState.basketItems.size)
        assertEquals(2L, finalState.basketItems[0].product.id)
    }

    @Test
    fun testClearBasketIntent_EmptiesBasket() = runTest {
        // Add products
        viewModel.processIntent(ProductsIntent.AddToBasket(sampleProducts[0], 1))
        viewModel.processIntent(ProductsIntent.AddToBasket(sampleProducts[1], 1))

        // Clear basket
        viewModel.processIntent(ProductsIntent.ClearBasket)

        val state = viewModel.state.first()
        assertTrue(state.isBasketEmpty)
        assertEquals(0, state.totalQuantity)
    }

    @Test
    fun testBasketIntent_HandlesMultipleProductsWithDifferentQuantities() = runTest {
        viewModel.processIntent(ProductsIntent.AddToBasket(sampleProducts[0], 2))
        viewModel.processIntent(ProductsIntent.AddToBasket(sampleProducts[1], 3))

        val state = viewModel.state.first()
        assertEquals(2, state.basketItems.size)
        assertEquals(5, state.totalQuantity)
        // (100 * 2) + (200 * 3) = 200 + 600 = 800
        assertEquals(800.0, state.totalRetailPrice, 0.01)
    }

    // Fake implementations for testing
    private class FakePagingStrategy(
        private val products: List<Product>,
        var loadBehavior: () -> LoadBehavior = { LoadBehavior.Success }
    ) : PagingStrategy {

        override fun createProductPager(onEvent: (PagingEvent) -> Unit): ProductPager {
            return FakeProductPager(
                onEvent = onEvent,
                products = products,
                // IMPORTANT: Use indirection { loadBehavior() } instead of just loadBehavior
                // This allows us to change loadBehavior dynamically in tests and have
                // the pager see the new value on each call
                loadBehavior = { loadBehavior() }
            )
        }
    }

    private class FakeProductPager(
        private val onEvent: (PagingEvent) -> Unit,
        private val products: List<Product>,
        private val loadBehavior: () -> LoadBehavior
    ) : ProductPager {
        private var currentPage = 0
        private val productsPerPage = 2

        override suspend fun loadNextProducts() {
            onEvent(PagingEvent.LoadingChanged(true))

            // Simulate network delay
            delay(500)

            when (val behavior = loadBehavior()) {
                is LoadBehavior.Error -> {
                    onEvent(PagingEvent.Error(behavior.message))
                    onEvent(PagingEvent.LoadingChanged(false))
                    return
                }

                LoadBehavior.Success -> {
                    val startIndex = currentPage * productsPerPage
                    val endIndex = minOf(startIndex + productsPerPage, products.size)

                    if (startIndex < products.size) {
                        val products = products.subList(startIndex, endIndex)
                        onEvent(PagingEvent.ProductsLoaded(products))
                        currentPage++
                    }

                    onEvent(PagingEvent.LoadingChanged(false))
                }
            }
        }

        override fun reset() {
            currentPage = 0
        }
    }

    private sealed interface LoadBehavior {
        object Success : LoadBehavior
        data class Error(val message: String) : LoadBehavior
    }
}
