package com.example.ui.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.SalonService
import com.example.model.Stylist
import com.example.ui.components.LuxuryGoldButton
import com.example.ui.theme.DeepBlackBackground
import com.example.ui.theme.DeepBlackBorder
import com.example.ui.theme.DeepBlackCard
import com.example.ui.theme.DeepBlackCardElevated
import com.example.ui.theme.GoldChampagne
import com.example.ui.theme.LuxurySerif
import com.example.ui.theme.OffWhite
import com.example.ui.theme.SophisticatedWhite
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextMuted

@Composable
fun BookingDialog(
    services: List<SalonService>,
    stylists: List<Stylist>,
    preselectedService: SalonService?,
    preselectedStylist: Stylist?,
    onDismiss: () -> Unit,
    onConfirm: (
        clientName: String,
        clientPhone: String,
        isVip: Boolean,
        service: SalonService,
        stylist: Stylist,
        time: String,
        notes: String
    ) -> Unit
) {
    var selectedService by remember {
        mutableStateOf(preselectedService ?: services.firstOrNull())
    }
    var selectedStylist by remember {
        mutableStateOf(preselectedStylist ?: stylists.firstOrNull())
    }
    var clientName by remember { mutableStateOf("") }
    var clientPhone by remember { mutableStateOf("") }
    var isVipClient by remember { mutableStateOf(true) }
    var selectedTime by remember { mutableStateOf("14:00") }
    var notes by remember { mutableStateOf("") }

    val availableTimes = listOf("09:30", "11:00", "13:30", "14:00", "15:30", "17:00", "18:30")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("booking_dialog_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DeepBlackCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "AGENDAMENTO VIP",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = LuxurySerif,
                                color = GoldChampagne,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text = "Maison de Beauté • Experiência Exclusiva",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(DeepBlackCardElevated)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar",
                            tint = SophisticatedWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Service Selector
                Text(
                    text = "1. SELECIONE O SERVIÇO",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = GoldChampagne,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(services) { service ->
                        val isSelected = selectedService?.id == service.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color(0x33D4AF37) else DeepBlackCardElevated)
                                .border(
                                    1.dp,
                                    if (isSelected) GoldChampagne else DeepBlackBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedService = service }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Column {
                                Text(
                                    text = service.name,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = if (isSelected) GoldChampagne else SophisticatedWhite,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Text(
                                    text = "R$ ${"%.2f".format(service.price)} • ${service.durationMinutes} min",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isSelected) OffWhite else TextDim
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stylist Selector
                Text(
                    text = "2. ESPECIALISTA DE ALTA COSTURA",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = GoldChampagne,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(stylists) { stylist ->
                        val isSelected = selectedStylist?.id == stylist.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color(0x33D4AF37) else DeepBlackCardElevated)
                                .border(
                                    1.dp,
                                    if (isSelected) GoldChampagne else DeepBlackBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedStylist = stylist }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) GoldChampagne else Color(0xFF333340)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stylist.avatarInitials,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isSelected) DeepBlackBackground else SophisticatedWhite,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = stylist.name,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = if (isSelected) GoldChampagne else SophisticatedWhite,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                    Text(
                                        text = stylist.role,
                                        style = MaterialTheme.typography.bodySmall.copy(color = TextDim),
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Time selector
                Text(
                    text = "3. HORÁRIO DISPONÍVEL (HOJE)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = GoldChampagne,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availableTimes) { timeSlot ->
                        val isSelected = selectedTime == timeSlot
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) GoldChampagne else DeepBlackCardElevated)
                                .clickable { selectedTime = timeSlot }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = timeSlot,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = if (isSelected) DeepBlackBackground else SophisticatedWhite,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Client Inputs
                Text(
                    text = "4. DADOS DO CLIENTE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = GoldChampagne,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = clientName,
                    onValueChange = { clientName = it },
                    label = { Text("Nome Completo") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = GoldChampagne)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_client_name"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldChampagne,
                        unfocusedBorderColor = DeepBlackBorder,
                        focusedTextColor = SophisticatedWhite,
                        unfocusedTextColor = SophisticatedWhite,
                        cursorColor = GoldChampagne
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = clientPhone,
                    onValueChange = { clientPhone = it },
                    label = { Text("Telefone / WhatsApp") },
                    placeholder = { Text("(11) 98765-4321") },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = GoldChampagne)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_client_phone"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldChampagne,
                        unfocusedBorderColor = DeepBlackBorder,
                        focusedTextColor = SophisticatedWhite,
                        unfocusedTextColor = SophisticatedWhite,
                        cursorColor = GoldChampagne
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DeepBlackCardElevated)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Tratamento VIP Maison",
                            style = MaterialTheme.typography.labelMedium.copy(color = SophisticatedWhite)
                        )
                        Text(
                            text = "Acesso ao lounge exclusivo e champanhe",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextDim)
                        )
                    }
                    Switch(
                        checked = isVipClient,
                        onCheckedChange = { isVipClient = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = DeepBlackBackground,
                            checkedTrackColor = GoldChampagne
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Price and confirm
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "VALOR TOTAL",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextDim, fontSize = 10.sp)
                        )
                        Text(
                            text = "R$ ${"%.2f".format(selectedService?.price ?: 0.0)}",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontFamily = LuxurySerif,
                                color = GoldChampagne,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    LuxuryGoldButton(
                        text = "Confirmar",
                        testTag = "confirm_booking_button",
                        onClick = {
                            val service = selectedService ?: services.first()
                            val stylist = selectedStylist ?: stylists.first()
                            val name = if (clientName.isBlank()) "Cliente VIP" else clientName
                            val phone = if (clientPhone.isBlank()) "(11) 98000-0000" else clientPhone
                            onConfirm(name, phone, isVipClient, service, stylist, selectedTime, notes)
                        }
                    )
                }
            }
        }
    }
}
