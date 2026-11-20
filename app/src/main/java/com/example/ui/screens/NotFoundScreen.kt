package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExploreOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.NivoraFooter
import com.example.ui.state.NivoraRoute

@Composable
fun NotFoundScreen(
    attemptedRoute: String,
    onNavigate: (NivoraRoute) -> Unit,
    onNavigateBack: () -> Unit,
    onRequestResetDemoData: () -> Unit,
) {
    BackHandler(onBack = onNavigateBack)
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 720.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            EmptyStateCard(
                icon = Icons.Outlined.ExploreOff,
                title = "Page not found",
                message = "The route \"$attemptedRoute\" is not part of the Nivora prototype. Return to Home or browse the Shop.",
                primaryActionLabel = "Go to Home",
                onPrimaryAction = { onNavigate(NivoraRoute.Home) },
                secondaryActionLabel = "Shop essentials",
                onSecondaryAction = { onNavigate(NivoraRoute.Shop()) },
                testTag = "route_not_found_state",
            )
        }

        NivoraFooter(
            onNavigate = onNavigate,
            onRequestResetDemoData = onRequestResetDemoData,
        )
    }
}
