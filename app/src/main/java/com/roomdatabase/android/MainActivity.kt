package com.roomdatabase.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.room.Room
import com.roomdatabase.android.ui.theme.RoomDatabaseTheme

class MainActivity : ComponentActivity() {

    // Initialize DB : We should use Dagger for this, this is not the most optimal way
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,  // Context
            ContactDatabase::class.java,  // Files for database
            "contacts.db"  // Name of the database
        ).build()
    }

    private val viewModel by viewModels<ContactViewModel> {
        // Factory is exact necessary
        viewModelFactory {
            object :ViewModelProvider.Factory{
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ContactViewModel(db.dao) as T
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RoomDatabaseTheme {

                val state by viewModel.state.collectAsState()  // To collect state and observe that

                ContactScreen(state = state, onEvent = viewModel::onEvent)
            }
        }
    }
}

