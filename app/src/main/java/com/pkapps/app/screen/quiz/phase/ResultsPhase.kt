package com.pkapps.app.screen.quiz.phase

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pkapps.R
import com.pkapps.app.screen.quiz.QuizUiState

@Composable
fun ResultsPhase(
    data: QuizUiState.ResultsPhase,
    onStartAgain: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.results),
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(16.dp)
        )

        val correctColor = MaterialTheme.colorScheme.secondary
        val errorColor = MaterialTheme.colorScheme.error

        Canvas(
            Modifier
                .height(48.dp)
                .fillMaxWidth()
        ) {
            val correctWidth = size.width * data.results.correctRatio.toFloat()
            val errorWidth = size.width - correctWidth

            drawRect(correctColor, size = Size(correctWidth, size.height))
            drawRect(
                errorColor,
                topLeft = Offset(correctWidth, 0f),
                size = Size(errorWidth, size.height)
            )
        }

        Row {
            Text(
                stringResource(R.string.correct, data.results.correctAnswers),
                color = correctColor
            )
            Spacer(Modifier.weight(1f))
            Text(stringResource(R.string.wrong, data.results.wrongAnswers), color = errorColor)
        }

        Spacer(Modifier.height(64.dp))

        Button(onClick = onStartAgain) {
            Text(stringResource(R.string.start_again))
        }
    }
}