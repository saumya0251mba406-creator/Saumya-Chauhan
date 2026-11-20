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
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.CartLineItem
import com.example.data.CartSummary
import com.example.data.CheckoutFormState
import com.example.data.CheckoutValidationErrors
import com.example.data.NivoraCatalog
import com.example.ui.components.CartSummaryCard
import com.example.ui.components.DemoNoticeBanner
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.NivoraFooter
import com.example.ui.components.NivoraFormField
import com.example.ui.state.NivoraRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    cartItems: List<CartLineItem>,
    cartSummary: CartSummary,
    formState: CheckoutFormState,
    validationErrors: CheckoutValidationErrors,
    isSubmitting: Boolean,
    onUpdateForm: ((CheckoutFormState) -> CheckoutFormState) -> Unit,
    onSubmitOrder: () -> Unit,
    onNavigate: (NivoraRoute) -> Unit,
    onNavigateBack: () -> Unit,
    onRequestResetDemoData: () -> Unit,
) {
    BackHandler(onBack = onNavigateBack)
    val scrollState = rememberScrollState()
    var stateDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .testTag("screen_checkout"),
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
                Text(
                    text = "Demo checkout",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.semantics { heading() },
                )

                DemoNoticeBanner(
                    text = "Demo checkout — use fictional details only. No payment will be processed.",
                    prominent = true,
                    modifier = Modifier.testTag("checkout_demo_notice"),
                )

                if (cartItems.isEmpty()) {
                    EmptyStateCard(
                        icon = Icons.Outlined.ShoppingBag,
                        title = "Your cart is empty",
                        message = "Add at least one Nivora essential or starter bundle before opening demo checkout.",
                        primaryActionLabel = "Shop essentials",
                        onPrimaryAction = { onNavigate(NivoraRoute.Shop()) },
                        secondaryActionLabel = "Back to cart",
                        onSecondaryAction = { onNavigate(NivoraRoute.Cart) },
                        testTag = "checkout_empty_cart_guard",
                    )
                } else {
                    val formSection = @Composable {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Card(
                                shape = MaterialTheme.shapes.medium,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Column(
                                    modifier = Modifier.padding(18.dp),
                                    verticalArrangement = Arrangement.spacedBy(14.dp),
                                ) {
                                    Text(
                                        text = "Guest delivery simulation details",
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.semantics { heading() },
                                    )
                                    Text(
                                        text = "No account login is required. Contact and address fields stay in temporary memory only and are never saved.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )

                                    NivoraFormField(
                                        label = "Full name",
                                        value = formState.fullName,
                                        onValueChange = { v -> onUpdateForm { it.copy(fullName = v) } },
                                        placeholder = "e.g. Aarav Sharma (fictional)",
                                        errorText = validationErrors.fullName,
                                        testTag = "checkout_full_name",
                                    )

                                    NivoraFormField(
                                        label = "Email",
                                        value = formState.email,
                                        onValueChange = { v -> onUpdateForm { it.copy(email = v) } },
                                        placeholder = "e.g. student@example.com",
                                        errorText = validationErrors.email,
                                        keyboardType = KeyboardType.Email,
                                        testTag = "checkout_email",
                                    )

                                    NivoraFormField(
                                        label = "Mobile number (10 digits)",
                                        value = formState.mobileNumber,
                                        onValueChange = { v ->
                                            val digits = v.filter { it.isDigit() }.take(10)
                                            onUpdateForm { it.copy(mobileNumber = digits) }
                                        },
                                        placeholder = "10-digit fictional number",
                                        errorText = validationErrors.mobileNumber,
                                        keyboardType = KeyboardType.Phone,
                                        testTag = "checkout_mobile",
                                    )

                                    NivoraFormField(
                                        label = "Address line",
                                        value = formState.addressLine,
                                        onValueChange = { v -> onUpdateForm { it.copy(addressLine = v) } },
                                        placeholder = "e.g. Hostel Block B, Room 12 (fictional)",
                                        errorText = validationErrors.addressLine,
                                        testTag = "checkout_address",
                                    )

                                    NivoraFormField(
                                        label = "City",
                                        value = formState.city,
                                        onValueChange = { v -> onUpdateForm { it.copy(city = v) } },
                                        placeholder = "e.g. Dehradun",
                                        errorText = validationErrors.city,
                                        testTag = "checkout_city",
                                    )

                                    // State selector (strictly limited to 4 states)
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = "State",
                                            style = MaterialTheme.typography.labelLarge,
                                        )
                                        ExposedDropdownMenuBox(
                                            expanded = stateDropdownExpanded,
                                            onExpandedChange = { stateDropdownExpanded = !stateDropdownExpanded },
                                        ) {
                                            OutlinedTextField(
                                                value = formState.state,
                                                onValueChange = {},
                                                readOnly = true,
                                                placeholder = { Text("Select state") },
                                                trailingIcon = {
                                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                                        expanded = stateDropdownExpanded,
                                                    )
                                                },
                                                isError = validationErrors.state != null,
                                                modifier = Modifier
                                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                                    .fillMaxWidth()
                                                    .testTag("checkout_state_dropdown")
                                                    .semantics {
                                                        if (validationErrors.state != null) {
                                                            error(validationErrors.state)
                                                        }
                                                    },
                                                shape = MaterialTheme.shapes.small,
                                            )
                                            ExposedDropdownMenu(
                                                expanded = stateDropdownExpanded,
                                                onDismissRequest = { stateDropdownExpanded = false },
                                            ) {
                                                NivoraCatalog.ALLOWED_CHECKOUT_STATES.forEach { stateName ->
                                                    DropdownMenuItem(
                                                        text = { Text(stateName) },
                                                        onClick = {
                                                            onUpdateForm { it.copy(state = stateName) }
                                                            stateDropdownExpanded = false
                                                        },
                                                        modifier = Modifier.testTag("checkout_state_${stateName.lowercase().replace(" ", "_")}"),
                                                    )
                                                }
                                            }
                                        }
                                        if (validationErrors.state != null) {
                                            Text(
                                                text = validationErrors.state,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                                            )
                                        }
                                        Text(
                                            text = "These states represent the prototype’s initial market focus. Actual delivery coverage is not verified.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }

                                    NivoraFormField(
                                        label = "PIN code (6 digits)",
                                        value = formState.pinCode,
                                        onValueChange = { v ->
                                            val digits = v.filter { it.isDigit() }.take(6)
                                            onUpdateForm { it.copy(pinCode = digits) }
                                        },
                                        placeholder = "e.g. 248001",
                                        errorText = validationErrors.pinCode,
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Done,
                                        testTag = "checkout_pin",
                                    )
                                }
                            }

                            // Simulated payment section (NO card/CVV/UPI/bank details)
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
                                        text = "Payment simulation",
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.semantics { heading() },
                                    )
                                    Surface(
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                                        shape = MaterialTheme.shapes.small,
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                                        modifier = Modifier.fillMaxWidth(),
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        ) {
                                            RadioButton(selected = true, onClick = null)
                                            Icon(
                                                imageVector = Icons.Outlined.Lock,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp),
                                                tint = MaterialTheme.colorScheme.primary,
                                            )
                                            Text(
                                                text = "Simulated payment — no money collected",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.SemiBold,
                                                ),
                                            )
                                        }
                                    }
                                    Text(
                                        text = "This prototype never asks for card numbers, CVV, UPI IDs, or bank account credentials.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }

                    val summaryColumn = @Composable {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Card(
                                shape = MaterialTheme.shapes.medium,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Text(
                                        text = "Items in demo order",
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.semantics { heading() },
                                    )
                                    cartItems.forEach { item ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                        ) {
                                            Text(
                                                text = "${item.quantity} × ${item.name}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier.weight(1f),
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = NivoraCatalog.formatRupees(item.lineTotalPaise),
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.SemiBold,
                                                ),
                                            )
                                        }
                                    }
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                    Text(
                                        text = NivoraCatalog.PROTOTYPE_DISCLAIMER,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }

                            CartSummaryCard(
                                summary = cartSummary,
                                title = "Order total",
                            )

                            Button(
                                onClick = onSubmitOrder,
                                enabled = !isSubmitting && cartItems.isNotEmpty(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 52.dp)
                                    .testTag("place_demo_order_button"),
                                shape = MaterialTheme.shapes.small,
                            ) {
                                Text(if (isSubmitting) "Processing demo order..." else "Place demo order")
                            }
                        }
                    }

                    if (isWide) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(18.dp),
                            verticalAlignment = Alignment.Top,
                        ) {
                            Column(modifier = Modifier.weight(1.25f)) { formSection() }
                            Column(modifier = Modifier.weight(0.95f)) { summaryColumn() }
                        }
                    } else {
                        formSection()
                        summaryColumn()
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
