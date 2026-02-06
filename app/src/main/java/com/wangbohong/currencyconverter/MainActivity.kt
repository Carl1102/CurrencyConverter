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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wangbohong.currencyconverter.ui.theme.CurrencyConverterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CurrencyConverterTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ConverterScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
fun ConverterScreen(modifier: Modifier = Modifier) {
    var audText by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }

    val currencies = listOf("USD", "EUR", "CNY")
    var selectedCurrency by remember { mutableStateOf("USD") }

    fun getRate(currency: String): Double {
        return when (currency) {
            "USD" -> 0.66
            "EUR" -> 0.61
            "CNY" -> 4.75
            else -> 1.0
        }
    }

    val audValue = audText.toDoubleOrNull()
    val rate = getRate(selectedCurrency)
    val resultText = if (audValue == null) "" else "%.2f".format(audValue * rate)

    LaunchedEffect(audText, selectedCurrency) {
        errorText = when {
            audText.isBlank() -> ""
            audValue == null -> "Please enter a valid number."
            else -> ""
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Currency Converter",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = audText,
            onValueChange = { audText = it },
            label = { Text("Amount (AUD)") },
            singleLine = true,
            supportingText = {
                if (errorText.isNotEmpty()) Text(text = errorText)
            }
        )

        Text(text = "Convert to:")

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            currencies.forEach { currency ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedCurrency == currency,
                        onClick = { selectedCurrency = currency }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = currency)
                }
            }
        }

        Text(text = "Rate: 1 AUD = %.4f %s".format(rate, selectedCurrency))

        if (audText.isNotBlank() && audValue != null) {
            Text(
                text = "Result: $selectedCurrency $resultText",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Preview(showBackground = true)
@androidx.compose.runtime.Composable
fun ConverterScreenPreview() {
    CurrencyConverterTheme {
        ConverterScreen()
    }
}