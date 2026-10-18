package com.example.ui.auth

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DeepBlackBackground
import com.example.ui.theme.DeepBlackCard
import com.example.ui.theme.DeepBlackSurface
import com.example.ui.theme.GoldChampagne
import com.example.ui.theme.GoldLinearGradient
import com.example.ui.theme.GoldSubtleBorder
import com.example.ui.theme.LuxurySerif
import com.example.ui.theme.SophisticatedWhite
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextMuted

@Composable
fun SalonSignInScreen(
    isSigningIn: Boolean,
    errorMessage: String?,
    onSignInClick: () -> Unit,
    onDismissError: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF09090B),
                        Color(0xFF121216),
                        Color(0xFF18181E)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .widthIn(max = 500.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Emblema Maison de Beauté
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF2E271D),
                                DeepBlackSurface
                            )
                        )
                    )
                    .border(
                        BorderStroke(2.dp, GoldChampagne),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Estrela VIP",
                        tint = GoldChampagne,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "MB",
                        fontFamily = LuxurySerif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = GoldChampagne,
                        letterSpacing = 2.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "MAISON DE BEAUTÉ",
                fontFamily = LuxurySerif,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = SophisticatedWhite,
                letterSpacing = 3.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = "HAUTE COIFFURE & CONCIERGE VIP",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = GoldChampagne,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Card de Login
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        BorderStroke(1.dp, GoldSubtleBorder),
                        RoundedCornerShape(20.dp)
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = DeepBlackCard.copy(alpha = 0.95f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Acesso Exclusivo à Nuvem",
                        fontFamily = LuxurySerif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = SophisticatedWhite,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Conecte-se para sincronizar agendamentos em tempo real no Cloud Firestore, gerenciar clientes VIP e acessar o painel executivo.",
                        fontSize = 13.sp,
                        color = TextDim,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Animated Error Banner
                    AnimatedVisibility(
                        visible = errorMessage != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        errorMessage?.let { msg ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF331515),
                                border = BorderStroke(1.dp, Color(0xFFE57373))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Aviso",
                                        tint = Color(0xFFFF8A80),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = msg,
                                        color = Color(0xFFFFEBEE),
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }

                    // Botão Google Sign-In
                    Button(
                        onClick = onSignInClick,
                        enabled = !isSigningIn,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("google_sign_in_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF1F1F1F)
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        if (isSigningIn) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = GoldChampagne,
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Autenticando com Google...",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1F1F1F)
                            )
                        } else {
                            // Ícone estilizado do Google com cores
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4285F4)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "G",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Continuar com o Google",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F1F1F),
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Segurança",
                            tint = GoldChampagne.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Cloud Firestore Enterprise • Jetpack Credential Manager",
                            fontSize = 11.sp,
                            color = TextMuted,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Maison de Beauté © 2026 • São Paulo • Paris",
                fontSize = 11.sp,
                color = TextMuted.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}
