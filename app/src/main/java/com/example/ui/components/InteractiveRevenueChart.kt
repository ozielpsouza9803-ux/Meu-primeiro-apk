package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DailyRevenuePoint
import com.example.ui.theme.DeepBlackBackground
import com.example.ui.theme.DeepBlackBorder
import com.example.ui.theme.DeepBlackCard
import com.example.ui.theme.DeepBlackCardElevated
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
import java.util.Locale

/**
 * Period options for comparative weekly revenue analysis
 */
enum class ChartPeriod(val label: String, val growthText: String, val totalFormatted: String) {
    CURRENT("Semana Atual", "+18.4% WoW", "R$ 132.850,00"),
    PREVIOUS("Semana Anterior", "+12.1% WoW", "R$ 112.200,00"),
    TARGET("Meta / Projeção", "+25.3% Alvo", "R$ 148.500,00")
}

data class EnrichedDailyPoint(
    val shortDay: String,
    val fullDay: String,
    val revenue: Double,
    val appointmentsCount: Int,
    val averageTicket: Double
)

/**
 * High-end Interactive Line Chart built with Jetpack Compose Canvas.
 *
 * Features:
 * - Smooth cubic Bézier spline curve in Champagne Gold gradient
 * - Shaded area fill beneath the curve fading into deep luxury black
 * - Y-axis dotted gridlines and reference thresholds
 * - Touch & drag interactive scrubber with glowing guideline beam
 * - Animated data point node with pulsing halo on touch
 * - Dynamic interactive tooltip with day details, revenue, and metrics
 * - Comparative period switcher (Semana Atual, Anterior, Meta)
 * - Interactive day chips row for precise tapping
 */
@Composable
fun InteractiveRevenueChart(
    points: List<DailyRevenuePoint>,
    modifier: Modifier = Modifier
) {
    var selectedPeriod by remember { mutableStateOf(ChartPeriod.CURRENT) }

    // Enriched dataset according to selected period
    val activePoints = remember(selectedPeriod, points) {
        when (selectedPeriod) {
            ChartPeriod.CURRENT -> {
                val fallbackDays = listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom")
                val fullDays = listOf("Segunda-feira", "Terça-feira", "Quarta-feira", "Quinta-feira", "Sexta-feira", "Sábado", "Domingo")
                val appointments = listOf(26, 31, 34, 38, 52, 62, 39)
                val base = if (points.isNotEmpty()) points else fallbackDays.map { DailyRevenuePoint(it, 15000.0) }
                base.mapIndexed { idx, pt ->
                    EnrichedDailyPoint(
                        shortDay = pt.day,
                        fullDay = fullDays.getOrElse(idx) { pt.day },
                        revenue = pt.revenue,
                        appointmentsCount = appointments.getOrElse(idx) { 30 },
                        averageTicket = if (appointments.getOrElse(idx) { 30 } > 0) pt.revenue / appointments.getOrElse(idx) { 30 } else 485.0
                    )
                }
            }
            ChartPeriod.PREVIOUS -> {
                listOf(
                    EnrichedDailyPoint("Seg", "Segunda-feira", 10800.0, 23, 469.0),
                    EnrichedDailyPoint("Ter", "Terça-feira", 12500.0, 27, 463.0),
                    EnrichedDailyPoint("Qua", "Quarta-feira", 14100.0, 30, 470.0),
                    EnrichedDailyPoint("Qui", "Quinta-feira", 15300.0, 32, 478.0),
                    EnrichedDailyPoint("Sex", "Sexta-feira", 21000.0, 44, 477.0),
                    EnrichedDailyPoint("Sáb", "Sábado", 24500.0, 50, 490.0),
                    EnrichedDailyPoint("Dom", "Domingo", 14000.0, 30, 466.0)
                )
            }
            ChartPeriod.TARGET -> {
                listOf(
                    EnrichedDailyPoint("Seg", "Segunda-feira", 14000.0, 28, 500.0),
                    EnrichedDailyPoint("Ter", "Terça-feira", 16000.0, 32, 500.0),
                    EnrichedDailyPoint("Qua", "Quarta-feira", 18000.0, 36, 500.0),
                    EnrichedDailyPoint("Qui", "Quinta-feira", 20000.0, 40, 500.0),
                    EnrichedDailyPoint("Sex", "Sexta-feira", 27000.0, 54, 500.0),
                    EnrichedDailyPoint("Sáb", "Sábado", 32000.0, 64, 500.0),
                    EnrichedDailyPoint("Dom", "Domingo", 21500.0, 43, 500.0)
                )
            }
        }
    }

    // Default selection to the peak day (Saturday / index 5)
    var selectedIndex by remember(activePoints) {
        val peakIndex = activePoints.indices.maxByOrNull { activePoints[it].revenue } ?: (activePoints.size - 1)
        mutableIntStateOf(peakIndex)
    }

    val selectedPoint = activePoints.getOrElse(selectedIndex) { activePoints.first() }
    val totalRevenue = remember(activePoints) { activePoints.sumOf { it.revenue } }
    val averageDailyRevenue = remember(activePoints) { totalRevenue / activePoints.size.coerceAtLeast(1) }
    val maxRevenue = remember(activePoints) { (activePoints.maxOfOrNull { it.revenue } ?: 30000.0) * 1.12 }
    val minRevenue = 0.0

    // Animation progress for chart entry & period switches
    var animationTarget by remember { mutableStateOf(0f) }
    LaunchedEffect(selectedPeriod) {
        animationTarget = 0f
        animationTarget = 1f
    }
    val chartProgress by animateFloatAsState(
        targetValue = animationTarget,
        animationSpec = tween(durationMillis = 850),
        label = "chart_curve_progress"
    )

    LuxuryCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("interactive_revenue_chart_card"),
        borderColor = GoldChampagne.copy(alpha = 0.65f)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 1. Header with Title, Icon and Growth Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0x26D4AF37)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShowChart,
                            contentDescription = "Gráfico de Faturamento",
                            tint = GoldChampagne,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "CRESCIMENTO DO FATURAMENTO",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = LuxurySerif,
                                color = GoldChampagne,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.1.sp
                            )
                        )
                        Text(
                            text = "Curva interativa com análise diária",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextDim,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                // Growth pill
                LuxuryBadge(
                    text = selectedPeriod.growthText,
                    isGold = true
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Period Switcher Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DeepBlackBackground)
                    .border(1.dp, DeepBlackBorder, RoundedCornerShape(10.dp))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ChartPeriod.entries.forEach { period ->
                    val isSelected = selectedPeriod == period
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) GoldChampagne else Color.Transparent)
                            .clickable { selectedPeriod = period }
                            .padding(vertical = 7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = period.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isSelected) DeepBlackBackground else TextMuted,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Interactive Highlight Metric Card (Tooltip Details)
            AnimatedContent(
                targetState = selectedPoint,
                transitionSpec = { fadeIn(tween(180)) togetherWith fadeOut(tween(150)) },
                label = "tooltip_content"
            ) { point ->
                val diffPercentage = ((point.revenue - averageDailyRevenue) / averageDailyRevenue * 100)
                val isPeak = point.revenue == activePoints.maxOf { it.revenue }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DeepBlackCardElevated)
                        .border(1.dp, GoldChampagne.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(GoldChampagne)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = point.fullDay.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = GoldLight,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp,
                                        fontSize = 11.sp
                                    )
                                )
                            }

                            if (isPeak) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0x33D4AF37))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Diamond,
                                        contentDescription = null,
                                        tint = GoldChampagne,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "PICO DA SEMANA",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = GoldChampagne,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            } else {
                                Text(
                                    text = if (diffPercentage >= 0) "+${String.format(Locale.US, "%.1f", diffPercentage)}% vs média"
                                           else "${String.format(Locale.US, "%.1f", diffPercentage)}% vs média",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (diffPercentage >= 0) SuccessEmerald else TextDim,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    text = "R$ ${String.format(Locale.GERMANY, "%,.2f", point.revenue)}",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontFamily = LuxurySerif,
                                        color = SophisticatedWhite,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Atendimentos",
                                        style = MaterialTheme.typography.bodySmall.copy(color = TextDim, fontSize = 10.sp)
                                    )
                                    Text(
                                        text = "${point.appointmentsCount} clientes",
                                        style = MaterialTheme.typography.bodySmall.copy(color = OffWhite, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Ticket Médio",
                                        style = MaterialTheme.typography.bodySmall.copy(color = TextDim, fontSize = 10.sp)
                                    )
                                    Text(
                                        text = "R$ ${String.format(Locale.GERMANY, "%.0f", point.averageTicket)}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = GoldChampagne, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Instruction Hint for touch / drag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TouchApp,
                        contentDescription = null,
                        tint = GoldChampagne.copy(alpha = 0.7f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Toque ou arraste para inspecionar os dias",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextDim,
                            fontSize = 10.sp
                        )
                    )
                }

                Text(
                    text = "Total: ${selectedPeriod.totalFormatted}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = GoldChampagne,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 5. JETPACK COMPOSE CANVAS - INTERACTIVE LINE CHART
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DeepBlackBackground)
                    .border(1.dp, DeepBlackBorder, RoundedCornerShape(12.dp))
                    .testTag("canvas_weekly_revenue_chart")
            ) {
                val chartWidth = constraints.maxWidth.toFloat()
                val chartHeight = constraints.maxHeight.toFloat()

                // Padding inside canvas
                val paddingLeft = 46f
                val paddingRight = 36f
                val paddingTop = 32f
                val paddingBottom = 40f

                val usableWidth = chartWidth - paddingLeft - paddingRight
                val usableHeight = chartHeight - paddingTop - paddingBottom

                // Calculate (x, y) coordinates for each point
                val count = activePoints.size
                val stepX = if (count > 1) usableWidth / (count - 1) else usableWidth

                val pointCoordinates = remember(activePoints, chartWidth, chartHeight, chartProgress) {
                    activePoints.mapIndexed { index, point ->
                        val x = paddingLeft + (index * stepX)
                        val normalizedVal = ((point.revenue - minRevenue) / (maxRevenue - minRevenue)).toFloat().coerceIn(0f, 1f)
                        val animatedNormalizedVal = normalizedVal * chartProgress
                        val y = (chartHeight - paddingBottom) - (animatedNormalizedVal * usableHeight)
                        Offset(x, y)
                    }
                }

                // Interactive Pointer Gestures (Drag & Tap)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(activePoints, usableWidth, count) {
                            detectTapGestures { tapOffset ->
                                val relativeX = tapOffset.x - paddingLeft
                                val tappedIndex = (relativeX / stepX + 0.5f).toInt().coerceIn(0, count - 1)
                                selectedIndex = tappedIndex
                            }
                        }
                        .pointerInput(activePoints, usableWidth, count) {
                            detectDragGestures { change, _ ->
                                change.consume()
                                val relativeX = change.position.x - paddingLeft
                                val draggedIndex = (relativeX / stepX + 0.5f).toInt().coerceIn(0, count - 1)
                                selectedIndex = draggedIndex
                            }
                        }
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // A. Dotted Reference Lines & Y-Axis Labels
                        val gridSteps = 3
                        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)

                        for (i in 0..gridSteps) {
                            val fraction = i / gridSteps.toFloat()
                            val lineY = paddingTop + (fraction * usableHeight)

                            // Horizontal subtle line
                            drawLine(
                                color = DeepBlackBorder.copy(alpha = 0.8f),
                                start = Offset(paddingLeft - 8f, lineY),
                                end = Offset(chartWidth - paddingRight + 8f, lineY),
                                strokeWidth = 1f,
                                pathEffect = dashEffect
                            )
                        }

                        if (pointCoordinates.size >= 2) {
                            // B. Cubic Bézier Curve Path
                            val curvePath = Path().apply {
                                moveTo(pointCoordinates.first().x, pointCoordinates.first().y)
                                for (i in 0 until pointCoordinates.size - 1) {
                                    val current = pointCoordinates[i]
                                    val next = pointCoordinates[i + 1]

                                    val controlPoint1 = Offset(
                                        x = current.x + (next.x - current.x) * 0.5f,
                                        y = current.y
                                    )
                                    val controlPoint2 = Offset(
                                        x = current.x + (next.x - current.x) * 0.5f,
                                        y = next.y
                                    )

                                    cubicTo(
                                        controlPoint1.x, controlPoint1.y,
                                        controlPoint2.x, controlPoint2.y,
                                        next.x, next.y
                                    )
                                }
                            }

                            // C. Shaded Gradient Fill Beneath the Curve
                            val fillPath = Path().apply {
                                addPath(curvePath)
                                lineTo(pointCoordinates.last().x, chartHeight - paddingBottom)
                                lineTo(pointCoordinates.first().x, chartHeight - paddingBottom)
                                close()
                            }

                            drawPath(
                                path = fillPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        GoldChampagne.copy(alpha = 0.32f),
                                        GoldChampagne.copy(alpha = 0.08f),
                                        Color.Transparent
                                    ),
                                    startY = paddingTop,
                                    endY = chartHeight - paddingBottom
                                )
                            )

                            // D. Glowing Golden Stroke Curve
                            drawPath(
                                path = curvePath,
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        GoldLight,
                                        GoldChampagne,
                                        GoldDark,
                                        GoldLight
                                    )
                                ),
                                style = Stroke(
                                    width = 3.5.dp.toPx(),
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )

                            // E. Data Points along the curve
                            pointCoordinates.forEachIndexed { idx, pt ->
                                val isSelected = idx == selectedIndex
                                if (!isSelected) {
                                    // Outer ring
                                    drawCircle(
                                        color = DeepBlackBackground,
                                        radius = 4.5.dp.toPx(),
                                        center = pt
                                    )
                                    drawCircle(
                                        color = GoldChampagne.copy(alpha = 0.85f),
                                        radius = 3.dp.toPx(),
                                        center = pt
                                    )
                                }
                            }

                            // F. Selected Point Interactive Indicator
                            if (selectedIndex in pointCoordinates.indices) {
                                val selectedCoord = pointCoordinates[selectedIndex]

                                // Vertical guideline beam
                                drawLine(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            GoldChampagne.copy(alpha = 0.8f),
                                            GoldChampagne.copy(alpha = 0.2f),
                                            Color.Transparent
                                        ),
                                        startY = selectedCoord.y,
                                        endY = chartHeight - paddingBottom
                                    ),
                                    start = Offset(selectedCoord.x, selectedCoord.y),
                                    end = Offset(selectedCoord.x, chartHeight - paddingBottom),
                                    strokeWidth = 2.dp.toPx(),
                                    pathEffect = dashEffect
                                )

                                // Outer Pulsing Halo
                                drawCircle(
                                    color = GoldChampagne.copy(alpha = 0.22f),
                                    radius = 16.dp.toPx(),
                                    center = selectedCoord
                                )
                                // Middle glowing ring
                                drawCircle(
                                    color = GoldChampagne,
                                    radius = 7.dp.toPx(),
                                    center = selectedCoord
                                )
                                // Inner white luxury core
                                drawCircle(
                                    color = SophisticatedWhite,
                                    radius = 3.5.dp.toPx(),
                                    center = selectedCoord
                                )
                            }
                        }
                    }

                    // G. Y-Axis Reference Value Labels Overlay
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                start = 8.dp,
                                top = (paddingTop / 2.75).dp,
                                bottom = (paddingBottom / 2.75).dp
                            ),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        val maxLabel = "${(maxRevenue / 1000).toInt()}k"
                        val midHighLabel = "${(maxRevenue * 0.66 / 1000).toInt()}k"
                        val midLowLabel = "${(maxRevenue * 0.33 / 1000).toInt()}k"

                        Text(text = maxLabel, style = MaterialTheme.typography.bodySmall.copy(color = TextDim, fontSize = 9.sp))
                        Text(text = midHighLabel, style = MaterialTheme.typography.bodySmall.copy(color = TextDim, fontSize = 9.sp))
                        Text(text = midLowLabel, style = MaterialTheme.typography.bodySmall.copy(color = TextDim, fontSize = 9.sp))
                        Text(text = "0", style = MaterialTheme.typography.bodySmall.copy(color = TextDim, fontSize = 9.sp))
                    }

                    // H. X-Axis Day Labels Overlay
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(
                                start = (paddingLeft / 2.75).dp,
                                end = (paddingRight / 2.75).dp,
                                bottom = 8.dp
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        activePoints.forEachIndexed { idx, point ->
                            val isSelected = idx == selectedIndex
                            Text(
                                text = point.shortDay,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isSelected) GoldChampagne else TextDim,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.clickable { selectedIndex = idx }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 6. Interactive Day Selector Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                activePoints.forEachIndexed { index, point ->
                    val isSelected = index == selectedIndex
                    val isPeak = point.revenue == activePoints.maxOf { it.revenue }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when {
                                    isSelected -> GoldChampagne
                                    isPeak -> Color(0x1AD4AF37)
                                    else -> DeepBlackCard
                                }
                            )
                            .border(
                                width = 1.dp,
                                color = when {
                                    isSelected -> GoldChampagne
                                    isPeak -> GoldChampagne.copy(alpha = 0.4f)
                                    else -> DeepBlackBorder
                                },
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedIndex = index }
                            .padding(vertical = 6.dp)
                    ) {
                        Text(
                            text = point.shortDay,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isSelected) DeepBlackBackground else OffWhite,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 10.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${(point.revenue / 1000).toInt()}k",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isSelected) DeepBlackBackground else GoldChampagne,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        )
                    }

                    if (index < activePoints.size - 1) {
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }
            }
        }
    }
}
