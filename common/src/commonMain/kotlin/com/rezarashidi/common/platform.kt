package com.rezarashidi.common

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.MenuDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.squareup.sqldelight.db.SqlDriver

expect fun getPlatformName(): String

expect class DriverFactory {
    fun createDriver(): SqlDriver
}
@Composable
expect fun DropdownMenuM(
    Mexpanded: Boolean,
    MonDismissRequest: () -> Unit,
    Mfocusable: Boolean = true,
    Mmodifier: Modifier = Modifier,
    Moffset: DpOffset = DpOffset(0.dp, 0.dp),
    Mcontent: @Composable ColumnScope.() -> Unit
)

@Composable
expect fun DropdownMenuItemM(
    MonClick: () -> Unit,
    Mmodifier: Modifier = Modifier,
    Menabled: Boolean = true,
    McontentPadding: PaddingValues = MenuDefaults.DropdownMenuItemContentPadding,
    MinteractionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    Mcontent: @Composable RowScope.() -> Unit
)

