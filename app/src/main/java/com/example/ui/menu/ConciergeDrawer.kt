package com.example.ui.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import com.google.firebase.auth.FirebaseUser
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.LuxuryBadge
import com.example.ui.components.LuxuryGoldButton
import com.example.ui.theme.DeepBlackBackground
import com.example.ui.theme.DeepBlackBorder
import com.example.ui.theme.DeepBlackCard
import com.example.ui.theme.DeepBlackCardElevated
import com.example.ui.theme.DeepBlackDivider
import com.example.ui.theme.DeepBlackSurface
import com.example.ui.theme.GoldChampagne
import com.example.ui.theme.GoldLinearGradient
import com.example.ui.theme.LuxurySerif
import com.example.ui.theme.OffWhite
import com.example.ui.theme.SophisticatedWhite
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.AppScreen

data class DrawerMenuItem(
    val screen: AppScreen,
    val icon: ImageVector,
    val title: String,
    val badge: String? = null,
    val isAdminArea: Boolean = false
)

@Composable
fun ConciergeDrawerContent(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    onCloseDrawer: () -> Unit,
    onOpenBooking: () -> Unit,
    currentUser: FirebaseUser? = null,
    onSignOut: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val menuItems = listOf(
        DrawerMenuItem(
            screen = AppScreen.HOME,
            icon = Icons.Default.Home,
            title = "Início & Experiência VIP"
        ),
        DrawerMenuItem(
            screen = AppScreen.NEW_BOOKING,
            icon = Icons.Default.CalendarMonth,
            title = "Novo Agendamento VIP",
            badge = "Reserve"
        ),
        DrawerMenuItem(
            screen = AppScreen.ADMIN,
            icon = Icons.Default.AdminPanelSettings,
            title = "Painel Executivo ADM",
            badge = "Gestor",
            isAdminArea = true
        ),
        DrawerMenuItem(
            screen = AppScreen.APPOINTMENTS,
            icon = Icons.Default.CalendarMonth,
            title = "Agenda do Salão",
            badge = "Hoje"
        ),
        DrawerMenuItem(
            screen = AppScreen.SERVICES,
            icon = Icons.Default.ContentCut,
            title = "Menu de Alta Costura"
        ),
        DrawerMenuItem(
            screen = AppScreen.STYLISTS,
            icon = Icons.Default.People,
            title = "Especialistas & Visagistas"
        ),
        DrawerMenuItem(
            screen = AppScreen.BOUTIQUE,
            icon = Icons.Default.ShoppingBag,
            title = "Boutique & Cosméticos"
        ),
        DrawerMenuItem(
            screen = AppScreen.PROFILE,
            icon = Icons.Default.Person,
            title = "Meu Perfil & Histórico VIP",
            badge = "VIP"
        )
    )

    ModalDrawerSheet(
        modifier = modifier
            .fillMaxHeight()
            .width(320.dp)
            .testTag("concierge_drawer_sheet"),
        drawerContainerColor = DeepBlackSurface,
        drawerContentColor = SophisticatedWhite
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Header with Salon Crest
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_salon_logo),
                            contentDescription = "Maison Crest",
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .border(1.dp, GoldChampagne, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "MAISON DE BEAUTÉ",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = LuxurySerif,
                                    fontWeight = FontWeight.Bold,
                                    color = SophisticatedWhite,
                                    letterSpacing = 1.2.sp
                                )
                            )
                            Text(
                                text = "Concierge & Gestão",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = GoldChampagne,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = onCloseDrawer,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(DeepBlackCard)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar Menu",
                            tint = SophisticatedWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // VIP Manager Profile Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(DeepBlackCard)
                        .border(1.dp, Color(0x33D4AF37), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(GoldLinearGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            val initials = currentUser?.displayName
                                ?.split(" ")
                                ?.filter { it.isNotBlank() }
                                ?.mapNotNull { it.firstOrNull()?.toString() }
                                ?.take(2)
                                ?.joinToString("")
                                ?.uppercase() ?: "MB"
                            Text(
                                text = initials,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = DeepBlackBackground,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentUser?.displayName ?: "Atendimento VIP Concierge",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = SophisticatedWhite,
                                    fontWeight = FontWeight.Bold
                                ),
                                maxLines = 1
                            )
                            Text(
                                text = currentUser?.email ?: "Perfil Master • Cloud Firestore",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextDim,
                                    fontSize = 10.sp
                                ),
                                maxLines = 1
                            )
                        }
                        IconButton(
                            onClick = onSignOut,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Sair da Conta",
                                tint = GoldChampagne,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "ÁREAS DE NAVEGAÇÃO",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextDim,
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                )

                // Menu items
                menuItems.forEach { item ->
                    val isSelected = currentScreen == item.screen
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = if (isSelected) GoldChampagne else if (item.isAdminArea) GoldChampagne else OffWhite,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = if (isSelected) GoldChampagne else SophisticatedWhite,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 13.sp
                                    )
                                )
                                if (item.badge != null) {
                                    LuxuryBadge(text = item.badge, isGold = isSelected || item.isAdminArea)
                                }
                            }
                        },
                        selected = isSelected,
                        onClick = {
                            onNavigate(item.screen)
                            onCloseDrawer()
                        },
                        modifier = Modifier
                            .padding(vertical = 3.dp)
                            .testTag("drawer_item_${item.screen.name.lowercase()}"),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = Color(0x2BD4AF37),
                            unselectedContainerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = DeepBlackDivider)
                Spacer(modifier = Modifier.height(14.dp))

                // Quick Mode Switch Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x1AD4AF37))
                        .border(1.dp, Color(0x40D4AF37), RoundedCornerShape(12.dp))
                        .clickable {
                            if (currentScreen == AppScreen.ADMIN) {
                                onNavigate(AppScreen.HOME)
                            } else {
                                onNavigate(AppScreen.ADMIN)
                            }
                            onCloseDrawer()
                        }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                        .testTag("drawer_toggle_mode_button")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = null,
                                tint = GoldChampagne,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (currentScreen == AppScreen.ADMIN) "Ir para Visão Cliente VIP" else "Acessar Painel Executivo ADM",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = GoldChampagne,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }

            // Bottom Actions & Quick Contact
            Column(modifier = Modifier.padding(top = 16.dp)) {
                LuxuryGoldButton(
                    text = "Agendar Horário VIP",
                    onClick = {
                        onCloseDrawer()
                        onOpenBooking()
                    },
                    icon = Icons.Default.CalendarMonth,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "drawer_book_button"
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Concierge WhatsApp Direct Call
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DeepBlackCard)
                        .clickable { /* Concierge call */ }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        tint = GoldChampagne,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Linha Direta Concierge",
                            style = MaterialTheme.typography.labelSmall.copy(color = SophisticatedWhite, fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = "(11) 99841-2200 • WhatsApp",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextDim, fontSize = 10.sp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Maison de Beauté v1.0 • Haute Parfumerie & Coiffure",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextDim,
                        fontSize = 9.sp
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}
