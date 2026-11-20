package com.example.data

object NivoraCatalog {

    const val PROTOTYPE_DISCLAIMER =
        "College project prototype. Products and specifications are illustrative. No real payment is collected and no order is shipped."

    const val FORMULATION_DISCLAIMER =
        "Final formulation and ingredient information will be confirmed before commercial launch."

    const val GENERAL_FEEDBACK_TARGET_ID = "general-app-feedback"
    const val GENERAL_FEEDBACK_TARGET_LABEL = "General app feedback"

    val ALLOWED_CHECKOUT_STATES: List<String> = listOf(
        "Uttarakhand",
        "Delhi",
        "Haryana",
        "Uttar Pradesh",
    )

    val products: List<Product> = listOf(
        Product(
            id = "nivora-face-wash",
            name = "Daily Gentle Face Wash",
            slug = "daily-gentle-face-wash",
            category = ProductCategory.SKINCARE,
            size = "100 ml",
            pricePaise = 29900,
            shortDescription = "A straightforward cleansing step for an everyday routine.",
            longDescription = "Created as an uncomplicated daily cleanser for college students and young adults who want a clear, affordable starting point without navigating confusing product claims.",
            routineStep = "Cleanse",
            packagingConfig = PackagingConfig(
                variant = PackagingVariant.CLEANSER_TUBE,
                bodyHex = 0xFFEAE5DA,
                accentHex = 0xFF526B51,
                labelText = "Daily Gentle Face Wash",
                subLabelText = "100 ml • Cleanse",
                contentDescription = "Illustrative Nivora Daily Gentle Face Wash 100 ml tube packaging in warm cream and muted sage green",
            ),
            illustrativeUsage = "Illustrative use guidance: Wet face with water, massage a small amount gently, rinse clean, and pat dry. This guidance is illustrative only and does not replace final product instructions.",
            formulationDisclaimer = FORMULATION_DISCLAIMER,
        ),
        Product(
            id = "nivora-body-lotion",
            name = "Everyday Body Lotion",
            slug = "everyday-body-lotion",
            category = ProductCategory.SKINCARE,
            size = "200 ml",
            pricePaise = 39900,
            shortDescription = "A simple body-care essential for your daily routine.",
            longDescription = "A straightforward everyday body lotion concept designed to keep daily body care accessible, easy to understand, and reasonably priced for regular student use.",
            routineStep = "Body care",
            packagingConfig = PackagingConfig(
                variant = PackagingVariant.LOTION_PUMP,
                bodyHex = 0xFFF3EFE6,
                accentHex = 0xFF627B60,
                labelText = "Everyday Body Lotion",
                subLabelText = "200 ml • Body care",
                contentDescription = "Illustrative Nivora Everyday Body Lotion 200 ml pump bottle packaging in soft cream and sage green",
            ),
            illustrativeUsage = "Illustrative use guidance: Smooth an even amount onto clean skin after showering as part of an everyday routine. This guidance is illustrative only and does not replace final product instructions.",
            formulationDisclaimer = FORMULATION_DISCLAIMER,
        ),
        Product(
            id = "nivora-shampoo",
            name = "Everyday Shampoo",
            slug = "everyday-shampoo",
            category = ProductCategory.HAIRCARE,
            size = "200 ml",
            pricePaise = 34900,
            shortDescription = "An uncomplicated cleansing step for your haircare routine.",
            longDescription = "A clear, practical hair cleanser concept built for busy campus and early-career mornings, replacing cluttered haircare aisles with one easy-to-understand option.",
            routineStep = "Cleanse hair",
            packagingConfig = PackagingConfig(
                variant = PackagingVariant.SHAMPOO_BOTTLE,
                bodyHex = 0xFFE3ECE1,
                accentHex = 0xFF425941,
                labelText = "Everyday Shampoo",
                subLabelText = "200 ml • Cleanse hair",
                contentDescription = "Illustrative Nivora Everyday Shampoo 200 ml bottle packaging in soft sage green and cream",
            ),
            illustrativeUsage = "Illustrative use guidance: Apply to wet hair and scalp, lather gently, and rinse thoroughly with water. This guidance is illustrative only and does not replace final product instructions.",
            formulationDisclaimer = FORMULATION_DISCLAIMER,
        ),
        Product(
            id = "nivora-hair-serum",
            name = "Everyday Hair Serum",
            slug = "everyday-hair-serum",
            category = ProductCategory.HAIRCARE,
            size = "50 ml",
            pricePaise = 49900,
            shortDescription = "A finishing step for a simple haircare routine.",
            longDescription = "A lightweight finishing step intended to pair naturally with Everyday Shampoo so young consumers can follow a simple two-step haircare routine without guesswork.",
            routineStep = "Finish",
            packagingConfig = PackagingConfig(
                variant = PackagingVariant.SERUM_DROPPER,
                bodyHex = 0xFFEDE4D8,
                accentHex = 0xFF5D6B52,
                labelText = "Everyday Hair Serum",
                subLabelText = "50 ml • Finish",
                contentDescription = "Illustrative Nivora Everyday Hair Serum 50 ml dropper bottle packaging in warm beige and sage green",
            ),
            illustrativeUsage = "Illustrative use guidance: Warm a small amount between palms and apply lightly along hair lengths as a finishing step. This guidance is illustrative only and does not replace final product instructions.",
            formulationDisclaimer = FORMULATION_DISCLAIMER,
        ),
    )

    val bundles: List<StarterBundle> = listOf(
        StarterBundle(
            id = "nivora-bundle-daily-basics",
            name = "Daily Basics Duo",
            slug = "daily-basics-duo",
            description = "Pairs Daily Gentle Face Wash and Everyday Body Lotion for a simple two-product skincare start.",
            includedProductIds = listOf("nivora-face-wash", "nivora-body-lotion"),
            bundlePricePaise = 64900,
        ),
        StarterBundle(
            id = "nivora-bundle-haircare-basics",
            name = "Haircare Basics Duo",
            slug = "haircare-basics-duo",
            description = "Pairs Everyday Shampoo and Everyday Hair Serum for a straightforward two-step haircare routine.",
            includedProductIds = listOf("nivora-shampoo", "nivora-hair-serum"),
            bundlePricePaise = 79900,
        ),
    )

    fun getProductById(id: String): Product? = products.find { it.id == id || it.slug == id }

    fun getBundleById(id: String): StarterBundle? = bundles.find { it.id == id || it.slug == id }

    fun formatRupees(paise: Int): String {
        val negative = paise < 0
        val absPaise = kotlin.math.abs(paise)
        val rupees = absPaise / 100
        val remPaise = absPaise % 100
        val prefix = if (negative) "-₹" else "₹"
        return if (remPaise == 0) {
            "$prefix$rupees"
        } else {
            val padded = remPaise.toString().padStart(2, '0')
            "$prefix$rupees.$padded"
        }
    }

    fun buildCartLineItem(itemId: String, quantity: Int): CartLineItem? {
        val clampedQty = quantity.coerceIn(1, CartCalculator.MAX_ITEM_QUANTITY)
        getProductById(itemId)?.let { product ->
            return CartLineItem(
                itemId = product.id,
                itemType = CartItemType.PRODUCT,
                name = product.name,
                subtitle = "${product.category.displayName} • ${product.size}",
                unitPricePaise = product.pricePaise,
                quantity = clampedQty,
                productIdForArtwork = product.id,
            )
        }
        getBundleById(itemId)?.let { bundle ->
            val includedSummary = bundle.includedProducts.joinToString(" + ") { it.name }
            return CartLineItem(
                itemId = bundle.id,
                itemType = CartItemType.BUNDLE,
                name = bundle.name,
                subtitle = "Starter bundle ($includedSummary)",
                unitPricePaise = bundle.bundlePricePaise,
                quantity = clampedQty,
                bundleIdForArtwork = bundle.id,
            )
        }
        return null
    }

    fun feedbackTargetOptions(): List<Pair<String, String>> {
        val list = mutableListOf(GENERAL_FEEDBACK_TARGET_ID to GENERAL_FEEDBACK_TARGET_LABEL)
        products.forEach { list.add(it.id to it.name) }
        bundles.forEach { list.add(it.id to "${it.name} (Starter Bundle)") }
        return list
    }
}
