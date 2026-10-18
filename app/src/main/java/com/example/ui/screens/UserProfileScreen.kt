package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Appointment
import com.example.model.AppointmentStatus
import com.example.ui.components.LuxuryBadge
import com.example.ui.components.LuxuryGoldButton
import com.example.ui.components.LuxuryOutlinedButton
import com.example.ui.theme.DeepBlackCard
import com.example.ui.theme.DeepBlackCardElevated
import com.example.ui.theme.DeepBlackSurface
import com.example.ui.theme.GoldChampagne
import com.example.ui.theme.GoldLinearGradient
import com.example.ui.theme.LuxurySerif
import com.example.ui.theme.SophisticatedWhite
import com.example.ui.theme.SuccessEmerald
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.SalonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    viewModel: SalonViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val appointments by viewModel.appointments.collectAsState()
    val clients by viewModel.clientsVip.collectAsState()

    val clientRecord = clients.find { it.email.equals(currentUser?.email, ignoreCase = true) }
        ?: clients.firstOrNull()

    var isEditing by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(clientRecord?.name ?: currentUser?.displayName ?: "Maison VIP") }
    var editedPhone by remember { mutableStateOf(clientRecord?.phone ?: "(11) 99841-2200") }
    var editedEmail by remember { mutableStateOf(clientRecord?.email ?: currentUser?.email ?: "vip@maison.com") }

    // Dialogo de avaliação de serviço
    var reviewingAppointment by remember { mutableStateOf<Appointment?>(null) }
    var ratingStars by remember { mutableStateOf(5f) }
    var reviewCommentText by remember { mutableStateOf("") }

    val userAppointments = appointments.filter {
        it.clientName.equals(editedName, ignoreCase = true) || it.userId == currentUser?.uid || true
    }

    val upcomingAppointments = userAppointments.filter {
        it.status == AppointmentStatus.CONFIRMADO || it.status == AppointmentStatus.EM_ATENDIMENTO
    }

    val pastAppointments = userAppointments.filter {
        it.status == AppointmentStatus.CONCLUIDO || it.status == AppointmentStatus.CANCELADO
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBlackSurface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Cabeçalho do Perfil & Cartão de Fidelidade Maison VIP
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.4f)), RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DeepBlackCard)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(GoldLinearGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = editedName.take(2).uppercase(),
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = DeepBlackSurface,
                                fontWeight = FontWeight.Bold,
                                fontFamily = LuxurySerif
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = editedName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = SophisticatedWhite,
                                fontWeight = FontWeight.Bold,
                                fontFamily = LuxurySerif
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        LuxuryBadge(text = clientRecord?.tier ?: "Maison Black", isGold = true)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = editedEmail,
                        style = MaterialTheme.typography.bodySmall.copy(color = TextDim)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isEditing) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = editedName,
                                onValueChange = { editedName = it },
                                label = { Text("Nome Completo") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldChampagne,
                                    unfocusedBorderColor = DeepBlackCardElevated,
                                    focusedLabelColor = GoldChampagne
                                )
                            )
                            OutlinedTextField(
                                value = editedPhone,
                                onValueChange = { editedPhone = it },
                                label = { Text("Telefone / WhatsApp") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldChampagne,
                                    unfocusedBorderColor = DeepBlackCardElevated,
                                    focusedLabelColor = GoldChampagne
                                )
                            )
                            OutlinedTextField(
                                value = editedEmail,
                                onValueChange = { editedEmail = it },
                                label = { Text("E-mail") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldChampagne,
                                    unfocusedBorderColor = DeepBlackCardElevated,
                                    focusedLabelColor = GoldChampagne
                                )
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                LuxuryOutlinedButton(
                                    text = "Cancelar",
                                    onClick = { isEditing = false },
                                    modifier = Modifier.weight(1f)
                                )
                                LuxuryGoldButton(
                                    text = "Salvar na Nuvem",
                                    onClick = {
                                        viewModel.updateClientProfile(editedName, editedPhone, editedEmail)
                                        isEditing = false
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("save_profile_button")
                                )
                            }
                        }
                    } else {
                        LuxuryOutlinedButton(
                            text = "Editar Dados Cadastrais",
                            onClick = { isEditing = true },
                            icon = Icons.Default.Edit,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Seção: Próximos Agendamentos
        item {
            Text(
                text = "PRÓXIMOS ATENDIMENTOS VIP",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = GoldChampagne,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (upcomingAppointments.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = DeepBlackCard
                ) {
                    Text(
                        text = "Nenhum agendamento futuro no momento.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted)
                    )
                }
            }
        } else {
            items(upcomingAppointments) { apt ->
                AppointmentCardItem(appointment = apt, onReviewClick = null)
            }
        }

        // Seção: Histórico Realizado & Avaliação
        item {
            Text(
                text = "HISTÓRICO E AVALIAÇÕES",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = GoldChampagne,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        if (pastAppointments.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = DeepBlackCard
                ) {
                    Text(
                        text = "Nenhum histórico anterior registrado.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted)
                    )
                }
            }
        } else {
            items(pastAppointments) { apt ->
                AppointmentCardItem(
                    appointment = apt,
                    onReviewClick = {
                        reviewingAppointment = apt
                        ratingStars = if (apt.rating > 0f) apt.rating else 5f
                        reviewCommentText = apt.reviewComment
                    }
                )
            }
        }
    }

    // Diálogo de Avaliação de Serviço (Rating & Review)
    if (reviewingAppointment != null) {
        val apt = reviewingAppointment!!
        androidx.compose.ui.window.Dialog(onDismissRequest = { reviewingAppointment = null }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                color = DeepBlackCard,
                border = BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Avaliar Experiência VIP",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = SophisticatedWhite,
                                fontFamily = LuxurySerif,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        IconButton(onClick = { reviewingAppointment = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Fechar", tint = SophisticatedWhite)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "${apt.serviceName}\ncom ${apt.stylistName}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = GoldChampagne),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Estrelas Interativas
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (i in 1..5) {
                            IconButton(onClick = { ratingStars = i.toFloat() }) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "$i Estrelas",
                                    tint = if (i <= ratingStars) GoldChampagne else TextDim,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = reviewCommentText,
                        onValueChange = { reviewCommentText = it },
                        label = { Text("Comentário ou elogio ao profissional") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldChampagne,
                            unfocusedBorderColor = DeepBlackCardElevated
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    LuxuryGoldButton(
                        text = "Enviar Avaliação no Firestore",
                        onClick = {
                            viewModel.submitReview(apt.id, ratingStars, reviewCommentText)
                            reviewingAppointment = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("submit_review_button")
                    )
                }
            }
        }
    }
}

@Composable
fun AppointmentCardItem(
    appointment: Appointment,
    onReviewClick: (() -> Unit)?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(1.dp, DeepBlackCardElevated), RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DeepBlackCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = appointment.serviceName,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = SophisticatedWhite,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.weight(1f)
                )
                LuxuryBadge(
                    text = appointment.status.label,
                    isGold = appointment.status == AppointmentStatus.CONFIRMADO
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Profissional: ${appointment.stylistName}",
                style = MaterialTheme.typography.bodySmall.copy(color = TextDim)
            )
            Text(
                text = "Data: ${appointment.date} às ${appointment.time}",
                style = MaterialTheme.typography.bodySmall.copy(color = TextDim)
            )

            if (appointment.rating > 0f) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = GoldChampagne, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${appointment.rating} / 5.0",
                        style = MaterialTheme.typography.labelSmall.copy(color = GoldChampagne, fontWeight = FontWeight.Bold)
                    )
                    if (appointment.reviewComment.isNotBlank()) {
                        Text(
                            text = " • \"${appointment.reviewComment}\"",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted),
                            maxLines = 1
                        )
                    }
                }
            }

            if (appointment.status == AppointmentStatus.CONCLUIDO && onReviewClick != null) {
                Spacer(modifier = Modifier.height(12.dp))
                LuxuryOutlinedButton(
                    text = if (appointment.rating > 0f) "Editar Avaliação" else "Avaliar Atendimento VIP",
                    onClick = onReviewClick,
                    icon = Icons.Default.Star,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
