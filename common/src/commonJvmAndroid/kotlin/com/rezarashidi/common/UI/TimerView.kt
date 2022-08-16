package com.rezarashidi.common.UI

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.rezarashidi.common.TodoDatabaseQueries
import kotlinx.coroutines.*
import kotlinx.datetime.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class TimerData(val min: Int = 0) {
    private var startTime = Clock.System.now()

    init {
        startTime = Clock.System.now()
    }
    @OptIn(ExperimentalTime::class)
    fun passtime(): DateTimePeriod {
        return Clock.System.now().minus(startTime).toDateTimePeriod()
    }

    fun getSecend() = passtime().seconds
    fun getMinute() = passtime().minutes
    fun gethour() = passtime().hours
}

class Countdwontimer(val min: Int = 0) {
    var startTime = 0L
    var lasttime = Instant.fromEpochSeconds(startTime).toLocalDateTime(TimeZone.UTC)

    init {
        startTime = min * 60L
    }

    fun passtime(): LocalDateTime {
        lasttime = Instant.fromEpochSeconds(startTime).toLocalDateTime(TimeZone.UTC)
        startTime -= 1L
        return lasttime
    }

    fun getSecend() = passtime().second
    fun getMinute() = lasttime.minute
    fun gethour() = lasttime.hour
}

fun timernew(scope: CoroutineScope, puase: Boolean, min: Int = 0) {
}
@OptIn(ExperimentalTime::class)
@Composable
fun TimerView(min: Int = 0, db: TodoDatabaseQueries, showbuttom: MutableState<Boolean>, taskid: Long) {
    var puase by remember {
        mutableStateOf(true)
    }
    var timer by remember { mutableStateOf(0) }
    val scope= rememberCoroutineScope()
    DisposableEffect(true) {
        var time = min * 60
        scope.launch {

            while (puase) {
                if (min > 0) {
                    time--
                    delay(1000)
                } else {
                    time++
                    delay(1000)
                }

                timer = time
            }

        }

        onDispose {
            val passtime = if (min > 0) {
                min * 60 - timer
            } else {
                timer
            }

            db.insertTimerecord(
                null,
                taskid,
                passtime.toLong(),
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()
            )
        }
    }
    val durtion = remember(timer) {
        Duration.seconds(timer)
    }

    durtion.toComponents { hours, minutes, seconds, nanoseconds ->
//        println("$hours : $minutes : $seconds")
        Text("$hours : $minutes : $seconds", style = MaterialTheme.typography.h5, modifier = Modifier.border(
            3.dp, MaterialTheme.colors.primary,
            AbsoluteRoundedCornerShape(8.dp)
        ).padding(8.dp).clickable {
            val passtime = if (min > 0) {
                min * 60 - timer
            } else {
                timer
            }

            db.insertTimerecord(
                null,
                taskid,
                passtime.toLong(),
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()
            )
            puase = !puase
            showbuttom.value = true
        })
    }
}