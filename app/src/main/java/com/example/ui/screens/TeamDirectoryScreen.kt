package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
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
import com.example.model.Stylist
import com.example.ui.components.LuxuryBadge
import com.example.ui.components.LuxuryCard
import com.example.ui.components.LuxuryGoldButton
import com.example.ui.theme.DeepBlackBackground
import com.example.ui.theme.DeepBlackBorder
import com.example.ui.theme.DeepBlackCard
import com.example.ui.theme.DeepBlackCardElevated
import com.example.ui.theme.GoldChampagne
import com.example.ui.theme.GoldDiagonalGradient
import com.example.ui.theme.LuxurySerif
import com.example.ui.theme.OffWhite
import com.example.ui.theme.SophisticatedWhite
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.SalonViewModel

@Composable
fun TeamDirectoryScreen(
    viewModel: SalonViewModel,
    onOpenDrawer: () -> Unit,
    onNavigateBack: () -> Unit = { viewModel.navigateTo(AppScreen.HOME) },
    onNavigateToNewBooking: () -> Unit = { viewModel.navigateTo(AppScreen.NEW_BOOKING) },
    modifier: Modifier = Modifier
) {
    val stylists by viewModel.stylists.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBlackBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("team_directory_lazy_column")
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
                                text = "MESTRES DA BELEZA",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = LuxurySerif,
                                    color = SophisticatedWhite,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = "Equipe de Especialistas & Visagistas",
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

            items(stylists) { stylist ->
                LuxuryCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, GoldDiagonalGradient, CircleShape)
                                    .background(DeepBlackCardElevated),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stylist.avatarInitials,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontFamily = LuxurySerif,
                                        color = GoldChampagne,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stylist.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = LuxurySerif,
                                        color = SophisticatedWhite,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = stylist.role,
                                    style = MaterialTheme.typography.bodySmall.copy(color = GoldChampagne, fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = stylist.title,
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextDim, fontSize = 11.sp)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = GoldChampagne, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${stylist.rating}",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = SophisticatedWhite, fontWeight = FontWeight.Bold)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Especialidades:",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.sp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            stylist.specialties.forEach { spec ->
                                LuxuryBadge(text = spec, isGold = false)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        LuxuryGoldButton(
                            text = "Agendar com ${stylist.name.split(" ").first()}",
                            onClick = {
                                viewModel.setPreselectedBooking(stylist = stylist)
                                onNavigateToNewBooking()
                            },
                            icon = Icons.Default.CalendarMonth,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}
