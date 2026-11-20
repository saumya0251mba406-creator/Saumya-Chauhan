package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.data.NivoraCatalog
import com.example.data.Product
import com.example.data.ProductCategory
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.FilterControls
import com.example.ui.components.NivoraFooter
import com.example.ui.components.ProductCard
import com.example.ui.state.NivoraRoute
import com.example.ui.state.ShopFilterState
import com.example.ui.state.ShopSortOption

@Composable
fun ShopScreen(
    filterState: ShopFilterState,
    filteredProducts: List<Product>,
    onSearchChange: (String) -> Unit,
    onCategorySelect: (ProductCategory?) -> Unit,
    onSortSelect: (ShopSortOption) -> Unit,
    onClearFilters: () -> Unit,
    onAddToCart: (String, Int) -> Unit,
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
            .testTag("screen_shop"),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .widthIn(max = 960.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            val isWide = maxWidth >= 640.dp

            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Shop essentials",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.semantics { heading() },
                    )
                    Text(
                        text = "Four straightforward skincare and haircare essentials—no overwhelming catalogue or hidden upsells.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                FilterControls(
                    filterState = filterState,
                    resultCount = filteredProducts.size,
                    totalCount = NivoraCatalog.products.size,
                    onSearchChange = onSearchChange,
                    onCategorySelect = onCategorySelect,
                    onSortSelect = onSortSelect,
                    onClearFilters = onClearFilters,
                )

                if (filteredProducts.isEmpty()) {
                    EmptyStateCard(
                        icon = Icons.Outlined.SearchOff,
                        title = "No matching essentials found",
                        message = "Try clearing your search or switching category filters to see all four Nivora starter essentials.",
                        primaryActionLabel = "Clear filters",
                        onPrimaryAction = onClearFilters,
                        secondaryActionLabel = "Explore starter bundles",
                        onSecondaryAction = { onNavigate(NivoraRoute.Bundles) },
                        testTag = "shop_empty_state",
                    )
                } else if (isWide) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        filteredProducts.chunked(2).forEach { rowProducts ->
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
                                if (rowProducts.size == 1) {
                                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        filteredProducts.forEach { product ->
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
        }

        NivoraFooter(
            onNavigate = onNavigate,
            onRequestResetDemoData = onRequestResetDemoData,
        )
    }
}
