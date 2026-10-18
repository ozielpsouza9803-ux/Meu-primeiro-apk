package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ServiceCategory
import com.example.ui.home.ServiceCard
import com.example.ui.theme.DeepBlackBackground
import com.example.ui.theme.DeepBlackCard
import com.example.ui.theme.GoldChampagne
import com.example.ui.theme.LuxurySerif
import com.example.ui.theme.SophisticatedWhite
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.SalonViewModel

@Composable
fun ServicesCatalogScreen(
    viewModel: SalonViewModel,
    onOpenDrawer: () -> Unit,
    onNavigateBack: () -> Unit = { viewModel.navigateTo(AppScreen.HOME) },
    onNavigateToNewBooking: () -> Unit = { viewModel.navigateTo(AppScreen.NEW_BOOKING) },
    modifier: Modifier = Modifier
) {
    val services by viewModel.services.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val filteredServices = if (uiState.selectedCategory == ServiceCategory.TODOS) {
        services
    } else {
        services.filter { it.category == uiState.selectedCategory }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBlackBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("services_catalog_lazy_column")
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(DeepBlackCard)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = GoldChampagne, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "MENU DE ALTA COSTURA",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = LuxurySerif,
                                    color = SophisticatedWhite,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = "Rituais de Beleza e Bem-Estar",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextDim, fontSize = 11.sp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onOpenDrawer,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(DeepBlackCard)
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = GoldChampagne, modifier = Modifier.size(20.dp))
                    }
                }
            }

            // Categories
            item {
                val categories = ServiceCategory.entries
                ScrollableTabRow(
                    selectedTabIndex = categories.indexOf(uiState.selectedCategory),
                    containerColor = Color.Transparent,
                    contentColor = GoldChampagne,
                    edgePadding = 16.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[categories.indexOf(uiState.selectedCategory)]),
                            color = GoldChampagne,
                            height = 2.dp
                        )
                    },
                    divider = {}
                ) {
                    categories.forEach { category ->
                        val isSelected = uiState.selectedCategory == category
                        Tab(
                            selected = isSelected,
                            onClick = { viewModel.setCategoryFilter(category) },
                            text = {
                                Text(
                                    text = category.label,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = if (isSelected) GoldChampagne else TextMuted,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            items(filteredServices) { service ->
                ServiceCard(
                    service = service,
                    onBook = {
                        viewModel.setPreselectedBooking(service = service)
                        onNavigateToNewBooking()
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}
