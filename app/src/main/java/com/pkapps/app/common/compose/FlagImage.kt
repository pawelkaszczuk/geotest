package com.pkapps.app.common.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.pkapps.ui.domain.model.Country

@Composable
fun CountryFlag(
    country: Country,
    smallRoundedCorners: Boolean = false,
    modifier: Modifier
) {
    val roundedCornerDp = if (smallRoundedCorners) 4.dp else 16.dp
    AsyncImage(
        model = country.flag,
        contentDescription = country.flagDescription,
        modifier = modifier
            .aspectRatio(1.6f)
            .clip(RoundedCornerShape(roundedCornerDp))
            .background(MaterialTheme.colorScheme.background)
            .border(
                BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                RoundedCornerShape(roundedCornerDp)
            )
    )
}
