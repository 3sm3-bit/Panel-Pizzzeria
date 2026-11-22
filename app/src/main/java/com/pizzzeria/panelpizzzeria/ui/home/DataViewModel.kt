package com.pizzzeria.panelpizzzeria.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.pizzzeria.panelpizzzeria.BuildConfig
import com.pizzzeria.panelpizzzeria.model.Category
import com.pizzzeria.panelpizzzeria.model.ModificationItem
import com.pizzzeria.panelpizzzeria.model.Product
import com.pizzzeria.panelpizzzeria.model.hasPaidModifications
import com.pizzzeria.panelpizzzeria.model.isMasa
import com.pizzzeria.panelpizzzeria.model.isOrilla
import com.pizzzeria.panelpizzzeria.model.toDomain
import com.pizzzeria.panelpizzzeria.repository.db.dao.CategoryDao
import com.pizzzeria.panelpizzzeria.repository.db.dao.ProductDao
import com.pizzzeria.panelpizzzeria.repository.network.DataNetwork
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class CartItem(
    val cartItemId: String = UUID.randomUUID().toString(),
    val product: Product,
    val selectedMasa: ModificationItem? = null,
    val selectedOrilla: ModificationItem? = null,
    val selectedOrillas: Map<Int, ModificationItem> = emptyMap(),
    val paidModifications: Map<Int, ModificationItem> = emptyMap(),
    val quantity: Int = 1,
    val customSelections: Map<Int, ModificationItem> = emptyMap()
) {
    val unitPrice: Double
        get() {
            var total = product.price
            if (selectedMasa != null) {
                total += selectedMasa.price
            }
            val isTradicional = selectedMasa == null || selectedMasa.name.contains("TRADICIONAL", ignoreCase = true)
            if (isTradicional) {
                if (selectedOrilla != null) total += selectedOrilla.price
                selectedOrillas.values.forEach { orilla ->
                    total += orilla.price
                }
            }
            paidModifications.values.forEach { mod ->
                total += mod.price
            }
            customSelections.values.forEach { mod ->
                total += mod.price
            }
            return total
        }

    val totalPrice: Double
        get() = unitPrice * quantity
}

sealed interface OrderState {
    object Idle : OrderState
    object Loading : OrderState
    data class Success(val customerName: String) : OrderState
    data class Error(val message: String) : OrderState
}

@HiltViewModel
class DataViewModel @Inject constructor(
    private val categoryDao: CategoryDao,
    private val productDao: ProductDao,
    private val dataNetwork: DataNetwork
) : ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId: StateFlow<String?> = _selectedCategoryId.asStateFlow()

    private val _filteredProducts = MutableStateFlow<List<Product>>(emptyList())
    val filteredProducts: StateFlow<List<Product>> = _filteredProducts.asStateFlow()

    private val _customerName = MutableStateFlow("")
    val customerName: StateFlow<String> = _customerName.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _orderState = MutableStateFlow<OrderState>(OrderState.Idle)
    val orderState: StateFlow<OrderState> = _orderState.asStateFlow()

    private val _selectedProductForModal = MutableStateFlow<Product?>(null)
    val selectedProductForModal: StateFlow<Product?> = _selectedProductForModal.asStateFlow()

    private val _modalSelections = MutableStateFlow<Map<Int, ModificationItem>>(emptyMap())
    val modalSelections: StateFlow<Map<Int, ModificationItem>> = _modalSelections.asStateFlow()

    private val _modalMasa = MutableStateFlow<ModificationItem?>(null)
    val modalMasa: StateFlow<ModificationItem?> = _modalMasa.asStateFlow()

    private val _modalOrillas = MutableStateFlow<Map<Int, ModificationItem>>(emptyMap())
    val modalOrillas: StateFlow<Map<Int, ModificationItem>> = _modalOrillas.asStateFlow()

    private val _modalPaidSelections = MutableStateFlow<Map<Int, ModificationItem>>(emptyMap())
    val modalPaidSelections: StateFlow<Map<Int, ModificationItem>> = _modalPaidSelections.asStateFlow()

    fun openProductModal(product: Product) {
        _selectedProductForModal.value = product
        val initialSelections = mutableMapOf<Int, ModificationItem>()

        val masaGroup = product.groupModifications.firstOrNull {
            (it.name.contains("MASA", ignoreCase = true) || it.dishModificationGroupId == 1) && it.modifications.isNotEmpty()
        }
        val defaultMasa = masaGroup?.modifications?.firstOrNull {
            it.name.contains("TRADICIONAL", ignoreCase = true)
        } ?: masaGroup?.modifications?.firstOrNull()
        _modalMasa.value = defaultMasa
        _modalOrillas.value = emptyMap()
        _modalPaidSelections.value = emptyMap()

        product.groupModifications.forEach { group ->
            if (!group.isMasa && !group.isOrilla && !group.hasPaidModifications && group.modifications.isNotEmpty()) {
                initialSelections[group.dishModificationGroupId] = group.modifications.first()
            }
        }
        _modalSelections.value = initialSelections
    }

    fun closeProductModal() {
        _selectedProductForModal.value = null
        _modalSelections.value = emptyMap()
        _modalMasa.value = null
        _modalOrillas.value = emptyMap()
        _modalPaidSelections.value = emptyMap()
    }

    fun updateModalMasa(masa: ModificationItem) {
        _modalMasa.value = masa
        val isTradicional = masa.name.contains("TRADICIONAL", ignoreCase = true)
        if (!isTradicional) {
            _modalOrillas.value = emptyMap()
        }
    }

    fun toggleModalOrilla(groupId: Int, orilla: ModificationItem) {
        val current = _modalOrillas.value.toMutableMap()
        if (current[groupId]?.dishModificationId == orilla.dishModificationId) {
            current.remove(groupId)
        } else {
            current[groupId] = orilla
        }
        _modalOrillas.value = current
    }

    fun toggleModalPaidModification(groupId: Int, item: ModificationItem) {
        val current = _modalPaidSelections.value.toMutableMap()
        if (current[groupId]?.dishModificationId == item.dishModificationId) {
            current.remove(groupId)
        } else {
            current[groupId] = item
        }
        _modalPaidSelections.value = current
    }

    fun updateModalSelection(groupId: Int, item: ModificationItem) {
        val current = _modalSelections.value.toMutableMap()
        current[groupId] = item
        _modalSelections.value = current
    }

    fun addCustomizedProductToCart() {
        val product = _selectedProductForModal.value ?: return
        val selections = _modalSelections.value
        val masa = _modalMasa.value
        val orillas = _modalOrillas.value
        val paidMods = _modalPaidSelections.value

        val currentCart = _cartItems.value.toMutableList()
        val existingIndex = currentCart.indexOfFirst {
            it.product.productId == product.productId && 
            it.customSelections == selections && 
            it.selectedMasa == masa && 
            it.selectedOrillas == orillas &&
            it.paidModifications == paidMods
        }

        if (existingIndex >= 0) {
            val existing = currentCart[existingIndex]
            currentCart[existingIndex] = existing.copy(quantity = existing.quantity + 1)
        } else {
            currentCart.add(
                CartItem(
                    product = product,
                    selectedMasa = masa,
                    selectedOrillas = orillas,
                    paidModifications = paidMods,
                    customSelections = selections,
                    quantity = 1
                )
            )
        }
        _cartItems.value = currentCart
        closeProductModal()
    }

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val customOrder = listOf("8", "9", "10", "4")
                val dbCategories = categoryDao.getAllCategories().map { it.toDomain() }

                val filteredCats = dbCategories.filter { it.categoryId != "11" }.map { cat ->
                    if (cat.categoryId == "4") {
                        cat.copy(categoryName = "BEBIDAS", namePanel = "BEBIDAS")
                    } else {
                        cat
                    }
                }.toMutableList()

                if (filteredCats.none { it.categoryId == "4" }) {
                    filteredCats.add(
                        Category(
                            categoryId = "4",
                            categoryName = "BEBIDAS",
                            namePanel = "BEBIDAS"
                        )
                    )
                }

                val cats = filteredCats.sortedBy { category ->
                    val index = customOrder.indexOf(category.categoryId)
                    if (index != -1) index else Int.MAX_VALUE
                }

                val prods = productDao.getAllProducts().map { it.toDomain() }
                _categories.value = cats
                _products.value = prods

                if (cats.isNotEmpty() && _selectedCategoryId.value == null) {
                    selectCategory(cats.first().categoryId)
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _loading.value = false
            }
        }
    }

    fun selectCategory(categoryId: String) {
        _selectedCategoryId.value = categoryId
        if (categoryId == "4") {
            _filteredProducts.value = _products.value.filter { it.menuCategoryId == "4" || it.menuCategoryId == "11" }
        } else {
            _filteredProducts.value = _products.value.filter { it.menuCategoryId == categoryId }
        }
    }

    fun updateCustomerName(name: String) {
        _customerName.value = name
    }

    fun addToCart(product: Product) {
        val currentCart = _cartItems.value.toMutableList()

        val masaGroup = product.groupModifications.firstOrNull {
            (it.name.contains("MASA", ignoreCase = true) || it.dishModificationGroupId == 1) && it.modifications.isNotEmpty()
        }
        val defaultMasa = masaGroup?.modifications?.firstOrNull {
            it.name.contains("TRADICIONAL", ignoreCase = true)
        } ?: masaGroup?.modifications?.firstOrNull()

        val existingIndex = currentCart.indexOfFirst {
            it.product.productId == product.productId && it.selectedMasa == defaultMasa && it.selectedOrilla == null
        }

        if (existingIndex >= 0) {
            val existing = currentCart[existingIndex]
            currentCart[existingIndex] = existing.copy(quantity = existing.quantity + 1)
        } else {
            currentCart.add(
                CartItem(
                    product = product,
                    selectedMasa = defaultMasa,
                    selectedOrilla = null,
                    quantity = 1
                )
            )
        }
        _cartItems.value = currentCart
    }

    fun updateCartItemMasa(cartItemId: String, newMasa: ModificationItem) {
        val currentCart = _cartItems.value.toMutableList()
        val index = currentCart.indexOfFirst { it.cartItemId == cartItemId }
        if (index >= 0) {
            val item = currentCart[index]
            val isTradicional = newMasa.name.contains("TRADICIONAL", ignoreCase = true)
            val updatedOrilla = if (isTradicional) item.selectedOrilla else null
            currentCart[index] = item.copy(
                selectedMasa = newMasa,
                selectedOrilla = updatedOrilla
            )
            _cartItems.value = currentCart
        }
    }

    fun toggleCartItemOrilla(cartItemId: String, orilla: ModificationItem) {
        val currentCart = _cartItems.value.toMutableList()
        val index = currentCart.indexOfFirst { it.cartItemId == cartItemId }
        if (index >= 0) {
            val item = currentCart[index]
            val newOrilla = if (item.selectedOrilla?.dishModificationId == orilla.dishModificationId) null else orilla
            currentCart[index] = item.copy(selectedOrilla = newOrilla)
            _cartItems.value = currentCart
        }
    }

    fun updateQuantity(cartItemId: String, delta: Int) {
        val currentCart = _cartItems.value.toMutableList()
        val index = currentCart.indexOfFirst { it.cartItemId == cartItemId }
        if (index >= 0) {
            val item = currentCart[index]
            val newQty = item.quantity + delta
            if (newQty >= 1) {
                currentCart[index] = item.copy(quantity = newQty)
                _cartItems.value = currentCart
            }
        }
    }


    fun removeFromCart(cartItemId: String) {
        val currentCart = _cartItems.value.toMutableList()
        currentCart.removeAll { it.cartItemId == cartItemId }
        _cartItems.value = currentCart
    }

    fun confirmOrder() {
        val name = _customerName.value
        val items = _cartItems.value
        if (name.isBlank() || items.isEmpty()) return

        viewModelScope.launch {
            _orderState.value = OrderState.Loading
            try {
                val token = BuildConfig.KEY_POSTER
                val fields = mutableMapOf<String, String>()
                fields["spot_id"] = BuildConfig.ID_BRANCH
                fields["first_name"] = name
                val defaultPhone = when (BuildConfig.ID_BRANCH) {
                    "1" -> "+524490000000"
                    "3" -> "+524491000000"
                    else -> "+524490000000"
                }
                fields["phone"] = defaultPhone.replace(Regex("[^0-9]"), "")

                val commentsList = mutableListOf<String>()
                items.forEachIndexed { index, cartItem ->
                    fields["products[$index][product_id]"] = cartItem.product.productId
                    fields["products[$index][count]"] = cartItem.quantity.toString()
                    fields["products[$index][price]"] = (cartItem.unitPrice * 100).toLong().toString()

                    val mods = mutableListOf<Map<String, Int>>()
                    cartItem.selectedMasa?.let { masa ->
                        mods.add(mapOf("m" to masa.dishModificationId, "a" to 1))
                    }
                    cartItem.selectedOrilla?.let { orilla ->
                        mods.add(mapOf("m" to orilla.dishModificationId, "a" to 1))
                    }
                    cartItem.selectedOrillas.values.forEach { orilla ->
                        mods.add(mapOf("m" to orilla.dishModificationId, "a" to 1))
                    }
                    cartItem.paidModifications.values.forEach { mod ->
                        mods.add(mapOf("m" to mod.dishModificationId, "a" to 1))
                    }
                    cartItem.customSelections.values.forEach { mod ->
                        mods.add(mapOf("m" to mod.dishModificationId, "a" to 1))
                    }
                    if (mods.isNotEmpty()) {
                        fields["products[$index][modification]"] = Gson().toJson(mods)
                    }

                    var desc = "${cartItem.quantity}x ${cartItem.product.productName}"
                    cartItem.selectedMasa?.let { desc += " (${it.name})" }
                    cartItem.selectedOrilla?.let { desc += " [${it.name}]" }
                    cartItem.selectedOrillas.values.forEach { desc += " [${it.name}]" }
                    cartItem.paidModifications.values.forEach { desc += " [${it.name}]" }
                    if (cartItem.customSelections.isNotEmpty()) {
                        val customNames = cartItem.customSelections.values.joinToString(", ") { it.name }
                        desc += " [$customNames]"
                    }
                    commentsList.add(desc)
                }

                fields["comment"] = "Cliente: $name"

                val response = dataNetwork.createIncomingOrder(token, fields)
                if (response.isSuccessful) {
                    val orderId = response.body()?.response?.incomingOrderId
                    Log.d("DataViewModel", "Order created successfully in Poster. ID: $orderId")
                    _orderState.value = OrderState.Success(name)
                    kotlinx.coroutines.delay(4000)
                    _cartItems.value = emptyList()
                    _customerName.value = ""
                    _orderState.value = OrderState.Idle
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    Log.e("DataViewModel", "Failed to create order in Poster: $errorBody")
                    _orderState.value = OrderState.Error("No se pudo registrar el pedido en Poster ($errorBody).")
                }
            } catch (e: Exception) {
                Log.e("DataViewModel", "Exception creating order in Poster", e)
                _orderState.value = OrderState.Error("Error de red: ${e.localizedMessage ?: "Verifica tu conexión"}")
            }
        }
    }

    fun resetOrderState() {
        _orderState.value = OrderState.Idle
    }
}
