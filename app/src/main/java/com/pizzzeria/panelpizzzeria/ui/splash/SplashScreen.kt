package com.pizzzeria.panelpizzzeria.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pizzzeria.panelpizzzeria.R
import com.pizzzeria.panelpizzzeria.utils.red_600
import com.pizzzeria.panelpizzzeria.utils.textB12

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val isLoaded by viewModel.isLoaded.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(isLoaded) {
        if (isLoaded) {
            onNavigateToHome()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                Image(painter = painterResource(R.drawable.ic_logo_planel)
                    , contentDescription = "null",modifier = Modifier.width(250.dp).height(250.dp))

                Spacer(modifier = Modifier.height(24.dp))
                if (isLoading) {
                    CircularProgressIndicator(color = red_600)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Cargando menú y productos...",
                        style = textB12,
                        color = red_600
                    )
                } else if (errorMessage != null) {
                    Text(
                        text = "Error: $errorMessage",
                        style = textB12,
                        color = red_600
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.checkAndFetchData() }) {
                        Text("Reintentar")
                    }
                }
            }
        }
    }
}
