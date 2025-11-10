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
import lt.vitalijus.cmp_custom_pagination.core.utils.currentTimeMillis
import lt.vitalijus.cmp_custom_pagination.core.utils.pager.ProductPager
import lt.vitalijus.cmp_custom_pagination.data.persistence.OrderRepository
import lt.vitalijus.cmp_custom_pagination.domain.model.Product
import lt.vitalijus.cmp_custom_pagination.domain.model.DeliveryAddress
import lt.vitalijus.cmp_custom_pagination.domain.model.PaymentMethod
import lt.vitalijus.cmp_custom_pagination.domain.model.Order
import lt.vitalijus.cmp_custom_pagination.domain.model.OrderStatus
import lt.vitalijus.cmp_custom_pagination.domain.model.ProductRating
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
    private val orderRepository: OrderRepository,
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
        // Load persisted data
        scope.launch {
            loadPersistedData()
        }

        // Collect intents and process them through the state machine
        scope.launch {
            intentFlow.collect { intent ->
                handleIntent(intent)
            }
        }
    }

    private suspend fun loadPersistedData() {
        val orders = orderRepository.getOrders()
        val favorites = orderRepository.getFavorites()
        val deliveryAddress = orderRepository.getLastDeliveryAddress()
        val paymentMethod = orderRepository.getLastPaymentMethod()

        _state.update {
            it.copy(
                orders = orders,
                favoriteProductIds = favorites,
                currentDeliveryAddress = deliveryAddress,
                currentPaymentMethod = paymentMethod
            )
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
     * The state machine validates transitions and blocks invalid ones.
     * Invalid transitions during pagination are expected (fast scrolling) and handled silently.
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

                is ProductsIntent.ToggleFavorite -> handleToggleFavorite(intent.productId)

                is ProductsIntent.SetDeliveryAddress -> handleSetDeliveryAddress(intent.address)

                is ProductsIntent.SetPaymentMethod -> handleSetPaymentMethod(intent.paymentMethod)

                ProductsIntent.ConfirmOrder -> handleConfirmOrder()

                is ProductsIntent.UpdateOrderStatus -> handleUpdateOrderStatus(
                    intent.orderId,
                    intent.status
                )

                is ProductsIntent.RateProduct -> handleRateProduct(
                    intent.orderId,
                    intent.productId,
                    intent.rating,
                    intent.comment
                )
            }
        } catch (_: IllegalStateException) {
            // Invalid transition blocked by state machine
            // This is expected during fast scrolling/pagination - silently ignore
        }
    }

    /**
     * Dispatch a mutation to update state via reducer.
     * Also updates the state machine based on mutation results.
     */
    private fun dispatchMutation(mutation: ProductsMutation) {
        _state.update { currentState ->
            val newState = ProductsReducer.reduce(
                state = currentState,
                mutation = mutation
            )
            scope.launch {
                persistState(newState, mutation)
            }
            newState
        }

        stateMachine.applyMutation(mutation = mutation)
    }

    private suspend fun persistState(state: ProductsState, mutation: ProductsMutation) {
        // Only persist relevant mutations to avoid excessive writes
        when (mutation) {
            is ProductsMutation.OrderCreated,
            is ProductsMutation.OrderUpdated -> {
                state.orders.forEach { order ->
                    orderRepository.saveOrder(order)
                }
            }

            is ProductsMutation.FavoriteToggled -> {
                orderRepository.saveFavorites(state.favoriteProductIds)
            }

            is ProductsMutation.DeliveryAddressSet -> {
                state.currentDeliveryAddress?.let {
                    orderRepository.saveDeliveryAddress(it)
                }
            }

            is ProductsMutation.PaymentMethodSet -> {
                state.currentPaymentMethod?.let {
                    orderRepository.savePaymentMethod(it)
                }
            }

            else -> { /* No persistence needed for other mutations */
            }
        }
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

    private fun handleToggleFavorite(productId: Long) {
        val currentFavorites = _state.value.favoriteProductIds
        val isAdded = !currentFavorites.contains(productId)

        dispatchMutation(mutation = ProductsMutation.FavoriteToggled(productId = productId))
        emitEffect(effect = ProductsEffect.ShowFavoriteToggled(isAdded = isAdded))
    }

    private fun handleSetDeliveryAddress(address: DeliveryAddress) {
        dispatchMutation(mutation = ProductsMutation.DeliveryAddressSet(address = address))
    }

    private fun handleSetPaymentMethod(paymentMethod: PaymentMethod) {
        dispatchMutation(mutation = ProductsMutation.PaymentMethodSet(paymentMethod = paymentMethod))
    }

    private fun handleConfirmOrder() {
        val currentState = _state.value
        val address = currentState.currentDeliveryAddress
        val payment = currentState.currentPaymentMethod
        val items = currentState.basketItems

        if (address == null || payment == null || items.isEmpty()) {
            emitEffect(effect = ProductsEffect.ShowError(message = "Missing order information"))
            return
        }

        val nowMillis = currentTimeMillis()
        val order = Order(
            id = "ORDER-$nowMillis",
            items = items,
            totalAmount = currentState.totalRetailPrice,
            status = OrderStatus.PAYMENT_PROCESSING,
            deliveryAddress = address,
            paymentMethod = payment,
            createdAt = nowMillis,
            estimatedDeliveryTime = nowMillis + (30 * 60 * 1000) // 30 minutes
        )

        dispatchMutation(mutation = ProductsMutation.OrderCreated(order = order))

        emitEffect(ProductsEffect.OrderCreated(order.id))

        // Start delivery simulation
        scope.launch {
            simulateOrderDelivery(order.id)
        }
    }

    private suspend fun simulateOrderDelivery(orderId: String) {
        val statuses = listOf(
            OrderStatus.PAYMENT_CONFIRMED to 2000L,
            OrderStatus.PREPARING to 5000L,
            OrderStatus.OUT_FOR_DELIVERY to 8000L,
            OrderStatus.DELIVERED to 10000L
        )

        for ((status, delay) in statuses) {
            kotlinx.coroutines.delay(delay)
            handleUpdateOrderStatus(orderId, status)
        }
    }

    private fun handleUpdateOrderStatus(orderId: String, status: OrderStatus) {
        val currentOrder = _state.value.orders.find { it.id == orderId } ?: return

        val updatedOrder = currentOrder.copy(
            status = status,
            actualDeliveryTime = if (status == OrderStatus.DELIVERED) {
                currentTimeMillis()
            } else {
                currentOrder.actualDeliveryTime
            }
        )

        dispatchMutation(mutation = ProductsMutation.OrderUpdated(order = updatedOrder))

        if (status == OrderStatus.DELIVERED) {
            emitEffect(effect = ProductsEffect.OrderDelivered(orderId = orderId))
        }
    }

    private fun handleRateProduct(orderId: String, productId: Long, rating: Int, comment: String) {
        val currentOrder = _state.value.orders.find { it.id == orderId } ?: return

        val productRating = ProductRating(
            productId = productId,
            rating = rating,
            comment = comment,
            createdAt = currentTimeMillis()
        )

        val updatedRatings = currentOrder.ratings + (productId to productRating)
        val updatedOrder = currentOrder.copy(ratings = updatedRatings)

        dispatchMutation(mutation = ProductsMutation.OrderUpdated(order = updatedOrder))
    }
}
