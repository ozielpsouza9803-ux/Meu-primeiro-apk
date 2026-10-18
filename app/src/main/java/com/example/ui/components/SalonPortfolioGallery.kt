package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.PortfolioItem
import com.example.ui.theme.DeepBlackCard
import com.example.ui.theme.GoldChampagne
import com.example.ui.theme.LuxurySerif
import com.example.ui.theme.SophisticatedWhite
import com.example.ui.theme.TextDim

@Composable
fun SalonPortfolioGallery(
    portfolioItems: List<PortfolioItem>,
    modifier: Modifier = Modifier
) {
    if (portfolioItems.isEmpty()) return

    var selectedVideoItem by remember { mutableStateOf<PortfolioItem?>(null) }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        SectionHeader(
            title = "GALERIA DE FOTOS & VÍDEOS",
            subtitle = "Portfólio em alta definição de nossos serviços e tratamentos VIP",
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("portfolio_gallery_row"),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(portfolioItems) { item ->
                PortfolioCard(
                    item = item,
                    onClick = {
                        if (item.mediaType == "VIDEO") {
                            selectedVideoItem = item
                        }
                    }
                )
            }
        }
    }

    selectedVideoItem?.let { video ->
        VideoPlayerDialog(
            videoUrl = video.imageUrl,
            title = video.title,
            onDismiss = { selectedVideoItem = null }
        )
    }
}

@Composable
fun PortfolioCard(
    item: PortfolioItem,
    onClick: () -> Unit
) {
    val isVideo = item.mediaType == "VIDEO"

    Card(
        modifier = Modifier
            .width(280.dp)
            .height(360.dp)
            .border(BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.3f)), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DeepBlackCard)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.85f)
                            ),
                            startY = 200f
                        )
                    )
            )

            if (isVideo) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(GoldChampagne.copy(alpha = 0.9f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Reproduzir Vídeo",
                            tint = Color.Black,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LuxuryBadge(text = if (isVideo) "VÍDEO VIP" else item.category, isGold = true)
                }

                Column {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = SophisticatedWhite,
                            fontWeight = FontWeight.Bold,
                            fontFamily = LuxurySerif
                        ),
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Por ${item.stylistName}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = GoldChampagne,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextDim,
                            fontSize = 11.sp
                        ),
                        maxLines = 2
                    )
                }
            }
        }
    }
}
