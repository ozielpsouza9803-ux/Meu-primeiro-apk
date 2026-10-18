package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DeepBlackBackground
import com.example.ui.theme.DeepBlackCard
import com.example.ui.theme.GoldChampagne
import com.example.ui.theme.GoldLinearGradient
import com.example.ui.theme.LuxurySerif
import com.example.ui.theme.SophisticatedWhite
import kotlinx.coroutines.delay

@Composable
fun IntroSplashGate(
    onFinished: () -> Unit
) {
    val alphaAnim = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(0.8f) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    LaunchedEffect(Unit) {
        alphaAnim.animateTo(1f, animationSpec = tween(800))
        scaleAnim.animateTo(1f, animationSpec = tween(800, easing = FastOutSlowInEasing))
        delay(2200) // 2.2 seconds display time
        alphaAnim.animateTo(0f, animationSpec = tween(600))
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlackBackground)
            .testTag("intro_splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .alpha(alphaAnim.value)
                .scale(scaleAnim.value * pulseScale)
        ) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(GoldLinearGradient),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Diamond,
                    contentDescription = null,
                    tint = DeepBlackBackground,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "MAISON DE BEAUTÉ",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = GoldChampagne,
                    fontWeight = FontWeight.Bold,
                    fontFamily = LuxurySerif,
                    letterSpacing = 2.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Alta Costura & Experiência VIP",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = SophisticatedWhite,
                    letterSpacing = 1.5.sp
                )
            )
        }
    }
}
