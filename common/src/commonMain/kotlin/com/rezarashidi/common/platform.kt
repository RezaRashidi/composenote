package com.rezarashidi.common

import com.squareup.sqldelight.db.SqlDriver

expect fun getPlatformName(): String

expect class DriverFactory {
    fun createDriver(): SqlDriver
}
