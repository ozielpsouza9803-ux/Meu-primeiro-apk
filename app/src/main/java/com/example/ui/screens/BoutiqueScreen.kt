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
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LuxuryBadge
import com.example.ui.components.LuxuryCard
import com.example.ui.components.LuxuryGoldButton
import com.example.ui.theme.DeepBlackBackground
import com.example.ui.theme.DeepBlackCard
import com.example.ui.theme.GoldChampagne
import com.example.ui.theme.LuxurySerif
import com.example.ui.theme.OffWhite
import com.example.ui.theme.SophisticatedWhite
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.SalonViewModel

@Composable
fun BoutiqueScreen(
    viewModel: SalonViewModel,
    onOpenDrawer: () -> Unit,
    onNavigateBack: () -> Unit = { viewModel.navigateTo(AppScreen.HOME) },
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBlackBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("boutique_screen_lazy_column")
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
                                text = "BOUTIQUE EXCLUSIVA",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = LuxurySerif,
                                    color = SophisticatedWhite,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = "Cosméticos & Elixires Home Care",
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

            items(products) { product ->
                LuxuryCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = product.brand.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = GoldChampagne,
                                        letterSpacing = 1.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                                Text(
                                    text = product.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = LuxurySerif,
                                        color = SophisticatedWhite,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            if (product.isExclusive) {
                                LuxuryBadge(text = "Exclusivo", isGold = true)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = product.description,
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, lineHeight = 16.sp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "R$ ${"%.2f".format(product.price)}",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        color = GoldChampagne,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "Disponível: ${product.stockQuantity} unidades",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextDim, fontSize = 10.sp)
                                )
                            }

                            LuxuryGoldButton(
                                text = "Reservar Frasco",
                                onClick = {
                                    viewModel.adjustStock(product.id, -1)
                                },
                                icon = Icons.Default.ShoppingBag
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}
