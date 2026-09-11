package com.example.ch04starter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ch04starter.ui.theme.Ch04StarterTheme

@Composable
fun BmiScreen() {
    var beratKg    by rememberSaveable { mutableFloatStateOf(60f) }
    var tinggiCm   by rememberSaveable { mutableFloatStateOf(165f) }
    var isDihitung by rememberSaveable { mutableStateOf(false) }

    val bmi by remember {
        derivedStateOf {
            val tinggiM = tinggiCm / 100f
            beratKg / (tinggiM * tinggiM)
        }
    }

    val (kategori, warna) = remember(bmi) {
        when {
            bmi < 18.5f -> "Berat Badan Kurang" to Color(0xFF1565C0)
            bmi < 25.0f -> "Berat Badan Normal" to Color(0xFF2E7D32)
            bmi < 30.0f -> "Kelebihan Berat"    to Color(0xFFE65100)
            else        -> "Obesitas"            to Color(0xFFC62828)
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(kategori) {
        if (isDihitung) {
            snackbarHostState.showSnackbar(kategori)
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { innerPadding ->
        Column(
            modifier            = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text  = "Kalkulator BMI",
                style = MaterialTheme.typography.headlineMedium
            )

            InputSlider(
                label         = "Berat Badan",
                value         = beratKg,
                unit          = "kg",
                range         = 30f..150f,
                onValueChange = { beratKg = it; isDihitung = false }
            )

            InputSlider(
                label         = "Tinggi Badan",
                value         = tinggiCm,
                unit          = "cm",
                range         = 100f..220f,
                onValueChange = { tinggiCm = it; isDihitung = false }
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick  = { isDihitung = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Hitung BMI")
                }

                OutlinedButton(
                    onClick  = {
                        beratKg = 60f
                        tinggiCm = 165f
                        isDihitung = false
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reset")
                }
            }

            AnimatedVisibility(visible = isDihitung) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors   = CardDefaults.cardColors(
                            containerColor = warna.copy(alpha = 0.1f)
                        )
                    ) {
                        Column(
                            modifier            = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text  = "${"%.1f".format(bmi)}",
                                style = MaterialTheme.typography.displayMedium,
                                color = warna
                            )
                            Text(
                                text  = kategori,
                                style = MaterialTheme.typography.titleMedium,
                                color = warna
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text  = "Berat: ${"%.0f".format(beratKg)} kg  " +
                                        "Tinggi: ${"%.0f".format(tinggiCm)} cm",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    InterpretasiBmiTable()
                }
            }
        }
    }
}

@Composable
fun InputSlider(
    label:         String,
    value:         Float,
    unit:          String,
    range:         ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodyLarge)
            Text(
                text       = "${"%.0f".format(value)} $unit",
                style      = MaterialTheme.typography.bodyLarge,
                color      = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
        Slider(
            value         = value,
            onValueChange = onValueChange,
            valueRange    = range,
            modifier      = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun InterpretasiBmiTable() {
    Column(
        modifier            = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text  = "Interpretasi BMI",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Kurus",      style = MaterialTheme.typography.bodyMedium)
            Text("< 18.5",     style = MaterialTheme.typography.bodyMedium, color = Color(0xFF1565C0))
        }
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Normal",     style = MaterialTheme.typography.bodyMedium)
            Text("18.5 – 24.9", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF2E7D32))
        }
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Gemuk",      style = MaterialTheme.typography.bodyMedium)
            Text("25 – 29.9",  style = MaterialTheme.typography.bodyMedium, color = Color(0xFFE65100))
        }
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Obesitas",   style = MaterialTheme.typography.bodyMedium)
            Text("≥ 30",       style = MaterialTheme.typography.bodyMedium, color = Color(0xFFC62828))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BmiScreenPreview() {
    Ch04StarterTheme { BmiScreen() }
}
