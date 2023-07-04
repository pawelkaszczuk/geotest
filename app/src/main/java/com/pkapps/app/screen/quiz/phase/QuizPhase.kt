package com.pkapps.app.screen.quiz.phase

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pkapps.R
import com.pkapps.app.common.compose.CountryFlag
import com.pkapps.app.screen.quiz.QuizUiState
import com.pkapps.ui.domain.model.Country
import com.pkapps.ui.domain.model.QuizQuestion

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun QuizPhase(
    data: QuizUiState.QuizPhase,
    onSelectAnswer: (QuizQuestion, Country) -> Unit,
    onNext: () -> Unit = { },
) {
    val pagerState = rememberPagerState(initialPage = data.currentQuestion)
    LaunchedEffect(data.currentQuestion) {
        pagerState.animateScrollToPage(data.currentQuestion)
    }

    val progress by animateFloatAsState(data.progress.toFloat())
    LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth())

    HorizontalPager(
        state = pagerState,
        userScrollEnabled = false,
        pageCount = data.quiz.questions.count()
    ) {
        val question = data.quiz.questions[it]
        QuestionCard(
            question = question,
            selected = data.pickedAnswers[question],
            onSelectAnswer = onSelectAnswer,
            onNext = onNext
        )
    }
}

@Composable
private fun QuestionCard(
    question: QuizQuestion,
    selected: Country? = null,
    onSelectAnswer: (QuizQuestion, Country) -> Unit = { _, _ -> },
    onNext: () -> Unit = { },
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {

            val configuration = LocalConfiguration.current
            when (configuration.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> {
                    Row() {
                        CountryFlag(
                            country = question.correctAnswer,
                            modifier = Modifier
                                .height(200.dp)
                                .aspectRatio(1.5f)
                        )
                        Spacer(Modifier.padding(8.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            ButtonArea(question, selected, onSelectAnswer, onNext)
                        }
                    }
                }

                else -> {
                    CountryFlag(
                        country = question.correctAnswer,
                        modifier = Modifier.fillMaxWidth()
                    )

                    ButtonArea(question, selected, onSelectAnswer, onNext)
                }
            }

        }
    }
}

@Composable
private fun ButtonArea(
    question: QuizQuestion,
    selected: Country? = null,
    onSelectAnswer: (QuizQuestion, Country) -> Unit = { _, _ -> },
    onNext: () -> Unit = { },
) {
    for (country in question.options) {
        Button(
            onClick = { onSelectAnswer(question, country) },
            colors = QuestionButtonColor(selected, country, question.correctAnswer),
            enabled = selected == null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(country.name)
        }
    }

    Spacer(Modifier.height(ButtonDefaults.MinHeight))

    Column(Modifier.height(ButtonDefaults.MinHeight)) {
        AnimatedVisibility(
            enter = fadeIn(),
            exit = fadeOut(),
            visible = selected != null
        ) {
            Button(onClick = onNext) {
                Text(stringResource(R.string.next))
            }
        }
    }
}

@Composable
private fun QuestionButtonColor(
    selected: Country?,
    country: Country,
    correctAnswer: Country
): ButtonColors {

    return when (country) {
        correctAnswer ->
            ButtonDefaults.buttonColors(
                disabledContentColor = MaterialTheme.colorScheme.onSecondary,
                disabledContainerColor = MaterialTheme.colorScheme.secondary
            )

        selected ->
            ButtonDefaults.buttonColors(
                disabledContentColor = MaterialTheme.colorScheme.onError,
                disabledContainerColor = MaterialTheme.colorScheme.error
            )

        else ->
            ButtonDefaults.buttonColors()
    }
}