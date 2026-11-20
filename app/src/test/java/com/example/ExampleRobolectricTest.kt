package com.example

import android.app.Application
import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import com.example.data.CheckoutFormState
import com.example.data.NivoraCatalog
import com.example.data.NivoraLocalStore
import com.example.data.TriResponse
import com.example.ui.state.NivoraRoute
import com.example.ui.state.NivoraViewModel
import com.example.ui.theme.NivoraTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var app: Application

    @Before
    fun setup() {
        app = ApplicationProvider.getApplicationContext()
        NivoraLocalStore(app).resetDemoData()
    }

    @Test
    fun `app name is Nivora`() {
        assertEquals("Nivora", app.getString(R.string.app_name))
    }

    @Test
    fun `cart persists across ViewModel recreation and survives corrupted storage safely`() {
        val vm1 = NivoraViewModel(app)
        vm1.addToCart("nivora-face-wash", 1)
        assertEquals(29900, vm1.uiState.value.cartSummary.subtotalPaise)

        val vm2 = NivoraViewModel(app)
        assertEquals(1, vm2.uiState.value.cartItems.size)
        assertEquals("nivora-face-wash", vm2.uiState.value.cartItems.first().itemId)

        // Corrupt the stored JSON and ensure store does not crash
        val prefs = app.getSharedPreferences(NivoraLocalStore.PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(NivoraLocalStore.KEY_CART, "{corrupted_json").apply()
        val vm3 = NivoraViewModel(app)
        assertTrue(vm3.uiState.value.cartItems.isEmpty())
    }

    @Test
    fun `checkout creates minimal order, clears cart, discards personal info, and prevents duplicate submission`() {
        val vm = NivoraViewModel(app)
        vm.addToCart("nivora-bundle-haircare-basics", 1)
        vm.navigateTo(NivoraRoute.Checkout)
        vm.updateCheckoutForm {
            CheckoutFormState(
                fullName = "Priya Rawat",
                email = "priya@example.com",
                mobileNumber = "9876543210",
                addressLine = "12 Rajpur Road",
                city = "Dehradun",
                state = "Uttarakhand",
                pinCode = "248001",
            )
        }

        vm.submitDemoOrder()
        // Immediate second call with empty cart must not create duplicate order
        vm.submitDemoOrder()

        val state = vm.uiState.value
        assertEquals(1, state.orders.size)
        assertTrue(state.cartItems.isEmpty())
        assertTrue(state.currentRoute is NivoraRoute.OrderConfirmation)
        assertEquals("", state.checkoutForm.fullName)
        assertEquals("", state.checkoutForm.email)
        assertEquals("", state.checkoutForm.mobileNumber)
        assertEquals("", state.checkoutForm.addressLine)
        assertEquals(79900, state.orders.first().totalPaise)

        // Verify persisted SharedPreferences never contains personal checkout fields
        val prefs = app.getSharedPreferences(NivoraLocalStore.PREFS_NAME, Context.MODE_PRIVATE)
        val rawOrders = prefs.getString(NivoraLocalStore.KEY_ORDERS, "") ?: ""
        assertFalse(rawOrders.contains("Priya Rawat"))
        assertFalse(rawOrders.contains("priya@example.com"))
        assertFalse(rawOrders.contains("9876543210"))
        assertFalse(rawOrders.contains("Rajpur Road"))
    }

    @Test
    fun `feedback persists locally and resetDemoData clears only Nivora keys`() {
        val unrelatedPrefs = app.getSharedPreferences("unrelated_app_prefs", Context.MODE_PRIVATE)
        unrelatedPrefs.edit().putString("keep_me", "yes").apply()

        val vm = NivoraViewModel(app)
        vm.addToCart("nivora-face-wash", 1)
        vm.updateFeedbackForm {
            it.copy(
                targetId = "nivora-face-wash",
                rating = 5,
                infoEasyToUnderstand = TriResponse.YES,
                pricingAffordable = TriResponse.YES,
                comment = "Very clear description.",
            )
        }
        vm.submitFeedback()
        assertEquals(1, vm.uiState.value.feedbackEntries.size)

        vm.confirmResetDemoData()
        assertTrue(vm.uiState.value.cartItems.isEmpty())
        assertTrue(vm.uiState.value.feedbackEntries.isEmpty())
        assertTrue(vm.uiState.value.orders.isEmpty())
        assertNull(vm.uiState.value.lastOrderId)
        assertEquals("yes", unrelatedPrefs.getString("keep_me", null))
    }

    @Test
    fun `home screen renders Nivora brand and navigates to shop`() {
        val vm = NivoraViewModel(app)
        composeTestRule.setContent {
            NivoraTheme {
                NivoraApp(viewModel = vm)
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("screen_home").assertIsDisplayed()
        composeTestRule.onNodeWithTag("hero_shop_cta").assertIsDisplayed()
        composeTestRule.onNodeWithTag("hero_shop_cta").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("screen_shop").assertIsDisplayed()
        composeTestRule.onNodeWithTag("product_card_${NivoraCatalog.products.first().id}").assertExists()
        assertNotNull(vm.uiState.value.currentRoute as? NivoraRoute.Shop)

        composeTestRule.onNodeWithTag("view_details_${NivoraCatalog.products.first().id}")
            .performScrollTo()
            .performClick()
        composeTestRule.mainClock.advanceTimeBy(300)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("screen_product_details").assertIsDisplayed()
        assertNotNull(vm.uiState.value.currentRoute as? NivoraRoute.ProductDetails)
    }
}
