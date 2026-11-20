package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NivoraCatalog
import com.example.ui.state.NivoraRoute
import com.example.ui.theme.LoraFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NivoraHeader(
    currentRoute: NivoraRoute,
    canNavigateBack: Boolean,
    cartItemCount: Int,
    isCompactWidth: Boolean,
    onOpenMenu: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigate: (NivoraRoute) -> Unit,
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
        ),
        navigationIcon = {
            if (canNavigateBack && currentRoute !is NivoraRoute.Home) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("nav_back_button"),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Navigate back",
                    )
                }
            } else if (isCompactWidth) {
                IconButton(
                    onClick = onOpenMenu,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("nav_menu_button"),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Menu,
                        contentDescription = "Open navigation menu",
                    )
                }
            }
        },
        title = {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(role = Role.Button) { onNavigate(NivoraRoute.Home) }
                    .padding(horizontal = 4.dp, vertical = 6.dp)
                    .testTag("header_wordmark"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                NivoraLogoMark(size = 26.dp)
                Text(
                    text = "NIVORA",
                    fontFamily = LoraFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    letterSpacing = 1.6.sp,
                    maxLines = 1,
                )
            }
        },
        actions = {
            if (!isCompactWidth) {
                TextButton(
                    onClick = { onNavigate(NivoraRoute.Shop()) },
                    modifier = Modifier.heightIn(min = 48.dp),
                ) { Text("Shop") }
                TextButton(
                    onClick = { onNavigate(NivoraRoute.Bundles) },
                    modifier = Modifier.heightIn(min = 48.dp),
                ) { Text("Bundles") }
                TextButton(
                    onClick = { onNavigate(NivoraRoute.About) },
                    modifier = Modifier.heightIn(min = 48.dp),
                ) { Text("About") }
                TextButton(
                    onClick = { onNavigate(NivoraRoute.Feedback()) },
                    modifier = Modifier.heightIn(min = 48.dp),
                ) { Text("Feedback") }
            }

            IconButton(
                onClick = { onNavigate(NivoraRoute.Cart) },
                modifier = Modifier
                    .padding(end = 4.dp)
                    .size(48.dp)
                    .testTag("header_cart_button"),
            ) {
                BadgedBox(
                    badge = {
                        if (cartItemCount > 0) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.testTag("header_cart_badge"),
                            ) {
                                Text(cartItemCount.toString())
                            }
                        }
                    },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingBag,
                        contentDescription = "Open cart, $cartItemCount items",
                    )
                }
            }
        },
    )
}

@Composable
fun NivoraDrawerContent(
    currentRoute: NivoraRoute,
    cartItemCount: Int,
    onNavigate: (NivoraRoute) -> Unit,
    onRequestReset: () -> Unit,
    onCloseDrawer: () -> Unit,
) {
    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 20.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                NivoraLogoMark(size = 30.dp)
                Column {
                    Text(
                        text = "NIVORA",
                        fontFamily = LoraFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        letterSpacing = 1.4.sp,
                    )
                    Text(
                        text = "Simple care. Everyday confidence.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant,
            )

            DrawerNavItem(
                label = "Home",
                selected = currentRoute is NivoraRoute.Home,
                icon = Icons.Outlined.Home,
                onClick = {
                    onNavigate(NivoraRoute.Home)
                    onCloseDrawer()
                },
            )
            DrawerNavItem(
                label = "Shop essentials",
                selected = currentRoute is NivoraRoute.Shop,
                icon = Icons.Outlined.Storefront,
                onClick = {
                    onNavigate(NivoraRoute.Shop())
                    onCloseDrawer()
                },
            )
            DrawerNavItem(
                label = "Starter bundles",
                selected = currentRoute is NivoraRoute.Bundles,
                icon = Icons.Outlined.Inventory2,
                onClick = {
                    onNavigate(NivoraRoute.Bundles)
                    onCloseDrawer()
                },
            )
            DrawerNavItem(
                label = if (cartItemCount > 0) "Cart ($cartItemCount)" else "Cart",
                selected = currentRoute is NivoraRoute.Cart,
                icon = Icons.Outlined.ShoppingBag,
                onClick = {
                    onNavigate(NivoraRoute.Cart)
                    onCloseDrawer()
                },
            )
            DrawerNavItem(
                label = "About Nivora",
                selected = currentRoute is NivoraRoute.About,
                icon = Icons.Outlined.Info,
                onClick = {
                    onNavigate(NivoraRoute.About)
                    onCloseDrawer()
                },
            )
            DrawerNavItem(
                label = "Share feedback",
                selected = currentRoute is NivoraRoute.Feedback,
                icon = Icons.Outlined.Feedback,
                onClick = {
                    onNavigate(NivoraRoute.Feedback())
                    onCloseDrawer()
                },
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    onCloseDrawer()
                    onRequestReset()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                shape = MaterialTheme.shapes.small,
            ) {
                Icon(
                    imageVector = Icons.Outlined.RestartAlt,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reset demo data")
            }
        }
    }
}

@Composable
private fun DrawerNavItem(
    label: String,
    selected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
) {
    NavigationDrawerItem(
        label = { Text(label) },
        selected = selected,
        onClick = onClick,
        icon = { Icon(imageVector = icon, contentDescription = null) },
        modifier = Modifier.heightIn(min = 48.dp),
    )
}

@Composable
fun NivoraBottomBar(
    currentRoute: NivoraRoute,
    onNavigate: (NivoraRoute) -> Unit,
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
    ) {
        NavigationBarItem(
            selected = currentRoute is NivoraRoute.Home,
            onClick = { onNavigate(NivoraRoute.Home) },
            icon = { Icon(Icons.Outlined.Home, contentDescription = null) },
            label = { Text("Home", maxLines = 1, overflow = TextOverflow.Ellipsis) },
            modifier = Modifier.testTag("bottom_nav_home"),
        )
        NavigationBarItem(
            selected = currentRoute is NivoraRoute.Shop,
            onClick = { onNavigate(NivoraRoute.Shop()) },
            icon = { Icon(Icons.Outlined.Storefront, contentDescription = null) },
            label = { Text("Shop", maxLines = 1, overflow = TextOverflow.Ellipsis) },
            modifier = Modifier.testTag("bottom_nav_shop"),
        )
        NavigationBarItem(
            selected = currentRoute is NivoraRoute.Bundles,
            onClick = { onNavigate(NivoraRoute.Bundles) },
            icon = { Icon(Icons.Outlined.Inventory2, contentDescription = null) },
            label = { Text("Bundles", maxLines = 1, overflow = TextOverflow.Ellipsis) },
            modifier = Modifier.testTag("bottom_nav_bundles"),
        )
        NavigationBarItem(
            selected = currentRoute is NivoraRoute.About,
            onClick = { onNavigate(NivoraRoute.About) },
            icon = { Icon(Icons.Outlined.Info, contentDescription = null) },
            label = { Text("About", maxLines = 1, overflow = TextOverflow.Ellipsis) },
            modifier = Modifier.testTag("bottom_nav_about"),
        )
        NavigationBarItem(
            selected = currentRoute is NivoraRoute.Feedback,
            onClick = { onNavigate(NivoraRoute.Feedback()) },
            icon = { Icon(Icons.Outlined.Feedback, contentDescription = null) },
            label = { Text("Feedback", maxLines = 1, overflow = TextOverflow.Ellipsis) },
            modifier = Modifier.testTag("bottom_nav_feedback"),
        )
    }
}

@Composable
fun NivoraSideRail(
    currentRoute: NivoraRoute,
    onNavigate: (NivoraRoute) -> Unit,
) {
    NavigationRail(
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        NavigationRailItem(
            selected = currentRoute is NivoraRoute.Home,
            onClick = { onNavigate(NivoraRoute.Home) },
            icon = { Icon(Icons.Outlined.Home, contentDescription = null) },
            label = { Text("Home") },
        )
        NavigationRailItem(
            selected = currentRoute is NivoraRoute.Shop,
            onClick = { onNavigate(NivoraRoute.Shop()) },
            icon = { Icon(Icons.Outlined.Storefront, contentDescription = null) },
            label = { Text("Shop") },
        )
        NavigationRailItem(
            selected = currentRoute is NivoraRoute.Bundles,
            onClick = { onNavigate(NivoraRoute.Bundles) },
            icon = { Icon(Icons.Outlined.Inventory2, contentDescription = null) },
            label = { Text("Bundles") },
        )
        NavigationRailItem(
            selected = currentRoute is NivoraRoute.About,
            onClick = { onNavigate(NivoraRoute.About) },
            icon = { Icon(Icons.Outlined.Info, contentDescription = null) },
            label = { Text("About") },
        )
        NavigationRailItem(
            selected = currentRoute is NivoraRoute.Feedback,
            onClick = { onNavigate(NivoraRoute.Feedback()) },
            icon = { Icon(Icons.Outlined.Feedback, contentDescription = null) },
            label = { Text("Feedback") },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NivoraFooter(
    onNavigate: (NivoraRoute) -> Unit,
    onRequestResetDemoData: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                NivoraLogoMark(size = 24.dp)
                Column {
                    Text(
                        text = "NIVORA",
                        fontFamily = LoraFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        letterSpacing = 1.4.sp,
                    )
                    Text(
                        text = "Simple care. Everyday confidence.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                TextButton(
                    onClick = { onNavigate(NivoraRoute.Shop()) },
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .testTag("footer_link_shop"),
                ) { Text("Shop") }
                TextButton(
                    onClick = { onNavigate(NivoraRoute.Bundles) },
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .testTag("footer_link_bundles"),
                ) { Text("Bundles") }
                TextButton(
                    onClick = { onNavigate(NivoraRoute.About) },
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .testTag("footer_link_about"),
                ) { Text("About") }
                TextButton(
                    onClick = { onNavigate(NivoraRoute.Feedback()) },
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .testTag("footer_link_feedback"),
                ) { Text("Feedback") }
            }

            Text(
                text = NivoraCatalog.PROTOTYPE_DISCLAIMER,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Text(
                text = "DGU Venture Quest prototype • DBS Global University",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            OutlinedButton(
                onClick = onRequestResetDemoData,
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .testTag("footer_reset_demo_button"),
                shape = MaterialTheme.shapes.small,
            ) {
                Icon(
                    imageVector = Icons.Outlined.RestartAlt,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Reset demo data")
            }
        }
    }
}

@Composable
fun ResetDemoDataDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Reset Nivora demo data?",
                modifier = Modifier.semantics { heading() },
            )
        },
        text = {
            Text(
                text = "This will clear only Nivora's saved cart items, demo orders, prototype feedback, and local session interaction counts on this device. Unrelated storage will not be affected.",
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError,
                ),
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .testTag("confirm_reset_demo_button"),
            ) {
                Text("Reset data")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .testTag("cancel_reset_demo_button"),
            ) {
                Text("Cancel")
            }
        },
    )
}
