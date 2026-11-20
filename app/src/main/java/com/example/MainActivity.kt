package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.NivoraBottomBar
import com.example.ui.components.NivoraDrawerContent
import com.example.ui.components.NivoraHeader
import com.example.ui.components.NivoraSideRail
import com.example.ui.components.ResetDemoDataDialog
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.BundlesScreen
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.FeedbackScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NotFoundScreen
import com.example.ui.screens.OrderConfirmationScreen
import com.example.ui.screens.ProductDetailsScreen
import com.example.ui.screens.ShopScreen
import com.example.ui.state.NivoraRoute
import com.example.ui.state.NivoraViewModel
import com.example.ui.theme.LocalReducedMotion
import com.example.ui.theme.NivoraTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val initialPath = intent?.data?.path
        setContent {
            NivoraTheme {
                NivoraApp(initialPath = initialPath)
            }
        }
    }
}

@Composable
fun NivoraApp(
    initialPath: String? = null,
    viewModel: NivoraViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val reducedMotion = LocalReducedMotion.current

    LaunchedEffect(initialPath) {
        if (!initialPath.isNullOrBlank()) {
            viewModel.navigateToRawPath(initialPath)
        }
    }

    LaunchedEffect(uiState.statusMessage?.id) {
        val status = uiState.statusMessage ?: return@LaunchedEffect
        val actionLabel = if (!status.isError && status.text.startsWith("Added")) "View cart" else null
        val result = snackbarHostState.showSnackbar(
            message = status.text,
            actionLabel = actionLabel,
            duration = SnackbarDuration.Short,
        )
        if (result == SnackbarResult.ActionPerformed) {
            viewModel.navigateTo(NivoraRoute.Cart)
        }
        viewModel.dismissStatusMessage()
    }

    if (uiState.showResetConfirmationDialog) {
        ResetDemoDataDialog(
            onConfirm = { viewModel.confirmResetDemoData() },
            onDismiss = { viewModel.dismissResetDemoData() },
        )
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isCompactWidth = maxWidth < 700.dp

        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = isCompactWidth,
            drawerContent = {
                NivoraDrawerContent(
                    currentRoute = uiState.currentRoute,
                    cartItemCount = uiState.cartSummary.itemCount,
                    onNavigate = { viewModel.navigateTo(it) },
                    onRequestReset = { viewModel.requestResetDemoData() },
                    onCloseDrawer = { scope.launch { drawerState.close() } },
                )
            },
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background,
                contentWindowInsets = WindowInsets.safeDrawing,
                topBar = {
                    NivoraHeader(
                        currentRoute = uiState.currentRoute,
                        canNavigateBack = uiState.canNavigateBack,
                        cartItemCount = uiState.cartSummary.itemCount,
                        isCompactWidth = isCompactWidth,
                        onOpenMenu = { scope.launch { drawerState.open() } },
                        onNavigateBack = { viewModel.navigateBack() },
                        onNavigate = { viewModel.navigateTo(it) },
                    )
                },
                bottomBar = {
                    if (isCompactWidth) {
                        NivoraBottomBar(
                            currentRoute = uiState.currentRoute,
                            onNavigate = { viewModel.navigateTo(it) },
                        )
                    }
                },
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState) { data ->
                        val isErr = uiState.statusMessage?.isError == true
                        Snackbar(
                            snackbarData = data,
                            containerColor = if (isErr) {
                                MaterialTheme.colorScheme.errorContainer
                            } else {
                                MaterialTheme.colorScheme.onBackground
                            },
                            contentColor = if (isErr) {
                                MaterialTheme.colorScheme.onErrorContainer
                            } else {
                                MaterialTheme.colorScheme.background
                            },
                            actionColor = if (isErr) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.primaryContainer
                            },
                        )
                    }
                },
            ) { innerPadding ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                ) {
                    if (!isCompactWidth) {
                        NivoraSideRail(
                            currentRoute = uiState.currentRoute,
                            onNavigate = { viewModel.navigateTo(it) },
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        val animMillis = if (reducedMotion) 0 else 180
                        Crossfade(
                            targetState = uiState.currentRoute,
                            animationSpec = tween(durationMillis = animMillis),
                            label = "nivora_screen_transition",
                        ) { route ->
                            when (route) {
                                is NivoraRoute.Home -> HomeScreen(
                                    onNavigate = { viewModel.navigateTo(it) },
                                    onAddToCart = { id, qty -> viewModel.addToCart(id, qty) },
                                    onRequestResetDemoData = { viewModel.requestResetDemoData() },
                                )

                                is NivoraRoute.Shop -> ShopScreen(
                                    filterState = uiState.shopFilters,
                                    filteredProducts = uiState.filteredProducts,
                                    onSearchChange = { viewModel.updateSearchQuery(it) },
                                    onCategorySelect = { viewModel.selectCategory(it) },
                                    onSortSelect = { viewModel.selectSortOption(it) },
                                    onClearFilters = { viewModel.clearShopFilters() },
                                    onAddToCart = { id, qty -> viewModel.addToCart(id, qty) },
                                    onNavigate = { viewModel.navigateTo(it) },
                                    onNavigateBack = { viewModel.navigateBack() },
                                    onRequestResetDemoData = { viewModel.requestResetDemoData() },
                                )

                                is NivoraRoute.ProductDetails -> ProductDetailsScreen(
                                    productId = route.productId,
                                    productFeedback = uiState.feedbackEntries.filter {
                                        it.targetId == route.productId
                                    },
                                    onAddToCart = { id, qty -> viewModel.addToCart(id, qty) },
                                    onNavigate = { viewModel.navigateTo(it) },
                                    onNavigateBack = { viewModel.navigateBack() },
                                    onRequestResetDemoData = { viewModel.requestResetDemoData() },
                                )

                                is NivoraRoute.Bundles -> BundlesScreen(
                                    onAddBundleToCart = { id, qty -> viewModel.addToCart(id, qty) },
                                    onNavigate = { viewModel.navigateTo(it) },
                                    onNavigateBack = { viewModel.navigateBack() },
                                    onRequestResetDemoData = { viewModel.requestResetDemoData() },
                                )

                                is NivoraRoute.Cart -> CartScreen(
                                    cartItems = uiState.cartItems,
                                    cartSummary = uiState.cartSummary,
                                    onUpdateQuantity = { id, qty ->
                                        viewModel.updateCartItemQuantity(id, qty)
                                    },
                                    onUpdateQuantityFromText = { id, text ->
                                        viewModel.updateCartItemQuantityFromText(id, text)
                                    },
                                    onRemoveItem = { id -> viewModel.removeFromCart(id) },
                                    onNavigate = { viewModel.navigateTo(it) },
                                    onNavigateBack = { viewModel.navigateBack() },
                                    onRequestResetDemoData = { viewModel.requestResetDemoData() },
                                )

                                is NivoraRoute.Checkout -> CheckoutScreen(
                                    cartItems = uiState.cartItems,
                                    cartSummary = uiState.cartSummary,
                                    formState = uiState.checkoutForm,
                                    validationErrors = uiState.checkoutErrors,
                                    isSubmitting = uiState.isSubmittingOrder,
                                    onUpdateForm = { viewModel.updateCheckoutForm(it) },
                                    onSubmitOrder = { viewModel.submitDemoOrder() },
                                    onNavigate = { viewModel.navigateTo(it) },
                                    onNavigateBack = { viewModel.navigateBack() },
                                    onRequestResetDemoData = { viewModel.requestResetDemoData() },
                                )

                                is NivoraRoute.OrderConfirmation -> OrderConfirmationScreen(
                                    order = uiState.resolveConfirmedOrder(route.orderId),
                                    onNavigate = { viewModel.navigateTo(it) },
                                    onNavigateBack = { viewModel.navigateBack() },
                                    onRequestResetDemoData = { viewModel.requestResetDemoData() },
                                )

                                is NivoraRoute.About -> AboutScreen(
                                    eventCounts = uiState.eventCounts,
                                    onNavigate = { viewModel.navigateTo(it) },
                                    onNavigateBack = { viewModel.navigateBack() },
                                    onRequestResetDemoData = { viewModel.requestResetDemoData() },
                                )

                                is NivoraRoute.Feedback -> FeedbackScreen(
                                    formState = uiState.feedbackForm,
                                    validationErrors = uiState.feedbackErrors,
                                    isSubmitting = uiState.isSubmittingFeedback,
                                    submittedJustNow = uiState.feedbackSubmittedJustNow,
                                    savedEntries = uiState.feedbackEntries,
                                    onUpdateForm = { viewModel.updateFeedbackForm(it) },
                                    onSubmitFeedback = { viewModel.submitFeedback() },
                                    onNavigate = { viewModel.navigateTo(it) },
                                    onNavigateBack = { viewModel.navigateBack() },
                                    onRequestResetDemoData = { viewModel.requestResetDemoData() },
                                )

                                is NivoraRoute.NotFound -> NotFoundScreen(
                                    attemptedRoute = route.attemptedRoute,
                                    onNavigate = { viewModel.navigateTo(it) },
                                    onNavigateBack = { viewModel.navigateBack() },
                                    onRequestResetDemoData = { viewModel.requestResetDemoData() },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
