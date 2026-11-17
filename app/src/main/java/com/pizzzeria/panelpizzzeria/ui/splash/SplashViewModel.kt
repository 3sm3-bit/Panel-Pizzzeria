package com.pizzzeria.panelpizzzeria.ui.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pizzzeria.panelpizzzeria.BuildConfig
import com.pizzzeria.panelpizzzeria.model.toEntity
import com.pizzzeria.panelpizzzeria.repository.db.dao.CategoryDao
import com.pizzzeria.panelpizzzeria.repository.db.dao.ProductDao
import com.pizzzeria.panelpizzzeria.repository.network.DataNetwork
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val dataNetwork: DataNetwork,
    private val categoryDao: CategoryDao,
    private val productDao: ProductDao
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isLoaded = MutableStateFlow(false)
    val isLoaded: StateFlow<Boolean> = _isLoaded.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        checkAndFetchData()
    }

    fun checkAndFetchData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                Log.d("SplashViewModel", "Fetching latest data from network...")
                val token = BuildConfig.KEY_POSTER
                val spotId = BuildConfig.ID_BRANCH

                // Fetch Categories
                val categoriesResponse = dataNetwork.getCategories(token, spotId)
                if (categoriesResponse.isSuccessful) {
                    val categoryItems = categoriesResponse.body()?.response ?: emptyList()
                    val categoryEntities = categoryItems.mapNotNull { it.toEntity() }
                    categoryDao.clearAllCategories()
                    categoryDao.insertCategories(categoryEntities)
                    Log.d("SplashViewModel", "Cleared old categories and saved ${categoryEntities.size} categories to database.")
                } else {
                    Log.e("SplashViewModel", "Error fetching categories: ${categoriesResponse.errorBody()?.string()}")
                }

                // Fetch Products
                val productsResponse = dataNetwork.getProducts(token, spotId)
                if (productsResponse.isSuccessful) {
                    val productItems = productsResponse.body()?.response ?: emptyList()
                    val productEntities = productItems.mapNotNull { it.toEntity() }
                    productDao.clearAllProducts()
                    productDao.insertProducts(productEntities)
                    Log.d("SplashViewModel", "Cleared old products and saved ${productEntities.size} products to database.")
                } else {
                    Log.e("SplashViewModel", "Error fetching products: ${productsResponse.errorBody()?.string()}")
                }

                _isLoaded.value = true
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage
                Log.e("SplashViewModel", "Exception during data check/fetch", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
