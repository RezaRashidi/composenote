package com.rezarashidi.common.UI

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rezarashidi.common.TodoDatabaseQueries
import com.rezarashidi.common.network.diliy
import kotlinx.datetime.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

data class diliyrecords(
    val donedate: LocalDate,
    val reward: Long,
    val taskcount: Long,
    val time: Long,
)
@OptIn(ExperimentalTime::class)
@Composable
fun diliyrecord(db: TodoDatabaseQueries) {
//    val nowdate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.minus(DatePeriod(0, 0, 1))
    val alltask = db.getAllTasks().executeAsList().filter {
        it.isdone == 1L
    }
    val dates = remember {
        alltask.map {
            val date =
                Instant.fromEpochMilliseconds(it.Donedate!!).toLocalDateTime(TimeZone.currentSystemDefault()).date
            diliyrecords(
                date, it.reward, 0, (it.timeInHour * 60) + it.timeInMinute
            )
        }
    }
    val newdate by remember {
        mutableStateOf(mapOf<LocalDate, diliyrecords>().toMutableMap())
    }

    dates.forEach {
        if (newdate.keys.contains(it.donedate)) {
            val x = newdate[it.donedate]!!
            newdate[it.donedate] =
                diliyrecords(it.donedate, it.reward + x.reward, x.taskcount + 1, x.time + it.time)
        } else {
            newdate[it.donedate] = diliyrecords(it.donedate, it.reward, 1, it.time)
        }
    }



    LazyColumn(modifier = Modifier.background(MaterialTheme.colors.background)) {


        item {
            Row(Modifier.fillMaxWidth().padding(10.dp), horizontalArrangement = Arrangement.Center) {
                Text("Daily Record", style = MaterialTheme.typography.h6)
            }

        }
        item {
            Card(Modifier.fillMaxWidth().padding(8.dp), elevation = 5.dp) {
                Row(Modifier.fillMaxWidth().padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("date", Modifier.weight(2f))
                    Text("time", Modifier.weight(2f))
                    Text("TCount", Modifier.weight(2f))
                    Text("Reward", Modifier.weight(2f))
                }
            }
        }

        items(newdate.values.toList()) { it ->
            Card(Modifier.fillMaxWidth().padding(8.dp), elevation = 5.dp) {
                Row(Modifier.fillMaxWidth().padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(it.donedate.toString(), Modifier.weight(2f))
                    var totaltimeD = ""

                    Duration.minutes(it.time).toComponents { hours, minutes, seconds, nanoseconds ->
                        totaltimeD = "${hours}h:${minutes}m:${seconds}s"
                    }
                    Text(totaltimeD, Modifier.weight(2f))
                    Text(it.taskcount.toString(), Modifier.weight(2f))
                    Text(it.reward.toString(), Modifier.weight(1f))
                }
            }
        }
    }
}