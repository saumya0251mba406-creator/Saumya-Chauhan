package com.example.ui.state

import com.example.data.NivoraCatalog
import com.example.data.ProductCategory

sealed interface NivoraRoute {
    val title: String
    val routeKey: String

    data object Home : NivoraRoute {
        override val title: String = "Nivora — Simple Care"
        override val routeKey: String = "home"
    }

    data class Shop(val initialCategory: ProductCategory? = null) : NivoraRoute {
        override val title: String = "Shop Essentials"
        override val routeKey: String = "shop"
    }

    data class ProductDetails(val productId: String) : NivoraRoute {
        override val title: String =
            NivoraCatalog.getProductById(productId)?.name ?: "Product Not Found"
        override val routeKey: String = "product/$productId"
    }

    data object Bundles : NivoraRoute {
        override val title: String = "Starter Bundles"
        override val routeKey: String = "bundles"
    }

    data object Cart : NivoraRoute {
        override val title: String = "Your Cart"
        override val routeKey: String = "cart"
    }

    data object Checkout : NivoraRoute {
        override val title: String = "Demo Checkout"
        override val routeKey: String = "checkout"
    }

    data class OrderConfirmation(val orderId: String? = null) : NivoraRoute {
        override val title: String = "Order Confirmation"
        override val routeKey: String = "order-confirmation"
    }

    data object About : NivoraRoute {
        override val title: String = "About Nivora"
        override val routeKey: String = "about"
    }

    data class Feedback(val preselectedTargetId: String? = null) : NivoraRoute {
        override val title: String = "Share Feedback"
        override val routeKey: String = "feedback"
    }

    data class NotFound(val attemptedRoute: String) : NivoraRoute {
        override val title: String = "Page Not Found"
        override val routeKey: String = "not-found"
    }

    companion object {
        fun resolve(rawPath: String): NivoraRoute {
            val cleaned = rawPath.trim().trimStart('/').lowercase()
            return when {
                cleaned.isEmpty() || cleaned == "home" -> Home
                cleaned == "shop" -> Shop()
                cleaned == "shop/skincare" -> Shop(ProductCategory.SKINCARE)
                cleaned == "shop/haircare" -> Shop(ProductCategory.HAIRCARE)
                cleaned.startsWith("product/") -> {
                    val id = rawPath.trim().trimStart('/').substringAfter("product/").trim()
                    if (id.isEmpty()) NotFound(rawPath) else ProductDetails(id)
                }
                cleaned == "bundles" || cleaned == "starter-bundles" -> Bundles
                cleaned == "cart" -> Cart
                cleaned == "checkout" -> Checkout
                cleaned == "order-confirmation" -> OrderConfirmation()
                cleaned == "about" -> About
                cleaned == "feedback" -> Feedback()
                else -> NotFound(rawPath)
            }
        }
    }
}
