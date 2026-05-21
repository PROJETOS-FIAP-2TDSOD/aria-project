package com.fiap.ariachallenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import com.fiap.ariachallenge.navigation.AriaNavGraph
import com.fiap.ariachallenge.ui.theme.AriaChallengeTheme

@AndroidEntryPoint
class   MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AriaChallengeTheme {
                val navController = rememberNavController()
                AriaNavGraph(navController = navController)
            }
        }
    }
}
