package com.example.ui.state

import com.example.data.CartCalculator
import com.example.data.CartLineItem
import com.example.data.CartSummary
import com.example.data.CheckoutFormState
import com.example.data.CheckoutValidationErrors
import com.example.data.DemoOrder
import com.example.data.FeedbackEntry
import com.example.data.NivoraCatalog
import com.example.data.Product
import com.example.data.ProductCategory
import com.example.data.PrototypeEventCounts
import com.example.data.TriResponse
import com.example.data.TryBrandResponse

enum class ShopSortOption(val label: String) {
    FEATURED("Featured"),
    PRICE_LOW_TO_HIGH("Price: Low to High"),
    PRICE_HIGH_TO_LOW("Price: High to Low"),
}

data class ShopFilterState(
    val searchQuery: String = "",
    val selectedCategory: ProductCategory? = null,
    val sortOption: ShopSortOption = ShopSortOption.FEATURED,
) {
    val hasActiveFilters: Boolean
        get() = searchQuery.isNotBlank() || selectedCategory != null || sortOption != ShopSortOption.FEATURED

    fun applyTo(products: List<Product>): List<Product> {
        val query = searchQuery.trim().lowercase()
        val filtered = products.filter { product ->
            val matchesCategory = selectedCategory == null || product.category == selectedCategory
            val matchesQuery = query.isEmpty() ||
                product.name.lowercase().contains(query) ||
                product.shortDescription.lowercase().contains(query) ||
                product.routineStep.lowercase().contains(query)
            matchesCategory && matchesQuery
        }
        return when (sortOption) {
            ShopSortOption.FEATURED -> filtered
            ShopSortOption.PRICE_LOW_TO_HIGH -> filtered.sortedBy { it.pricePaise }
            ShopSortOption.PRICE_HIGH_TO_LOW -> filtered.sortedByDescending { it.pricePaise }
        }
    }
}

data class FeedbackFormState(
    val targetId: String = NivoraCatalog.GENERAL_FEEDBACK_TARGET_ID,
    val rating: Int? = null,
    val infoEasyToUnderstand: TriResponse? = null,
    val pricingAffordable: TriResponse? = null,
    val comment: String = "",
    val willingToTry: TryBrandResponse? = null,
)

data class FeedbackValidationErrors(
    val rating: String? = null,
    val infoEasyToUnderstand: String? = null,
    val pricingAffordable: String? = null,
    val comment: String? = null,
) {
    val hasErrors: Boolean
        get() = rating != null || infoEasyToUnderstand != null || pricingAffordable != null || comment != null
}

data class NivoraStatusMessage(
    val id: Long,
    val text: String,
    val isError: Boolean = false,
)

data class NivoraUiState(
    val backStack: List<NivoraRoute> = listOf(NivoraRoute.Home),
    val shopFilters: ShopFilterState = ShopFilterState(),
    val cartItems: List<CartLineItem> = emptyList(),
    val cartSummary: CartSummary = CartCalculator.calculateSummary(emptyList()),
    val orders: List<DemoOrder> = emptyList(),
    val lastOrderId: String? = null,
    val feedbackEntries: List<FeedbackEntry> = emptyList(),
    val eventCounts: PrototypeEventCounts = PrototypeEventCounts(),
    // Temporary in-memory checkout state (never persisted)
    val checkoutForm: CheckoutFormState = CheckoutFormState(),
    val checkoutErrors: CheckoutValidationErrors = CheckoutValidationErrors(),
    val isSubmittingOrder: Boolean = false,
    // Feedback form state
    val feedbackForm: FeedbackFormState = FeedbackFormState(),
    val feedbackErrors: FeedbackValidationErrors = FeedbackValidationErrors(),
    val isSubmittingFeedback: Boolean = false,
    val feedbackSubmittedJustNow: Boolean = false,
    // UI dialogs & notifications
    val showResetConfirmationDialog: Boolean = false,
    val statusMessage: NivoraStatusMessage? = null,
) {
    val currentRoute: NivoraRoute
        get() = backStack.lastOrNull() ?: NivoraRoute.Home

    val canNavigateBack: Boolean
        get() = backStack.size > 1

    val filteredProducts: List<Product>
        get() = shopFilters.applyTo(NivoraCatalog.products)

    fun resolveConfirmedOrder(orderId: String?): DemoOrder? {
        val targetId = orderId ?: lastOrderId ?: return null
        return orders.find { it.orderId == targetId }
    }
}
