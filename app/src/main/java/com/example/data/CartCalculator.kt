package com.example.data

object CartCalculator {
    const val MIN_ITEM_QUANTITY = 1
    const val MAX_ITEM_QUANTITY = 10
    const val FREE_SHIPPING_THRESHOLD_PAISE = 69900
    const val STANDARD_SHIPPING_PAISE = 4900

    fun calculateSummary(items: List<CartLineItem>): CartSummary {
        val itemCount = items.sumOf { it.quantity }
        val subtotalPaise = items.sumOf { it.unitPricePaise * it.quantity }
        val shippingPaise = when {
            items.isEmpty() || subtotalPaise == 0 -> 0
            subtotalPaise < FREE_SHIPPING_THRESHOLD_PAISE -> STANDARD_SHIPPING_PAISE
            else -> 0
        }
        val totalPaise = subtotalPaise + shippingPaise
        val remaining = if (subtotalPaise in 1 until FREE_SHIPPING_THRESHOLD_PAISE) {
            FREE_SHIPPING_THRESHOLD_PAISE - subtotalPaise
        } else {
            0
        }
        val qualifies = subtotalPaise >= FREE_SHIPPING_THRESHOLD_PAISE
        return CartSummary(
            itemCount = itemCount,
            subtotalPaise = subtotalPaise,
            shippingPaise = shippingPaise,
            totalPaise = totalPaise,
            amountToFreeShippingPaise = remaining,
            qualifiesForFreeShipping = qualifies,
        )
    }

    data class AddItemResult(
        val updatedItems: List<CartLineItem>,
        val addedQuantity: Int,
        val hitMaxLimit: Boolean,
    )

    fun addItem(
        currentItems: List<CartLineItem>,
        itemId: String,
        requestedQuantity: Int,
    ): AddItemResult? {
        val safeQty = requestedQuantity.coerceIn(MIN_ITEM_QUANTITY, MAX_ITEM_QUANTITY)
        val existingIndex = currentItems.indexOfFirst { it.itemId == itemId }
        if (existingIndex >= 0) {
            val existing = currentItems[existingIndex]
            val combined = existing.quantity + safeQty
            val hitMax = combined > MAX_ITEM_QUANTITY
            val finalQty = combined.coerceAtMost(MAX_ITEM_QUANTITY)
            val actualAdded = finalQty - existing.quantity
            val updated = currentItems.toMutableList().apply {
                this[existingIndex] = existing.copy(quantity = finalQty)
            }
            return AddItemResult(
                updatedItems = updated,
                addedQuantity = actualAdded,
                hitMaxLimit = hitMax,
            )
        } else {
            val newItem = NivoraCatalog.buildCartLineItem(itemId, safeQty) ?: return null
            return AddItemResult(
                updatedItems = currentItems + newItem,
                addedQuantity = newItem.quantity,
                hitMaxLimit = false,
            )
        }
    }

    data class UpdateQtyResult(
        val updatedItems: List<CartLineItem>,
        val errorMessage: String? = null,
    )

    fun updateQuantity(
        currentItems: List<CartLineItem>,
        itemId: String,
        rawQuantityInput: Int,
    ): UpdateQtyResult {
        if (rawQuantityInput < MIN_ITEM_QUANTITY) {
            return UpdateQtyResult(
                updatedItems = currentItems,
                errorMessage = "Quantity must be at least $MIN_ITEM_QUANTITY. Use Remove to delete this item.",
            )
        }
        if (rawQuantityInput > MAX_ITEM_QUANTITY) {
            return UpdateQtyResult(
                updatedItems = currentItems,
                errorMessage = "Maximum $MAX_ITEM_QUANTITY units allowed per item in this prototype.",
            )
        }
        val updated = currentItems.map { item ->
            if (item.itemId == itemId) item.copy(quantity = rawQuantityInput) else item
        }
        return UpdateQtyResult(updatedItems = updated, errorMessage = null)
    }

    fun parseDirectQuantity(text: String): Result<Int> {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            return Result.failure(IllegalArgumentException("Enter a quantity between 1 and $MAX_ITEM_QUANTITY."))
        }
        val parsed = trimmed.toIntOrNull()
            ?: return Result.failure(IllegalArgumentException("Quantity must be a whole number between 1 and $MAX_ITEM_QUANTITY."))
        if (parsed < MIN_ITEM_QUANTITY) {
            return Result.failure(IllegalArgumentException("Quantity must be at least $MIN_ITEM_QUANTITY."))
        }
        if (parsed > MAX_ITEM_QUANTITY) {
            return Result.failure(IllegalArgumentException("Maximum $MAX_ITEM_QUANTITY units allowed per item."))
        }
        return Result.success(parsed)
    }
}
