package lt.vitalijus.cmp_custom_pagination.presentation.products

import lt.vitalijus.cmp_custom_pagination.domain.model.Product
import lt.vitalijus.cmp_custom_pagination.presentation.products.mvi.ProductsIntent
import lt.vitalijus.cmp_custom_pagination.presentation.products.mvi.ProductsMutation
import lt.vitalijus.cmp_custom_pagination.presentation.products.mvi.ProductsStateMachine
import lt.vitalijus.cmp_custom_pagination.presentation.products.mvi.ProductsTransitionState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ProductsStateMachineTest {

    private lateinit var stateMachine: ProductsStateMachine

    private val sampleProduct = Product(
        id = 1,
        title = "Test Product",
        price = 100.0,
        description = "Test",
        category = "test",
        brand = "TestBrand",
        thumbnail = null
    )

    @Before
    fun setup() {
        stateMachine = ProductsStateMachine()
    }

    // Tests for Idle state transitions
    @Test
    fun testIdleState_LoadMore_TransitionsToLoading() {
        val newState = stateMachine.transition(ProductsIntent.LoadMore)
        assertEquals(ProductsTransitionState.LoadingProducts, newState)
        assertEquals(ProductsTransitionState.LoadingProducts, stateMachine.currentState)
    }

    @Test
    fun testIdleState_NavigateTo_RemainsIdle() {
        val newState = stateMachine.transition(ProductsIntent.NavigateTo(Screen.Basket))
        assertEquals(ProductsTransitionState.Idle, newState)
        assertEquals(ProductsTransitionState.Idle, stateMachine.currentState)
    }

    @Test(expected = IllegalStateException::class)
    fun testIdleState_AddToBasket_ThrowsException() {
        stateMachine.transition(ProductsIntent.AddToBasket(sampleProduct, 1))
    }

    @Test
    fun testIdleState_IsValidTransition_LoadMore_ReturnsTrue() {
        assertTrue(stateMachine.isTransitionValid(ProductsIntent.LoadMore))
    }

    @Test
    fun testIdleState_IsValidTransition_AddToBasket_ReturnsFalse() {
        assertFalse(stateMachine.isTransitionValid(ProductsIntent.AddToBasket(sampleProduct, 1)))
    }

    // Tests for LoadingProducts state transitions
    @Test
    fun testLoadingState_NavigateTo_RemainsLoading() {
        stateMachine.transition(ProductsIntent.LoadMore) // Go to Loading
        val newState = stateMachine.transition(ProductsIntent.NavigateTo(Screen.Basket))
        assertEquals(ProductsTransitionState.LoadingProducts, newState)
    }

    @Test(expected = IllegalStateException::class)
    fun testLoadingState_AddToBasket_ThrowsException() {
        stateMachine.transition(ProductsIntent.LoadMore) // Go to Loading
        stateMachine.transition(ProductsIntent.AddToBasket(sampleProduct, 1))
    }

    @Test(expected = IllegalStateException::class)
    fun testLoadingState_LoadMore_ThrowsException() {
        stateMachine.transition(ProductsIntent.LoadMore) // Go to Loading
        stateMachine.transition(ProductsIntent.LoadMore) // Try to load again
    }

    @Test
    fun testLoadingState_IsValidTransition_AddToBasket_ReturnsFalse() {
        stateMachine.transition(ProductsIntent.LoadMore) // Go to Loading
        assertFalse(stateMachine.isTransitionValid(ProductsIntent.AddToBasket(sampleProduct, 1)))
    }

    // Tests for Ready state transitions
    @Test
    fun testReadyState_LoadMore_TransitionsToLoading() {
        setupReadyState()
        val newState = stateMachine.transition(ProductsIntent.LoadMore)
        assertEquals(ProductsTransitionState.LoadingProducts, newState)
    }

    @Test
    fun testReadyState_AddToBasket_TransitionsToProcessingBasket() {
        setupReadyState()
        val newState = stateMachine.transition(ProductsIntent.AddToBasket(sampleProduct, 1))
        assertEquals(ProductsTransitionState.ProcessingBasket, newState)
    }

    @Test
    fun testReadyState_UpdateQuantity_TransitionsToProcessingBasket() {
        setupReadyState()
        val newState = stateMachine.transition(ProductsIntent.UpdateQuantity(1L, 5))
        assertEquals(ProductsTransitionState.ProcessingBasket, newState)
    }

    @Test
    fun testReadyState_RemoveProduct_TransitionsToProcessingBasket() {
        setupReadyState()
        val newState = stateMachine.transition(ProductsIntent.RemoveProduct(1L))
        assertEquals(ProductsTransitionState.ProcessingBasket, newState)
    }

    @Test
    fun testReadyState_ClearBasket_TransitionsToProcessingBasket() {
        setupReadyState()
        val newState = stateMachine.transition(ProductsIntent.ClearBasket)
        assertEquals(ProductsTransitionState.ProcessingBasket, newState)
    }

    @Test
    fun testReadyState_NavigateTo_RemainsReady() {
        setupReadyState()
        val newState = stateMachine.transition(ProductsIntent.NavigateTo(Screen.Basket))
        assertEquals(ProductsTransitionState.Ready, newState)
    }

    @Test
    fun testReadyState_IsValidTransition_AllIntents_ReturnsTrue() {
        setupReadyState()
        assertTrue(stateMachine.isTransitionValid(ProductsIntent.LoadMore))
        assertTrue(stateMachine.isTransitionValid(ProductsIntent.AddToBasket(sampleProduct, 1)))
        assertTrue(stateMachine.isTransitionValid(ProductsIntent.UpdateQuantity(1L, 5)))
        assertTrue(stateMachine.isTransitionValid(ProductsIntent.RemoveProduct(1L)))
        assertTrue(stateMachine.isTransitionValid(ProductsIntent.ClearBasket))
        assertTrue(stateMachine.isTransitionValid(ProductsIntent.NavigateTo(Screen.Basket)))
    }

    // Tests for Error state transitions
    @Test
    fun testErrorState_LoadMore_TransitionsToLoading() {
        setupErrorState()
        val newState = stateMachine.transition(ProductsIntent.LoadMore)
        assertEquals(ProductsTransitionState.LoadingProducts, newState)
    }

    @Test
    fun testErrorState_NavigateTo_RemainsInError() {
        setupErrorState()
        val newState = stateMachine.transition(ProductsIntent.NavigateTo(Screen.Basket))
        assertTrue(newState is ProductsTransitionState.Error)
    }

    @Test(expected = IllegalStateException::class)
    fun testErrorState_AddToBasket_ThrowsException() {
        setupErrorState()
        stateMachine.transition(ProductsIntent.AddToBasket(sampleProduct, 1))
    }

    @Test
    fun testErrorState_IsValidTransition_LoadMore_ReturnsTrue() {
        setupErrorState()
        assertTrue(stateMachine.isTransitionValid(ProductsIntent.LoadMore))
    }

    @Test
    fun testErrorState_IsValidTransition_AddToBasket_ReturnsFalse() {
        setupErrorState()
        assertFalse(stateMachine.isTransitionValid(ProductsIntent.AddToBasket(sampleProduct, 1)))
    }

    // Tests for ProcessingBasket state transitions
    @Test
    fun testProcessingBasketState_NavigateTo_RemainsProcessing() {
        setupProcessingBasketState()
        val newState = stateMachine.transition(ProductsIntent.NavigateTo(Screen.Basket))
        assertEquals(ProductsTransitionState.ProcessingBasket, newState)
    }

    @Test(expected = IllegalStateException::class)
    fun testProcessingBasketState_AddToBasket_ThrowsException() {
        setupProcessingBasketState()
        stateMachine.transition(ProductsIntent.AddToBasket(sampleProduct, 1))
    }

    @Test(expected = IllegalStateException::class)
    fun testProcessingBasketState_LoadMore_ThrowsException() {
        setupProcessingBasketState()
        stateMachine.transition(ProductsIntent.LoadMore)
    }

    @Test
    fun testProcessingBasketState_IsValidTransition_NavigateTo_ReturnsTrue() {
        setupProcessingBasketState()
        assertTrue(stateMachine.isTransitionValid(ProductsIntent.NavigateTo(Screen.Basket)))
    }

    @Test
    fun testProcessingBasketState_IsValidTransition_AddToBasket_ReturnsFalse() {
        setupProcessingBasketState()
        assertFalse(stateMachine.isTransitionValid(ProductsIntent.AddToBasket(sampleProduct, 1)))
    }

    // Tests for onMutationComplete
    @Test
    fun testOnMutationComplete_SetLoadingTrue_TransitionsToLoading() {
        stateMachine.applyMutation(ProductsMutation.SetLoading(true))
        assertEquals(ProductsTransitionState.LoadingProducts, stateMachine.currentState)
    }

    @Test
    fun testOnMutationComplete_SetLoadingFalse_TransitionsToReady() {
        stateMachine.applyMutation(ProductsMutation.SetLoading(false))
        assertEquals(ProductsTransitionState.Ready, stateMachine.currentState)
    }

    @Test
    fun testOnMutationComplete_ProductsLoaded_TransitionsToReady() {
        stateMachine.applyMutation(ProductsMutation.ProductsLoaded(emptyList()))
        assertEquals(ProductsTransitionState.Ready, stateMachine.currentState)
    }

    @Test
    fun testOnMutationComplete_LoadingError_TransitionsToError() {
        stateMachine.applyMutation(ProductsMutation.LoadingError("Test error"))
        val currentState = stateMachine.currentState
        assertTrue(currentState is ProductsTransitionState.Error)
        assertEquals("Test error", (currentState as ProductsTransitionState.Error).message)
    }

    @Test
    fun testOnMutationComplete_BasketUpdated_TransitionsToReady() {
        stateMachine.applyMutation(ProductsMutation.BasketUpdated(emptyList()))
        assertEquals(ProductsTransitionState.Ready, stateMachine.currentState)
    }

    // Complex flow tests
    @Test
    fun testCompleteFlow_IdleToLoadingToReady() {
        // Start in Idle
        assertEquals(ProductsTransitionState.Idle, stateMachine.currentState)

        // User clicks load more
        stateMachine.transition(ProductsIntent.LoadMore)
        assertEquals(ProductsTransitionState.LoadingProducts, stateMachine.currentState)

        // Loading completes successfully
        stateMachine.applyMutation(ProductsMutation.ProductsLoaded(listOf(sampleProduct)))
        assertEquals(ProductsTransitionState.Ready, stateMachine.currentState)

        // User can now add to basket
        stateMachine.transition(ProductsIntent.AddToBasket(sampleProduct, 1))
        assertEquals(ProductsTransitionState.ProcessingBasket, stateMachine.currentState)

        // Basket update completes
        stateMachine.applyMutation(ProductsMutation.BasketUpdated(emptyList()))
        assertEquals(ProductsTransitionState.Ready, stateMachine.currentState)
    }

    @Test
    fun testCompleteFlow_LoadingToError_RetryToReady() {
        // Start loading
        stateMachine.transition(ProductsIntent.LoadMore)
        assertEquals(ProductsTransitionState.LoadingProducts, stateMachine.currentState)

        // Error occurs
        stateMachine.applyMutation(ProductsMutation.LoadingError("Network error"))
        assertTrue(stateMachine.currentState is ProductsTransitionState.Error)

        // User retries
        stateMachine.transition(ProductsIntent.LoadMore)
        assertEquals(ProductsTransitionState.LoadingProducts, stateMachine.currentState)

        // This time it succeeds
        stateMachine.applyMutation(ProductsMutation.ProductsLoaded(listOf(sampleProduct)))
        assertEquals(ProductsTransitionState.Ready, stateMachine.currentState)
    }

    // Helper methods to set up specific states
    private fun setupReadyState() {
        stateMachine.transition(ProductsIntent.LoadMore)
        stateMachine.applyMutation(ProductsMutation.ProductsLoaded(listOf(sampleProduct)))
    }

    private fun setupErrorState() {
        stateMachine.transition(ProductsIntent.LoadMore)
        stateMachine.applyMutation(ProductsMutation.LoadingError("Test error"))
    }

    private fun setupProcessingBasketState() {
        setupReadyState()
        stateMachine.transition(ProductsIntent.AddToBasket(sampleProduct, 1))
    }
}
