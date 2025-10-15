package com.example.nxtbus.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.nxtbus.R

@Composable
fun LottieSuccessAnimation(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    loop: Boolean = false,
    speed: Float = 1.0f,
) {
    val compositionResult = rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.success_animation)
    )
    val iterations = if (loop) LottieConstants.IterateForever else 1
    val animationState = animateLottieCompositionAsState(
        composition = compositionResult.value,
        isPlaying = true,
        iterations = iterations,
        speed = speed
    )
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = compositionResult.value,
            progress = { animationState.value }
        )
    }
}
