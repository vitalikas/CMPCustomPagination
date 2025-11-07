package lt.vitalijus.cmp_custom_pagination.presentation.products.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun QuantityControl(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    buttonSize: Dp = 32.dp,
    textFieldWidth: Dp = 64.dp,
    textFieldHeight: Dp = 48.dp,
    minQuantity: Int = 1,
    maxQuantity: Int = 999,
    buttonBackgroundColor: Color? = null
) {
    var quantityText by remember(quantity) { mutableStateOf(quantity.toString()) }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = {
                if (quantity > minQuantity) {
                    val newQuantity = quantity - 1
                    quantityText = newQuantity.toString()
                    onQuantityChange(newQuantity)
                }
            },
            modifier = Modifier.size(buttonSize),
            contentPadding = PaddingValues(0.dp),
            colors = if (buttonBackgroundColor != null) {
                ButtonDefaults.buttonColors(
                    containerColor = buttonBackgroundColor,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            } else {
                ButtonDefaults.buttonColors()
            }
        ) {
            Text("-", style = MaterialTheme.typography.titleMedium)
        }

        OutlinedTextField(
            value = quantityText,
            onValueChange = { newValue ->
                if (newValue.isEmpty()) {
                    quantityText = ""
                    onQuantityChange(minQuantity)
                } else {
                    val parsed = newValue.toIntOrNull()
                    if (parsed != null && parsed >= minQuantity && parsed <= maxQuantity) {
                        quantityText = newValue
                        onQuantityChange(parsed)
                    }
                }
            },
            modifier = Modifier
                .width(textFieldWidth)
                .height(textFieldHeight),
            textStyle = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Button(
            onClick = {
                if (quantity < maxQuantity) {
                    val newQuantity = quantity + 1
                    quantityText = newQuantity.toString()
                    onQuantityChange(newQuantity)
                }
            },
            modifier = Modifier.size(buttonSize),
            contentPadding = PaddingValues(0.dp),
            colors = if (buttonBackgroundColor != null) {
                ButtonDefaults.buttonColors(
                    containerColor = buttonBackgroundColor,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            } else {
                ButtonDefaults.buttonColors()
            }
        ) {
            Text("+", style = MaterialTheme.typography.titleMedium)
        }
    }
}
