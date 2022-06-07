package com.rezarashidi.common

import android.content.Context
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.window.PopupProperties
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

actual fun getPlatformName(): String {

    return "Android"
}
actual class DriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(TodoDatabase.Schema, context, "Projects.db")
    }
}

@Composable
actual fun DropdownMenuM(
    Mexpanded: Boolean,
    MonDismissRequest: () -> Unit,
    Mfocusable: Boolean,
    Mmodifier: Modifier,
    Moffset: DpOffset,
    Mcontent: @Composable ColumnScope.() -> Unit,
) {
    DropdownMenu(
        expanded=Mexpanded,
        onDismissRequest=MonDismissRequest,
    modifier= Mmodifier,
    offset = Moffset,
    properties = PopupProperties(focusable = true),
    content=Mcontent
    )
}

@Composable
actual fun DropdownMenuItemM(
    MonClick: () -> Unit,
    Mmodifier: Modifier,
    Menabled: Boolean,
    McontentPadding: PaddingValues,
    MinteractionSource: MutableInteractionSource,
    Mcontent: @Composable RowScope.() -> Unit,
) {
    DropdownMenuItem(
        onClick=MonClick,
    modifier = Mmodifier,
    enabled = Menabled,
    contentPadding = McontentPadding,
    interactionSource = MinteractionSource,
    content=Mcontent
    )
}