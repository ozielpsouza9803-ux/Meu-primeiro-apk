package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppointmentStatus
import com.example.ui.theme.AlertRuby
import com.example.ui.theme.DeepBlackBackground
import com.example.ui.theme.DeepBlackBorder
import com.example.ui.theme.DeepBlackCard
import com.example.ui.theme.GoldChampagne
import com.example.ui.theme.GoldLinearGradient
import com.example.ui.theme.InfoSapphire
import com.example.ui.theme.LuxurySerif
import com.example.ui.theme.OffWhite
import com.example.ui.theme.SophisticatedWhite
import com.example.ui.theme.SuccessEmerald
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextMuted
import com.example.ui.theme.WarningAmber

@Composable
fun LuxuryGoldButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    testTag: String = "luxury_gold_button"
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isElevated = isPressed || isHovered

    val animatedElevation by animateDpAsState(
        targetValue = if (isElevated) 12.dp else 6.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "btn_elevation"
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (isElevated) 1.025f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "btn_scale"
    )

    Box(
        modifier = modifier
            .testTag(testTag)
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .shadow(
                elevation = animatedElevation,
                shape = RoundedCornerShape(12.dp),
                ambientColor = GoldChampagne,
                spotColor = GoldChampagne
            )
            .clip(RoundedCornerShape(12.dp))
            .background(GoldLinearGradient)
            .hoverable(interactionSource)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = DeepBlackBackground,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text.uppercase(),
                style = MaterialTheme.typography.labelLarge.copy(
                    color = DeepBlackBackground,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
            )
        }
    }
}

@Composable
fun LuxuryOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    testTag: String = "luxury_outlined_button"
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isElevated = isPressed || isHovered

    val animatedBorderColor by animateColorAsState(
        targetValue = if (isElevated) Color(0xFFF3E5AB) else GoldChampagne,
        animationSpec = tween(200),
        label = "out_btn_border"
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (isElevated) 1.03f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "out_btn_scale"
    )

    Box(
        modifier = modifier
            .testTag(testTag)
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .clip(RoundedCornerShape(12.dp))
            .border(width = 1.dp, color = animatedBorderColor, shape = RoundedCornerShape(12.dp))
            .background(if (isElevated) Color(0x33D4AF37) else Color(0x1AD4AF37))
            .hoverable(interactionSource)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = GoldChampagne,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = GoldChampagne,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

/**
 * Card com estética de luxo, profundidade 3D e efeitos de transição suave
 * de elevação (shadow), escala e iluminação dourada ao passar o mouse ou tocar.
 */
@Composable
fun LuxuryCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    borderColor: Color = DeepBlackBorder,
    cornerRadius: Dp = 16.dp,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isElevated = isPressed || isHovered

    // Transição suave de elevação da sombra ao tocar ou passar o mouse
    val animatedElevation by animateDpAsState(
        targetValue = if (isElevated) 16.dp else 6.dp,
        animationSpec = spring(
            stiffness = Spring.StiffnessMediumLow,
            dampingRatio = Spring.DampingRatioLowBouncy
        ),
        label = "luxury_card_elevation"
    )

    // Efeito suave de escala 3D
    val animatedScale by animateFloatAsState(
        targetValue = if (isElevated) 1.018f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "luxury_card_scale"
    )

    // Brilho dourado refinado na borda ao interagir
    val animatedBorderColor by animateColorAsState(
        targetValue = if (isElevated) GoldChampagne else borderColor,
        animationSpec = tween(durationMillis = 200),
        label = "luxury_card_border_color"
    )

    // Espessura da borda com transição fluida
    val animatedBorderWidth by animateDpAsState(
        targetValue = if (isElevated) 1.8.dp else 1.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "luxury_card_border_width"
    )

    // Sombra com aura champanhe ao passar o mouse / tocar
    val animatedSpotColor by animateColorAsState(
        targetValue = if (isElevated) GoldChampagne.copy(alpha = 0.5f) else Color(0x33000000),
        animationSpec = tween(durationMillis = 250),
        label = "luxury_card_spot_color"
    )

    // Leve iluminação no fundo do card
    val animatedContainerColor by animateColorAsState(
        targetValue = if (isElevated) Color(0xFF22222C) else DeepBlackCard,
        animationSpec = tween(durationMillis = 200),
        label = "luxury_card_container_color"
    )

    Surface(
        modifier = modifier
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .shadow(
                elevation = animatedElevation,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = Color.Black,
                spotColor = animatedSpotColor
            )
            .clip(RoundedCornerShape(cornerRadius))
            .border(
                width = animatedBorderWidth,
                color = animatedBorderColor,
                shape = RoundedCornerShape(cornerRadius)
            )
            .hoverable(interactionSource = interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { onClick?.invoke() }
            ),
        color = animatedContainerColor,
        contentColor = SophisticatedWhite
    ) {
        Box(
            modifier = Modifier.padding(16.dp)
        ) {
            content()
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(20.dp)
                        .background(GoldLinearGradient, shape = RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = LuxurySerif,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = SophisticatedWhite
                    )
                )
            }
            if (actionText != null && onActionClick != null) {
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = GoldChampagne,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.clickable(onClick = onActionClick)
                )
            }
        }
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted),
                modifier = Modifier.padding(start = 14.dp)
            )
        }
    }
}

@Composable
fun LuxuryBadge(
    text: String,
    modifier: Modifier = Modifier,
    isGold: Boolean = true
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50.dp))
            .background(if (isGold) Color(0x2BD4AF37) else Color(0x2BFFFFFF))
            .border(
                width = 1.dp,
                color = if (isGold) GoldChampagne else Color(0x40FFFFFF),
                shape = RoundedCornerShape(50.dp)
            )
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                color = if (isGold) GoldChampagne else OffWhite,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                fontSize = 9.sp
            )
        )
    }
}

@Composable
fun StatusBadge(status: AppointmentStatus) {
    val (bgColor, textColor, borderColor) = when (status) {
        AppointmentStatus.CONFIRMADO -> Triple(Color(0x263498DB), InfoSapphire, InfoSapphire)
        AppointmentStatus.EM_ATENDIMENTO -> Triple(Color(0x2BF39C12), WarningAmber, WarningAmber)
        AppointmentStatus.CONCLUIDO -> Triple(Color(0x262ECC71), SuccessEmerald, SuccessEmerald)
        AppointmentStatus.CANCELADO -> Triple(Color(0x26E74C3C), AlertRuby, AlertRuby)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, borderColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

/**
 * Card indicador de métrica administrativa com transição suave de elevação,
 * escala e borda dourada ao passar o mouse ou tocar.
 */
@Composable
fun StatMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    delta: String? = null,
    isPositiveDelta: Boolean = true,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isElevated = isPressed || isHovered

    val animatedElevation by animateDpAsState(
        targetValue = if (isElevated) 14.dp else 4.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "stat_elevation"
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (isElevated) 1.025f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "stat_scale"
    )

    val animatedBorderColor by animateColorAsState(
        targetValue = if (isElevated) GoldChampagne else Color(0x33D4AF37),
        animationSpec = tween(durationMillis = 200),
        label = "stat_border"
    )

    val animatedSpotColor by animateColorAsState(
        targetValue = if (isElevated) GoldChampagne.copy(alpha = 0.45f) else Color.Black,
        animationSpec = tween(durationMillis = 200),
        label = "stat_spot"
    )

    Surface(
        modifier = modifier
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .shadow(
                elevation = animatedElevation,
                shape = RoundedCornerShape(16.dp),
                spotColor = animatedSpotColor,
                ambientColor = Color.Black
            )
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, animatedBorderColor, RoundedCornerShape(16.dp))
            .hoverable(interactionSource = interactionSource)
            .clickable(interactionSource = interactionSource, indication = null) { },
        color = if (isElevated) Color(0xFF202028) else DeepBlackCard
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
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextMuted,
                        letterSpacing = 0.8.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (isElevated) GoldChampagne else Color(0x26D4AF37)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isElevated) DeepBlackBackground else GoldChampagne,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = LuxurySerif,
                    color = SophisticatedWhite,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (delta != null) {
                    Text(
                        text = delta,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (isPositiveDelta) SuccessEmerald else AlertRuby,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(color = TextDim),
                    maxLines = 1
                )
            }
        }
    }
}
