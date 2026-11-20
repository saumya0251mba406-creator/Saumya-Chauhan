package com.example.data

enum class ProductCategory(val displayName: String) {
    SKINCARE("Skincare"),
    HAIRCARE("Haircare"),
}

enum class PackagingVariant {
    CLEANSER_TUBE,
    LOTION_PUMP,
    SHAMPOO_BOTTLE,
    SERUM_DROPPER,
}

data class PackagingConfig(
    val variant: PackagingVariant,
    val bodyHex: Long,
    val accentHex: Long,
    val labelText: String,
    val subLabelText: String,
    val contentDescription: String,
)

data class Product(
    val id: String,
    val name: String,
    val slug: String,
    val category: ProductCategory,
    val size: String,
    val pricePaise: Int,
    val shortDescription: String,
    val longDescription: String,
    val routineStep: String,
    val packagingConfig: PackagingConfig,
    val illustrativeUsage: String,
    val formulationDisclaimer: String,
)

data class StarterBundle(
    val id: String,
    val name: String,
    val slug: String,
    val description: String,
    val includedProductIds: List<String>,
    val bundlePricePaise: Int,
) {
    val includedProducts: List<Product>
        get() = includedProductIds.mapNotNull { NivoraCatalog.getProductById(it) }

    val individualTotalPaise: Int
        get() = includedProducts.sumOf { it.pricePaise }

    val savingsPaise: Int
        get() = (individualTotalPaise - bundlePricePaise).coerceAtLeast(0)
}

enum class CartItemType {
    PRODUCT,
    BUNDLE,
}

data class CartLineItem(
    val itemId: String,
    val itemType: CartItemType,
    val name: String,
    val subtitle: String,
    val unitPricePaise: Int,
    val quantity: Int,
    val productIdForArtwork: String? = null,
    val bundleIdForArtwork: String? = null,
) {
    val lineTotalPaise: Int
        get() = unitPricePaise * quantity
}

data class CartSummary(
    val itemCount: Int,
    val subtotalPaise: Int,
    val shippingPaise: Int,
    val totalPaise: Int,
    val amountToFreeShippingPaise: Int,
    val qualifiesForFreeShipping: Boolean,
)

data class DemoOrderItem(
    val itemId: String,
    val itemType: CartItemType,
    val name: String,
    val quantity: Int,
    val unitPricePaise: Int,
) {
    val lineTotalPaise: Int
        get() = unitPricePaise * quantity
}

data class DemoOrder(
    val orderId: String,
    val createdAtDisplay: String,
    val items: List<DemoOrderItem>,
    val subtotalPaise: Int,
    val shippingPaise: Int,
    val totalPaise: Int,
)

enum class TriResponse(val label: String) {
    YES("Yes"),
    SOMEWHAT("Somewhat"),
    NO("No"),
}

enum class TryBrandResponse(val label: String) {
    YES("Yes"),
    MAYBE("Maybe"),
    NO("No"),
}

data class FeedbackEntry(
    val id: String,
    val targetId: String,
    val targetLabel: String,
    val rating: Int,
    val infoEasyToUnderstand: TriResponse,
    val pricingAffordable: TriResponse,
    val comment: String,
    val willingToTry: TryBrandResponse?,
    val createdAtDisplay: String,
)

data class PrototypeEventCounts(
    val productView: Int = 0,
    val addToCart: Int = 0,
    val beginCheckout: Int = 0,
    val demoOrderCompleted: Int = 0,
    val feedbackSubmitted: Int = 0,
)

data class CheckoutFormState(
    val fullName: String = "",
    val email: String = "",
    val mobileNumber: String = "",
    val addressLine: String = "",
    val city: String = "",
    val state: String = "",
    val pinCode: String = "",
)

data class CheckoutValidationErrors(
    val fullName: String? = null,
    val email: String? = null,
    val mobileNumber: String? = null,
    val addressLine: String? = null,
    val city: String? = null,
    val state: String? = null,
    val pinCode: String? = null,
) {
    val hasErrors: Boolean
        get() = listOf(fullName, email, mobileNumber, addressLine, city, state, pinCode).any { it != null }
}
