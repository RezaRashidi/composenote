package com.rezarashidi.android

import com.rezarashidi.common.App
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.remember
import com.rezarashidi.common.DriverFactory
import com.rezarashidi.common.TodoDatabase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                
                val db = remember { TodoDatabase(DriverFactory(this).createDriver()).todoDatabaseQueries}

                App(db)
            }
        }
    }
}