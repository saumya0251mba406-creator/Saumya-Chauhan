package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.Button
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.NivoraCatalog
import com.example.data.StarterBundle
import com.example.ui.components.BundleIllustration
import com.example.ui.components.DemoNoticeBanner
import com.example.ui.components.NivoraFooter
import com.example.ui.components.PriceDisplay
import com.example.ui.components.ProductCard
import com.example.ui.state.NivoraRoute

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    onNavigate: (NivoraRoute) -> Unit,
    onAddToCart: (String, Int) -> Unit,
    onRequestResetDemoData: () -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .testTag("screen_home"),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .widthIn(max = 960.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            val isWide = maxWidth >= 640.dp

            Column(verticalArrangement = Arrangement.spacedBy(28.dp)) {
                // A. Hero
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 22.dp, vertical = 26.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(50),
                        ) {
                            Text(
                                text = "Affordable skincare and haircare, without the confusion",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            )
                        }

                        Text(
                            text = "Simple care. Everyday confidence.",
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.semantics { heading() },
                        )

                        Text(
                            text = "Discover affordable skincare and haircare essentials, with clear choices for your everyday routine.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Button(
                                onClick = { onNavigate(NivoraRoute.Shop()) },
                                modifier = Modifier
                                    .heightIn(min = 48.dp)
                                    .testTag("hero_shop_cta"),
                                shape = MaterialTheme.shapes.small,
                            ) {
                                Text("Shop essentials")
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                )
                            }

                            OutlinedButton(
                                onClick = { onNavigate(NivoraRoute.Bundles) },
                                modifier = Modifier
                                    .heightIn(min = 48.dp)
                                    .testTag("hero_bundles_cta"),
                                shape = MaterialTheme.shapes.small,
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Inventory2,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Explore starter bundles")
                            }
                        }
                    }
                }

                // B. Three brand principles (intended principles, not proven performance claims)
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Our intended brand principles",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.semantics { heading() },
                    )
                    Text(
                        text = "Built around what students and young professionals told us matters most when shopping online.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    val principles = listOf(
                        Triple(
                            Icons.Outlined.Spa,
                            "Simple choices",
                            "Just four starter essentials so you do not have to compare dozens of nearly identical bottles.",
                        ),
                        Triple(
                            Icons.Outlined.Description,
                            "Clear information",
                            "Plain-language routine steps and honest prototype notes without exaggerated beauty claims.",
                        ),
                        Triple(
                            Icons.Outlined.Payments,
                            "Student-friendly pricing",
                            "Illustrative pricing between ₹299 and ₹499, plus paired starter bundles for everyday value.",
                        ),
                    )

                    if (isWide) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            principles.forEach { (icon, title, body) ->
                                PrincipleCard(
                                    icon = icon,
                                    title = title,
                                    body = body,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            principles.forEach { (icon, title, body) ->
                                PrincipleCard(icon = icon, title = title, body = body)
                            }
                        }
                    }
                }

                // C. Featured essentials (4 seeded products)
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Featured essentials",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.semantics { heading() },
                        )
                        TextButton(
                            onClick = { onNavigate(NivoraRoute.Shop()) },
                            modifier = Modifier.heightIn(min = 48.dp),
                        ) {
                            Text("View all 4")
                        }
                    }

                    val products = NivoraCatalog.products
                    if (isWide) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            products.chunked(2).forEach { rowProducts ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                                ) {
                                    rowProducts.forEach { product ->
                                        ProductCard(
                                            product = product,
                                            onViewDetails = {
                                                onNavigate(NivoraRoute.ProductDetails(product.id))
                                            },
                                            onAddToCart = { onAddToCart(product.id, 1) },
                                            modifier = Modifier.weight(1f),
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            products.forEach { product ->
                                ProductCard(
                                    product = product,
                                    onViewDetails = {
                                        onNavigate(NivoraRoute.ProductDetails(product.id))
                                    },
                                    onAddToCart = { onAddToCart(product.id, 1) },
                                )
                            }
                        }
                    }
                }

                // D. Routine overview
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = "How the 4-item starter catalogue fits together",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.semantics { heading() },
                        )
                        val steps = listOf(
                            Triple("1. Face cleansing", "Daily Gentle Face Wash", "Cleanse • 100 ml"),
                            Triple("2. Body care", "Everyday Body Lotion", "Body care • 200 ml"),
                            Triple("3. Hair cleansing", "Everyday Shampoo", "Cleanse hair • 200 ml"),
                            Triple("4. Hair finishing", "Everyday Hair Serum", "Finish • 50 ml"),
                        )
                        steps.forEachIndexed { idx, (stepTitle, productName, meta) ->
                            val targetProduct = NivoraCatalog.products[idx]
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(MaterialTheme.shapes.small)
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stepTitle,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                    Text(
                                        text = "$productName ($meta)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                }
                                TextButton(
                                    onClick = { onNavigate(NivoraRoute.ProductDetails(targetProduct.id)) },
                                    modifier = Modifier.heightIn(min = 48.dp),
                                ) {
                                    Text("View")
                                }
                            }
                        }
                        DemoNoticeBanner(
                            text = "Note: These four essentials cover basic cleansing, body care, and hair finishing. They do not represent a complete skincare routine and do not replace sun protection.",
                            prominent = false,
                        )
                    }
                }

                // E. Starter bundle preview
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Starter bundles",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.semantics { heading() },
                        )
                        TextButton(
                            onClick = { onNavigate(NivoraRoute.Bundles) },
                            modifier = Modifier.heightIn(min = 48.dp),
                        ) {
                            Text("All bundles")
                        }
                    }

                    val bundles = NivoraCatalog.bundles
                    if (isWide) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            bundles.forEach { bundle ->
                                BundlePreviewCard(
                                    bundle = bundle,
                                    onAddBundle = { onAddToCart(bundle.id, 1) },
                                    onExploreBundles = { onNavigate(NivoraRoute.Bundles) },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            bundles.forEach { bundle ->
                                BundlePreviewCard(
                                    bundle = bundle,
                                    onAddBundle = { onAddToCart(bundle.id, 1) },
                                    onExploreBundles = { onNavigate(NivoraRoute.Bundles) },
                                )
                            }
                        }
                    }
                }

                // F. About preview & G. Feedback CTA
                val aboutAndFeedback = @Composable {
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
                                text = "About the Nivora concept",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.semantics { heading() },
                            )
                            Text(
                                text = "Nivora is a student venture prototype created for DGU Venture Quest at DBS Global University. It explores how a small, transparent personal-care range can make everyday shopping less confusing for college students and young professionals.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            OutlinedButton(
                                onClick = { onNavigate(NivoraRoute.About) },
                                modifier = Modifier.heightIn(min = 48.dp),
                                shape = MaterialTheme.shapes.small,
                            ) {
                                Text("Read about Nivora")
                            }
                        }
                    }

                    Card(
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Feedback,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                                Text(
                                    text = "Help shape Nivora",
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.semantics { heading() },
                                )
                            }
                            Text(
                                text = "Tried this prototype? Tell us whether the product information felt clear and whether the illustrative pricing fits a student budget.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Button(
                                onClick = { onNavigate(NivoraRoute.Feedback()) },
                                modifier = Modifier
                                    .heightIn(min = 48.dp)
                                    .testTag("home_feedback_cta"),
                                shape = MaterialTheme.shapes.small,
                            ) {
                                Text("Share prototype feedback")
                            }
                        }
                    }
                }
                aboutAndFeedback()
            }
        }

        NivoraFooter(
            onNavigate = onNavigate,
            onRequestResetDemoData = onRequestResetDemoData,
        )
    }
}

@Composable
private fun PrincipleCard(
    icon: ImageVector,
    title: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp),
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun BundlePreviewCard(
    bundle: StarterBundle,
    onAddBundle: () -> Unit,
    onExploreBundles: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            BundleIllustration(bundle = bundle, height = 132.dp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = bundle.name,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f),
                )
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(50),
                ) {
                    Text(
                        text = "Save ${NivoraCatalog.formatRupees(bundle.savingsPaise)}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                bundle.includedProducts.forEach { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = "${item.name} (${NivoraCatalog.formatRupees(item.pricePaise)})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            PriceDisplay(
                pricePaise = bundle.bundlePricePaise,
                compareAtPaise = bundle.individualTotalPaise,
                emphasize = true,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(
                    onClick = onExploreBundles,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp),
                    shape = MaterialTheme.shapes.small,
                ) {
                    Text("Details")
                }
                Button(
                    onClick = onAddBundle,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .testTag("home_add_bundle_${bundle.id}"),
                    shape = MaterialTheme.shapes.small,
                ) {
                    Text("Add bundle")
                }
            }
        }
    }
}
