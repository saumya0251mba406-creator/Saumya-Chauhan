package com.example.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

/**
 * Local-only prototype persistence backed by versioned, Nivora-prefixed SharedPreferences keys.
 * Safely ignores corrupted, outdated, or unparseable data without crashing.
 * Never persists checkout contact or address information.
 */
class NivoraLocalStore(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun loadCart(): List<CartLineItem> {
        val raw = prefs.getString(KEY_CART, null) ?: return emptyList()
        return try {
            val array = JSONArray(raw)
            val merged = mutableListOf<CartLineItem>()
            for (i in 0 until array.length()) {
                val obj = array.optJSONObject(i) ?: continue
                val itemId = obj.optString("itemId", "").trim()
                val quantity = obj.optInt("quantity", -1)
                if (itemId.isEmpty() || quantity < CartCalculator.MIN_ITEM_QUANTITY) continue
                val addResult = CartCalculator.addItem(merged, itemId, quantity)
                if (addResult != null) {
                    merged.clear()
                    merged.addAll(addResult.updatedItems)
                }
            }
            merged
        } catch (_: Throwable) {
            emptyList()
        }
    }

    fun saveCart(items: List<CartLineItem>) {
        val array = JSONArray()
        items.forEach { item ->
            val obj = JSONObject().apply {
                put("itemId", item.itemId)
                put("quantity", item.quantity.coerceIn(CartCalculator.MIN_ITEM_QUANTITY, CartCalculator.MAX_ITEM_QUANTITY))
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_CART, array.toString()).apply()
    }

    fun loadOrders(): List<DemoOrder> {
        val raw = prefs.getString(KEY_ORDERS, null) ?: return emptyList()
        return try {
            val array = JSONArray(raw)
            buildList {
                for (i in 0 until array.length()) {
                    val obj = array.optJSONObject(i) ?: continue
                    val orderId = obj.optString("orderId", "").trim()
                    val createdAt = obj.optString("createdAtDisplay", "").trim()
                    if (orderId.isEmpty() || createdAt.isEmpty()) continue
                    val itemsJson = obj.optJSONArray("items") ?: continue
                    val items = mutableListOf<DemoOrderItem>()
                    for (j in 0 until itemsJson.length()) {
                        val itemObj = itemsJson.optJSONObject(j) ?: continue
                        val itemId = itemObj.optString("itemId", "")
                        val name = itemObj.optString("name", "")
                        val typeStr = itemObj.optString("itemType", CartItemType.PRODUCT.name)
                        val itemType = runCatching { CartItemType.valueOf(typeStr) }.getOrDefault(CartItemType.PRODUCT)
                        val qty = itemObj.optInt("quantity", 0)
                        val unitPrice = itemObj.optInt("unitPricePaise", -1)
                        if (itemId.isNotEmpty() && name.isNotEmpty() && qty > 0 && unitPrice >= 0) {
                            items.add(
                                DemoOrderItem(
                                    itemId = itemId,
                                    itemType = itemType,
                                    name = name,
                                    quantity = qty,
                                    unitPricePaise = unitPrice,
                                )
                            )
                        }
                    }
                    if (items.isEmpty()) continue
                    add(
                        DemoOrder(
                            orderId = orderId,
                            createdAtDisplay = createdAt,
                            items = items,
                            subtotalPaise = obj.optInt("subtotalPaise", 0).coerceAtLeast(0),
                            shippingPaise = obj.optInt("shippingPaise", 0).coerceAtLeast(0),
                            totalPaise = obj.optInt("totalPaise", 0).coerceAtLeast(0),
                        )
                    )
                }
            }
        } catch (_: Throwable) {
            emptyList()
        }
    }

    fun saveOrder(order: DemoOrder) {
        val existing = loadOrders().filterNot { it.orderId == order.orderId }
        val updated = listOf(order) + existing
        val array = JSONArray()
        updated.forEach { entry ->
            val itemsArray = JSONArray()
            entry.items.forEach { item ->
                itemsArray.put(
                    JSONObject().apply {
                        put("itemId", item.itemId)
                        put("itemType", item.itemType.name)
                        put("name", item.name)
                        put("quantity", item.quantity)
                        put("unitPricePaise", item.unitPricePaise)
                    }
                )
            }
            array.put(
                JSONObject().apply {
                    put("orderId", entry.orderId)
                    put("createdAtDisplay", entry.createdAtDisplay)
                    put("items", itemsArray)
                    put("subtotalPaise", entry.subtotalPaise)
                    put("shippingPaise", entry.shippingPaise)
                    put("totalPaise", entry.totalPaise)
                }
            )
        }
        prefs.edit()
            .putString(KEY_ORDERS, array.toString())
            .putString(KEY_LAST_ORDER_ID, order.orderId)
            .apply()
    }

    fun loadLastOrderId(): String? {
        val id = prefs.getString(KEY_LAST_ORDER_ID, null)?.trim()
        return if (id.isNullOrEmpty()) null else id
    }

    fun loadFeedback(): List<FeedbackEntry> {
        val raw = prefs.getString(KEY_FEEDBACK, null) ?: return emptyList()
        return try {
            val array = JSONArray(raw)
            buildList {
                for (i in 0 until array.length()) {
                    val obj = array.optJSONObject(i) ?: continue
                    val id = obj.optString("id", "").trim()
                    val targetId = obj.optString("targetId", "").trim()
                    val targetLabel = obj.optString("targetLabel", "").trim()
                    val rating = obj.optInt("rating", 0)
                    val infoStr = obj.optString("infoEasyToUnderstand", "")
                    val priceStr = obj.optString("pricingAffordable", "")
                    val createdAt = obj.optString("createdAtDisplay", "").trim()
                    if (id.isEmpty() || targetId.isEmpty() || rating !in 1..5 || createdAt.isEmpty()) continue
                    val infoResp = runCatching { TriResponse.valueOf(infoStr) }.getOrNull() ?: continue
                    val priceResp = runCatching { TriResponse.valueOf(priceStr) }.getOrNull() ?: continue
                    val tryStr = obj.optString("willingToTry", "")
                    val tryResp = if (tryStr.isNotEmpty()) runCatching { TryBrandResponse.valueOf(tryStr) }.getOrNull() else null
                    val comment = obj.optString("comment", "").take(500)
                    add(
                        FeedbackEntry(
                            id = id,
                            targetId = targetId,
                            targetLabel = targetLabel.ifEmpty { targetId },
                            rating = rating,
                            infoEasyToUnderstand = infoResp,
                            pricingAffordable = priceResp,
                            comment = comment,
                            willingToTry = tryResp,
                            createdAtDisplay = createdAt,
                        )
                    )
                }
            }
        } catch (_: Throwable) {
            emptyList()
        }
    }

    fun saveFeedback(entry: FeedbackEntry) {
        val updated = listOf(entry) + loadFeedback()
        val array = JSONArray()
        updated.forEach { item ->
            array.put(
                JSONObject().apply {
                    put("id", item.id)
                    put("targetId", item.targetId)
                    put("targetLabel", item.targetLabel)
                    put("rating", item.rating)
                    put("infoEasyToUnderstand", item.infoEasyToUnderstand.name)
                    put("pricingAffordable", item.pricingAffordable.name)
                    put("comment", item.comment.take(500))
                    if (item.willingToTry != null) {
                        put("willingToTry", item.willingToTry.name)
                    }
                    put("createdAtDisplay", item.createdAtDisplay)
                }
            )
        }
        prefs.edit().putString(KEY_FEEDBACK, array.toString()).apply()
    }

    fun loadEvents(): PrototypeEventCounts {
        val raw = prefs.getString(KEY_EVENTS, null) ?: return PrototypeEventCounts()
        return try {
            val obj = JSONObject(raw)
            PrototypeEventCounts(
                productView = obj.optInt("productView", 0).coerceAtLeast(0),
                addToCart = obj.optInt("addToCart", 0).coerceAtLeast(0),
                beginCheckout = obj.optInt("beginCheckout", 0).coerceAtLeast(0),
                demoOrderCompleted = obj.optInt("demoOrderCompleted", 0).coerceAtLeast(0),
                feedbackSubmitted = obj.optInt("feedbackSubmitted", 0).coerceAtLeast(0),
            )
        } catch (_: Throwable) {
            PrototypeEventCounts()
        }
    }

    fun saveEvents(events: PrototypeEventCounts) {
        val obj = JSONObject().apply {
            put("productView", events.productView)
            put("addToCart", events.addToCart)
            put("beginCheckout", events.beginCheckout)
            put("demoOrderCompleted", events.demoOrderCompleted)
            put("feedbackSubmitted", events.feedbackSubmitted)
        }
        prefs.edit().putString(KEY_EVENTS, obj.toString()).apply()
    }

    fun resetDemoData() {
        prefs.edit()
            .remove(KEY_CART)
            .remove(KEY_ORDERS)
            .remove(KEY_LAST_ORDER_ID)
            .remove(KEY_FEEDBACK)
            .remove(KEY_EVENTS)
            .apply()
    }

    companion object {
        const val PREFS_NAME = "nivora_prototype_store_v1"
        const val KEY_CART = "nivora_v1_cart"
        const val KEY_ORDERS = "nivora_v1_orders"
        const val KEY_LAST_ORDER_ID = "nivora_v1_last_order_id"
        const val KEY_FEEDBACK = "nivora_v1_feedback"
        const val KEY_EVENTS = "nivora_v1_events"
    }
}
