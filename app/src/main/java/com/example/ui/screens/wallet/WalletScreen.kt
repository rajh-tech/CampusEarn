package com.example.ui.screens.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.data.repository.CampusEarnRepository
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    repository: CampusEarnRepository
) {
    val currentUser by repository.currentUser.collectAsState()
    val payments by repository.payments.collectAsState()

    val myPayments = payments.filter { it.receiverId == currentUser.id }
    val totalEarned = myPayments.filter { it.status == PaymentStatus.PAID }.sumOf { it.amount }
    val pendingAmount = myPayments.filter { it.status == PaymentStatus.PENDING }.sumOf { it.amount }
    val processingPayouts = myPayments.filter { it.status == PaymentStatus.PROCESSING }.sumOf { it.amount }
    val availableBalance = (totalEarned - processingPayouts).coerceAtLeast(0.0)

    var showPayoutSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Top Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Student Earnings Ledger",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Slate900)
                )
                Text(
                    text = "Transparent escrow ledger. Earnings in Indian Rupees (INR ₹).",
                    style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main Balance Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DeepBluePrimary)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Available for Payout",
                                style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFF93C5FD))
                            )
                            Surface(
                                color = Color(0x33FFFFFF),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF86EFAC), modifier = Modifier.size(14.dp))
                                    Text("Zero Fee Guarantee", style = MaterialTheme.typography.labelSmall.copy(color = Color.White))
                                }
                            }
                        }

                        Text(
                            text = "₹${availableBalance.toInt()}",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        )

                        // Pending & Processing breakdown
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("In Escrow Pending", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFCBD5E1)))
                                Text("₹${pendingAmount.toInt()}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = AmberLight))
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Processing Payouts", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFCBD5E1)))
                                Text("₹${processingPayouts.toInt()}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PurpleLight))
                            }
                        }

                        Button(
                            onClick = { showPayoutSheet = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = availableBalance >= 100.0
                        ) {
                            Icon(Icons.Default.AccountBalance, contentDescription = null, tint = DeepBluePrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (availableBalance >= 100) "Request Payout (UPI / Bank)" else "Min Payout ₹100",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = DeepBluePrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }

            // Trust & Payout Notice
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate200))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Slate600)
                        Text(
                            text = "CampusEarn operates an authentic audit ledger. Funds are disbursed to your verified Indian UPI ID or Bank account within 24 hours of employer task approval.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate600)
                        )
                    }
                }
            }

            // Transaction History Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Transaction History",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Slate900)
                    )
                    Text(
                        text = "${myPayments.size} records",
                        style = MaterialTheme.typography.labelSmall.copy(color = Slate500)
                    )
                }
            }

            // Payment items
            items(myPayments) { payment ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate200))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = payment.jobTitle,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Slate900),
                                modifier = Modifier.weight(1f)
                            )
                            StatusBadge(status = payment.status.label)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Ref: ${payment.transactionReference}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Slate400)
                                )
                                Text(
                                    text = "${payment.paymentMethod} • ${payment.createdAt}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Slate500)
                                )
                            }
                            Text(
                                text = "₹${payment.amount.toInt()}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (payment.status == PaymentStatus.PAID) EmeraldEarn else DeepBluePrimary
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    // Payout Request Modal BottomSheet
    if (showPayoutSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPayoutSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            var isUpi by remember { mutableStateOf(true) }
            var upiIdInput by remember { mutableStateOf("rahul@okhdfcbank") }
            var bankAccountInput by remember { mutableStateOf("") }
            var ifscInput by remember { mutableStateOf("") }
            var payoutAmountInput by remember { mutableStateOf(availableBalance.toInt().toString()) }
            var isSuccess by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Request Earnings Payout",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Slate900)
                )

                Text(
                    text = "Available to withdraw: ₹${availableBalance.toInt()} • Min ₹100",
                    style = MaterialTheme.typography.bodySmall.copy(color = EmeraldEarn, fontWeight = FontWeight.Bold)
                )

                // Payout method selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilterChip(
                        selected = isUpi,
                        onClick = { isUpi = true },
                        label = { Text("⚡ UPI ID (Fastest)") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = !isUpi,
                        onClick = { isUpi = false },
                        label = { Text("🏦 Bank Transfer") },
                        modifier = Modifier.weight(1f)
                    )
                }

                if (isUpi) {
                    OutlinedTextField(
                        value = upiIdInput,
                        onValueChange = { upiIdInput = it },
                        label = { Text("Your UPI ID (VPA)") },
                        placeholder = { Text("e.g. name@okhdfcbank, mobile@paytm") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Payment, contentDescription = null) }
                    )
                } else {
                    OutlinedTextField(
                        value = bankAccountInput,
                        onValueChange = { bankAccountInput = it },
                        label = { Text("Bank Account Number") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = ifscInput,
                        onValueChange = { ifscInput = it },
                        label = { Text("IFSC Code (e.g. HDFC0001234)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = payoutAmountInput,
                    onValueChange = { payoutAmountInput = it },
                    label = { Text("Payout Amount in ₹ (INR)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Safety Guarantee: CampusEarn never asks for UPI PINs, ATM cards, or net banking passwords. Payouts are pushed directly to your VPA or account.",
                        style = MaterialTheme.typography.labelSmall.copy(color = Slate600),
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Button(
                    onClick = {
                        val amt = payoutAmountInput.toDoubleOrNull() ?: availableBalance
                        val target = if (isUpi) upiIdInput else "$bankAccountInput ($ifscInput)"
                        repository.requestPayout(amt, target, isUpi)
                        showPayoutSheet = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldEarn),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Confirm Payout Request", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
