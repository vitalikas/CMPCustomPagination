@file:OptIn(ExperimentalCoroutinesApi::class)

package lt.vitalijus.cmp_custom_pagination.presentation.products

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import lt.vitalijus.cmp_custom_pagination.presentation.products.mvi.ProductsEffect
import lt.vitalijus.cmp_custom_pagination.presentation.products.mvi.ProductsIntent
import lt.vitalijus.cmp_custom_pagination.presentation.products.mvi.ProductsState
import lt.vitalijus.cmp_custom_pagination.presentation.products.mvi.ProductsStateMachine
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ProductsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: ProductsViewModel
    private lateinit var mockPagingStrategy: FakePagingStrategy
    private lateinit var mockPagingFactory: ProductPagingFactory
    private lateinit var addToBasketUseCase: AddToBasketUseCase
    private lateinit var stateMachine: ProductsStateMachine

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
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockPagingStrategy = FakePagingStrategy(products = sampleProducts)
        mockPagingFactory = ProductPagingFactory(pagingStrategy = mockPagingStrategy)
        addToBasketUseCase = AddToBasketUseCase()
        stateMachine = ProductsStateMachine()
        viewModel = ProductsViewModel(
            pagerFactory = mockPagingFactory,
            addToBasketUseCase = addToBasketUseCase,
            stateMachine = stateMachine
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialState() = runTest {
        val state = viewModel.state.value

        assertTrue(state.products.isEmpty())
        assertTrue(state.basketItems.isEmpty())
        assertFalse(state.isLoadingMore)
        assertNull(state.error)
        assertTrue(state.isBasketEmpty)
        assertEquals(0, state.totalQuantity)
        assertEquals(0.0, state.totalRetailPrice, 0.01)
    }

    @Test
    fun testLoadMore_LoadsProducts() = runTest {
        val states = mutableListOf<ProductsState>()
        val collectJob = launch(testDispatcher) {
            viewModel.state.collect { states.add(it) }
        }

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.processIntent(ProductsIntent.LoadMore)
        testDispatcher.scheduler.advanceUntilIdle()

        val finalState = states.lastOrNull { it.products.isNotEmpty() } ?: states.last()

        assertTrue(
            "Expected at least 2 products, got ${finalState.products.size}",
            finalState.products.size >= 2
        )
        assertEquals("Product 1", finalState.products[0].title)
        assertEquals("Product 2", finalState.products[1].title)
        assertFalse(finalState.isLoadingMore)

        collectJob.cancel()
    }

    @Test
    fun testLoadMore_AppendsProducts() = runTest {
        val states = mutableListOf<ProductsState>()
        val collectJob = launch(testDispatcher) {
            viewModel.state.collect { states.add(it) }
        }

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.processIntent(ProductsIntent.LoadMore)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.processIntent(ProductsIntent.LoadMore)
        testDispatcher.scheduler.advanceUntilIdle()

        val finalState = states.lastOrNull { it.products.size >= 3 } ?: states.last()

        assertTrue(
            "Expected 3 products, got ${finalState.products.size}",
            finalState.products.size == 3
        )
        assertEquals("Product 1", finalState.products[0].title)
        assertEquals("Product 2", finalState.products[1].title)
        assertEquals("Product 3", finalState.products[2].title)

        collectJob.cancel()
    }

    @Test
    fun testLoadMore_HandlesError() = runTest {
        val states = mutableListOf<ProductsState>()
        val effects = mutableListOf<ProductsEffect>()

        val stateJob = launch(testDispatcher) {
            viewModel.state.collect { states.add(it) }
        }
        val effectJob = launch(testDispatcher) {
            viewModel.effects.collect { effects.add(it) }
        }

        testDispatcher.scheduler.advanceUntilIdle()

        mockPagingStrategy.loadBehavior = { LoadBehavior.Error("Network error") }

        viewModel.processIntent(ProductsIntent.LoadMore)
        testDispatcher.scheduler.advanceUntilIdle()

        val finalState = states.lastOrNull { it.error != null } ?: states.last()
        assertEquals("Network error", finalState.error)
        assertFalse(finalState.isLoadingMore)

        val errorEffect = effects.filterIsInstance<ProductsEffect.ShowError>().firstOrNull()
        assertNotNull("Expected error effect", errorEffect)
        assertEquals("Network error", errorEffect?.message)

        stateJob.cancel()
        effectJob.cancel()
    }

    @Test
    fun testAddToBasket_AddsProduct() = runTest {
        val states = mutableListOf<ProductsState>()
        val effects = mutableListOf<ProductsEffect>()

        val stateJob = launch(testDispatcher) {
            viewModel.state.collect { states.add(it) }
        }
        val effectJob = launch(testDispatcher) {
            viewModel.effects.collect { effects.add(it) }
        }

        testDispatcher.scheduler.advanceUntilIdle()

        // Load products first to transition to Ready state
        viewModel.processIntent(ProductsIntent.LoadMore)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.processIntent(ProductsIntent.AddToBasket(sampleProducts[0], 2))
        testDispatcher.scheduler.advanceUntilIdle()

        val finalState = states.lastOrNull { it.basketItems.isNotEmpty() } ?: states.last()

        assertEquals(1, finalState.basketItems.size)
        assertEquals(sampleProducts[0].id, finalState.basketItems[0].product.id)
        assertEquals(2, finalState.basketItems[0].quantity)
        assertEquals(2, finalState.totalQuantity)
        assertEquals(200.0, finalState.totalRetailPrice, 0.01)
        assertFalse(finalState.isBasketEmpty)

        assertTrue(
            "Expected ShowBasketUpdated effect",
            effects.any { it is ProductsEffect.ShowBasketUpdated })

        stateJob.cancel()
        effectJob.cancel()
    }

    @Test
    fun testAddToBasket_UpdatesExistingProduct() = runTest {
        val states = mutableListOf<ProductsState>()
        val collectJob = launch(testDispatcher) {
            viewModel.state.collect { states.add(it) }
        }

        testDispatcher.scheduler.advanceUntilIdle()

        // Load products first to transition to Ready state
        viewModel.processIntent(ProductsIntent.LoadMore)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.processIntent(ProductsIntent.AddToBasket(sampleProducts[0], 1))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.processIntent(ProductsIntent.AddToBasket(sampleProducts[0], 2))
        testDispatcher.scheduler.advanceUntilIdle()

        val finalState = states.lastOrNull {
            it.basketItems.isNotEmpty() && it.basketItems[0].quantity == 3
        } ?: states.last()

        assertEquals(1, finalState.basketItems.size)
        assertEquals(3, finalState.basketItems[0].quantity)
        assertEquals(3, finalState.totalQuantity)

        collectJob.cancel()
    }

    @Test
    fun testUpdateQuantity_ChangesQuantity() = runTest {
        val states = mutableListOf<ProductsState>()
        val collectJob = launch(testDispatcher) {
            viewModel.state.collect { states.add(it) }
        }

        testDispatcher.scheduler.advanceUntilIdle()

        // Load products first to transition to Ready state
        viewModel.processIntent(ProductsIntent.LoadMore)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.processIntent(ProductsIntent.AddToBasket(sampleProducts[0], 2))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.processIntent(ProductsIntent.UpdateQuantity(sampleProducts[0].id, 5))
        testDispatcher.scheduler.advanceUntilIdle()

        val finalState = states.lastOrNull {
            it.basketItems.isNotEmpty() && it.basketItems[0].quantity == 5
        } ?: states.last()

        assertEquals(5, finalState.basketItems[0].quantity)
        assertEquals(5, finalState.totalQuantity)
        assertEquals(500.0, finalState.totalRetailPrice, 0.01)

        collectJob.cancel()
    }

    @Test
    fun testUpdateQuantity_RemovesWhenZero() = runTest {
        val states = mutableListOf<ProductsState>()
        val collectJob = launch(testDispatcher) {
            viewModel.state.collect { states.add(it) }
        }

        testDispatcher.scheduler.advanceUntilIdle()

        // Load products first to transition to Ready state
        viewModel.processIntent(ProductsIntent.LoadMore)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.processIntent(ProductsIntent.AddToBasket(sampleProducts[0], 2))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.processIntent(ProductsIntent.UpdateQuantity(sampleProducts[0].id, 0))
        testDispatcher.scheduler.advanceUntilIdle()

        val finalState = states.last()
        assertTrue(finalState.isBasketEmpty)
        assertEquals(0, finalState.totalQuantity)

        collectJob.cancel()
    }

    @Test
    fun testRemoveProduct_RemovesItem() = runTest {
        val states = mutableListOf<ProductsState>()
        val collectJob = launch(testDispatcher) {
            viewModel.state.collect { states.add(it) }
        }

        testDispatcher.scheduler.advanceUntilIdle()

        // Load products first to transition to Ready state
        viewModel.processIntent(ProductsIntent.LoadMore)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.processIntent(ProductsIntent.AddToBasket(sampleProducts[0], 1))
        viewModel.processIntent(ProductsIntent.AddToBasket(sampleProducts[1], 1))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.processIntent(ProductsIntent.RemoveProduct(sampleProducts[0].id))
        testDispatcher.scheduler.advanceUntilIdle()

        val finalState = states.lastOrNull {
            it.basketItems.size == 1 && it.basketItems[0].product.id == sampleProducts[1].id
        } ?: states.last()

        assertEquals(1, finalState.basketItems.size)
        assertEquals(sampleProducts[1].id, finalState.basketItems[0].product.id)

        collectJob.cancel()
    }

    @Test
    fun testClearBasket_RemovesAllItems() = runTest {
        val states = mutableListOf<ProductsState>()
        val collectJob = launch(testDispatcher) {
            viewModel.state.collect { states.add(it) }
        }

        testDispatcher.scheduler.advanceUntilIdle()

        // Load products first to transition to Ready state
        viewModel.processIntent(ProductsIntent.LoadMore)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.processIntent(ProductsIntent.AddToBasket(sampleProducts[0], 1))
        viewModel.processIntent(ProductsIntent.AddToBasket(sampleProducts[1], 2))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.processIntent(ProductsIntent.ClearBasket)
        testDispatcher.scheduler.advanceUntilIdle()

        val finalState = states.last()
        assertTrue(finalState.isBasketEmpty)
        assertEquals(0, finalState.totalQuantity)
        assertEquals(0.0, finalState.totalRetailPrice, 0.01)

        collectJob.cancel()
    }

    @Test
    fun testNavigateTo_EmitsNavigationEffect() = runTest {
        val effects = mutableListOf<ProductsEffect>()
        val collectJob = launch(testDispatcher) {
            viewModel.effects.collect { effects.add(it) }
        }

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.processIntent(ProductsIntent.NavigateTo(Screen.Basket))
        testDispatcher.scheduler.advanceUntilIdle()

        val navigationEffect = effects.filterIsInstance<ProductsEffect.NavigateTo>().firstOrNull()
        assertNotNull("Expected NavigateTo effect", navigationEffect)
        assertEquals(Screen.Basket, navigationEffect?.screen)

        collectJob.cancel()
    }

    @Test
    fun testMultipleBasketOperations() = runTest {
        val states = mutableListOf<ProductsState>()
        val collectJob = launch(testDispatcher) {
            viewModel.state.collect { states.add(it) }
        }

        testDispatcher.scheduler.advanceUntilIdle()

        // Load products first to transition to Ready state
        viewModel.processIntent(ProductsIntent.LoadMore)
        testDispatcher.scheduler.advanceUntilIdle()

        // Add multiple products
        viewModel.processIntent(ProductsIntent.AddToBasket(sampleProducts[0], 2))
        viewModel.processIntent(ProductsIntent.AddToBasket(sampleProducts[1], 3))
        testDispatcher.scheduler.advanceUntilIdle()

        val afterAdd = states.lastOrNull { it.basketItems.size == 2 } ?: states.last()
        assertEquals(2, afterAdd.basketItems.size)
        assertEquals(5, afterAdd.totalQuantity)

        // Update one quantity
        viewModel.processIntent(ProductsIntent.UpdateQuantity(sampleProducts[0].id, 5))
        testDispatcher.scheduler.advanceUntilIdle()

        val afterUpdate = states.lastOrNull { it.totalQuantity == 8 } ?: states.last()
        assertEquals(8, afterUpdate.totalQuantity)

        // Remove one product
        viewModel.processIntent(ProductsIntent.RemoveProduct(sampleProducts[1].id))
        testDispatcher.scheduler.advanceUntilIdle()

        val afterRemove = states.lastOrNull {
            it.basketItems.size == 1 && it.totalQuantity == 5
        } ?: states.last()
        assertEquals(1, afterRemove.basketItems.size)
        assertEquals(5, afterRemove.totalQuantity)

        collectJob.cancel()
    }

    @Test
    fun testDerivedStateCalculations() = runTest {
        val states = mutableListOf<ProductsState>()
        val collectJob = launch(testDispatcher) {
            viewModel.state.collect { states.add(it) }
        }

        testDispatcher.scheduler.advanceUntilIdle()

        // Load products first to transition to Ready state
        viewModel.processIntent(ProductsIntent.LoadMore)
        testDispatcher.scheduler.advanceUntilIdle()

        // Product 1: price = 100.0
        // Product 2: price = 200.0
        viewModel.processIntent(ProductsIntent.AddToBasket(sampleProducts[0], 2))
        viewModel.processIntent(ProductsIntent.AddToBasket(sampleProducts[1], 1))
        testDispatcher.scheduler.advanceUntilIdle()

        val finalState = states.lastOrNull {
            it.basketItems.size == 2
        } ?: states.last()

        // Total quantity: 2 + 1 = 3
        assertEquals(3, finalState.totalQuantity)

        // Total retail: (100 * 2) + (200 * 1) = 400
        assertEquals(400.0, finalState.totalRetailPrice, 0.01)

        // Total cost: (100 * 0.7 * 2) + (200 * 0.7 * 1) = 140 + 140 = 280
        assertEquals(280.0, finalState.totalCostPrice, 0.01)

        collectJob.cancel()
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
                        val productsToLoad = products.subList(startIndex, endIndex)
                        onEvent(PagingEvent.ProductsLoaded(productsToLoad))
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
