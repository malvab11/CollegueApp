package com.example.collegueapp.presentation.school

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.collegueapp.resources.Res
import com.example.collegueapp.resources.ic_logo
import org.jetbrains.compose.resources.painterResource

internal const val SchoolName = "Colegio Albert Einstein"
internal const val SchoolLocation = "Celendín"

/** The institutional crest keeps its original colors on a white backing in both themes. */
@Composable
internal fun SchoolLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(Res.drawable.ic_logo),
        contentDescription = "Escudo del Colegio Albert Einstein de Celendín",
        modifier = modifier.clip(RoundedCornerShape(16.dp)).background(Color.White).padding(4.dp),
        contentScale = ContentScale.Fit
    )
}
