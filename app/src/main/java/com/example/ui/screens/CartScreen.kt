package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.CartItemType
import com.example.data.CartLineItem
import com.example.data.CartSummary
import com.example.data.NivoraCatalog
import com.example.ui.components.BundleIllustration
import com.example.ui.components.CartSummaryCard
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.NivoraFooter
import com.example.ui.components.PriceDisplay
import com.example.ui.components.ProductIllustration
import com.example.ui.components.QuantitySelector
import com.example.ui.state.NivoraRoute

@Composable
fun CartScreen(
    cartItems: List<CartLineItem>,
    cartSummary: CartSummary,
    onUpdateQuantity: (String, Int) -> Unit,
    onUpdateQuantityFromText: (String, String) -> String?,
    onRemoveItem: (String) -> Unit,
    onNavigate: (NivoraRoute) -> Unit,
    onNavigateBack: () -> Unit,
    onRequestResetDemoData: () -> Unit,
) {
    BackHandler(onBack = onNavigateBack)
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .testTag("screen_cart"),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .widthIn(max = 960.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            val isWide = maxWidth >= 720.dp

            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Your cart",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.semantics { heading() },
                    )
                    OutlinedButton(
                        onClick = { onNavigate(NivoraRoute.Shop()) },
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .testTag("cart_continue_shopping"),
                        shape = MaterialTheme.shapes.small,
                    ) {
                        Text("Continue shopping")
                    }
                }

                if (cartItems.isEmpty()) {
                    EmptyStateCard(
                        icon = Icons.Outlined.ShoppingBag,
                        title = "Your cart is empty",
                        message = "Explore Nivora's four everyday essentials or try a starter bundle to test the prototype checkout.",
                        primaryActionLabel = "Shop essentials",
                        onPrimaryAction = { onNavigate(NivoraRoute.Shop()) },
                        secondaryActionLabel = "Explore starter bundles",
                        onSecondaryAction = { onNavigate(NivoraRoute.Bundles) },
                        testTag = "cart_empty_state",
                    )
                } else {
                    val itemsList = @Composable {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            cartItems.forEach { line ->
                                CartLineItemCard(
                                    item = line,
                                    onUpdateQuantity = { qty -> onUpdateQuantity(line.itemId, qty) },
                                    onDirectTextSubmit = { raw -> onUpdateQuantityFromText(line.itemId, raw) },
                                    onRemove = { onRemoveItem(line.itemId) },
                                    onViewItem = {
                                        if (line.itemType == CartItemType.PRODUCT) {
                                            onNavigate(NivoraRoute.ProductDetails(line.itemId))
                                        } else {
                                            onNavigate(NivoraRoute.Bundles)
                                        }
                                    },
                                )
                            }
                        }
                    }

                    val summaryCard = @Composable {
                        CartSummaryCard(
                            summary = cartSummary,
                            primaryButtonLabel = "Proceed to demo checkout",
                            onPrimaryClick = { onNavigate(NivoraRoute.Checkout) },
                            primaryButtonEnabled = cartItems.isNotEmpty(),
                            primaryButtonTestTag = "proceed_to_checkout_button",
                        )
                    }

                    if (isWide) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(18.dp),
                            verticalAlignment = Alignment.Top,
                        ) {
                            Column(modifier = Modifier.weight(1.35f)) { itemsList() }
                            Column(modifier = Modifier.weight(0.95f)) { summaryCard() }
                        }
                    } else {
                        itemsList()
                        summaryCard()
                    }
                }
            }
        }

        NivoraFooter(
            onNavigate = onNavigate,
            onRequestResetDemoData = onRequestResetDemoData,
        )
    }
}

@Composable
private fun CartLineItemCard(
    item: CartLineItem,
    onUpdateQuantity: (Int) -> Unit,
    onDirectTextSubmit: (String) -> String?,
    onRemove: () -> Unit,
    onViewItem: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("cart_item_${item.itemId}"),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    modifier = Modifier.size(78.dp),
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                ) {
                    if (item.productIdForArtwork != null) {
                        val prod = NivoraCatalog.getProductById(item.productIdForArtwork)
                        if (prod != null) {
                            ProductIllustration(product = prod, height = 78.dp)
                        }
                    } else if (item.bundleIdForArtwork != null) {
                        val bundle = NivoraCatalog.getBundleById(item.bundleIdForArtwork)
                        if (bundle != null) {
                            BundleIllustration(bundle = bundle, height = 78.dp)
                        }
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "Unit: ${NivoraCatalog.formatRupees(item.unitPricePaise)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        TextButton(
                            onClick = onViewItem,
                            modifier = Modifier.heightIn(min = 36.dp),
                        ) {
                            Text("View")
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                QuantitySelector(
                    quantity = item.quantity,
                    onQuantityChange = onUpdateQuantity,
                    onDirectTextSubmit = onDirectTextSubmit,
                    itemLabel = item.name,
                    testTagPrefix = "cart_qty_${item.itemId}",
                )

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Line total",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    PriceDisplay(pricePaise = item.lineTotalPaise, emphasize = true)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(
                    onClick = onRemove,
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .testTag("cart_remove_${item.itemId}"),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.error,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Remove", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
