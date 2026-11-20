package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.PrototypeEventCounts
import com.example.ui.components.DemoNoticeBanner
import com.example.ui.components.NivoraFooter
import com.example.ui.state.NivoraRoute

@Composable
fun AboutScreen(
    eventCounts: PrototypeEventCounts,
    onNavigate: (NivoraRoute) -> Unit,
    onNavigateBack: () -> Unit,
    onRequestResetDemoData: () -> Unit,
) {
    BackHandler(onBack = onNavigateBack)
    val scrollState = rememberScrollState()
    var validationExpanded by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .testTag("screen_about"),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 840.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "About Nivora",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.semantics { heading() },
            )
            Text(
                text = "Simple care. Everyday confidence.",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )

            AboutSectionCard(
                title = "The customer problem",
                body = "Many students and young adults want a straightforward daily skincare and haircare routine, but shopping online often feels confusing. Wide product catalogues, technical ingredient claims, and varying price points make it hard to pick basic essentials with confidence.",
            )

            AboutSectionCard(
                title = "Who Nivora is designed for",
                body = "Nivora's primary focus is college students aged 18–24 who prefer affordable online shopping, alongside young working professionals aged 18–30. The venture's initial geographic focus is Uttarakhand and Delhi-NCR, with Uttar Pradesh as an additional planned market.",
            )

            AboutSectionCard(
                title = "Why a small essential range",
                body = "Instead of launching dozens of overlapping SKUs, this MVP deliberately presents four illustrative essentials (face wash, body lotion, shampoo, and hair serum) paired with two starter bundles. Keeping choices focused, product descriptions plain, and prices between ₹299 and ₹499 helps test whether simplicity reduces decision fatigue.",
            )

            AboutSectionCard(
                title = "Validating demand before scaling",
                body = "Before investing in manufacturing, packaging runs, or broad marketing, Nivora aims to validate actual customer interest, willingness to pay, and clarity of product information through small MVP tests and honest user feedback.",
            )

            DemoNoticeBanner(
                text = "Developed as a venture prototype for DGU Venture Quest, DBS Global University. This prototype is an academic assignment and does not represent university endorsement, regulatory approval, or a live commercial brand.",
                prominent = true,
            )

            // Collapsed "Project validation notes" section
            Card(
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(role = Role.Button) {
                                validationExpanded = !validationExpanded
                            }
                            .padding(horizontal = 18.dp, vertical = 16.dp)
                            .testTag("validation_notes_toggle"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Project validation notes",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.semantics { heading() },
                        )
                        Icon(
                            imageVector = if (validationExpanded) {
                                Icons.Outlined.ExpandLess
                            } else {
                                Icons.Outlined.ExpandMore
                            },
                            contentDescription = if (validationExpanded) "Collapse notes" else "Expand notes",
                        )
                    }

                    AnimatedVisibility(visible = validationExpanded) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 18.dp, end = 18.dp, bottom = 18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            Text(
                                text = "Planned future MVP targets (not achieved results):",
                                style = MaterialTheme.typography.labelLarge,
                            )
                            Text(
                                text = "• 50 first-time customers during an initial 30-day pilot\n" +
                                    "• At least 5% visitor-to-purchase conversion\n" +
                                    "• At least 70% positive surveyed product experience",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            Text(
                                text = "Anonymous prototype session interactions on this device only (not real customer demand or analytics):",
                                style = MaterialTheme.typography.labelLarge,
                            )
                            Text(
                                text = "Product views: ${eventCounts.productView}  •  " +
                                    "Add to cart: ${eventCounts.addToCart}  •  " +
                                    "Begin checkout: ${eventCounts.beginCheckout}  •  " +
                                    "Demo orders completed: ${eventCounts.demoOrderCompleted}  •  " +
                                    "Feedback submitted: ${eventCounts.feedbackSubmitted}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.testTag("local_event_counts_text"),
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedButton(
                    onClick = { onNavigate(NivoraRoute.Shop()) },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp),
                    shape = MaterialTheme.shapes.small,
                ) {
                    Text("Explore shop")
                }
                Button(
                    onClick = { onNavigate(NivoraRoute.Feedback()) },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp),
                    shape = MaterialTheme.shapes.small,
                ) {
                    Text("Share feedback")
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
private fun AboutSectionCard(
    title: String,
    body: String,
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.semantics { heading() },
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
