package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.data.FeedbackEntry
import com.example.data.NivoraCatalog
import com.example.data.TriResponse
import com.example.data.TryBrandResponse
import com.example.ui.components.DemoNoticeBanner
import com.example.ui.components.NivoraFooter
import com.example.ui.components.NivoraFormField
import com.example.ui.state.FeedbackFormState
import com.example.ui.state.FeedbackValidationErrors
import com.example.ui.state.NivoraRoute

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FeedbackScreen(
    formState: FeedbackFormState,
    validationErrors: FeedbackValidationErrors,
    isSubmitting: Boolean,
    submittedJustNow: Boolean,
    savedEntries: List<FeedbackEntry>,
    onUpdateForm: ((FeedbackFormState) -> FeedbackFormState) -> Unit,
    onSubmitFeedback: () -> Unit,
    onNavigate: (NivoraRoute) -> Unit,
    onNavigateBack: () -> Unit,
    onRequestResetDemoData: () -> Unit,
) {
    BackHandler(onBack = onNavigateBack)
    val scrollState = rememberScrollState()
    var targetExpanded by remember { mutableStateOf(false) }
    val targetOptions = remember { NivoraCatalog.feedbackTargetOptions() }
    val selectedTargetLabel = targetOptions.find { it.first == formState.targetId }?.second
        ?: NivoraCatalog.GENERAL_FEEDBACK_TARGET_LABEL

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .testTag("screen_feedback"),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 820.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Help shape Nivora",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.semantics { heading() },
                )
                Text(
                    text = "Share your thoughts on product clarity, pricing, and overall experience. No personal contact details are requested.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            DemoNoticeBanner(
                text = "Prototype feedback is saved only on this device and is not submitted to a company server.",
                prominent = true,
            )

            if (submittedJustNow) {
                DemoNoticeBanner(
                    text = "Thank you! Your prototype feedback was saved locally in this session.",
                    prominent = false,
                    modifier = Modifier
                        .testTag("feedback_success_banner")
                        .semantics { liveRegion = LiveRegionMode.Polite },
                )
            }

            Card(
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    // Target product / bundle / general
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Product, bundle, or general experience",
                            style = MaterialTheme.typography.labelLarge,
                        )
                        ExposedDropdownMenuBox(
                            expanded = targetExpanded,
                            onExpandedChange = { targetExpanded = !targetExpanded },
                        ) {
                            OutlinedTextField(
                                value = selectedTargetLabel,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = targetExpanded)
                                },
                                modifier = Modifier
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                    .fillMaxWidth()
                                    .testTag("feedback_target_dropdown"),
                                shape = MaterialTheme.shapes.small,
                            )
                            ExposedDropdownMenu(
                                expanded = targetExpanded,
                                onDismissRequest = { targetExpanded = false },
                            ) {
                                targetOptions.forEach { (id, label) ->
                                    DropdownMenuItem(
                                        text = { Text(label) },
                                        onClick = {
                                            onUpdateForm { it.copy(targetId = id) }
                                            targetExpanded = false
                                        },
                                    )
                                }
                            }
                        }
                    }

                    // Rating 1..5
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Overall experience rating (1 to 5)",
                            style = MaterialTheme.typography.labelLarge,
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            (1..5).forEach { score ->
                                FilterChip(
                                    selected = formState.rating == score,
                                    onClick = { onUpdateForm { it.copy(rating = score) } },
                                    label = { Text("$score / 5") },
                                    modifier = Modifier
                                        .heightIn(min = 48.dp)
                                        .testTag("feedback_rating_$score"),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    ),
                                )
                            }
                        }
                        if (validationErrors.rating != null) {
                            Text(
                                text = validationErrors.rating,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                            )
                        }
                    }

                    // Info easy to understand
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Was the product information easy to understand?",
                            style = MaterialTheme.typography.labelLarge,
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            TriResponse.entries.forEach { option ->
                                FilterChip(
                                    selected = formState.infoEasyToUnderstand == option,
                                    onClick = { onUpdateForm { it.copy(infoEasyToUnderstand = option) } },
                                    label = { Text(option.label) },
                                    modifier = Modifier
                                        .heightIn(min = 48.dp)
                                        .testTag("feedback_info_${option.name.lowercase()}"),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    ),
                                )
                            }
                        }
                        if (validationErrors.infoEasyToUnderstand != null) {
                            Text(
                                text = validationErrors.infoEasyToUnderstand,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                            )
                        }
                    }

                    // Pricing affordable
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Did the pricing feel affordable?",
                            style = MaterialTheme.typography.labelLarge,
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            TriResponse.entries.forEach { option ->
                                FilterChip(
                                    selected = formState.pricingAffordable == option,
                                    onClick = { onUpdateForm { it.copy(pricingAffordable = option) } },
                                    label = { Text(option.label) },
                                    modifier = Modifier
                                        .heightIn(min = 48.dp)
                                        .testTag("feedback_pricing_${option.name.lowercase()}"),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    ),
                                )
                            }
                        }
                        if (validationErrors.pricingAffordable != null) {
                            Text(
                                text = validationErrors.pricingAffordable,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                            )
                        }
                    }

                    // Optional comment (max 500 chars)
                    NivoraFormField(
                        label = "Optional comment (${formState.comment.length}/500 characters)",
                        value = formState.comment,
                        onValueChange = { v -> onUpdateForm { it.copy(comment = v.take(500)) } },
                        placeholder = "Share what felt clear, confusing, or priced right...",
                        errorText = validationErrors.comment,
                        singleLine = false,
                        minLines = 3,
                        maxLines = 5,
                        imeAction = ImeAction.Default,
                        testTag = "feedback_comment_input",
                    )

                    // Optional willingness to try
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Would you be willing to try Nivora if launched? (Optional)",
                            style = MaterialTheme.typography.labelLarge,
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            TryBrandResponse.entries.forEach { option ->
                                val isSelected = formState.willingToTry == option
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        onUpdateForm {
                                            it.copy(willingToTry = if (isSelected) null else option)
                                        }
                                    },
                                    label = { Text(option.label) },
                                    modifier = Modifier
                                        .heightIn(min = 48.dp)
                                        .testTag("feedback_try_${option.name.lowercase()}"),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    ),
                                )
                            }
                        }
                    }

                    Button(
                        onClick = onSubmitFeedback,
                        enabled = !isSubmitting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .testTag("feedback_submit_button"),
                        shape = MaterialTheme.shapes.small,
                    ) {
                        Text(if (isSubmitting) "Saving feedback..." else "Save prototype feedback")
                    }
                }
            }

            // Locally saved feedback list (honestly labeled)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Saved prototype session feedback",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.semantics { heading() },
                )
                if (savedEntries.isEmpty()) {
                    Text(
                        text = "No feedback saved in this session yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    DemoNoticeBanner(
                        text = "Feedback from this prototype session — not verified purchases.",
                        prominent = false,
                    )
                    savedEntries.forEach { entry ->
                        FeedbackEntryCard(entry = entry)
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
