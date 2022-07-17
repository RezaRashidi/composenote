package com.rezarashidi.common

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.rezarashidi.common.UI.*

typealias Content = @Composable () -> Unit
val Cyan = Color(0xff03DAC5)
val mycolor=lightColors(primary = Cyan, background = Color(0xffF6F7FB), surface = Color.White)
@Composable
fun App(db: TodoDatabaseQueries) {
    MaterialTheme(
        colors = mycolor,
        shapes = Shapes(RoundedCornerShape(15),RoundedCornerShape(15),RoundedCornerShape(15))
    ){


        TabLayout({ projectList(db) },{ taskList(db) },{dailiesList(db)},db)

    }

}



