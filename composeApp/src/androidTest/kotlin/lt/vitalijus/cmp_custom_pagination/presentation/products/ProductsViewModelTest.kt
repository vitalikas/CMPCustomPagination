package lt.vitalijus.cmp_custom_pagination.presentation.products

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.ProductAction
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: ProductsViewModel
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
        viewModel = ProductsViewModel(
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

        assertTrue(state.browseProductsState.products.isEmpty())
        assertFalse(state.browseProductsState.isLoadingMore)
        assertNull(state.browseProductsState.error)
        assertTrue(state.basketState.isEmpty)
    }

    @Test
    fun testLoadMoreTriggersProductLoad() = runTest {
        val states = mutableListOf<ProductsState>()
        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.collect { productsState ->
                states.add(productsState)
            }
        }

        // Trigger load
        viewModel.onAction(ProductAction.LoadMore)

        val loadedState = states.last()
        assertEquals(2, loadedState.browseProductsState.products.size)
        assertEquals("Product 1", loadedState.browseProductsState.products[0].title)

        collectJob.cancel()
    }

    @Test
    fun testLoadMoreAppendsProducts() = runTest {
        val states = mutableListOf<ProductsState>()
        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.collect { productsState ->
                states.add(productsState)
            }
        }

        // First load
        viewModel.onAction(ProductAction.LoadMore)

        assertEquals(2, states.last().browseProductsState.products.size)

        // Second load
        viewModel.onAction(ProductAction.LoadMore)

        val finalState = states.last()
        assertEquals(4, finalState.browseProductsState.products.size)
        assertEquals("Product 1", finalState.browseProductsState.products[0].title)
        assertEquals("Product 2", finalState.browseProductsState.products[1].title)
        assertEquals("Product 3", finalState.browseProductsState.products[2].title)
        assertEquals("Product 4", finalState.browseProductsState.products[3].title)

        collectJob.cancel()
    }

    @Test
    fun testErrorEventUpdatesErrorState() = runTest {
        val states = mutableListOf<ProductsState>()
        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.collect { productsState ->
                states.add(productsState)
            }
        }

        mockPagingStrategy.pager.shouldFail = true
        viewModel.onAction(ProductAction.LoadMore)

        val errorState = states.last()
        assertEquals("Test error", errorState.browseProductsState.error)

        collectJob.cancel()
    }

    @Test
    fun testAddToBasketAddsNewProduct() = runTest {
        viewModel.onAction(ProductAction.AddToBasket(sampleProducts[0], 2))

        val state = viewModel.state.first()
        assertEquals(1, state.basketState.items.size)
        assertEquals(sampleProducts[0].id, state.basketState.items[0].product.id)
        assertEquals(2, state.basketState.items[0].quantity)
        assertEquals(2, state.basketState.totalQuantity)
    }

    @Test
    fun testAddToBasketUpdatesExistingProduct() = runTest {
        // Add product first time
        viewModel.onAction(ProductAction.AddToBasket(sampleProducts[0], 1))

        val firstState = viewModel.state.first()
        assertEquals(1, firstState.basketState.items[0].quantity)

        // Add same product again
        viewModel.onAction(ProductAction.AddToBasket(sampleProducts[0], 2))

        val secondState = viewModel.state.first()
        assertEquals(1, secondState.basketState.items.size)
        assertEquals(3, secondState.basketState.items[0].quantity)
    }

    @Test
    fun testAddToBasketCalculatesTotalPriceCorrectly() = runTest {
        viewModel.onAction(ProductAction.AddToBasket(sampleProducts[0], 2))

        val state = viewModel.state.first()
        // Product price is 100.0, quantity is 2
        assertEquals(200.0, state.basketState.totalRetailPrice, 0.01)
        assertEquals(140.0, state.basketState.totalCostPrice, 0.01) // 70% of retail
    }

    @Test
    fun testUpdateQuantityChangesProductQuantity() = runTest {
        // Add product
        viewModel.onAction(ProductAction.AddToBasket(sampleProducts[0], 2))

        // Update quantity
        viewModel.onAction(ProductAction.UpdateQuantity(1L, 5))

        val state = viewModel.state.first()
        assertEquals(1, state.basketState.items.size)
        assertEquals(5, state.basketState.items[0].quantity)
        assertEquals(5, state.basketState.totalQuantity)
    }

    @Test
    fun testUpdateQuantityWithZeroRemovesProduct() = runTest {
        // Add product
        viewModel.onAction(ProductAction.AddToBasket(sampleProducts[0], 2))

        // Update quantity to 0
        viewModel.onAction(ProductAction.UpdateQuantity(1L, 0))

        val state = viewModel.state.first()
        assertTrue(state.basketState.isEmpty)
    }

    @Test
    fun testUpdateQuantityWithNegativeRemovesProduct() = runTest {
        // Add product
        viewModel.onAction(ProductAction.AddToBasket(sampleProducts[0], 2))

        // Update quantity to negative
        viewModel.onAction(ProductAction.UpdateQuantity(1L, -1))

        val state = viewModel.state.first()
        assertTrue(state.basketState.isEmpty)
    }

    @Test
    fun testRemoveProductRemovesItemFromBasket() = runTest {
        // Add two products
        viewModel.onAction(ProductAction.AddToBasket(sampleProducts[0], 1))

        viewModel.onAction(ProductAction.AddToBasket(sampleProducts[1], 1))

        val stateWithTwo = viewModel.state.first()
        assertEquals(2, stateWithTwo.basketState.items.size)

        // Remove first product
        viewModel.onAction(ProductAction.RemoveProduct(1L))

        val finalState = viewModel.state.first()
        assertEquals(1, finalState.basketState.items.size)
        assertEquals(2L, finalState.basketState.items[0].product.id)
    }

    @Test
    fun testClearBasketEmptiesBasket() = runTest {
        // Add products
        viewModel.onAction(ProductAction.AddToBasket(sampleProducts[0], 1))

        viewModel.onAction(ProductAction.AddToBasket(sampleProducts[1], 1))

        // Clear basket
        viewModel.onAction(ProductAction.ClearBasket)

        val state = viewModel.state.first()
        assertTrue(state.basketState.isEmpty)
        assertEquals(0, state.basketState.totalQuantity)
    }

    @Test
    fun testBasketHandlesMultipleProductsWithDifferentQuantities() = runTest {
        viewModel.onAction(ProductAction.AddToBasket(sampleProducts[0], 2))

        viewModel.onAction(ProductAction.AddToBasket(sampleProducts[1], 3))

        val state = viewModel.state.first()
        assertEquals(2, state.basketState.items.size)
        assertEquals(5, state.basketState.totalQuantity)
        // (100 * 2) + (200 * 3) = 200 + 600 = 800
        assertEquals(800.0, state.basketState.totalRetailPrice, 0.01)
    }

    // Fake implementations for testing
    private class FakePagingStrategy(private val products: List<Product>) : PagingStrategy {
        lateinit var pager: FakeProductPager

        override fun createProductPager(onEvent: (PagingEvent) -> Unit): ProductPager {
            pager = FakeProductPager(onEvent, products)
            return pager
        }
    }

    private class FakeProductPager(
        private val onEvent: (PagingEvent) -> Unit,
        private val products: List<Product>
    ) : ProductPager {
        private var currentPage = 0
        private val productsPerPage = 2
        var shouldFail = false

        override suspend fun loadNextProducts() {
            onEvent(PagingEvent.LoadingChanged(true))

            if (shouldFail) {
                onEvent(PagingEvent.Error("Test error"))
                onEvent(PagingEvent.LoadingChanged(false))
                return
            }

            /**
             * Page 1: startIndex=0, endIndex=2 → loads products 0,2 ✓
             * Page 2: startIndex=2, endIndex=4 → loads products 2,4 ✓
             * Page 3: startIndex=4, endIndex=4 → startIndex=4, size=4 → condition is false, no load ✓
             */
            val startIndex = currentPage * productsPerPage
            val endIndex = minOf(startIndex + productsPerPage, products.size)

            if (startIndex < products.size) {
                val products = products.subList(startIndex, endIndex)
                onEvent(PagingEvent.ProductsLoaded(products))
                currentPage++
            }

            onEvent(PagingEvent.LoadingChanged(false))
        }

        override fun reset() {
            currentPage = 0
        }
    }
}
