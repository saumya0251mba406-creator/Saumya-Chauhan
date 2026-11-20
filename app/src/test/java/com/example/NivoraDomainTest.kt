package com.example

import com.example.data.CartCalculator
import com.example.data.CheckoutFormState
import com.example.data.CheckoutValidator
import com.example.data.NivoraCatalog
import com.example.data.ProductCategory
import com.example.ui.state.NivoraRoute
import com.example.ui.state.ShopFilterState
import com.example.ui.state.ShopSortOption
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NivoraDomainTest {

    @Test
    fun `catalog contains exactly 4 products and 2 bundles with no sunscreen or facial serum`() {
        assertEquals(4, NivoraCatalog.products.size)
        assertEquals(2, NivoraCatalog.bundles.size)
        assertTrue(NivoraCatalog.products.none { it.name.contains("Sunscreen", ignoreCase = true) })
        assertTrue(NivoraCatalog.products.none { it.name.equals("Facial Serum", ignoreCase = true) })
        assertEquals(
            listOf("nivora-face-wash", "nivora-body-lotion", "nivora-shampoo", "nivora-hair-serum"),
            NivoraCatalog.products.map { it.id },
        )
    }

    @Test
    fun `price example A - one face wash`() {
        val add = CartCalculator.addItem(emptyList(), "nivora-face-wash", 1)!!
        val summary = CartCalculator.calculateSummary(add.updatedItems)
        assertEquals(29900, summary.subtotalPaise)
        assertEquals(4900, summary.shippingPaise)
        assertEquals(34800, summary.totalPaise)
        assertEquals("₹299", NivoraCatalog.formatRupees(summary.subtotalPaise))
        assertEquals("₹49", NivoraCatalog.formatRupees(summary.shippingPaise))
        assertEquals("₹348", NivoraCatalog.formatRupees(summary.totalPaise))
    }

    @Test
    fun `price example B - one shampoo plus one hair serum individually`() {
        val step1 = CartCalculator.addItem(emptyList(), "nivora-shampoo", 1)!!
        val step2 = CartCalculator.addItem(step1.updatedItems, "nivora-hair-serum", 1)!!
        val summary = CartCalculator.calculateSummary(step2.updatedItems)
        assertEquals(84800, summary.subtotalPaise)
        assertEquals(0, summary.shippingPaise)
        assertEquals(84800, summary.totalPaise)
        assertEquals("₹848", NivoraCatalog.formatRupees(summary.totalPaise))
    }

    @Test
    fun `price example C - Daily Basics Duo bundle`() {
        val bundle = NivoraCatalog.getBundleById("nivora-bundle-daily-basics")!!
        assertEquals(69800, bundle.individualTotalPaise)
        assertEquals(64900, bundle.bundlePricePaise)
        assertEquals(4900, bundle.savingsPaise)

        val add = CartCalculator.addItem(emptyList(), bundle.id, 1)!!
        assertEquals(1, add.updatedItems.size)
        val summary = CartCalculator.calculateSummary(add.updatedItems)
        assertEquals(1, summary.itemCount)
        assertEquals(64900, summary.subtotalPaise)
        assertEquals(4900, summary.shippingPaise)
        assertEquals(69800, summary.totalPaise)
    }

    @Test
    fun `price example D - Haircare Basics Duo bundle`() {
        val bundle = NivoraCatalog.getBundleById("nivora-bundle-haircare-basics")!!
        assertEquals(84800, bundle.individualTotalPaise)
        assertEquals(79900, bundle.bundlePricePaise)
        assertEquals(4900, bundle.savingsPaise)

        val add = CartCalculator.addItem(emptyList(), bundle.id, 1)!!
        assertEquals(1, add.updatedItems.size)
        val summary = CartCalculator.calculateSummary(add.updatedItems)
        assertEquals(1, summary.itemCount)
        assertEquals(79900, summary.subtotalPaise)
        assertEquals(0, summary.shippingPaise)
        assertEquals(79900, summary.totalPaise)
    }

    @Test
    fun `adding existing item merges quantities and caps at 10`() {
        val first = CartCalculator.addItem(emptyList(), "nivora-face-wash", 6)!!
        val second = CartCalculator.addItem(first.updatedItems, "nivora-face-wash", 7)!!
        assertEquals(1, second.updatedItems.size)
        assertEquals(10, second.updatedItems.first().quantity)
        assertTrue(second.hitMaxLimit)
        assertEquals(4, second.addedQuantity)
    }

    @Test
    fun `direct quantity parser rejects zero, negative, fractional, and above 10`() {
        assertTrue(CartCalculator.parseDirectQuantity("0").isFailure)
        assertTrue(CartCalculator.parseDirectQuantity("-2").isFailure)
        assertTrue(CartCalculator.parseDirectQuantity("1.5").isFailure)
        assertTrue(CartCalculator.parseDirectQuantity("11").isFailure)
        assertEquals(5, CartCalculator.parseDirectQuantity("5").getOrNull())
    }

    @Test
    fun `shop search, category filter, and sorting work together`() {
        val filters = ShopFilterState(
            searchQuery = "everyday",
            selectedCategory = ProductCategory.HAIRCARE,
            sortOption = ShopSortOption.PRICE_HIGH_TO_LOW,
        )
        val results = filters.applyTo(NivoraCatalog.products)
        assertEquals(listOf("nivora-hair-serum", "nivora-shampoo"), results.map { it.id })
    }

    @Test
    fun `checkout validator rejects whitespace, invalid email, bad mobile, bad PIN, and unlisted states`() {
        val invalid = CheckoutFormState(
            fullName = "   ",
            email = "bad-email",
            mobileNumber = "12345",
            addressLine = "",
            city = " ",
            state = "Maharashtra",
            pinCode = "2480",
        )
        val errors = CheckoutValidator.validate(invalid)
        assertTrue(errors.hasErrors)
        assertNotNull(errors.fullName)
        assertNotNull(errors.email)
        assertNotNull(errors.mobileNumber)
        assertNotNull(errors.addressLine)
        assertNotNull(errors.city)
        assertNotNull(errors.state)
        assertNotNull(errors.pinCode)

        val valid = CheckoutFormState(
            fullName = "Rohan Verma",
            email = "rohan@example.com",
            mobileNumber = "9876543210",
            addressLine = "Hostel 4, Room 12",
            city = "Dehradun",
            state = "Uttarakhand",
            pinCode = "248001",
        )
        val validErrors = CheckoutValidator.validate(valid)
        assertFalse(validErrors.hasErrors)
        assertNull(validErrors.state)
    }

    @Test
    fun `route resolver handles valid and invalid paths safely`() {
        assertEquals(NivoraRoute.Home, NivoraRoute.resolve("/"))
        assertEquals(NivoraRoute.Bundles, NivoraRoute.resolve("/bundles"))
        assertEquals(
            NivoraRoute.ProductDetails("nivora-face-wash"),
            NivoraRoute.resolve("product/nivora-face-wash"),
        )
        assertTrue(NivoraRoute.resolve("nonexistent-page") is NivoraRoute.NotFound)
    }
}
