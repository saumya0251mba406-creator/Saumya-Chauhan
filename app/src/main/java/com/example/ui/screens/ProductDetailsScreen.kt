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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.FeedbackEntry
import com.example.data.NivoraCatalog
import com.example.ui.components.CategoryAndSizePill
import com.example.ui.components.DemoNoticeBanner
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.NivoraFooter
import com.example.ui.components.PriceDisplay
import com.example.ui.components.ProductIllustration
import com.example.ui.components.QuantitySelector
import com.example.ui.state.NivoraRoute

@Composable
fun ProductDetailsScreen(
    productId: String,
    productFeedback: List<FeedbackEntry>,
    onAddToCart: (String, Int) -> Unit,
    onNavigate: (NivoraRoute) -> Unit,
    onNavigateBack: () -> Unit,
    onRequestResetDemoData: () -> Unit,
) {
    BackHandler(onBack = onNavigateBack)
    val product = remember(productId) { NivoraCatalog.getProductById(productId) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .testTag("screen_product_details"),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .widthIn(max = 920.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            if (product == null) {
                EmptyStateCard(
                    icon = Icons.Outlined.ErrorOutline,
                    title = "Product not found",
                    message = "We could not find an essential matching \"$productId\". Browse the four seeded Nivora products in the shop.",
                    primaryActionLabel = "Go to Shop",
                    onPrimaryAction = { onNavigate(NivoraRoute.Shop()) },
                    secondaryActionLabel = "Back to Home",
                    onSecondaryAction = { onNavigate(NivoraRoute.Home) },
                    testTag = "product_not_found_state",
                )
            } else {
                val isWide = maxWidth >= 680.dp
                var quantity by remember(product.id) { mutableIntStateOf(1) }

                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    TextButton(
                        onClick = { onNavigate(NivoraRoute.Shop(product.category)) },
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .testTag("back_to_category_link"),
                    ) {
                        Text("← Back to ${product.category.displayName} in Shop")
                    }

                    val infoBlock = @Composable {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                CategoryAndSizePill(
                                    category = product.category.displayName,
                                    size = product.size,
                                )
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(50),
                                ) {
                                    Text(
                                        text = "Routine step: ${product.routineStep}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    )
                                }
                            }

                            Text(
                                text = product.name,
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.semantics { heading() },
                            )

                            PriceDisplay(pricePaise = product.pricePaise, emphasize = true)

                            Text(
                                text = product.shortDescription,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )

                            Text(
                                text = product.longDescription,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Quantity (1–10)",
                                    style = MaterialTheme.typography.labelLarge,
                                )
                                QuantitySelector(
                                    quantity = quantity,
                                    onQuantityChange = { quantity = it },
                                    itemLabel = product.name,
                                    testTagPrefix = "details_qty",
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                Button(
                                    onClick = { onAddToCart(product.id, quantity) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .heightIn(min = 48.dp)
                                        .testTag("details_add_to_cart"),
                                    shape = MaterialTheme.shapes.small,
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.ShoppingBag,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Add to cart")
                                }
                                OutlinedButton(
                                    onClick = { onNavigate(NivoraRoute.Cart) },
                                    modifier = Modifier
                                        .heightIn(min = 48.dp)
                                        .testTag("details_view_cart"),
                                    shape = MaterialTheme.shapes.small,
                                ) {
                                    Text("View cart")
                                }
                            }
                        }
                    }

                    if (isWide) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(22.dp),
                            verticalAlignment = Alignment.Top,
                        ) {
                            ProductIllustration(
                                product = product,
                                height = 320.dp,
                                modifier = Modifier.weight(0.9f),
                            )
                            Column(modifier = Modifier.weight(1.1f)) {
                                infoBlock()
                            }
                        }
                    } else {
                        ProductIllustration(product = product, height = 220.dp)
                        infoBlock()
                    }

                    // Illustrative usage + formulation disclaimer
                    Card(
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Text(
                                text = "Illustrative usage guidance",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.semantics { heading() },
                            )
                            Text(
                                text = product.illustrativeUsage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            Text(
                                text = "Formulation & specification notice",
                                style = MaterialTheme.typography.titleSmall,
                            )
                            Text(
                                text = product.formulationDisclaimer,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    // Prototype Feedback Section (NO fake reviews or stars!)
                    Card(
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("product_feedback_section"),
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "Prototype feedback",
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.semantics { heading() },
                                )
                                OutlinedButton(
                                    onClick = { onNavigate(NivoraRoute.Feedback(product.id)) },
                                    modifier = Modifier
                                        .heightIn(min = 48.dp)
                                        .testTag("leave_product_feedback_button"),
                                    shape = MaterialTheme.shapes.small,
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Feedback,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Add feedback")
                                }
                            }

                            if (productFeedback.isEmpty()) {
                                Text(
                                    text = "No feedback yet.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.testTag("no_feedback_yet_text"),
                                )
                            } else {
                                DemoNoticeBanner(
                                    text = "Feedback from this prototype session — not verified purchases.",
                                    prominent = false,
                                )
                                productFeedback.forEach { entry ->
                                    FeedbackEntryCard(entry = entry)
                                }
                            }
                        }
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
fun FeedbackEntryCard(
    entry: FeedbackEntry,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = entry.targetLabel,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                )
                Text(
                    text = "Rating: ${entry.rating}/5",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Text(
                text = "Clear info: ${entry.infoEasyToUnderstand.label} • Affordable pricing: ${entry.pricingAffordable.label}" +
                    if (entry.willingToTry != null) " • Willing to try: ${entry.willingToTry.label}" else "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (entry.comment.isNotBlank()) {
                Text(
                    text = entry.comment,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Text(
                text = "${entry.createdAtDisplay} • Local prototype session",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
