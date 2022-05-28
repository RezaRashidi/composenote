package com.rezarashidi.common

import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.rezarashidi.common.UI.TabLayout
import com.rezarashidi.common.UI.projectList

typealias Content = @Composable () -> Unit
val Cyan = Color(0xff03DAC5)
val mycolor=lightColors(primary = Cyan)
@Composable
fun App() {
    MaterialTheme(
        colors = mycolor
    ){


        TabLayout({ projectList() },{},{})

    }

}
