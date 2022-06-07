package com.rezarashidi.common

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver

actual fun getPlatformName(): String {
    return "Desktop"
}
actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        TodoDatabase.Schema.create(driver)
        return driver
    }
}

actual fun DropdownMenuM(
    Mexpanded: Boolean,
    MonDismissRequest: () -> Unit,
    Mfocusable: Boolean,
    Mmodifier: Modifier,
    Moffset: DpOffset,
    Mcontent: @Composable ColumnScope.() -> Unit,
) {
    fun DropdownMenu(
        expanded: Boolean =Mexpanded,
        onDismissRequest: () -> Unit =MonDismissRequest,
        focusable: Boolean =Mfocusable,
        modifier: Modifier =Mmodifier,
        offset: DpOffset =Moffset,
        content: @Composable() (ColumnScope.() -> Unit) =Mcontent
    ) {
    }
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