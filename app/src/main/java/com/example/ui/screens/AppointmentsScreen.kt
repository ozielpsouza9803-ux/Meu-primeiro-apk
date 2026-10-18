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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.FloatingActionButton
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
import com.example.ui.admin.AdminAppointmentStatusFilters
import com.example.ui.admin.AppointmentManagementCard
import com.example.ui.components.LuxuryBadge
import com.example.ui.theme.DeepBlackBackground
import com.example.ui.theme.DeepBlackCard
import com.example.ui.theme.GoldChampagne
import com.example.ui.theme.LuxurySerif
import com.example.ui.theme.SophisticatedWhite
import com.example.ui.theme.TextDim
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.SalonViewModel

@Composable
fun AppointmentsScreen(
    viewModel: SalonViewModel,
    onOpenDrawer: () -> Unit,
    onNavigateBack: () -> Unit = { viewModel.navigateTo(AppScreen.HOME) },
    onNavigateToNewBooking: () -> Unit = { viewModel.navigateTo(AppScreen.NEW_BOOKING) },
    modifier: Modifier = Modifier
) {
    val appointments by viewModel.appointments.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val filteredAppointments = if (uiState.adminFilterStatus == null) {
        appointments
    } else {
        appointments.filter { it.status == uiState.adminFilterStatus }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBlackBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("appointments_screen_lazy_column")
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
                                text = "AGENDA DO SALÃO",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = LuxurySerif,
                                    color = SophisticatedWhite,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = "Controle de Atendimentos VIP",
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

            item {
                AdminAppointmentStatusFilters(
                    selectedStatus = uiState.adminFilterStatus,
                    onSelect = { viewModel.setAdminStatusFilter(it) },
                    totalAppointments = appointments.size
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            items(filteredAppointments) { apt ->
                AppointmentManagementCard(
                    appointment = apt,
                    onUpdateStatus = { newStatus ->
                        viewModel.updateAppointmentStatus(apt.id, newStatus)
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(90.dp))
            }
        }

        FloatingActionButton(
            onClick = onNavigateToNewBooking,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("appointments_fab_add"),
            containerColor = GoldChampagne,
            contentColor = DeepBlackBackground,
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = DeepBlackBackground)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "NOVO HORÁRIO",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepBlackBackground
                    )
                )
            }
        }
    }
}
