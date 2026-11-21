package com.pizzzeria.panelpizzzeria.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.pizzzeria.panelpizzzeria.model.Product
import com.pizzzeria.panelpizzzeria.model.ModificationItem
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.pizzzeria.panelpizzzeria.BuildConfig
import com.pizzzeria.panelpizzzeria.utils.green_600
import com.pizzzeria.panelpizzzeria.utils.grey_100
import com.pizzzeria.panelpizzzeria.utils.red_600
import com.pizzzeria.panelpizzzeria.utils.textB12
import com.pizzzeria.panelpizzzeria.utils.textB16
import com.pizzzeria.panelpizzzeria.utils.textB18
import com.pizzzeria.panelpizzzeria.utils.textB20
import com.pizzzeria.panelpizzzeria.utils.textB22
import com.pizzzeria.panelpizzzeria.utils.textB30
import com.pizzzeria.panelpizzzeria.utils.textM10
import com.pizzzeria.panelpizzzeria.utils.textS18
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HomeScreen(viewModel: DataViewModel = hiltViewModel()) {
    val categories by viewModel.categories.collectAsState()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()
    val filteredProducts by viewModel.filteredProducts.collectAsState()
    val customerName by viewModel.customerName.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val orderState by viewModel.orderState.collectAsState()
    val selectedProductForModal by viewModel.selectedProductForModal.collectAsState()
    val modalSelections by viewModel.modalSelections.collectAsState()

    val focusManager = LocalFocusManager.current
    var showNameMissingDialog by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "promo_glow")
    val promoAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "promoAngle"
    )

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

                Text(
                text = if(BuildConfig.ID_BRANCH == "1")"SUCURSAL PLAZA FIESTA" else "SUCURSAL UNIVERSIDAD",
                    style = textB22,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )


                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = category.categoryId == selectedCategoryId
                        val isPromo = category.categoryId == "PROMO"
                        val promoBoxModifier = if (isPromo) {
                            Modifier
                                .padding(4.dp)
                                .rotatingGlowBorder(promoAngle, green_600, RoundedCornerShape(20.dp), 4.dp)
                        } else {
                            Modifier
                        }

                        Box(modifier = promoBoxModifier) {
                            Button(
                                onClick = { viewModel.selectCategory(category.categoryId) },
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                shape = RoundedCornerShape(20.dp),
                                border = if (!isSelected) BorderStroke(1.dp, green_600) else null,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) green_600 else Color.White,
                                    contentColor = if (isSelected) Color.White else green_600
                                )
                            ) {
                                Text(
                                    text = category.namePanel,
                                    style = textB12,
                                    color = if (isSelected) Color.White else green_600
                                )
                            }
                        }
                    }
                }


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
                            val formattedPrice = formatCurrency(product.price)

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .clickable {
                                        if (product.menuCategoryId == "14") {
                                            viewModel.openProductModal(product)
                                        } else {
                                            viewModel.addToCart(product)
                                        }
                                    },
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
                        style = textB16,
                        color =Color.Black,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    CompactTextField(
                        value = customerName,
                        onValueChange = { viewModel.updateCustomerName(it) },
                        label = "Nombre de cliente",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
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
                                            val unitPriceStr = formatCurrency(cartItem.unitPrice)
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
                                    if (cartItem.customSelections.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                            cartItem.customSelections.values.forEach { mod ->
                                                Text(
                                                    text = "• ${mod.name}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color.DarkGray
                                                )
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
                                                        val priceAddStr = if (orilla.price > 0) " (+${formatCurrency(orilla.price)})" else ""
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

                                    val subtotalStr = formatCurrency(cartItem.totalPrice)
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
                    val grandTotalStr = formatCurrency(grandTotal)

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

    if (selectedProductForModal != null) {
        ProductCustomizationDialog(
            product = selectedProductForModal!!,
            selections = modalSelections,
            onSelectionChange = { groupId, item -> viewModel.updateModalSelection(groupId, item) },
            onConfirm = { viewModel.addCustomizedProductToCart() },
            onDismiss = { viewModel.closeProductModal() }
        )
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

@Composable
fun CompactTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, red_600),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (value.isEmpty()) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = TextStyle(color = Color.Black, fontSize = 14.sp),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCustomizationDialog(
    product: Product,
    selections: Map<Int, ModificationItem>,
    onSelectionChange: (Int, ModificationItem) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White,
        title = {
            Text(
                text = product.productName,
                style = textB18,
                color = Color.Black
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                product.groupModifications.forEach { group ->
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = group.name,
                            style = textB16,
                            color = green_600
                        )
                        if (group.modifications.size == 1) {
                            val singleMod = group.modifications.first()
                            Text(
                                text = "• ${singleMod.name}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.DarkGray
                            )
                        } else {
                            var expanded by remember { mutableStateOf(false) }
                            val currentSelection = selections[group.dishModificationGroupId] ?: group.modifications.first()

                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded }
                            ) {
                                OutlinedTextField(
                                    value = currentSelection.name,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth(),
                                    shape = RoundedCornerShape(20.dp),
                                    textStyle = TextStyle(color = Color.Black, fontSize = 14.sp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = green_600,
                                        unfocusedBorderColor = green_600,
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    group.modifications.forEach { mod ->
                                        DropdownMenuItem(
                                            text = { Text(mod.name, color = Color.Black) },
                                            onClick = {
                                                onSelectionChange(group.dishModificationGroupId, mod)
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = green_600),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agregar al pedido", color = Color.White, style = textB16)
            }
        }
    )
}

fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("es-MX")).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    return "$ ${formatter.format(amount)}"
}

fun Modifier.rotatingGlowBorder(
    angle: Float,
    color: Color,
    shape: Shape,
    strokeWidth: Dp = 6.dp
): Modifier = this
    .padding(6.dp)
    .drawBehind {
        val strokePx = strokeWidth.toPx()
        val outline = shape.createOutline(size, layoutDirection, this)
        val brush = Brush.sweepGradient(
            listOf(color, Color.White, color, Color.Transparent),
            center = center
        )
        rotate(angle) {
            drawOutline(
                outline = outline,
                brush = brush,
                style = Stroke(width = strokePx)
            )
        }
    }
