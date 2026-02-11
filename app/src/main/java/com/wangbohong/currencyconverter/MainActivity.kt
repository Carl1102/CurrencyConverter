package com.wangbohong.currencyconverter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

private enum class AppScreen { CONVERTER, SETTINGS }

private val exchangeRates = mapOf(
    "USD" to 0.66,
    "EUR" to 0.61,
    "CNY" to 4.75
)

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CurrencyConverterTheme {
                var screen by rememberSaveable { mutableStateOf(AppScreen.CONVERTER) }

                val currencies = exchangeRates.keys.toList()

                var defaultCurrency by rememberSaveable { mutableStateOf("USD") }
                var decimalPlaces by rememberSaveable { mutableIntStateOf(2) }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(if (screen == AppScreen.CONVERTER) "Currency Converter" else "Settings") },
                            navigationIcon = {
                                if (screen == AppScreen.SETTINGS) {
                                    IconButton(onClick = { screen = AppScreen.CONVERTER }) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                                    }
                                }
                            },
                            actions = {
                                if (screen == AppScreen.CONVERTER) {
                                    IconButton(onClick = { screen = AppScreen.SETTINGS }) {
                                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                                    }
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    when (screen) {
                        AppScreen.CONVERTER -> ConverterScreen(
                            modifier = Modifier.padding(innerPadding),
                            currencies = currencies,
                            defaultCurrency = defaultCurrency,
                            decimalPlaces = decimalPlaces
                        )

                        AppScreen.SETTINGS -> SettingsScreen(
                            modifier = Modifier.padding(innerPadding),
                            currencies = currencies,
                            defaultCurrency = defaultCurrency,
                            onDefaultCurrencyChange = { defaultCurrency = it },
                            decimalPlaces = decimalPlaces,
                            onDecimalPlacesChange = { decimalPlaces = it }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConverterScreen(
    modifier: Modifier = Modifier,
    currencies: List<String>,
    defaultCurrency: String,
    decimalPlaces: Int
) {
    var audText by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }

    var selectedCurrency by rememberSaveable { mutableStateOf(defaultCurrency) }

    LaunchedEffect(defaultCurrency) {
        selectedCurrency = defaultCurrency
    }

    val audValue = audText.toDoubleOrNull()
    val rate = exchangeRates[selectedCurrency] ?: 1.0

    LaunchedEffect(audText) {
        errorText = when {
            audText.isBlank() -> ""
            audValue == null -> "Please enter a valid number."
            else -> ""
        }
    }

    val resultText = if (audValue == null) "" else {
        val converted = audValue * rate
        "%.${decimalPlaces}f".format(converted)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Enter AUD, then choose a currency", style = MaterialTheme.typography.bodyMedium)

        OutlinedTextField(
            value = audText,
            onValueChange = { audText = it },
            label = { Text("Amount (AUD)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            supportingText = { if (errorText.isNotEmpty()) Text(errorText) }
        )

        Text("Convert to:")

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            currencies.forEach { currency ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedCurrency == currency,
                        onClick = { selectedCurrency = currency }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(currency)
                }
            }
        }

        Text("Rate: 1 AUD = %.4f %s".format(rate, selectedCurrency))

        if (audText.isNotBlank() && audValue != null) {
            Text(
                "Result: $selectedCurrency $resultText",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    currencies: List<String>,
    defaultCurrency: String,
    onDefaultCurrencyChange: (String) -> Unit,
    decimalPlaces: Int,
    onDecimalPlacesChange: (Int) -> Unit
) {
    val decimalOptions = listOf(0, 1, 2, 3, 4)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Default target currency", style = MaterialTheme.typography.titleMedium)

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            currencies.forEach { currency ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = defaultCurrency == currency,
                        onClick = { onDefaultCurrencyChange(currency) }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(currency)
                }
            }
        }

        HorizontalDivider()

        Text("Decimal places", style = MaterialTheme.typography.titleMedium)

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            decimalOptions.forEach { d ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = decimalPlaces == d,
                        onClick = { onDecimalPlacesChange(d) }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("$d")
                }
            }
        }

        Text(
            "These settings apply to the conversion result formatting.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}