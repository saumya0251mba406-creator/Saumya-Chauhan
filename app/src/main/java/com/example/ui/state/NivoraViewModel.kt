package com.example.ui.state

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.data.CartCalculator
import com.example.data.CheckoutFormState
import com.example.data.CheckoutValidationErrors
import com.example.data.CheckoutValidator
import com.example.data.DemoOrder
import com.example.data.DemoOrderItem
import com.example.data.FeedbackEntry
import com.example.data.NivoraCatalog
import com.example.data.NivoraLocalStore
import com.example.data.ProductCategory
import com.example.data.PrototypeEventCounts
import com.example.data.TriResponse
import com.example.data.TryBrandResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class NivoraViewModel(application: Application) : AndroidViewModel(application) {

    private val store = NivoraLocalStore(application)
    private val _uiState = MutableStateFlow(loadInitialState())
    val uiState: StateFlow<NivoraUiState> = _uiState.asStateFlow()

    private fun loadInitialState(): NivoraUiState {
        val cart = store.loadCart()
        val orders = store.loadOrders()
        val lastOrderId = store.loadLastOrderId()
        val feedback = store.loadFeedback()
        val events = store.loadEvents()
        return NivoraUiState(
            backStack = listOf(NivoraRoute.Home),
            cartItems = cart,
            cartSummary = CartCalculator.calculateSummary(cart),
            orders = orders,
            lastOrderId = lastOrderId,
            feedbackEntries = feedback,
            eventCounts = events,
        )
    }

    fun navigateTo(route: NivoraRoute) {
        _uiState.update { state ->
            val updatedShopFilters = when (route) {
                is NivoraRoute.Shop -> if (route.initialCategory != null) {
                    state.shopFilters.copy(selectedCategory = route.initialCategory)
                } else {
                    state.shopFilters
                }
                else -> state.shopFilters
            }
            val updatedFeedbackForm = when (route) {
                is NivoraRoute.Feedback -> if (route.preselectedTargetId != null) {
                    state.feedbackForm.copy(targetId = route.preselectedTargetId)
                } else {
                    state.feedbackForm
                }
                else -> state.feedbackForm
            }
            val feedbackResetFlag = if (route is NivoraRoute.Feedback) false else state.feedbackSubmittedJustNow

            var updatedEvents = state.eventCounts
            if (route is NivoraRoute.ProductDetails && NivoraCatalog.getProductById(route.productId) != null) {
                updatedEvents = updatedEvents.copy(productView = updatedEvents.productView + 1)
                store.saveEvents(updatedEvents)
            } else if (route is NivoraRoute.Checkout && state.cartItems.isNotEmpty()) {
                updatedEvents = updatedEvents.copy(beginCheckout = updatedEvents.beginCheckout + 1)
                store.saveEvents(updatedEvents)
            }

            val current = state.currentRoute
            val nextStack = if (current == route) {
                state.backStack
            } else if (route is NivoraRoute.Home) {
                listOf(NivoraRoute.Home)
            } else {
                state.backStack + route
            }

            state.copy(
                backStack = nextStack,
                shopFilters = updatedShopFilters,
                feedbackForm = updatedFeedbackForm,
                feedbackSubmittedJustNow = feedbackResetFlag,
                eventCounts = updatedEvents,
            )
        }
    }

    fun navigateBack(): Boolean {
        var didPop = false
        _uiState.update { state ->
            if (state.backStack.size > 1) {
                didPop = true
                state.copy(backStack = state.backStack.dropLast(1))
            } else {
                state
            }
        }
        return didPop
    }

    fun navigateToRawPath(path: String) {
        navigateTo(NivoraRoute.resolve(path))
    }

    // --- Shop filters & sorting ---

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(shopFilters = it.shopFilters.copy(searchQuery = query)) }
    }

    fun selectCategory(category: ProductCategory?) {
        _uiState.update { it.copy(shopFilters = it.shopFilters.copy(selectedCategory = category)) }
    }

    fun selectSortOption(sortOption: ShopSortOption) {
        _uiState.update { it.copy(shopFilters = it.shopFilters.copy(sortOption = sortOption)) }
    }

    fun clearShopFilters() {
        _uiState.update { it.copy(shopFilters = ShopFilterState()) }
    }

    // --- Cart actions ---

    fun addToCart(itemId: String, quantity: Int = 1) {
        _uiState.update { state ->
            val result = CartCalculator.addItem(state.cartItems, itemId, quantity)
                ?: return@update state.withStatus("Item could not be added.", isError = true)

            store.saveCart(result.updatedItems)
            val updatedEvents = state.eventCounts.copy(addToCart = state.eventCounts.addToCart + 1)
            store.saveEvents(updatedEvents)

            val itemName = NivoraCatalog.getProductById(itemId)?.name
                ?: NivoraCatalog.getBundleById(itemId)?.name
                ?: "Item"

            val message = when {
                result.hitMaxLimit && result.addedQuantity == 0 ->
                    "$itemName is already at the maximum limit of ${CartCalculator.MAX_ITEM_QUANTITY} per item."
                result.hitMaxLimit ->
                    "Added ${result.addedQuantity} $itemName (reached max ${CartCalculator.MAX_ITEM_QUANTITY} per item)."
                else ->
                    "Added ${result.addedQuantity} × $itemName to your cart."
            }

            state.copy(
                cartItems = result.updatedItems,
                cartSummary = CartCalculator.calculateSummary(result.updatedItems),
                eventCounts = updatedEvents,
                statusMessage = NivoraStatusMessage(
                    id = System.nanoTime(),
                    text = message,
                    isError = result.hitMaxLimit && result.addedQuantity == 0,
                ),
            )
        }
    }

    fun updateCartItemQuantity(itemId: String, newQuantity: Int) {
        _uiState.update { state ->
            val result = CartCalculator.updateQuantity(state.cartItems, itemId, newQuantity)
            if (result.errorMessage != null) {
                return@update state.withStatus(result.errorMessage, isError = true)
            }
            store.saveCart(result.updatedItems)
            state.copy(
                cartItems = result.updatedItems,
                cartSummary = CartCalculator.calculateSummary(result.updatedItems),
            )
        }
    }

    fun updateCartItemQuantityFromText(itemId: String, rawText: String): String? {
        val parsed = CartCalculator.parseDirectQuantity(rawText)
        return parsed.fold(
            onSuccess = { qty ->
                updateCartItemQuantity(itemId, qty)
                null
            },
            onFailure = { err ->
                val msg = err.message ?: "Enter a valid quantity (1–10)."
                showStatus(msg, isError = true)
                msg
            },
        )
    }

    fun removeFromCart(itemId: String) {
        _uiState.update { state ->
            val removed = state.cartItems.find { it.itemId == itemId }
            val updated = state.cartItems.filterNot { it.itemId == itemId }
            store.saveCart(updated)
            state.copy(
                cartItems = updated,
                cartSummary = CartCalculator.calculateSummary(updated),
                statusMessage = if (removed != null) {
                    NivoraStatusMessage(
                        id = System.nanoTime(),
                        text = "Removed ${removed.name} from your cart.",
                    )
                } else state.statusMessage,
            )
        }
    }

    // --- Checkout actions (in-memory only for contact/address) ---

    fun updateCheckoutForm(transform: (CheckoutFormState) -> CheckoutFormState) {
        _uiState.update { state ->
            val nextForm = transform(state.checkoutForm)
            val revalidated = if (state.checkoutErrors.hasErrors) {
                CheckoutValidator.validate(nextForm)
            } else {
                state.checkoutErrors
            }
            state.copy(checkoutForm = nextForm, checkoutErrors = revalidated)
        }
    }

    fun submitDemoOrder() {
        val currentState = _uiState.value
        if (currentState.isSubmittingOrder) return
        if (currentState.cartItems.isEmpty()) {
            showStatus("Your cart is empty. Add an essential before checking out.", isError = true)
            return
        }

        val errors = CheckoutValidator.validate(currentState.checkoutForm)
        if (errors.hasErrors) {
            _uiState.update {
                it.copy(
                    checkoutErrors = errors,
                    statusMessage = NivoraStatusMessage(
                        id = System.nanoTime(),
                        text = "Please fix the highlighted checkout fields.",
                        isError = true,
                    ),
                )
            }
            return
        }

        _uiState.update { it.copy(isSubmittingOrder = true) }

        val summary = CartCalculator.calculateSummary(currentState.cartItems)
        val orderItems = currentState.cartItems.map { line ->
            DemoOrderItem(
                itemId = line.itemId,
                itemType = line.itemType,
                name = line.name,
                quantity = line.quantity,
                unitPricePaise = line.unitPricePaise,
            )
        }
        val order = DemoOrder(
            orderId = generateOrderId(),
            createdAtDisplay = formatNow(),
            items = orderItems,
            subtotalPaise = summary.subtotalPaise,
            shippingPaise = summary.shippingPaise,
            totalPaise = summary.totalPaise,
        )

        // Persist minimal non-sensitive order first, then clear cart
        store.saveOrder(order)
        store.saveCart(emptyList())

        val updatedEvents = currentState.eventCounts.copy(
            demoOrderCompleted = currentState.eventCounts.demoOrderCompleted + 1,
        )
        store.saveEvents(updatedEvents)

        _uiState.update { state ->
            val cleanedStack = state.backStack.filterNot { it is NivoraRoute.Checkout } +
                NivoraRoute.OrderConfirmation(order.orderId)
            state.copy(
                backStack = cleanedStack,
                cartItems = emptyList(),
                cartSummary = CartCalculator.calculateSummary(emptyList()),
                orders = listOf(order) + state.orders,
                lastOrderId = order.orderId,
                eventCounts = updatedEvents,
                // Clear temporary personal fields immediately
                checkoutForm = CheckoutFormState(),
                checkoutErrors = CheckoutValidationErrors(),
                isSubmittingOrder = false,
                statusMessage = NivoraStatusMessage(
                    id = System.nanoTime(),
                    text = "Demo order ${order.orderId} confirmed.",
                ),
            )
        }
    }

    // --- Feedback actions ---

    fun updateFeedbackForm(transform: (FeedbackFormState) -> FeedbackFormState) {
        _uiState.update { state ->
            val updated = transform(state.feedbackForm)
            val safeComment = if (updated.comment.length > 500) {
                updated.comment.take(500)
            } else {
                updated.comment
            }
            state.copy(
                feedbackForm = updated.copy(comment = safeComment),
                feedbackSubmittedJustNow = false,
            )
        }
    }

    fun submitFeedback() {
        val state = _uiState.value
        if (state.isSubmittingFeedback) return

        val form = state.feedbackForm
        val errors = FeedbackValidationErrors(
            rating = if (form.rating == null || form.rating !in 1..5) {
                "Select an overall experience rating from 1 to 5."
            } else null,
            infoEasyToUnderstand = if (form.infoEasyToUnderstand == null) {
                "Let us know if the product information was easy to understand."
            } else null,
            pricingAffordable = if (form.pricingAffordable == null) {
                "Let us know if the pricing felt affordable."
            } else null,
            comment = if (form.comment.length > 500) {
                "Comments must be 500 characters or fewer."
            } else null,
        )

        if (errors.hasErrors) {
            _uiState.update {
                it.copy(
                    feedbackErrors = errors,
                    statusMessage = NivoraStatusMessage(
                        id = System.nanoTime(),
                        text = "Please complete the required feedback questions.",
                        isError = true,
                    ),
                )
            }
            return
        }

        _uiState.update { it.copy(isSubmittingFeedback = true) }

        val targetLabel = NivoraCatalog.feedbackTargetOptions()
            .find { it.first == form.targetId }
            ?.second ?: NivoraCatalog.GENERAL_FEEDBACK_TARGET_LABEL

        val entry = FeedbackEntry(
            id = "fb-${UUID.randomUUID().toString().take(8)}",
            targetId = form.targetId,
            targetLabel = targetLabel,
            rating = form.rating ?: 5,
            infoEasyToUnderstand = form.infoEasyToUnderstand ?: TriResponse.YES,
            pricingAffordable = form.pricingAffordable ?: TriResponse.YES,
            comment = form.comment.trim(),
            willingToTry = form.willingToTry,
            createdAtDisplay = formatNow(),
        )

        store.saveFeedback(entry)
        val updatedEvents = state.eventCounts.copy(
            feedbackSubmitted = state.eventCounts.feedbackSubmitted + 1,
        )
        store.saveEvents(updatedEvents)

        _uiState.update { current ->
            current.copy(
                feedbackEntries = listOf(entry) + current.feedbackEntries,
                eventCounts = updatedEvents,
                feedbackForm = FeedbackFormState(targetId = form.targetId),
                feedbackErrors = FeedbackValidationErrors(),
                isSubmittingFeedback = false,
                feedbackSubmittedJustNow = true,
                statusMessage = NivoraStatusMessage(
                    id = System.nanoTime(),
                    text = "Thank you — your prototype feedback was saved locally.",
                ),
            )
        }
    }

    // --- Reset demo data ---

    fun requestResetDemoData() {
        _uiState.update { it.copy(showResetConfirmationDialog = true) }
    }

    fun dismissResetDemoData() {
        _uiState.update { it.copy(showResetConfirmationDialog = false) }
    }

    fun confirmResetDemoData() {
        store.resetDemoData()
        _uiState.update { state ->
            val safeRoute = when (state.currentRoute) {
                is NivoraRoute.OrderConfirmation, is NivoraRoute.Checkout -> listOf(NivoraRoute.Home)
                else -> state.backStack
            }
            state.copy(
                backStack = safeRoute,
                cartItems = emptyList(),
                cartSummary = CartCalculator.calculateSummary(emptyList()),
                orders = emptyList(),
                lastOrderId = null,
                feedbackEntries = emptyList(),
                eventCounts = PrototypeEventCounts(),
                checkoutForm = CheckoutFormState(),
                checkoutErrors = CheckoutValidationErrors(),
                feedbackForm = FeedbackFormState(),
                feedbackErrors = FeedbackValidationErrors(),
                feedbackSubmittedJustNow = false,
                showResetConfirmationDialog = false,
                statusMessage = NivoraStatusMessage(
                    id = System.nanoTime(),
                    text = "Nivora demo data has been reset on this device.",
                ),
            )
        }
    }

    fun dismissStatusMessage() {
        _uiState.update { it.copy(statusMessage = null) }
    }

    fun showStatus(text: String, isError: Boolean = false) {
        _uiState.update {
            it.copy(statusMessage = NivoraStatusMessage(id = System.nanoTime(), text = text, isError = isError))
        }
    }

    private fun NivoraUiState.withStatus(text: String, isError: Boolean = false): NivoraUiState =
        copy(statusMessage = NivoraStatusMessage(id = System.nanoTime(), text = text, isError = isError))

    private fun generateOrderId(): String {
        val datePart = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
        val suffix = UUID.randomUUID().toString().replace("-", "").take(5).uppercase(Locale.US)
        return "NIV-$datePart-$suffix"
    }

    private fun formatNow(): String =
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US).format(Date())
}
