package com.pkapps.app.screen.countrypicker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pkapps.R
import com.pkapps.app.common.compose.CountryFlag
import com.pkapps.app.common.compose.Loading
import com.pkapps.app.common.compose.NetworkErrorScreen
import com.pkapps.ui.domain.model.Country

@Composable
fun CountryPickerScreen(
    vm: CountryPickerViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNewPick: () -> Unit,
) {
    val uiState by vm.uiState.collectAsState()

    when (val state = uiState) {
        is CountryPickerUiState.Loading -> Loading()
        is CountryPickerUiState.Loaded -> Loaded(
            data = state,
            onBack = onBack,
            onToggleSearch = vm::toggleSearch,
            onClearSearch = vm::clearSearch,
            onSelect = vm::select,
            updateSearchQuery = vm::updateSearchQuery,
            onConfirmChoice = vm::confirmChoice
        )

        is CountryPickerUiState.NetworkError -> NetworkErrorScreen(vm::load)
        is CountryPickerUiState.ConfirmedChoice -> LaunchedEffect(state) { onNewPick() }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun Loaded(
    data: CountryPickerUiState.Loaded,
    onBack: () -> Unit,
    onToggleSearch: () -> Unit,
    onClearSearch: () -> Unit,
    onSelect: (Country) -> Unit,
    updateSearchQuery: (String) -> Unit,
    onConfirmChoice: () -> Unit,
) {
    Scaffold(
        topBar = {
            CountryPickerTopBar(
                data = data,
                onBack = onBack,
                onToggleSearch = onToggleSearch,
                onClearSearch = onClearSearch,
                updateSearchQuery = updateSearchQuery
            )
        },
        floatingActionButton = {
            if (data.selected != null) {
                FloatingActionButton(onClick = onConfirmChoice) {
                    Icon(Icons.Filled.Done, stringResource(R.string.done))
                }
            }
        }

    ) { padding ->
        val keyboardController = LocalSoftwareKeyboardController.current
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(data.countries) { country ->
                CountryRow(country, country == data.selected, Modifier.clickable {
                    keyboardController?.hide()
                    onSelect(country)
                })
            }
        }
    }


}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CountryPickerTopBar(
    data: CountryPickerUiState.Loaded,
    onBack: () -> Unit,
    onToggleSearch: () -> Unit,
    onClearSearch: () -> Unit,
    updateSearchQuery: (String) -> Unit
) {
    TopAppBar(
        title = {
            val focusRequester = remember { FocusRequester() }

            if (data.showSearch) {
                TextField(
                    singleLine = true,
                    label = { Text(stringResource(R.string.pick_a_country)) },
                    placeholder = { Text(stringResource(R.string.search_by_name)) },
                    value = data.searchQuery,
                    onValueChange = updateSearchQuery,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    trailingIcon = {
                        if (data.searchQuery != "") {
                            IconButton(onClick = onClearSearch) {
                                Icon(Icons.Filled.Cancel, null)
                            }
                        }
                    }
                )
            } else {
                Text(stringResource(R.string.pick_a_country), Modifier
                    .fillMaxWidth()
                    .clickable { onToggleSearch() })
            }

            LaunchedEffect(data.showSearch) {
                if (data.showSearch) {
                    focusRequester.requestFocus()
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, stringResource(R.string.arrow_back))
            }
        },
        actions = {
            IconButton(onClick = onToggleSearch) {
                val vector = if (data.showSearch) Icons.Filled.FilterAlt else Icons.Outlined.FilterAlt
                Icon(vector, stringResource(R.string.toggle_search))
            }
        }
    )
}


@Composable
fun CountryRow(country: Country, isSelected: Boolean = false, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
            .padding(8.dp, 4.dp)
            .fillMaxWidth()
    ) {
        CountryFlag(
            country = country,
            smallRoundedCorners = true,
            modifier = Modifier.width(64.dp)
        )
        Text(
            country.name,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier
                .padding(8.dp)
        )
    }
}