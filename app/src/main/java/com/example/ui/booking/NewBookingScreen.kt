package com.example.ui.booking

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.Appointment
import com.example.model.SalonService
import com.example.model.ServiceCategory
import com.example.model.Stylist
import com.example.ui.components.LuxuryBadge
import com.example.ui.components.LuxuryCard
import com.example.ui.components.LuxuryGoldButton
import com.example.ui.components.LuxuryOutlinedButton
import com.example.ui.theme.AlertRuby
import com.example.ui.theme.DeepBlackBackground
import com.example.ui.theme.DeepBlackBorder
import com.example.ui.theme.DeepBlackCard
import com.example.ui.theme.DeepBlackCardElevated
import com.example.ui.theme.DeepBlackSurface
import com.example.ui.theme.GoldChampagne
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldLinearGradient
import com.example.ui.theme.LuxurySerif
import com.example.ui.theme.OffWhite
import com.example.ui.theme.SophisticatedWhite
import com.example.ui.theme.SuccessEmerald
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextMuted
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.SalonViewModel
import java.util.Locale

/**
 * Data class representing a selectable booking date pill.
 */
data class BookingDateOption(
    val id: String,
    val dayOfWeek: String,
    val dayNumber: String,
    val month: String,
    val isToday: Boolean = false
)

/**
 * Tela completa de formulário para novos agendamentos VIP na Maison de Beauté.
 * Permite ao cliente selecionar Serviço de Alta Costura, Especialista/Visagista,
 * Data e Horário preferido, com validação detalhada de dados em tempo real e na submissão.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewBookingScreen(
    viewModel: SalonViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAppointments: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val services by viewModel.services.collectAsState()
    val stylists by viewModel.stylists.collectAsState()

    val focusManager = LocalFocusManager.current

    // State for selections
    var selectedCategory by remember { mutableStateOf(ServiceCategory.TODOS) }
    var selectedService by remember {
        mutableStateOf<SalonService?>(uiState.preselectedService ?: services.firstOrNull())
    }
    var selectedStylist by remember {
        mutableStateOf<Stylist?>(uiState.preselectedStylist ?: stylists.firstOrNull())
    }

    // Available booking dates
    val dateOptions = remember {
        listOf(
            BookingDateOption("d_today", "HOJE", "18", "OUT", isToday = true),
            BookingDateOption("d_tomorrow", "DOM", "19", "OUT"),
            BookingDateOption("d_mon", "SEG", "20", "OUT"),
            BookingDateOption("d_tue", "TER", "21", "OUT"),
            BookingDateOption("d_wed", "QUA", "22", "OUT"),
            BookingDateOption("d_thu", "QUI", "23", "OUT"),
            BookingDateOption("d_fri", "SEX", "24", "OUT"),
            BookingDateOption("d_sat", "SÁB", "25", "OUT")
        )
    }
    var selectedDateOption by remember { mutableStateOf(dateOptions.first()) }

    // Time slots by period
    val morningTimes = listOf("09:00", "09:45", "10:30", "11:15")
    val afternoonTimes = listOf("13:30", "14:15", "15:00", "16:00", "17:15")
    val eveningTimes = listOf("18:00", "18:45", "19:30", "20:15")

    var selectedTime by remember { mutableStateOf("14:15") }

    // Client form state
    var clientName by remember { mutableStateOf("") }
    var clientPhone by remember { mutableStateOf("") }
    var isVipClient by remember { mutableStateOf(true) }
    var selectedBeverage by remember { mutableStateOf("Champagne Moët & Chandon") }
    var notes by remember { mutableStateOf("") }

    // Validation state
    var hasAttemptedSubmit by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var serviceError by remember { mutableStateOf<String?>(null) }
    var stylistError by remember { mutableStateOf<String?>(null) }
    var timeError by remember { mutableStateOf<String?>(null) }

    // Success dialog state
    var confirmedAppointment by remember { mutableStateOf<Appointment?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Update selections if viewModel preselected items change
    LaunchedEffect(uiState.preselectedService) {
        if (uiState.preselectedService != null) {
            selectedService = uiState.preselectedService
        }
    }
    LaunchedEffect(uiState.preselectedStylist) {
        if (uiState.preselectedStylist != null) {
            selectedStylist = uiState.preselectedStylist
        }
    }

    // Real-time validation helper
    fun validate(): Boolean {
        var isValid = true

        if (selectedService == null) {
            serviceError = "Selecione um serviço para continuar."
            isValid = false
        } else {
            serviceError = null
        }

        if (selectedStylist == null) {
            stylistError = "Selecione o profissional de sua preferência."
            isValid = false
        } else {
            stylistError = null
        }

        if (selectedTime.isBlank()) {
            timeError = "Selecione o horário desejado para o atendimento."
            isValid = false
        } else {
            timeError = null
        }

        val trimmedName = clientName.trim()
        if (trimmedName.length < 3) {
            nameError = "Por favor, informe o nome completo (mínimo 3 letras)."
            isValid = false
        } else {
            nameError = null
        }

        val digitsOnly = clientPhone.filter { it.isDigit() }
        if (digitsOnly.length < 10) {
            phoneError = "Telefone inválido. Informe o DDD e pelo menos 10 dígitos (ex: 11 98888-7777)."
            isValid = false
        } else {
            phoneError = null
        }

        return isValid
    }

    // Filter services according to selected category
    val filteredServices = services.filter {
        selectedCategory == ServiceCategory.TODOS || it.category == selectedCategory
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("new_booking_screen"),
        containerColor = DeepBlackBackground,
        contentColor = SophisticatedWhite,
        topBar = {
            BookingTopBar(
                onBack = onNavigateBack,
                onReset = {
                    clientName = ""
                    clientPhone = ""
                    notes = ""
                    hasAttemptedSubmit = false
                    nameError = null
                    phoneError = null
                    serviceError = null
                    stylistError = null
                    timeError = null
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("booking_form_scrollable"),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. BANNER DE APRESENTAÇÃO VIP
            item {
                BookingHeaderBanner()
            }

            // 2. RESUMO DE ETAPAS INTERATIVAS
            item {
                BookingStepsIndicator(
                    hasService = selectedService != null,
                    hasStylist = selectedStylist != null,
                    hasTime = selectedTime.isNotBlank(),
                    hasClient = clientName.trim().length >= 3 && clientPhone.filter { it.isDigit() }.length >= 10
                )
            }

            // 3. SEÇÃO: ESCOLHA DO SERVIÇO
            item {
                SectionHeader(
                    title = "1. SERVIÇO DE ALTA COSTURA",
                    subtitle = "Escolha o procedimento ou tratamento de beleza",
                    icon = Icons.Default.ContentCut
                )

                // Filtro por categorias
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ServiceCategory.values()) { category ->
                        val isSelected = selectedCategory == category
                        LuxuryFilterChip(
                            label = category.label,
                            isSelected = isSelected,
                            onClick = { selectedCategory = category }
                        )
                    }
                }

                // Erro de validação de serviço
                AnimatedVisibility(visible = serviceError != null) {
                    ErrorNotice(message = serviceError ?: "")
                }

                // Grid / Lista de Serviços
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    filteredServices.forEach { service ->
                        val isSelected = selectedService?.id == service.id
                        ServiceSelectionCard(
                            service = service,
                            isSelected = isSelected,
                            onSelect = {
                                selectedService = service
                                serviceError = null
                            }
                        )
                    }
                }
            }

            // 4. SEÇÃO: ESCOLHA DO ESPECIALISTA / VISAGISTA
            item {
                SectionHeader(
                    title = "2. PROFISSIONAL DE PREFERÊNCIA",
                    subtitle = "Selecione o mestre de sua confiança ou o primeiro disponível",
                    icon = Icons.Default.Person
                )

                AnimatedVisibility(visible = stylistError != null) {
                    ErrorNotice(message = stylistError ?: "")
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Lista de Especialistas
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(stylists) { stylist ->
                        val isSelected = selectedStylist?.id == stylist.id
                        StylistSelectionCard(
                            stylist = stylist,
                            isSelected = isSelected,
                            onSelect = {
                                selectedStylist = stylist
                                stylistError = null
                            }
                        )
                    }
                }
            }

            // 5. SEÇÃO: DATA E HORÁRIO PREFERIDO
            item {
                SectionHeader(
                    title = "3. DATA E HORÁRIO PREFERIDO",
                    subtitle = "Selecione o melhor momento para sua sessão exclusiva",
                    icon = Icons.Default.DateRange
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "SELECIONE O DIA",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = GoldChampagne,
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Linha de datas
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(dateOptions) { option ->
                        val isSelected = selectedDateOption.id == option.id
                        DateOptionCard(
                            dateOption = option,
                            isSelected = isSelected,
                            onSelect = { selectedDateOption = option }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "HORÁRIOS DISPONÍVEIS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = GoldChampagne,
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                AnimatedVisibility(visible = timeError != null) {
                    ErrorNotice(message = timeError ?: "")
                }

                // Turno Manhã
                TimeSlotGroup(
                    periodTitle = "Manhã",
                    timeSlots = morningTimes,
                    selectedTime = selectedTime,
                    onSelect = {
                        selectedTime = it
                        timeError = null
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Turno Tarde
                TimeSlotGroup(
                    periodTitle = "Tarde",
                    timeSlots = afternoonTimes,
                    selectedTime = selectedTime,
                    onSelect = {
                        selectedTime = it
                        timeError = null
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Turno Noite
                TimeSlotGroup(
                    periodTitle = "Noite",
                    timeSlots = eveningTimes,
                    selectedTime = selectedTime,
                    onSelect = {
                        selectedTime = it
                        timeError = null
                    }
                )
            }

            // 6. SEÇÃO: DADOS DO CLIENTE & VALIDAÇÃO
            item {
                SectionHeader(
                    title = "4. DADOS DO CLIENTE & EXPERIÊNCIA",
                    subtitle = "Identificação para recepção VIP e confirmação via WhatsApp",
                    icon = Icons.Default.Verified
                )

                Spacer(modifier = Modifier.height(12.dp))

                LuxuryCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Campo: Nome Completo
                        Column {
                            OutlinedTextField(
                                value = clientName,
                                onValueChange = {
                                    clientName = it
                                    if (hasAttemptedSubmit) {
                                        nameError = if (it.trim().length < 3) {
                                            "Por favor, informe o nome completo (mínimo 3 caracteres)."
                                        } else null
                                    }
                                },
                                label = { Text("Nome Completo do Cliente") },
                                placeholder = { Text("Ex: Dra. Helena Albuquerque", color = TextMuted) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = if (nameError != null) AlertRuby else GoldChampagne
                                    )
                                },
                                isError = nameError != null,
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                ),
                                colors = luxuryTextFieldColors(),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("booking_input_name")
                            )

                            if (nameError != null) {
                                Text(
                                    text = nameError!!,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = AlertRuby,
                                        fontSize = 12.sp
                                    ),
                                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                )
                            }
                        }

                        // Campo: Telefone / WhatsApp
                        Column {
                            OutlinedTextField(
                                value = clientPhone,
                                onValueChange = {
                                    // Filtra e permite formatação flexível
                                    clientPhone = it
                                    if (hasAttemptedSubmit) {
                                        val digits = it.filter { ch -> ch.isDigit() }
                                        phoneError = if (digits.length < 10) {
                                            "Informe o DDD e telefone válido (ex: 11 98888-7777)."
                                        } else null
                                    }
                                },
                                label = { Text("Telefone / WhatsApp com DDD") },
                                placeholder = { Text("(11) 99888-7777", color = TextMuted) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = null,
                                        tint = if (phoneError != null) AlertRuby else GoldChampagne
                                    )
                                },
                                isError = phoneError != null,
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Phone,
                                    imeAction = ImeAction.Next
                                ),
                                colors = luxuryTextFieldColors(),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("booking_input_phone")
                            )

                            if (phoneError != null) {
                                Text(
                                    text = phoneError!!,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = AlertRuby,
                                        fontSize = 12.sp
                                    ),
                                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                )
                            }
                        }

                        // Switch: Cliente VIP Maison
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DeepBlackCardElevated)
                                .border(1.dp, Color(0x33D4AF37), RoundedCornerShape(12.dp))
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Diamond,
                                        contentDescription = null,
                                        tint = GoldChampagne,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Protocolo VIP Maison",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = SophisticatedWhite,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                                Text(
                                    text = "Recepção com toalha aromática aquecida e sala privativa",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextDim, fontSize = 11.sp)
                                )
                            }

                            Switch(
                                checked = isVipClient,
                                onCheckedChange = { isVipClient = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = DeepBlackBackground,
                                    checkedTrackColor = GoldChampagne,
                                    uncheckedThumbColor = TextMuted,
                                    uncheckedTrackColor = DeepBlackCard
                                )
                            )
                        }

                        // Bebida de Boas-Vindas de Cortesia
                        Column {
                            Text(
                                text = "BEBIDA DE CORTESIA NA CHEGADA",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = GoldChampagne,
                                    letterSpacing = 1.1.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val beverages = listOf(
                                    "Champagne Moët & Chandon" to Icons.Default.LocalBar,
                                    "Café Nespresso Supremo" to Icons.Default.LocalCafe,
                                    "Água Francesa com Gás" to Icons.Default.WaterDrop,
                                    "Chá Frutas Vermelhas" to Icons.Default.LocalCafe
                                )
                                beverages.forEach { (bevName, icon) ->
                                    val isSelected = selectedBeverage == bevName
                                    BeverageChip(
                                        name = bevName,
                                        icon = icon,
                                        isSelected = isSelected,
                                        onClick = { selectedBeverage = bevName }
                                    )
                                }
                            }
                        }

                        // Observações e Recomendações
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Observações ou Solicitações Especiais") },
                            placeholder = {
                                Text(
                                    "Ex: Cabelos com mechas recentes; preferência por temperatura amena.",
                                    color = TextMuted,
                                    fontSize = 13.sp
                                )
                            },
                            minLines = 2,
                            maxLines = 4,
                            colors = luxuryTextFieldColors(),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("booking_input_notes")
                        )
                    }
                }
            }

            // 7. CARD EXECUTIVO DE RESUMO DA RESERVA
            item {
                BookingLiveSummaryCard(
                    service = selectedService,
                    stylist = selectedStylist,
                    dateOption = selectedDateOption,
                    time = selectedTime,
                    beverage = selectedBeverage,
                    isVip = isVipClient
                )
            }

            // 8. PAINEL DE ERROS GERAIS (SE HOUVER FALHA NA VALIDAÇÃO)
            item {
                AnimatedVisibility(
                    visible = hasAttemptedSubmit && (nameError != null || phoneError != null || serviceError != null || stylistError != null || timeError != null)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(AlertRuby.copy(alpha = 0.12f))
                            .border(1.dp, AlertRuby.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = AlertRuby,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Atenção: Preencha os campos obrigatórios",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = AlertRuby,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                if (serviceError != null) Text("• $serviceError", color = SophisticatedWhite, fontSize = 12.sp)
                                if (stylistError != null) Text("• $stylistError", color = SophisticatedWhite, fontSize = 12.sp)
                                if (timeError != null) Text("• $timeError", color = SophisticatedWhite, fontSize = 12.sp)
                                if (nameError != null) Text("• $nameError", color = SophisticatedWhite, fontSize = 12.sp)
                                if (phoneError != null) Text("• $phoneError", color = SophisticatedWhite, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // 9. BOTÕES DE AÇÃO: CONFIRMAR AGENDAMENTO VIP & VOLTAR
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LuxuryGoldButton(
                        text = "CONFIRMAR AGENDAMENTO VIP",
                        icon = Icons.Default.CheckCircle,
                        onClick = {
                            hasAttemptedSubmit = true
                            focusManager.clearFocus()
                            val isValid = validate()
                            if (isValid && selectedService != null && selectedStylist != null) {
                                val completeNotes = if (notes.isNotBlank()) {
                                    "$notes | Cortesia: $selectedBeverage"
                                } else {
                                    "Cortesia solicitada: $selectedBeverage"
                                }

                                val fullDate = if (selectedDateOption.isToday) {
                                    "Hoje (${selectedDateOption.dayNumber}/${selectedDateOption.month})"
                                } else {
                                    "${selectedDateOption.dayOfWeek} (${selectedDateOption.dayNumber}/${selectedDateOption.month})"
                                }

                                val apt = viewModel.confirmBooking(
                                    clientName = clientName.trim(),
                                    clientPhone = clientPhone.trim(),
                                    isVip = isVipClient,
                                    service = selectedService!!,
                                    stylist = selectedStylist!!,
                                    time = selectedTime,
                                    notes = completeNotes,
                                    date = fullDate
                                )
                                confirmedAppointment = apt
                                showSuccessDialog = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        testTag = "btn_submit_vip_booking"
                    )

                    LuxuryOutlinedButton(
                        text = "VOLTAR AO INÍCIO",
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        testTag = "btn_cancel_booking"
                    )
                }
            }
        }
    }

    // Modal de Sucesso com Protocolo VIP e Ações
    if (showSuccessDialog && confirmedAppointment != null) {
        BookingSuccessConfirmationModal(
            appointment = confirmedAppointment!!,
            onDismiss = {
                showSuccessDialog = false
                onNavigateBack()
            },
            onGoToAppointments = {
                showSuccessDialog = false
                onNavigateToAppointments()
            }
        )
    }
}

/**
 * TopBar estilizada com identidade visual de luxo.
 */
@Composable
private fun BookingTopBar(
    onBack: () -> Unit,
    onReset: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DeepBlackBackground)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(DeepBlackCard)
                    .border(1.dp, Color(0x33D4AF37), CircleShape)
                    .testTag("booking_top_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = GoldChampagne,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "NOVO AGENDAMENTO",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = LuxurySerif,
                        fontWeight = FontWeight.Bold,
                        color = SophisticatedWhite,
                        letterSpacing = 1.2.sp
                    )
                )
                Text(
                    text = "Maison de Beauté • Sessão Exclusiva",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = GoldChampagne,
                        fontSize = 11.sp
                    )
                )
            }
        }

        IconButton(
            onClick = onReset,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(DeepBlackCard)
                .testTag("booking_top_reset_btn")
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Limpar formulário",
                tint = TextDim,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * Banner superior com gradiente e slogan VIP.
 */
@Composable
private fun BookingHeaderBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        DeepBlackCardElevated,
                        Color(0xFF1E1A11),
                        DeepBlackCard
                    )
                )
            )
            .border(1.dp, GoldChampagne.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
            .padding(20.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LuxuryBadge(text = "CONCIERGE VIP")
                Text(
                    text = "RESERVA ONLINE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = GoldChampagne,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Personalize Sua Experiência",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = LuxurySerif,
                    fontWeight = FontWeight.Bold,
                    color = SophisticatedWhite
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Escolha seu procedimento de alta costura, seu especialista de preferência e o horário que melhor se adapta à sua rotina.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextDim,
                    lineHeight = 20.sp
                )
            )
        }
    }
}

/**
 * Indicador visual dos 4 passos de agendamento.
 */
@Composable
private fun BookingStepsIndicator(
    hasService: Boolean,
    hasStylist: Boolean,
    hasTime: Boolean,
    hasClient: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DeepBlackCard)
            .border(1.dp, Color(0x26D4AF37), RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StepDotItem(number = "1", label = "Serviço", isDone = hasService)
        StepDivider()
        StepDotItem(number = "2", label = "Mestre", isDone = hasStylist)
        StepDivider()
        StepDotItem(number = "3", label = "Horário", isDone = hasTime)
        StepDivider()
        StepDotItem(number = "4", label = "Dados", isDone = hasClient)
    }
}

@Composable
private fun StepDotItem(
    number: String,
    label: String,
    isDone: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(if (isDone) GoldChampagne else DeepBlackCardElevated)
                .border(
                    1.dp,
                    if (isDone) GoldChampagne else Color(0x44D4AF37),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isDone) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = DeepBlackBackground,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Text(
                    text = number,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = SophisticatedWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = if (isDone) GoldChampagne else TextDim,
                fontSize = 10.sp,
                fontWeight = if (isDone) FontWeight.Bold else FontWeight.Normal
            )
        )
    }
}

@Composable
private fun StepDivider() {
    Box(
        modifier = Modifier
            .width(20.dp)
            .height(1.dp)
            .background(Color(0x33D4AF37))
    )
}

/**
 * Cabeçalho de cada seção com ícone e descrição.
 */
@Composable
private fun SectionHeader(
    title: String,
    subtitle: String,
    icon: ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(DeepBlackCardElevated)
                .border(1.dp, GoldChampagne.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GoldChampagne,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = LuxurySerif,
                    fontWeight = FontWeight.Bold,
                    color = SophisticatedWhite,
                    letterSpacing = 1.sp
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextDim,
                    fontSize = 11.sp
                )
            )
        }
    }
}

/**
 * Card de Serviço com seleção interativa, borda dourada quando selecionado e preço em destaque.
 */
@Composable
private fun ServiceSelectionCard(
    service: SalonService,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    val animatedElevation by animateDpAsState(
        targetValue = if (isSelected || isPressed || isHovered) 10.dp else 2.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "service_elev"
    )

    val animatedBorderColor by animateColorAsState(
        targetValue = if (isSelected) GoldChampagne else if (isHovered) Color(0x66D4AF37) else DeepBlackBorder,
        animationSpec = tween(200),
        label = "service_border"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("booking_service_card_${service.id}")
            .shadow(
                elevation = animatedElevation,
                shape = RoundedCornerShape(16.dp),
                ambientColor = if (isSelected) GoldChampagne else Color.Transparent,
                spotColor = if (isSelected) GoldChampagne else Color.Transparent
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onSelect),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) DeepBlackCardElevated else DeepBlackCard
        ),
        border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, animatedBorderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = service.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = if (isSelected) GoldChampagne else SophisticatedWhite,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    if (service.badge != null) {
                        LuxuryBadge(text = service.badge)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = service.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextDim,
                        lineHeight = 16.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = GoldChampagne,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${service.durationMinutes} min",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextDim, fontSize = 11.sp)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = GoldChampagne,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format(Locale.US, "%.1f", service.rating),
                            style = MaterialTheme.typography.bodySmall.copy(color = SophisticatedWhite, fontSize = 11.sp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = String.format(Locale.GERMANY, "R$ %.2f", service.price),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = LuxurySerif,
                        fontWeight = FontWeight.Bold,
                        color = GoldChampagne
                    )
                )

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) GoldChampagne else Color.Transparent)
                        .border(
                            1.dp,
                            if (isSelected) GoldChampagne else Color(0x66D4AF37),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = DeepBlackBackground,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Card de Seleção do Stylist / Especialista com avatar em círculo e especialidades.
 */
@Composable
private fun StylistSelectionCard(
    stylist: Stylist,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    val animatedElevation by animateDpAsState(
        targetValue = if (isSelected || isPressed || isHovered) 10.dp else 2.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "stylist_elev"
    )

    Card(
        modifier = Modifier
            .width(220.dp)
            .testTag("booking_stylist_card_${stylist.id}")
            .shadow(
                elevation = animatedElevation,
                shape = RoundedCornerShape(16.dp),
                ambientColor = if (isSelected) GoldChampagne else Color.Transparent,
                spotColor = if (isSelected) GoldChampagne else Color.Transparent
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onSelect),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) DeepBlackCardElevated else DeepBlackCard
        ),
        border = BorderStroke(
            if (isSelected) 1.5.dp else 1.dp,
            if (isSelected) GoldChampagne else DeepBlackBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(GoldChampagne, Color(0xFF8C6D23))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stylist.avatarInitials,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = DeepBlackBackground,
                            fontWeight = FontWeight.Bold,
                            fontFamily = LuxurySerif
                        )
                    )
                }

                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(GoldChampagne)
                            .border(1.dp, DeepBlackBackground, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = DeepBlackBackground,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stylist.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) GoldChampagne else SophisticatedWhite,
                    textAlign = TextAlign.Center
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = stylist.role,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextDim,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = GoldChampagne,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${stylist.rating} (${stylist.reviewsCount})",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SophisticatedWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

/**
 * Pílula de Data selecionável com dia da semana, número e mês.
 */
@Composable
private fun DateOptionCard(
    dateOption: BookingDateOption,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(76.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onSelect)
            .testTag("date_pill_${dateOption.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) GoldChampagne else DeepBlackCard
        ),
        border = BorderStroke(
            1.dp,
            if (isSelected) GoldChampagne else Color(0x33D4AF37)
        )
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = dateOption.dayOfWeek,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isSelected) DeepBlackBackground else TextDim,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = dateOption.dayNumber,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = LuxurySerif,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) DeepBlackBackground else SophisticatedWhite
                )
            )

            Text(
                text = dateOption.month,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (isSelected) DeepBlackBackground else GoldChampagne,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

/**
 * Grupo de horários por período (Manhã, Tarde, Noite).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TimeSlotGroup(
    periodTitle: String,
    timeSlots: List<String>,
    selectedTime: String,
    onSelect: (String) -> Unit
) {
    Column {
        Text(
            text = periodTitle,
            style = MaterialTheme.typography.bodySmall.copy(
                color = TextDim,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(bottom = 6.dp)
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            timeSlots.forEach { timeSlot ->
                val isSelected = selectedTime == timeSlot
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) GoldLinearGradient else Brush.linearGradient(listOf(DeepBlackCard, DeepBlackCard)))
                        .border(
                            1.dp,
                            if (isSelected) GoldChampagne else Color(0x33D4AF37),
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { onSelect(timeSlot) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("time_slot_$timeSlot"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = if (isSelected) DeepBlackBackground else GoldChampagne,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = timeSlot,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (isSelected) DeepBlackBackground else SophisticatedWhite,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Chip para seleção de bebida cortesia.
 */
@Composable
private fun BeverageChip(
    name: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) DeepBlackCardElevated else DeepBlackCard)
            .border(
                1.dp,
                if (isSelected) GoldChampagne else Color(0x26D4AF37),
                RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) GoldChampagne else TextDim,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (isSelected) SophisticatedWhite else TextDim,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 12.sp
                )
            )
        }
    }
}

/**
 * Chip de categoria com visual refinado.
 */
@Composable
private fun LuxuryFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) GoldLinearGradient else Brush.linearGradient(listOf(DeepBlackCard, DeepBlackCard)))
            .border(
                1.dp,
                if (isSelected) GoldChampagne else Color(0x33D4AF37),
                RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = if (isSelected) DeepBlackBackground else SophisticatedWhite,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        )
    }
}

/**
 * Card executivo com o resumo da reserva em tempo real.
 */
@Composable
private fun BookingLiveSummaryCard(
    service: SalonService?,
    stylist: Stylist?,
    dateOption: BookingDateOption,
    time: String,
    beverage: String,
    isVip: Boolean
) {
    LuxuryCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = GoldChampagne,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "RESUMO DA RESERVA",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontFamily = LuxurySerif,
                            fontWeight = FontWeight.Bold,
                            color = SophisticatedWhite,
                            letterSpacing = 1.2.sp
                        )
                    )
                }

                if (isVip) {
                    LuxuryBadge(text = "VIP EXPERIENCE")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            SummaryLineItem(
                label = "Serviço Selecionado",
                value = service?.name ?: "Nenhum serviço selecionado",
                highlight = service != null
            )
            SummaryLineItem(
                label = "Especialista / Visagista",
                value = stylist?.name ?: "Nenhum profissional selecionado",
                highlight = stylist != null
            )
            SummaryLineItem(
                label = "Data e Horário",
                value = "${dateOption.dayOfWeek} (${dateOption.dayNumber}/${dateOption.month}) às $time",
                highlight = true
            )
            SummaryLineItem(
                label = "Duração Prevista",
                value = "${service?.durationMinutes ?: 60} minutos de sessão",
                highlight = false
            )
            SummaryLineItem(
                label = "Cortesia no Lounge",
                value = beverage,
                highlight = false
            )

            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0x33D4AF37))
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "VALOR TOTAL PREVISTO",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextDim,
                        letterSpacing = 1.sp
                    )
                )

                Text(
                    text = String.format(Locale.GERMANY, "R$ %.2f", service?.price ?: 0.0),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = LuxurySerif,
                        fontWeight = FontWeight.Bold,
                        color = GoldChampagne
                    )
                )
            }
        }
    }
}

@Composable
private fun SummaryLineItem(
    label: String,
    value: String,
    highlight: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(color = TextDim, fontSize = 12.sp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                color = if (highlight) SophisticatedWhite else TextMuted,
                fontWeight = if (highlight) FontWeight.SemiBold else FontWeight.Normal,
                fontSize = 12.sp,
                textAlign = TextAlign.End
            ),
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

/**
 * Caixa de aviso de erro com ícone.
 */
@Composable
private fun ErrorNotice(message: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = AlertRuby,
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall.copy(
                color = AlertRuby,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

/**
 * Estilos padrão de cores para campos de texto de luxo.
 */
@Composable
private fun luxuryTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = SophisticatedWhite,
    unfocusedTextColor = SophisticatedWhite,
    focusedContainerColor = DeepBlackCard,
    unfocusedContainerColor = DeepBlackCard,
    cursorColor = GoldChampagne,
    focusedBorderColor = GoldChampagne,
    unfocusedBorderColor = Color(0x44D4AF37),
    focusedLabelColor = GoldChampagne,
    unfocusedLabelColor = TextDim,
    errorBorderColor = AlertRuby,
    errorLabelColor = AlertRuby
)

/**
 * Modal de Confirmação de Sucesso com Protocolo e Ações.
 */
@Composable
private fun BookingSuccessConfirmationModal(
    appointment: Appointment,
    onDismiss: () -> Unit,
    onGoToAppointments: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("booking_success_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DeepBlackCard),
            border = BorderStroke(1.5.dp, GoldChampagne)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(SuccessEmerald.copy(alpha = 0.15f))
                        .border(1.5.dp, SuccessEmerald, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Sucesso",
                        tint = SuccessEmerald,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "AGENDAMENTO CONFIRMADO",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = LuxurySerif,
                        fontWeight = FontWeight.Bold,
                        color = GoldChampagne,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Sua experiência foi registrada no sistema com sucesso.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = SophisticatedWhite,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Cartão com detalhes
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(DeepBlackCardElevated)
                        .border(1.dp, Color(0x33D4AF37), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        DetailRow(label = "Protocolo", value = "#${appointment.id}")
                        DetailRow(label = "Cliente", value = appointment.clientName)
                        DetailRow(label = "Serviço", value = appointment.serviceName)
                        DetailRow(label = "Especialista", value = appointment.stylistName)
                        DetailRow(label = "Data & Hora", value = "${appointment.date} às ${appointment.time}")
                        DetailRow(label = "Valor", value = String.format(Locale.GERMANY, "R$ %.2f", appointment.price))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                LuxuryGoldButton(
                    text = "VER NA AGENDA VIP",
                    icon = Icons.Default.CalendarMonth,
                    onClick = onGoToAppointments,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "btn_success_view_agenda"
                )

                Spacer(modifier = Modifier.height(10.dp))

                LuxuryOutlinedButton(
                    text = "CONCLUIR",
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "btn_success_dismiss"
                )
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall.copy(color = TextDim, fontSize = 12.sp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                color = SophisticatedWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        )
    }
}
