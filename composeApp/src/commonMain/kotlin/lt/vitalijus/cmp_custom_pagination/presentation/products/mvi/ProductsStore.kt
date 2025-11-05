package lt.vitalijus.cmp_custom_pagination.presentation.products.mvi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lt.vitalijus.cmp_custom_pagination.core.utils.pager.ProductPager
import lt.vitalijus.cmp_custom_pagination.domain.model.Product
import lt.vitalijus.cmp_custom_pagination.domain.paging.PagingEvent
import lt.vitalijus.cmp_custom_pagination.domain.paging.ProductPagingFactory
import lt.vitalijus.cmp_custom_pagination.domain.usecase.basket.AddToBasketUseCase
import lt.vitalijus.cmp_custom_pagination.presentation.products.Screen

/**
 * Redux-style Store for managing Products state:
 * - handles intents, mutations, and emits effects.
 * - includes state machine validation for intents.
 */
class ProductsStore(
    pagerFactory: ProductPagingFactory,
    private val addToBasketUseCase: AddToBasketUseCase,
    private val stateMachine: ProductsStateMachine,
    private val scope: CoroutineScope
) {
    private val _state = MutableStateFlow(ProductsState())
    val state = _state.asStateFlow()

    private val _effects = Channel<ProductsEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private val _intentFlow = MutableSharedFlow<ProductsIntent>(
        extraBufferCapacity = 64,
        replay = 0
    )
    val intentFlow = _intentFlow.asSharedFlow()

    init {
        // Collect intents and process them through the state machine
        scope.launch {
            intentFlow.collect { intent ->
                handleIntent(intent)
            }
        }
    }

    private val pager: ProductPager = pagerFactory.create { event ->
        when (event) {
            is PagingEvent.LoadingChanged -> {
                dispatchMutation(
                    mutation = ProductsMutation.SetLoading(isLoading = event.isLoading)
                )
            }

            is PagingEvent.ProductsLoaded -> {
                dispatchMutation(mutation = ProductsMutation.ProductsLoaded(products = event.products))
            }

            is PagingEvent.Error -> {
                dispatchMutation(
                    mutation = ProductsMutation.LoadingError(
                        message = event.message ?: "Unknown error"
                    )
                )
                emitEffect(
                    effect = ProductsEffect.ShowError(
                        message = event.message ?: "Unknown error"
                    )
                )
            }
        }
    }

    /**
     * Main entry point for user intents.
     * Emits to SharedFlow for state machine processing.
     */
    fun processIntent(intent: ProductsIntent) {
        _intentFlow.tryEmit(intent)
    }

    /**
     * Handles intents with state machine validation.
     * The state machine returns the validated intent to execute based on state transitions.
     */
    private fun handleIntent(intent: ProductsIntent) {
        try {
            // Validate transition
            stateMachine.transition(intent)

            // Execute the intent
            when (intent) {
                ProductsIntent.LoadMore -> handleLoadMore()

                is ProductsIntent.AddToBasket -> handleAddToBasket(intent.product, intent.quantity)

                is ProductsIntent.UpdateQuantity -> handleUpdateQuantity(
                    intent.productId,
                    intent.newQuantity
                )

                is ProductsIntent.RemoveProduct -> handleRemoveProduct(intent.productId)

                ProductsIntent.ClearBasket -> handleClearBasket()

                is ProductsIntent.NavigateTo -> handleNavigation(intent.screen)
            }
        } catch (_: IllegalStateException) {
            // Invalid transition detected
            println("Invalid transition blocked: ${stateMachine.currentState::class.simpleName} + ${intent::class.simpleName}")
            emitEffect(ProductsEffect.ShowError("Action not available in current state"))
        }
    }

    /**
     * Dispatch a mutation to update state via reducer.
     * Also updates the state machine based on mutation results.
     */
    private fun dispatchMutation(mutation: ProductsMutation) {
        _state.update { currentState ->
            ProductsReducer.reduce(
                state = currentState,
                mutation = mutation
            )
        }

        stateMachine.applyMutation(mutation = mutation)
    }

    private fun emitEffect(effect: ProductsEffect) {
        scope.launch {
            _effects.send(effect)
        }
    }

    private fun handleLoadMore() {
        scope.launch {
            pager.loadNextProducts()
        }
    }

    private fun handleAddToBasket(
        product: Product,
        quantity: Int
    ) {
        val result = addToBasketUseCase.execute(
            currentItems = _state.value.basketItems,
            product = product,
            quantity = quantity
        )

        result.fold(
            onSuccess = { updatedItems ->
                dispatchMutation(mutation = ProductsMutation.BasketUpdated(items = updatedItems))
                emitEffect(effect = ProductsEffect.ShowBasketUpdated)
            },
            onFailure = { error ->
                emitEffect(
                    effect = ProductsEffect.ShowError(
                        message = error.message ?: "Failed to add to basket"
                    )
                )
            }
        )
    }

    private fun handleUpdateQuantity(productId: Long, newQuantity: Int) {
        if (newQuantity <= 0) {
            handleRemoveProduct(productId = productId)
            return
        }

        val updatedItems = _state.value.basketItems.map { item ->
            if (item.product.id == productId) {
                item.copy(quantity = newQuantity)
            } else {
                item
            }
        }

        dispatchMutation(mutation = ProductsMutation.BasketUpdated(items = updatedItems))
    }

    private fun handleRemoveProduct(productId: Long) {
        val updatedItems = _state.value.basketItems.filter { it.product.id != productId }
        dispatchMutation(mutation = ProductsMutation.BasketUpdated(items = updatedItems))
    }

    private fun handleClearBasket() {
        dispatchMutation(mutation = ProductsMutation.BasketUpdated(items = emptyList()))
    }

    private fun handleNavigation(screen: Screen) {
        emitEffect(effect = ProductsEffect.NavigateTo(screen))
    }
}
