package com.pkapps.app.screen.quiz

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pkapps.app.common.compose.Loading
import com.pkapps.app.common.compose.NetworkErrorScreen
import com.pkapps.app.screen.quiz.phase.QuizPhase
import com.pkapps.app.screen.quiz.phase.ResultsPhase


@Composable
fun QuizScreen(
    vm: QuizViewModel = hiltViewModel(),
    onStartAgain: () -> Unit
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is QuizUiState.Loading -> Loading()
        is QuizUiState.QuizPhase -> QuizPhase(state, vm::pickAnswer, vm::onNext)
        is QuizUiState.ResultsPhase -> ResultsPhase(state, onStartAgain)
        is QuizUiState.NetworkError -> NetworkErrorScreen(vm::load)
    }
}