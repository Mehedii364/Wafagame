package com.example.games.shop

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.database.entities.ShopInventoryEntity
import com.example.core.settings.AppLanguage
import com.example.ui.WafaMainViewModel
import com.example.ui.theme.*

data class CustomerOrder(
    val customerName: String,
    val avatar: String,
    val productId: String,
    val quantity: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WafaShopScreen(
    viewModel: WafaMainViewModel,
    language: AppLanguage,
    onBack: () -> Unit
) {
    val inventory by viewModel.shopInventory.collectAsStateWithLifecycle()
    val player by viewModel.player.collectAsStateWithLifecycle()

    var activeTab by remember { mutableIntStateOf(0) } // 0 = Counter & Customers, 1 = Stock & Wholesale

    val shopTitles = listOf(
        "টং দোকান (Tong Dokan)",
        "পাড়ার মুদি দোকান (Local Grocery)",
        "মডার্ন গ্রোসারি (Modern Mart)",
        "সুপার শপ (Super Shop)",
        "মেগা ডিপার্টমেন্টাল (Mega Store)"
    )
    val currentShopTier = (player.shopLevel - 1).coerceIn(0, 4)

    // Current Customer at counter
    val customerAvatars = remember { listOf("👨‍💼", "👵", "🧑‍🎓", "👳", "👩‍🍳", "👨‍🌾") }
    var currentCustomer by remember {
        mutableStateOf(CustomerOrder("করিম চাচা", "👨‍🌾", "p_oil", 1))
    }

    fun generateNextCustomer() {
        if (inventory.isNotEmpty()) {
            val randomItem = inventory.random()
            val qty = (1..3).random()
            val names = listOf("করিম চাচা", "রাহেলা খালা", "সুমন ভাই", "মিজান সাহেব", "রুপা আপু")
            currentCustomer = CustomerOrder(
                customerName = names.random(),
                avatar = customerAvatars.random(),
                productId = randomItem.productId,
                quantity = qty
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (language == AppLanguage.BANGLA) "মেসার্স ওয়াফা স্টোর" else "Wafa Mudi Shop",
                            fontWeight = FontWeight.Bold,
                            color = CyberGold
                        )
                        Text(
                            text = shopTitles[currentShopTier],
                            fontSize = 11.sp,
                            color = NeonEmerald
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("shop_back_btn")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(14.dp)
        ) {
            // Tab Selector: Store Counter vs Wholesale Supply
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = DarkSurfaceCard,
                contentColor = CyberGold
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = { Text(if (language == AppLanguage.BANGLA) "দোকানের কাউন্টার" else "Store Counter", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = { Text(if (language == AppLanguage.BANGLA) "পাইকারি মালামাল" else "Wholesale Stock", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (activeTab == 0) {
                // COUNTER & SERVING CUSTOMERS
                val orderedProduct = inventory.find { it.productId == currentCustomer.productId }
                val hasEnoughStock = (orderedProduct?.stock ?: 0) >= currentCustomer.quantity
                val saleAmount = (orderedProduct?.salePrice ?: 20) * currentCustomer.quantity

                // Customer Counter Visual Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = DarkSurfaceCard,
                    border = BorderStroke(1.5.dp, CyberGold)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (language == AppLanguage.BANGLA) "দোকানে ক্রেতা উপস্থিত" else "Customer at Counter",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Customer Avatar & Speech Bubble
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = currentCustomer.avatar, fontSize = 48.sp)
                            Spacer(modifier = Modifier.width(12.dp))

                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = DarkSurfaceVariant,
                                border = BorderStroke(1.dp, NeonEmerald)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "${currentCustomer.customerName}:",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = CyberGold
                                    )
                                    Text(
                                        text = "\"আমাকে ${currentCustomer.quantity}টি ${orderedProduct?.nameBn ?: "পণ্য"} দিন প্লিজ!\"",
                                        fontSize = 13.sp,
                                        color = TextPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stock Availability Pill
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (hasEnoughStock) DarkSurface else Color(0xFF3E1A1A),
                            border = BorderStroke(1.dp, if (hasEnoughStock) NeonEmerald else DangerRed)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Current Stock: ${orderedProduct?.stock ?: 0} units",
                                    color = if (hasEnoughStock) NeonEmerald else DangerRed,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Bill: $saleAmount 🪙",
                                    color = CyberGold,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Serve Button
                        Button(
                            onClick = {
                                if (hasEnoughStock && orderedProduct != null) {
                                    viewModel.sellProductToCustomer(
                                        productId = orderedProduct.productId,
                                        qty = currentCustomer.quantity,
                                        saleTotal = saleAmount
                                    ) {
                                        generateNextCustomer()
                                    }
                                }
                            },
                            enabled = hasEnoughStock,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("shop_serve_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = DarkBackground),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.ShoppingBag, contentDescription = "Serve")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (hasEnoughStock) {
                                    if (language == AppLanguage.BANGLA) "পণ্য বিক্রয় করুন (+$saleAmount কয়েন)" else "Serve & Collect $saleAmount Coins"
                                } else {
                                    if (language == AppLanguage.BANGLA) "স্টক শেষ! পাইকারি থেকে আনুন" else "Out of Stock! Restock first"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quick Stock Summary List
                Text(
                    text = if (language == AppLanguage.BANGLA) "দোকানের বর্তমান মজুত:" else "Store Inventory Overview:",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(inventory) { item ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = DarkSurfaceCard,
                            border = BorderStroke(1.dp, DarkBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = item.nameBn, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "মজুত: ${item.stock}", color = if (item.stock < 5) DangerRed else NeonEmerald, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(text = "বিক্রয়: ${item.salePrice} 🪙", color = CyberGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            } else {
                // WHOLESALE RESTOCK MARKET
                Text(
                    text = if (language == AppLanguage.BANGLA) "কারওয়ান বাজার পাইকারি আড়ত (মালামাল কিনুন):" else "Wholesale Depot (Restock products):",
                    fontWeight = FontWeight.Bold,
                    color = CyberGold,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(inventory) { item ->
                        val batchCost = item.costPrice * 10
                        val canAfford = player.coins >= batchCost

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurfaceCard,
                            border = BorderStroke(1.dp, DarkBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = item.nameBn, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                                    Text(text = "বর্তমান মজুত: ${item.stock} টি", fontSize = 11.sp, color = TextSecondary)
                                    Text(text = "পাইকারি দর: ${item.costPrice} 🪙/টি", fontSize = 11.sp, color = NeonEmerald)
                                }

                                Button(
                                    onClick = {
                                        viewModel.restockProduct(item.productId, 10, batchCost)
                                        viewModel.effectsHelper.vibrateSuccess()
                                    },
                                    enabled = canAfford,
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberGold, contentColor = DarkBackground),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(text = "+10 টি ($batchCost 🪙)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
