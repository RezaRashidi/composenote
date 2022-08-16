// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.rezarashidi.common

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import javax.swing.ViewportLayout

@Preview
@Composable
fun AppPreview() {
//    val db=TodoDatabase(driver = DriverFactory().createDriver())
//    val fuck=db.todoDatabaseQueries.selectAll().executeAsList()
//    Text(fuck.toString())

    val db = remember {TodoDatabase(DriverFactory().createDriver()).todoDatabaseQueries}





    App(db)

}