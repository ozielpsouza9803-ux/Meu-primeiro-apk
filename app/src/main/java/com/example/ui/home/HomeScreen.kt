package com.example.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Product
import com.example.model.SalonService
import com.example.model.ServiceCategory
import com.example.model.Stylist
import com.example.ui.components.LuxuryBadge
import com.example.ui.components.LuxuryCard
import com.example.ui.components.LuxuryGoldButton
import com.example.ui.components.LuxuryOutlinedButton
import com.example.ui.components.SalonPortfolioGallery
import com.example.ui.components.SectionHeader
import com.example.ui.theme.DeepBlackBackground
import com.example.ui.theme.DeepBlackBorder
import com.example.ui.theme.DeepBlackCard
import com.example.ui.theme.DeepBlackCardElevated
import com.example.ui.theme.GoldChampagne
import com.example.ui.theme.GoldDiagonalGradient
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldLinearGradient
import com.example.ui.theme.LuxurySerif
import com.example.ui.theme.OffWhite
import com.example.ui.theme.SophisticatedWhite
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.SalonViewModel

@Composable
fun HomeScreen(
    viewModel: SalonViewModel,
    onOpenDrawer: () -> Unit,
    onNavigateToAdmin: () -> Unit = { viewModel.navigateTo(AppScreen.ADMIN) },
    onNavigateToAppointments: () -> Unit = { viewModel.navigateTo(AppScreen.APPOINTMENTS) },
    onNavigateToNewBooking: () -> Unit = { viewModel.navigateTo(AppScreen.NEW_BOOKING) },
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val services by viewModel.services.collectAsState()
    val stylists by viewModel.stylists.collectAsState()
    val products by viewModel.products.collectAsState()
    val portfolio by viewModel.portfolio.collectAsState()

    val filteredServices = services.filter { service ->
        val matchesCategory = (uiState.selectedCategory == ServiceCategory.TODOS) || (service.category == uiState.selectedCategory)
        val query = uiState.searchQuery.trim()
        val matchesQuery = query.isBlank() ||
            service.name.contains(query, ignoreCase = true) ||
            service.description.contains(query, ignoreCase = true) ||
            service.category.label.contains(query, ignoreCase = true)
        matchesCategory && matchesQuery
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DeepBlackBackground,
        contentColor = SophisticatedWhite,
        topBar = {
            HomeTopBar(
                onOpenDrawer = onOpenDrawer,
                onNavigateToAdmin = onNavigateToAdmin
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToNewBooking,
                modifier = Modifier
                    .padding(16.dp)
                    .testTag("fab_vip_booking"),
                containerColor = GoldChampagne,
                contentColor = DeepBlackBackground,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Agendar VIP",
                        tint = DeepBlackBackground
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AGENDAR",
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
                .testTag("home_screen_lazy_column")
        ) {
            // 1. BARRA DE PESQUISA ESTILIZADA DE LUXO
            item {
                HomeSearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = { viewModel.setSearchQuery(it) },
                    onClear = { viewModel.clearSearchQuery() },
                    resultCount = filteredServices.size
                )
            }

            // 2. HERO BANNER PRINCIPAL (COM PLACEHOLDER DE LUXO)
            item {
                HomeHeroBanner(
                    onBookClick = onNavigateToNewBooking
                )
            }

            // 3. APRESENTAÇÃO DO ESTABELECIMENTO
            item {
                HomeSalonPresentation()
            }

            // 4. SERVIÇOS EM DESTAQUE (com Categorias)
            item {
                SectionHeader(
                    title = "Serviços em Destaque",
                    subtitle = "Técnicas exclusivas de corte, cor e tratamentos biométricos",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    actionText = "Ver Agenda",
                    onActionClick = onNavigateToAppointments
                )

                // Category Tabs
                val categories = ServiceCategory.entries
                ScrollableTabRow(
                    selectedTabIndex = categories.indexOf(uiState.selectedCategory),
                    containerColor = Color.Transparent,
                    contentColor = GoldChampagne,
                    edgePadding = 20.dp,
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

            // CARD DE AGENDAMENTO VIP NA HOME
            item {
                HomeQuickBookingBanner(
                    onStartBooking = onNavigateToNewBooking,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                )
            }

            // Services list
            if (filteredServices.isNotEmpty()) {
                items(filteredServices) { service ->
                    ServiceCard(
                        service = service,
                        onBook = {
                            viewModel.setPreselectedBooking(service = service)
                            onNavigateToNewBooking()
                        },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )
                }
            } else {
                item {
                    HomeEmptySearchState(
                        query = uiState.searchQuery,
                        onReset = {
                            viewModel.clearSearchQuery()
                            viewModel.setCategoryFilter(ServiceCategory.TODOS)
                        }
                    )
                }
            }

            // 5. PROFISSIONAIS & MESTRES DA BELEZA
            item {
                Spacer(modifier = Modifier.height(18.dp))
                SectionHeader(
                    title = "Especialistas & Visagistas",
                    subtitle = "Talentos com reconhecimento internacional e formações parisienses",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )

                LazyRow(
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(stylists) { stylist ->
                        StylistCard(
                            stylist = stylist,
                            onBookWithStylist = {
                                viewModel.setPreselectedBooking(stylist = stylist)
                                onNavigateToNewBooking()
                            }
                        )
                    }
                }
            }

            // 5.1 GALERIA DE PORTFÓLIO (COIL + FIRESTORE)
            item {
                Spacer(modifier = Modifier.height(24.dp))
                SalonPortfolioGallery(portfolioItems = portfolio)
            }

            // 6. PRODUTOS & BOUTIQUE
            item {
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(
                    title = "Boutique & Cuidados Home Care",
                    subtitle = "Cosméticos e rituais capilares de altíssimo padrão",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    actionText = "Ver Estoque ADM",
                    onActionClick = onNavigateToAdmin
                )

                LazyRow(
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(products) { product ->
                        ProductCard(
                            product = product,
                            onReserve = {
                                viewModel.adjustStock(product.id, -1)
                            }
                        )
                    }
                }
            }

            // 7. FOOTER LUXO
            item {
                Spacer(modifier = Modifier.height(32.dp))
                HomeFooter(onNavigateToAdmin = onNavigateToAdmin)
                Spacer(modifier = Modifier.height(90.dp))
            }
        }
    }
}

@Composable
fun HomeTopBar(
    onOpenDrawer: () -> Unit,
    onNavigateToAdmin: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DeepBlackBackground)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onOpenDrawer,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(DeepBlackCard)
                    .testTag("top_menu_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu Concierge",
                    tint = GoldChampagne,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "MAISON DE BEAUTÉ",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = LuxurySerif,
                            fontWeight = FontWeight.Bold,
                            color = SophisticatedWhite,
                            letterSpacing = 1.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Selo de Excelência",
                        tint = GoldChampagne,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Text(
                    text = "Jardins • São Paulo",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextDim, fontSize = 10.sp)
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Quick ADM button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x33D4AF37))
                    .border(1.dp, GoldChampagne.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .clickable(onClick = onNavigateToAdmin)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("top_admin_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = GoldChampagne,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "ADM",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = GoldChampagne,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun HomeHeroBanner(
    onBookClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(290.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(24.dp))
            .border(1.5.dp, GoldChampagne, RoundedCornerShape(24.dp))
            .testTag("hero_banner")
    ) {
        // 1. Luxury Placeholder Background Layer with geometric gold aura
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF231E12),
                            Color(0xFF14120D),
                            Color(0xFF09090B)
                        )
                    )
                )
        )

        // 2. High-Resolution Haute Beauté Interior Image
        Image(
            painter = painterResource(id = R.drawable.hero_salon_interior),
            contentDescription = "Interior de Alto Padrão Maison de Beauté",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient overlay for readability and moody luxury feel
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x660D0D0F),
                            Color(0xCC0D0D0F),
                            Color(0xFA0D0D0F)
                        )
                    )
                )
        )

        // Hero Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                LuxuryBadge(text = "Alta Costura & Spa", isGold = true)
                Spacer(modifier = Modifier.width(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = GoldChampagne,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "5.0 (1.2k avaliações)",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 10.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "O ápice do visagismo e da beleza personalizada.",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = LuxurySerif,
                    fontWeight = FontWeight.Bold,
                    color = SophisticatedWhite,
                    lineHeight = 26.sp
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Ambiente privativo, espumante de boas-vindas e atendimento individualizado com mestres premiados.",
                style = MaterialTheme.typography.bodySmall.copy(color = OffWhite, fontSize = 12.sp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                LuxuryGoldButton(
                    text = "Agendar Experiência",
                    onClick = onBookClick,
                    icon = Icons.Default.Diamond,
                    modifier = Modifier.weight(1f),
                    testTag = "hero_book_experience_button"
                )
            }
        }
    }
}

@Composable
fun HomeSalonPresentation() {
    LuxuryCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        borderColor = Color(0x2BD4AF37)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0x26D4AF37)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Diamond,
                        contentDescription = null,
                        tint = GoldChampagne,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "CONCEITO HAUTE BEAUTÉ",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = LuxurySerif,
                            color = GoldChampagne,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "Excelência em cada detalhe desde 2014",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "A Maison de Beauté é um refúgio de sofisticação nos Jardins. Unimos a tradição dos grandes ateliês franceses às tecnologias biométricas de diagnóstico capilar e facial. Cada cliente é recebido com nosso serviço de Concierge, carta de champanhes e consultoria personalizada de imagem.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = OffWhite,
                    lineHeight = 20.sp
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = GoldChampagne,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Rua Oscar Freire, 1420",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = GoldChampagne,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Ter - Sáb: 09h às 20h",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }
            }
        }
    }
}

@Composable
fun ServiceCard(
    service: SalonService,
    onBook: () -> Unit,
    modifier: Modifier = Modifier
) {
    LuxuryCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onBook,
        borderColor = if (service.isFeatured) GoldChampagne else Color(0x99D4AF37)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (service.badge != null) {
                        LuxuryBadge(text = service.badge, isGold = true)
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    Text(
                        text = service.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = LuxurySerif,
                            color = SophisticatedWhite,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "R$ ${"%.2f".format(service.price)}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = GoldChampagne,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = TextDim,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${service.durationMinutes} min",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextDim, fontSize = 11.sp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = service.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextMuted,
                    lineHeight = 17.sp
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = GoldChampagne,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${service.rating} Exclusividade",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = GoldChampagne,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    )
                }

                LuxuryOutlinedButton(
                    text = "Agendar Horário",
                    onClick = onBook,
                    icon = Icons.Default.CalendarMonth
                )
            }
        }
    }
}

@Composable
fun StylistCard(
    stylist: Stylist,
    onBookWithStylist: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(220.dp)
            .shadow(6.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, DeepBlackBorder, RoundedCornerShape(18.dp))
            .clickable(onClick = onBookWithStylist),
        color = DeepBlackCard
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Stylist avatar badge with gold ring
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .border(2.dp, GoldDiagonalGradient, CircleShape)
                    .background(DeepBlackCardElevated),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stylist.avatarInitials,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = LuxurySerif,
                        color = GoldChampagne,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stylist.name,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = LuxurySerif,
                    color = SophisticatedWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = stylist.role,
                style = MaterialTheme.typography.bodySmall.copy(color = GoldChampagne, fontSize = 11.sp),
                textAlign = TextAlign.Center
            )

            Text(
                text = stylist.title,
                style = MaterialTheme.typography.bodySmall.copy(color = TextDim, fontSize = 10.sp),
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = GoldChampagne,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "${stylist.rating} (${stylist.reviewsCount})",
                    style = MaterialTheme.typography.bodySmall.copy(color = OffWhite, fontSize = 10.sp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LuxuryOutlinedButton(
                text = "Com ele(a)",
                onClick = onBookWithStylist,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    onReserve: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(200.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, DeepBlackBorder, RoundedCornerShape(16.dp)),
        color = DeepBlackCard
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = product.brand.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = GoldChampagne,
                        fontSize = 9.sp,
                        letterSpacing = 1.sp
                    )
                )
                if (product.isExclusive) {
                    LuxuryBadge(text = "Exclusivo", isGold = true)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.name,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = SophisticatedWhite,
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = product.category,
                style = MaterialTheme.typography.bodySmall.copy(color = TextDim, fontSize = 10.sp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "R$ ${"%.2f".format(product.price)}",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = GoldChampagne,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "${product.stockQuantity} un.",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextDim, fontSize = 10.sp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x26D4AF37))
                    .clickable(onClick = onReserve)
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = GoldChampagne,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "RESERVAR",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = GoldChampagne,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun HomeFooter(onNavigateToAdmin: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "MAISON DE BEAUTÉ",
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = LuxurySerif,
                color = GoldChampagne,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "L'Art de la Haute Coiffure et Soin • São Paulo",
            style = MaterialTheme.typography.bodySmall.copy(color = TextDim, fontSize = 11.sp),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "Acesso exclusivo para equipe e gestão executiva:",
            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        LuxuryOutlinedButton(
            text = "Acessar Painel Administrativo",
            onClick = onNavigateToAdmin,
            icon = Icons.Default.AdminPanelSettings,
            testTag = "footer_admin_button"
        )
    }
}

/**
 * Barra de pesquisa estilizada no topo da tela Home.
 * Filtra instantaneamente os serviços do salão mantendo a identidade visual de luxo.
 */
@Composable
fun HomeSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    resultCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = if (query.isNotBlank()) GoldChampagne.copy(alpha = 0.5f) else Color(0x33000000)
                )
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = if (query.isNotBlank()) 1.5.dp else 1.dp,
                    color = if (query.isNotBlank()) GoldChampagne else Color(0x55D4AF37),
                    shape = RoundedCornerShape(16.dp)
                ),
            color = Color(0xFF14141A)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = GoldChampagne,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 12.dp)
                        .testTag("home_search_input"),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = SophisticatedWhite,
                        fontSize = 14.sp
                    ),
                    singleLine = true,
                    cursorBrush = Brush.verticalGradient(listOf(GoldChampagne, GoldLight)),
                    decorationBox = { innerTextField ->
                        if (query.isEmpty()) {
                            Text(
                                text = "Buscar serviços (corte, visagismo, ouro 24k, mechas...)",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextDim,
                                    fontSize = 13.sp
                                )
                            )
                        }
                        innerTextField()
                    }
                )
                if (query.isNotEmpty()) {
                    IconButton(
                        onClick = onClear,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0x26D4AF37))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Limpar busca",
                            tint = GoldChampagne,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        if (query.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$resultCount ritual(is) encontrado(s)",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = GoldChampagne,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )
                )
                Text(
                    text = "Limpar busca",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextMuted,
                        fontSize = 11.sp
                    ),
                    modifier = Modifier.clickable(onClick = onClear)
                )
            }
        }
    }
}

/**
 * Estado elegante exibido quando nenhum serviço corresponde ao termo pesquisado.
 */
@Composable
fun HomeEmptySearchState(
    query: String,
    onReset: () -> Unit
) {
    LuxuryCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        borderColor = Color(0x33D4AF37)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color(0x26D4AF37)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = GoldChampagne,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "NENHUM RITUAL EXCLUSIVO ENCONTRADO",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = LuxurySerif,
                    color = SophisticatedWhite,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Não encontramos serviços para \"$query\". Experimente buscar por corte, visagismo, balayage, spa facial ou selecione uma das categorias.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextMuted,
                    lineHeight = 18.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
            LuxuryOutlinedButton(
                text = "Limpar Busca e Ver Todos",
                onClick = onReset,
                icon = Icons.Default.Close
            )
        }
    }
}

/**
 * Banner de Agendamento VIP na Home.
 * Proporciona acesso imediato e de alto padrão ao novo formulário de agendamento da Maison.
 */
@Composable
fun HomeQuickBookingBanner(
    onStartBooking: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onStartBooking)
            .testTag("home_quick_booking_banner"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DeepBlackCardElevated),
        border = BorderStroke(1.2.dp, GoldChampagne.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF221C12),
                            DeepBlackCard
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(GoldChampagne.copy(alpha = 0.15f))
                            .border(1.dp, GoldChampagne, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = GoldChampagne,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "NOVO AGENDAMENTO VIP",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = GoldChampagne,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.3.sp
                            )
                        )
                        Text(
                            text = "Sessão de Beleza Sob Medida",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextDim,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                LuxuryBadge(text = "EXCLUSIVO")
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Reserve sua Experiência na Maison",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = LuxurySerif,
                    fontWeight = FontWeight.Bold,
                    color = SophisticatedWhite
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Escolha seu tratamento, o especialista visagista e o horário de sua preferência com confirmação instantânea e protocolo VIP.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextDim,
                    lineHeight = 18.sp
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "• Mestre Visagista",
                        style = MaterialTheme.typography.bodySmall.copy(color = GoldChampagne, fontSize = 11.sp)
                    )
                    Text(
                        text = "• Horários Flexíveis",
                        style = MaterialTheme.typography.bodySmall.copy(color = GoldChampagne, fontSize = 11.sp)
                    )
                }

                LuxuryGoldButton(
                    text = "AGENDAR AGORA",
                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                    onClick = onStartBooking,
                    testTag = "btn_home_start_booking"
                )
            }
        }
    }
}

