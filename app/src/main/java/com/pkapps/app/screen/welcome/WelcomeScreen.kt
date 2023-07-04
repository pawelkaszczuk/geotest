package com.pkapps.app.screen.welcome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pkapps.R
import com.pkapps.app.common.compose.CountryFlag
import com.pkapps.app.common.compose.Loading
import com.pkapps.app.common.compose.NetworkErrorScreen

@Composable
fun WelcomeScreen(
    vm: WelcomeViewModel = hiltViewModel(),
    onChangeCountry: () -> Unit,
    onContinue: () -> Unit,
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is WelcomeUiState.Loading -> Loading()
        is WelcomeUiState.Loaded -> Loaded(state, onChangeCountry, onContinue)
        is WelcomeUiState.NetworkError -> NetworkErrorScreen(vm::load)
    }
}

@Composable
private fun Loaded(
    data: WelcomeUiState.Loaded,
    onChangeCountry: () -> Unit,
    onContinue: () -> Unit
) {

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {

        Text(
            text = stringResource(R.string.geo_test),
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(4.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(.8f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(4.dp)
            ) {
                Text(stringResource(R.string.your_country), Modifier.padding(4.dp))

                CountryFlag(
                    country = data.country,
                    modifier = Modifier
                        .height(100.dp)
                        .padding(4.dp)
                )

                Text(
                    text = data.country.name,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(Modifier.size(8.dp))

                Row(Modifier.fillMaxWidth()) {
                    TextButton(onClick = onChangeCountry, Modifier.weight(1f)) {
                        Text(text = stringResource(R.string.change_country))
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = onContinue, Modifier.weight(1f)) {
                        Text(text = stringResource(R.string.start))
                    }
                }
            }
        }
    }
}
