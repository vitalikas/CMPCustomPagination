package lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.order

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import lt.vitalijus.cmp_custom_pagination.core.utils.formatPrice
import lt.vitalijus.cmp_custom_pagination.domain.model.PaymentMethod
import lt.vitalijus.cmp_custom_pagination.domain.model.PaymentType

@Composable
fun PaymentScreen(
    totalAmount: Double,
    onConfirmPayment: (PaymentMethod) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPaymentType by remember { mutableStateOf(PaymentType.CREDIT_CARD) }
    var cardNumber by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Payment Method",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // Order Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Order Summary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Amount:")
                    Text(
                        text = formatPrice(totalAmount),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Payment Type Selection
        Column(modifier = Modifier.selectableGroup()) {
            Text(
                text = "Select Payment Type:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            PaymentType.entries.forEach { type ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedPaymentType == type,
                        onClick = { selectedPaymentType = type }
                    )
                    Text(
                        text = when (type) {
                            PaymentType.CREDIT_CARD -> "Credit Card"
                            PaymentType.DEBIT_CARD -> "Debit Card"
                            PaymentType.CASH_ON_DELIVERY -> "Cash on Delivery"
                        },
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        // Card Details (only if card payment is selected)
        if (selectedPaymentType == PaymentType.CREDIT_CARD || selectedPaymentType == PaymentType.DEBIT_CARD) {
            OutlinedTextField(
                value = cardNumber,
                onValueChange = { if (it.length <= 16) cardNumber = it },
                label = { Text("Card Number") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                placeholder = { Text("1234 5678 9012 3456") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val lastFourDigits =
                    if (selectedPaymentType != PaymentType.CASH_ON_DELIVERY && cardNumber.length >= 4) {
                        cardNumber.takeLast(4)
                    } else null

                onConfirmPayment(
                    PaymentMethod(
                        type = selectedPaymentType,
                        cardLastFourDigits = lastFourDigits
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = when (selectedPaymentType) {
                PaymentType.CASH_ON_DELIVERY -> true
                else -> cardNumber.length >= 16
            }
        ) {
            Text("Confirm Order")
        }
    }
}
