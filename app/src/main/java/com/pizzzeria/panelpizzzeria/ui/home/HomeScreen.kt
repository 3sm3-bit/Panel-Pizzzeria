package com.pizzzeria.panelpizzzeria.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.pizzzeria.panelpizzzeria.utils.green_600
import com.pizzzeria.panelpizzzeria.utils.grey_100
import com.pizzzeria.panelpizzzeria.utils.red_600
import com.pizzzeria.panelpizzzeria.utils.textB10
import com.pizzzeria.panelpizzzeria.utils.textB12
import com.pizzzeria.panelpizzzeria.utils.textB16
import com.pizzzeria.panelpizzzeria.utils.textB18
import com.pizzzeria.panelpizzzeria.utils.textB20
import com.pizzzeria.panelpizzzeria.utils.textB22
import com.pizzzeria.panelpizzzeria.utils.textB30
import com.pizzzeria.panelpizzzeria.utils.textM10
import com.pizzzeria.panelpizzzeria.utils.textS18
import java.util.Locale

@Composable
fun HomeScreen(viewModel: DataViewModel = hiltViewModel()) {
    val categories by viewModel.categories.collectAsState()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()
    val filteredProducts by viewModel.filteredProducts.collectAsState()
    val customerName by viewModel.customerName.collectAsState()
    val customerPhone by viewModel.customerPhone.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val orderState by viewModel.orderState.collectAsState()

    val focusManager = LocalFocusManager.current
    var showNameMissingDialog by remember { mutableStateOf(false) }

    LaunchedEffect(orderState) {
        if (orderState is OrderState.Loading || orderState is OrderState.Success) {
            focusManager.clearFocus()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = grey_100
        ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight()
                    .padding(end = 16.dp, top = 16.dp)
            ) {

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = category.categoryId == selectedCategoryId
                        Button(
                            onClick = { viewModel.selectCategory(category.categoryId) },
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            border = if (!isSelected) BorderStroke(1.dp, red_600) else null,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) red_600 else Color.White,
                                contentColor = if (isSelected) Color.White else red_600
                            )
                        ) {
                            Text(
                                text = category.namePanel,
                                style = textB12,
                                color = if (isSelected) Color.White else red_600
                            )
                        }
                    }
                }

                Text(
                    text = "Productos",
                    style = textB22,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (loading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (error != null) {
                    Text(text = "Error: $error", color = MaterialTheme.colorScheme.error)
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    ) {
                        items(filteredProducts) { product ->
                            val formattedPrice = String.format(Locale.getDefault(), "$ %.2f", product.price)

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .clickable { viewModel.addToCart(product) },
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                            ) {
                                Column{
                                    if (!product.photoUrl.isNullOrEmpty()) {
                                        AsyncImage(
                                            model = product.photoUrl,
                                            contentDescription = product.productName,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .aspectRatio(4f / 3f)
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .aspectRatio(4f / 3f),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = "Sin imagen", style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = product.productName,
                                            style = textB16,
                                            color = Color.Black,
                                            maxLines = 2,
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(end = 4.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = formattedPrice,
                                            style = textB18,
                                            color = green_600
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }


            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Tu pedido",
                        style = textB20,
                        color =Color.Black,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { viewModel.updateCustomerName(it) },
                        label = { Text("Nombre de cliente") },
                        placeholder = { Text("Escribe tu nombre aquí") },
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = red_600,
                            unfocusedBorderColor = red_600
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .padding(bottom = 6.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    OutlinedTextField(
                        value = customerPhone,
                        onValueChange = { viewModel.updateCustomerPhone(it) },
                        label = { Text("Teléfono") },
                        placeholder = { Text("Escribe tu teléfono aquí") },
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = red_600,
                            unfocusedBorderColor = red_600
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .padding(bottom = 8.dp),
                        singleLine = true
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Text(
                        text = "Productos seleccionados",
                        style = textB18,
                        color =Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(cartItems, key = { it.cartItemId }) { cartItem ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = cartItem.product.productName,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                            val unitPriceStr = String.format(Locale.getDefault(), "$ %.2f", cartItem.unitPrice)
                                            Text(
                                                text = "$unitPriceStr c/u",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            IconButton(onClick = { viewModel.updateQuantity(cartItem.cartItemId, -1) }) {
                                                Text(text = "-", style = MaterialTheme.typography.titleLarge)
                                            }
                                            Text(text = "${cartItem.quantity}", style = MaterialTheme.typography.bodyMedium)
                                            IconButton(onClick = { viewModel.updateQuantity(cartItem.cartItemId, 1) }) {
                                                Text(text = "+", style = MaterialTheme.typography.titleLarge)
                                            }
                                        }
                                    }
                                    val masaGroup = cartItem.product.groupModifications.firstOrNull {
                                        it.name.contains("MASA", ignoreCase = true) || it.dishModificationGroupId == 1
                                    }
                                    val orillaGroup = cartItem.product.groupModifications.firstOrNull {
                                        it.name.contains("Orilla", ignoreCase = true) || it.dishModificationGroupId == 11
                                    }

                                    val isTradicionalSelected = cartItem.selectedMasa == null ||
                                            cartItem.selectedMasa.name.contains("TRADICIONAL", ignoreCase = true)

                                    val hasMasa = masaGroup != null && masaGroup.modifications.isNotEmpty()
                                    val hasOrilla = isTradicionalSelected && orillaGroup != null && orillaGroup.modifications.isNotEmpty()

                                    if (hasMasa || hasOrilla) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Column(
                                                modifier = Modifier.weight(1.5f)
                                            ) {
                                                if (masaGroup != null && masaGroup.modifications.isNotEmpty()) {
                                                    Row(
                                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        masaGroup.modifications.forEach { masa ->
                                                            val isSelected = cartItem.selectedMasa?.dishModificationId == masa.dishModificationId
                                                            FilterChip(
                                                                selected = isSelected,
                                                                onClick = { viewModel.updateCartItemMasa(cartItem.cartItemId, masa) },
                                                                shape = RoundedCornerShape(20.dp),
                                                                border = FilterChipDefaults.filterChipBorder(
                                                                    enabled = true,
                                                                    selected = isSelected,
                                                                    borderColor = red_600,
                                                                    selectedBorderColor = red_600,
                                                                    borderWidth = 1.dp,
                                                                    selectedBorderWidth = 1.dp
                                                                ),
                                                                colors = FilterChipDefaults.filterChipColors(
                                                                    containerColor = Color.White,
                                                                    labelColor = red_600,
                                                                    selectedContainerColor = red_600,
                                                                    selectedLabelColor = Color.White
                                                                ),
                                                                label = { Text(text = masa.name, style = MaterialTheme.typography.labelSmall) }
                                                            )
                                                        }
                                                    }
                                                }
                                            }

                                            if (orillaGroup != null && isTradicionalSelected && orillaGroup.modifications.isNotEmpty()) {
                                                Column(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .padding(start = 4.dp),
                                                    horizontalAlignment = Alignment.End,
                                                    verticalArrangement = Arrangement.Top
                                                ) {
                                                    orillaGroup.modifications.forEach { orilla ->
                                                        val isOrillaActive = cartItem.selectedOrilla?.dishModificationId == orilla.dishModificationId
                                                        val priceAddStr = if (orilla.price > 0) " (+$${String.format(Locale.getDefault(), "%.2f", orilla.price)})" else ""
                                                        val cleanName = orilla.name
                                                            .replace(Regex("(?i)\\b(grande|mediana|chica)\\b"), "")
                                                            .replace(Regex("\\s+"), " ")
                                                            .trim()
                                                        val displayName = cleanName.ifEmpty { orilla.name }

                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.End
                                                        ) {
                                                            Text(
                                                                text = "$displayName$priceAddStr",
                                                                style = textM10,
                                                                color = Color.Black,
                                                                maxLines = 3,
                                                                modifier = Modifier
                                                                    .weight(1f, fill = false)
                                                                    .padding(end = 4.dp)
                                                            )
                                                            Switch(
                                                                checked = isOrillaActive,
                                                                onCheckedChange = { viewModel.toggleCartItemOrilla(cartItem.cartItemId, orilla) },
                                                                modifier = Modifier.scale(0.85f),
                                                                colors = SwitchDefaults.colors(
                                                                    checkedThumbColor = Color.White,
                                                                    checkedTrackColor = red_600,
                                                                    checkedBorderColor = red_600,
                                                                    uncheckedThumbColor = red_600,
                                                                    uncheckedTrackColor = Color.White,
                                                                    uncheckedBorderColor = red_600
                                                                )
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    val subtotalStr = String.format(Locale.getDefault(), "$ %.2f", cartItem.totalPrice)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            modifier = Modifier.clickable { viewModel.removeFromCart(cartItem.cartItemId) },
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Eliminar",
                                                tint = red_600,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                text = "Eliminar",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = red_600
                                            )
                                        }

                                        Text(
                                            text = "Subtotal: $subtotalStr",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    val grandTotal = cartItems.sumOf { it.totalPrice }
                    val grandTotalStr = String.format(Locale.getDefault(), "$ %.2f", grandTotal)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Total:", style = textS18,
                            color = Color.Black)
                        Text(text = grandTotalStr, style = textB20,
                            color = green_600)
                    }

                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            if (customerName.isBlank()) {
                                showNameMissingDialog = true
                            } else {
                                viewModel.confirmOrder()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = red_600,
                            contentColor = Color.White
                        ),
                        enabled = cartItems.isNotEmpty()
                    ) {
                        Text("Confirmar Pedido")
                    }

                    if (showNameMissingDialog) {
                        AlertDialog(
                            onDismissRequest = { showNameMissingDialog = false },
                            shape = RoundedCornerShape(20.dp),
                            containerColor = Color.White,
                            title = {
                                Text(
                                    text = "Nombre requerido",
                                    style = textB18,
                                    color = Color.Black
                                )
                            },
                            text = {
                                Text(
                                    text = "Falta el nombre del cliente. Por favor, ingresa el nombre para continuar.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.DarkGray
                                )
                            },
                            confirmButton = {
                                Button(
                                    onClick = { showNameMissingDialog = false },
                                    colors = ButtonDefaults.buttonColors(containerColor = red_600)
                                ) {
                                    Text("Aceptar", color = Color.White)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

        when (val state = orderState) {
            is OrderState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = red_600)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Procesando pedido...",
                                style = textB18,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
            is OrderState.Success -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(red_600)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${state.customerName}, \n\ntu pedido ha sido registrado. \n\nAhora dirígete a la caja a pagar.",
                        style = textB30,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
            is OrderState.Error -> {
                AlertDialog(
                    onDismissRequest = { viewModel.resetOrderState() },
                    shape = RoundedCornerShape(20.dp),
                    containerColor = Color.White,
                    title = {
                        Text(
                            text = "Error al registrar pedido",
                            style = textB18,
                            color = red_600
                        )
                    },
                    text = {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.DarkGray
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = { viewModel.resetOrderState() },
                            colors = ButtonDefaults.buttonColors(containerColor = red_600)
                        ) {
                            Text("Aceptar", color = Color.White, style = textB16)
                        }
                    }
                )
            }
            OrderState.Idle -> { }
        }
    }
}
