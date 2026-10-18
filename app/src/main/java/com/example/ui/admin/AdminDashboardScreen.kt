package com.example.ui.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Pix
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.model.ClientVip
import com.example.model.DailyRevenuePoint
import com.example.model.FinancialSummary
import com.example.model.Product
import com.example.model.Stylist
import com.example.ui.components.InteractiveRevenueChart
import com.example.ui.components.LuxuryBadge
import com.example.ui.components.LuxuryCard
import com.example.ui.components.LuxuryGoldButton
import com.example.ui.components.LuxuryOutlinedButton
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatMetricCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AlertRuby
import com.example.ui.theme.DeepBlackBackground
import com.example.ui.theme.DeepBlackBorder
import com.example.ui.theme.DeepBlackCard
import com.example.ui.theme.DeepBlackCardElevated
import com.example.ui.theme.DeepBlackDivider
import com.example.ui.theme.GoldChampagne
import com.example.ui.theme.GoldLinearGradient
import com.example.ui.theme.LuxurySerif
import com.example.ui.theme.OffWhite
import com.example.ui.theme.SophisticatedWhite
import com.example.ui.theme.SuccessEmerald
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextMuted
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.SalonViewModel

@Composable
fun AdminDashboardScreen(
    viewModel: SalonViewModel,
    onOpenDrawer: () -> Unit,
    onNavigateBack: () -> Unit = { viewModel.navigateTo(AppScreen.HOME) },
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    
    // Auth Guard
    if (currentUser == null) {
        Column(
            modifier = Modifier.fillMaxSize().background(DeepBlackBackground),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Acesso Negado: Necessário autenticação administrativa.", color = GoldChampagne)
        }
        return
    }

    val financial by viewModel.financialSummary.collectAsState()
    val appointments by viewModel.appointments.collectAsState()
    val clientsVip by viewModel.clientsVip.collectAsState()
    val stylists by viewModel.stylists.collectAsState()
    val products by viewModel.products.collectAsState()

    val showIntroAnimation by viewModel.showIntroAnimation.collectAsState()
    val appLogoUri by viewModel.appLogoUri.collectAsState()

    var activeTab by remember { mutableStateOf("Visão Geral") }
    val tabs = listOf("Visão Geral", "Agendamentos", "Financeiro", "Equipe", "Estoque", "Clientes VIP", "Configurações & Mídia")

    val photoPickerLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.addPortfolioItem(
                title = "Novo Trabalho Exclusivo",
                category = "Cabelos",
                mediaUri = uri.toString(),
                mediaType = "IMAGE",
                stylistName = "Marc Antoine",
                description = "Trabalho realizado no salão com produtos de alta performance."
            )
        }
    }

    val videoPickerLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.addPortfolioItem(
                title = "Vídeo Exclusivo de Tratamento",
                category = "Spa & Tratamento",
                mediaUri = uri.toString(),
                mediaType = "VIDEO",
                stylistName = "Sofia Vance",
                description = "Bastidores do tratamento de alta performance com infusão e acabamento."
            )
        }
    }

    val logoPickerLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.setAppLogoUri(uri.toString())
        }
    }

    val filteredAppointments = if (uiState.adminFilterStatus == null) {
        appointments
    } else {
        appointments.filter { it.status == uiState.adminFilterStatus }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DeepBlackBackground,
        contentColor = SophisticatedWhite,
        topBar = {
            AdminTopBar(
                onOpenDrawer = onOpenDrawer,
                onNavigateBack = onNavigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openBookingDialog() },
                modifier = Modifier
                    .padding(16.dp)
                    .testTag("admin_fab_new_appointment"),
                containerColor = GoldChampagne,
                contentColor = DeepBlackBackground,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Novo Agendamento",
                        tint = DeepBlackBackground
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "NOVO AGENDAMENTO",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepBlackBackground,
                            letterSpacing = 1.sp
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("admin_dashboard_lazy_column")
        ) {
            // 1. CARDS DE INDICADORES FINANCEIROS DE ALTO PADRÃO (FATURAMENTO, AGENDAMENTOS DO DIA, NOVOS CLIENTES)
            item {
                AdminFinancialIndicatorsSection(
                    financial = financial,
                    appointments = appointments,
                    clientsVip = clientsVip
                )
            }

            // Tab Selector
            item {
                ScrollableTabRow(
                    selectedTabIndex = tabs.indexOf(activeTab),
                    containerColor = DeepBlackBackground,
                    contentColor = GoldChampagne,
                    edgePadding = 16.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[tabs.indexOf(activeTab)]),
                            color = GoldChampagne,
                            height = 2.dp
                        )
                    },
                    divider = {}
                ) {
                    tabs.forEach { tabName ->
                        val isSelected = activeTab == tabName
                        Tab(
                            selected = isSelected,
                            onClick = { activeTab = tabName },
                            text = {
                                Text(
                                    text = tabName.uppercase(),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = if (isSelected) GoldChampagne else TextMuted,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        letterSpacing = 0.8.sp
                                    )
                                )
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Dynamic Content according to Active Tab
            when (activeTab) {
                "Visão Geral" -> {
                    item {
                        AdminMetricsGrid(financial = financial)
                    }
                    item {
                        RevenueWeeklyChart(points = financial.weeklyChart)
                    }
                    item {
                        SectionHeader(
                            title = "Agendamentos em Tempo Real",
                            subtitle = "Gerenciamento direto da esteira de atendimento",
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                            actionText = "Ver Todos",
                            onActionClick = { activeTab = "Agendamentos" }
                        )
                    }
                    items(appointments.take(4)) { apt ->
                        AppointmentManagementCard(
                            appointment = apt,
                            onUpdateStatus = { newStatus ->
                                viewModel.updateAppointmentStatus(apt.id, newStatus)
                            },
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                        )
                    }
                    item {
                        PaymentMethodsSummary(financial = financial)
                    }
                }

                "Agendamentos" -> {
                    item {
                        AdminAppointmentStatusFilters(
                            selectedStatus = uiState.adminFilterStatus,
                            onSelect = { viewModel.setAdminStatusFilter(it) },
                            totalAppointments = appointments.size
                        )
                    }
                    items(filteredAppointments) { apt ->
                        AppointmentManagementCard(
                            appointment = apt,
                            onUpdateStatus = { newStatus ->
                                viewModel.updateAppointmentStatus(apt.id, newStatus)
                            },
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                        )
                    }
                }

                "Financeiro" -> {
                    item {
                        AdminMetricsGrid(financial = financial)
                    }
                    item {
                        RevenueWeeklyChart(points = financial.weeklyChart)
                    }
                    item {
                        PaymentMethodsSummary(financial = financial)
                    }
                    item {
                        ExecutiveReportCard()
                    }
                }

                "Equipe" -> {
                    item {
                        SectionHeader(
                            title = "Produtividade dos Especialistas",
                            subtitle = "Faturamento por profissional e avaliação de clientes",
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                        )
                    }
                    items(stylists) { stylist ->
                        StylistPerformanceCard(
                            stylist = stylist,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                        )
                    }
                }

                "Estoque" -> {
                    item {
                        SectionHeader(
                            title = "Controle de Estoque & Produtos",
                            subtitle = "Rituais de bancada e cosméticos para venda direta",
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                        )
                    }
                    items(products) { product ->
                        ProductStockCard(
                            product = product,
                            onAdjust = { delta ->
                                viewModel.adjustStock(product.id, delta)
                            },
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                        )
                    }
                }

                "Clientes VIP" -> {
                    item {
                        SectionHeader(
                            title = "Gestão de Clientes VIP",
                            subtitle = "Membros Maison Black, Platinum e Gold com histórico de visitas",
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                        )
                    }
                    items(clientsVip) { client ->
                        ClientVipCard(
                            client = client,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                        )
                    }
                }

                "Configurações & Mídia" -> {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            SectionHeader(
                                title = "Gerenciamento de Mídia & Portfólio",
                                subtitle = "Envie fotos e vídeos da galeria do dispositivo para o carrossel da Home"
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                LuxuryGoldButton(
                                    text = "Enviar Foto",
                                    onClick = {
                                        photoPickerLauncher.launch(
                                            androidx.activity.result.PickVisualMediaRequest(
                                                androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
                                            )
                                        )
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                LuxuryGoldButton(
                                    text = "Enviar Vídeo",
                                    onClick = {
                                        videoPickerLauncher.launch(
                                            androidx.activity.result.PickVisualMediaRequest(
                                                androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.VideoOnly
                                            )
                                        )
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            SectionHeader(
                                title = "Identidade Visual & Logotipo",
                                subtitle = "Edite a foto do logotipo e ícone exibido no aplicativo"
                            )

                            LuxuryOutlinedButton(
                                text = "Alterar Logotipo / Ícone do App",
                                onClick = {
                                    logoPickerLauncher.launch(
                                        androidx.activity.result.PickVisualMediaRequest(
                                            androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
                                        )
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            SectionHeader(
                                title = "Tela de Introdução Animada (Splash)",
                                subtitle = "Ative ou desative o movimento especial na inicialização"
                            )

                            androidx.compose.material3.Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = DeepBlackCard)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Exibir Tela de Introdução na Inicialização",
                                            style = MaterialTheme.typography.titleSmall.copy(color = SophisticatedWhite, fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "Apresenta animação especial de boas-vindas ao abrir o app.",
                                            style = MaterialTheme.typography.bodySmall.copy(color = TextDim)
                                        )
                                    }
                                    androidx.compose.material3.Switch(
                                        checked = showIntroAnimation,
                                        onCheckedChange = { viewModel.setShowIntroAnimation(it) },
                                        colors = androidx.compose.material3.SwitchDefaults.colors(
                                            checkedThumbColor = DeepBlackBackground,
                                            checkedTrackColor = GoldChampagne
                                        ),
                                        modifier = Modifier.testTag("switch_intro_animation")
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun AdminTopBar(
    onOpenDrawer: () -> Unit,
    onNavigateBack: () -> Unit
) {
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
                    .testTag("admin_back_to_home")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Voltar para Início",
                    tint = GoldChampagne,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "PAINEL ADMINISTRATIVO",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = LuxurySerif,
                        color = SophisticatedWhite,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
                Text(
                    text = "Gestão Executiva & Métricas em Tempo Real",
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
                .testTag("admin_menu_button")
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu Lateral",
                tint = GoldChampagne,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun AdminFinancialIndicatorsSection(
    financial: FinancialSummary,
    appointments: List<Appointment>,
    clientsVip: List<ClientVip>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        // CARD 1: INDICADOR DE FATURAMENTO
        LuxuryCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            borderColor = GoldChampagne.copy(alpha = 0.7f)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0x26D4AF37)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Diamond,
                                contentDescription = null,
                                tint = GoldChampagne,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "FATURAMENTO EXECUTIVO",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = GoldChampagne,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = "Receita Hoje & Acumulado Mensal",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextDim, fontSize = 10.sp)
                            )
                        }
                    }

                    LuxuryBadge(text = "+${financial.growthMomPercentage}% MoM", isGold = true)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "Faturamento Hoje",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp)
                        )
                        Text(
                            text = "R$ ${"%,.2f".format(financial.revenueToday)}",
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontFamily = LuxurySerif,
                                color = SophisticatedWhite,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Acumulado Mês",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextDim, fontSize = 10.sp)
                        )
                        Text(
                            text = "R$ ${"%,.2f".format(financial.revenueMonth)}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = GoldChampagne,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Progress Bar of Monthly Target (342.8k / 380k - 90%)
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Meta Mensal: R$ 380.000,00",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextDim, fontSize = 10.sp)
                        )
                        Text(
                            text = "90.2% Atingido",
                            style = MaterialTheme.typography.bodySmall.copy(color = GoldChampagne, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFF22222B))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.902f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(GoldLinearGradient)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Ticket Médio: R$ ${"%.2f".format(financial.ticketAverage)}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp)
                    )
                    Text(
                        text = "A Receber Hoje: R$ ${"%,.2f".format(financial.pendingReceivables)}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextDim, fontSize = 11.sp)
                    )
                }
            }
        }

        // CARD 2: INDICADOR DE AGENDAMENTOS DO DIA
        LuxuryCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            borderColor = GoldChampagne.copy(alpha = 0.7f)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0x26D4AF37)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = GoldChampagne,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "AGENDAMENTOS DO DIA",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = GoldChampagne,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = "Esteira de Atendimento em Tempo Real",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextDim, fontSize = 10.sp)
                            )
                        }
                    }

                    Text(
                        text = "${financial.occupancyRate}% Ocupação",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = SuccessEmerald,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${appointments.size} Atendimentos",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontFamily = LuxurySerif,
                            color = SophisticatedWhite,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = "18 bancadas ativas",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextDim, fontSize = 11.sp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Status breakdown pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x263498DB))
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "6 Confirmados",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF5DADE2), fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x2BF39C12))
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "4 Em Curso",
                            style = MaterialTheme.typography.labelSmall.copy(color = WarningAmber, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x262ECC71))
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "8 Concluídos",
                            style = MaterialTheme.typography.labelSmall.copy(color = SuccessEmerald, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        )
                    }
                }
            }
        }

        // CARD 3: INDICADOR DE NOVOS CLIENTES
        LuxuryCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            borderColor = GoldChampagne.copy(alpha = 0.7f)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0x26D4AF37)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = null,
                                tint = GoldChampagne,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "NOVOS CLIENTES VIP",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = GoldChampagne,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = "Base de Fidelidade & Membros Club",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextDim, fontSize = 10.sp)
                            )
                        }
                    }

                    LuxuryBadge(text = "+22% Novos", isGold = true)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "+47 Clientes",
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontFamily = LuxurySerif,
                                color = SophisticatedWhite,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Cadastrados neste mês",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextDim, fontSize = 10.sp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Taxa de Retenção",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextDim, fontSize = 10.sp)
                        )
                        Text(
                            text = "94% Fidelidade",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = GoldChampagne,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DeepBlackCardElevated)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hoje: 34 VIPs atendidos",
                        style = MaterialTheme.typography.bodySmall.copy(color = OffWhite, fontSize = 11.sp)
                    )
                    Text(
                        text = "28 Maison Black • 19 Platinum",
                        style = MaterialTheme.typography.bodySmall.copy(color = GoldChampagne, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                    )
                }
            }
        }
    }
}

@Composable
fun AdminMetricsGrid(financial: FinancialSummary) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatMetricCard(
                title = "Mensal",
                value = "R$ ${"%,.0f".format(financial.revenueMonth)}",
                subtitle = "Meta: R$ 380k",
                delta = "+14%",
                icon = Icons.Default.TrendingUp,
                modifier = Modifier.weight(1f)
            )
            StatMetricCard(
                title = "Ticket Médio",
                value = "R$ ${"%.0f".format(financial.ticketAverage)}",
                subtitle = "Por atendimento",
                delta = "+8%",
                icon = Icons.Default.AttachMoney,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatMetricCard(
                title = "A Receber",
                value = "R$ ${"%,.0f".format(financial.pendingReceivables)}",
                subtitle = "Atendimentos hoje",
                icon = Icons.Default.EventNote,
                modifier = Modifier.weight(1f)
            )
            StatMetricCard(
                title = "Ocupação",
                value = "${financial.occupancyRate}%",
                subtitle = "18 cadeiras ativas",
                delta = "+5%",
                icon = Icons.Default.Percent,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun RevenueWeeklyChart(
    points: List<DailyRevenuePoint>,
    modifier: Modifier = Modifier
) {
    InteractiveRevenueChart(
        points = points,
        modifier = modifier
    )
}

@Composable
fun AdminAppointmentStatusFilters(
    selectedStatus: AppointmentStatus?,
    onSelect: (AppointmentStatus?) -> Unit,
    totalAppointments: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val isAll = selectedStatus == null
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isAll) GoldChampagne else DeepBlackCard)
                .clickable { onSelect(null) }
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "Todos ($totalAppointments)",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isAll) DeepBlackBackground else OffWhite,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        AppointmentStatus.entries.forEach { status ->
            val isSelected = selectedStatus == status
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) GoldChampagne else DeepBlackCard)
                    .clickable { onSelect(status) }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = status.label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isSelected) DeepBlackBackground else TextMuted,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

@Composable
fun AppointmentManagementCard(
    appointment: Appointment,
    onUpdateStatus: (AppointmentStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    LuxuryCard(
        modifier = modifier.fillMaxWidth(),
        borderColor = if (appointment.status == AppointmentStatus.EM_ATENDIMENTO) Color(0x66F39C12) else DeepBlackBorder
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = appointment.clientName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = LuxurySerif,
                                color = SophisticatedWhite,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        if (appointment.clientIsVip) {
                            Spacer(modifier = Modifier.width(6.dp))
                            LuxuryBadge(text = "VIP", isGold = true)
                        }
                    }
                    Text(
                        text = appointment.clientPhone,
                        style = MaterialTheme.typography.bodySmall.copy(color = TextDim, fontSize = 11.sp)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    StatusBadge(status = appointment.status)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "R$ ${"%.2f".format(appointment.price)}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = GoldChampagne,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${appointment.serviceName} • com ${appointment.stylistName}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = OffWhite,
                    fontWeight = FontWeight.SemiBold
                )
            )

            Text(
                text = "Horário: ${appointment.time} (${appointment.date})",
                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
            )

            if (appointment.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x1AD4AF37))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Nota: ${appointment.notes}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = GoldChampagne,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action buttons to advance status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (appointment.status == AppointmentStatus.CONFIRMADO) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x33F39C12))
                            .clickable { onUpdateStatus(AppointmentStatus.EM_ATENDIMENTO) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Iniciar Atendimento", style = MaterialTheme.typography.labelSmall.copy(color = WarningAmber))
                        }
                    }
                }

                if (appointment.status == AppointmentStatus.EM_ATENDIMENTO) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x332ECC71))
                            .clickable { onUpdateStatus(AppointmentStatus.CONCLUIDO) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessEmerald, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Finalizar & Cobrar", style = MaterialTheme.typography.labelSmall.copy(color = SuccessEmerald))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StylistPerformanceCard(
    stylist: Stylist,
    modifier: Modifier = Modifier
) {
    LuxuryCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0x26D4AF37)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stylist.avatarInitials,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = LuxurySerif,
                            color = GoldChampagne,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = stylist.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = SophisticatedWhite,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = stylist.role,
                        style = MaterialTheme.typography.bodySmall.copy(color = GoldChampagne, fontSize = 11.sp)
                    )
                    Text(
                        text = "${stylist.experienceYears} anos de experiência • ${stylist.reviewsCount} atendimentos",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextDim, fontSize = 10.sp)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Avaliação",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextDim, fontSize = 10.sp)
                )
                Text(
                    text = "${stylist.rating} ★",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = GoldChampagne,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
fun ProductStockCard(
    product: Product,
    onAdjust: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isLowStock = product.stockQuantity <= product.minStockAlert

    LuxuryCard(
        modifier = modifier.fillMaxWidth(),
        borderColor = if (isLowStock) Color(0x66E74C3C) else DeepBlackBorder
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = SophisticatedWhite,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    if (isLowStock) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Estoque Baixo",
                            tint = AlertRuby,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Text(
                    text = "${product.brand} • R$ ${"%.2f".format(product.price)}",
                    style = MaterialTheme.typography.bodySmall.copy(color = GoldChampagne, fontSize = 11.sp)
                )
                Text(
                    text = "Mínimo recomendado: ${product.minStockAlert} unidades",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextDim, fontSize = 10.sp)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { onAdjust(-1) },
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(DeepBlackCardElevated)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Diminuir", tint = OffWhite, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${product.stockQuantity}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = if (isLowStock) AlertRuby else SophisticatedWhite,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { onAdjust(1) },
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0x26D4AF37))
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Aumentar", tint = GoldChampagne, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun ClientVipCard(
    client: ClientVip,
    modifier: Modifier = Modifier
) {
    LuxuryCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = client.name,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontFamily = LuxurySerif,
                            color = SophisticatedWhite,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    LuxuryBadge(text = client.tier, isGold = true)
                }
                Text(
                    text = client.phone,
                    style = MaterialTheme.typography.bodySmall.copy(color = TextDim, fontSize = 11.sp)
                )
                Text(
                    text = "Última visita: ${client.lastVisit} • ${client.visitsCount} visitas no total",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 10.sp)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Total Investido",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextDim, fontSize = 9.sp)
                )
                Text(
                    text = "R$ ${"%,.2f".format(client.totalSpent)}",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = GoldChampagne,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
fun PaymentMethodsSummary(financial: FinancialSummary) {
    LuxuryCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column {
            Text(
                text = "RECEBIMENTOS POR MEIO DE PAGAMENTO",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = LuxurySerif,
                    color = SophisticatedWhite,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            PaymentRow(label = "Cartões Black & Infinity", amount = financial.cardBlackTotal, icon = Icons.Default.CreditCard)
            PaymentRow(label = "PIX Instantâneo", amount = financial.pixTotal, icon = Icons.Default.Pix)
            PaymentRow(label = "Débito Premium", amount = financial.debitTotal, icon = Icons.Default.CreditCard)
            PaymentRow(label = "Dinheiro & Espécie", amount = financial.cashTotal, icon = Icons.Default.AttachMoney)
        }
    }
}

@Composable
fun PaymentRow(label: String, amount: Double, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = GoldChampagne, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = label, style = MaterialTheme.typography.bodySmall.copy(color = OffWhite))
        }
        Text(
            text = "R$ ${"%,.2f".format(amount)}",
            style = MaterialTheme.typography.bodySmall.copy(
                color = GoldChampagne,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Composable
fun ExecutiveReportCard() {
    LuxuryCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        borderColor = Color(0x33D4AF37)
    ) {
        Column {
            Text(
                text = "EXPORTAÇÃO DE RELATÓRIO EXECUTIVO",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = LuxurySerif,
                    color = GoldChampagne,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Gere relatórios completos de faturamento, comissões de visagistas e balanço de estoque em formato digital.",
                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
            )
            Spacer(modifier = Modifier.height(12.dp))
            LuxuryGoldButton(
                text = "Exportar Relatório Mensal",
                onClick = { /* Simulated export */ },
                icon = Icons.Default.Download,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
