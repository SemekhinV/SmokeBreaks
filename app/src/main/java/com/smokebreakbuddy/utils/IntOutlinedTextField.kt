package com.smokebreakbuddy.utils

import android.annotation.SuppressLint
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import kotlin.text.toIntOrNull

@Composable
fun IntOutlinedTextField(
    label: String,
    initialValue: Int = 0, // Начальное значение Int
    onValueChange: (Int?) -> Unit, // Лямбда для получения Int? (null если некорректно)
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    minValue: Int = Int.MIN_VALUE,
    maxValue: Int = Int.MAX_VALUE
) {
    var textValue by remember { mutableStateOf(initialValue.toString()) }
    var isError by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = textValue,
        onValueChange = { newText ->
            textValue = newText
            val intValue = newText.toIntOrNull()
            isError = if (newText.isEmpty()) { // Разрешить пустое поле, если нужно
                onValueChange(null) // Или передать 0, или какое-то значение по умолчанию
                false // Не ошибка, если пустое поле допустимо и будет обработано
            } else if (intValue != null) {
                if (intValue in minValue..maxValue) {
                    onValueChange(intValue)
                    false
                } else {
                    onValueChange(null) // Значение вне диапазона
                    true // Ошибка, если вне диапазона
                }
            } else {
                onValueChange(null) // Не удалось преобразовать в Int
                true // Ошибка, если не число
            }
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        isError = isError,
        modifier = modifier,
        supportingText = {
            if (isError && textValue.isNotEmpty()) {
                val intValue = textValue.toIntOrNull()
                if (intValue == null) {
                    Text("Please enter a valid number")
                } else if (intValue < minValue) {
                    Text("Value must be at least $minValue")
                } else if (intValue > maxValue) {
                    Text("Value must not exceed $maxValue")
                }
            }
        }
    )
}