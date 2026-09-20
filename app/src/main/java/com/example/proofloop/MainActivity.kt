package com.example.proofloop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.proofloop.data.local.LocalProofStorage
import com.example.proofloop.data.repository.PracticeRepository
import com.example.proofloop.theme.ProofLoopTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Wire persistence so every practice attempt is saved to DataStore
        PracticeRepository.getInstance().initStorage(
            LocalProofStorage.getInstance(applicationContext)
        )

        enableEdgeToEdge()
        setContent {
            ProofLoopTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation()
                }
            }
        }
    }
}
