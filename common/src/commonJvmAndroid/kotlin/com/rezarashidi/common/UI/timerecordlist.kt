package com.rezarashidi.common.UI

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rezarashidi.common.TodoDatabaseQueries
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun timerecordlist(db: TodoDatabaseQueries) {
    val scrollState = rememberScrollState(0)
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background).verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        val times = db.getAllTimerecord().executeAsList()

        if (times.isEmpty()) {
            Card(elevation = 5.dp, modifier = Modifier.padding(10.dp)) {
                Text(
                    "There is no time record",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
        if (times.isNotEmpty()) {
            Card(elevation = 5.dp, modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                Row(modifier = Modifier.padding(10.dp)) {
                    Text("task", Modifier.weight(1f))
                    Text("date", Modifier.weight(2f))
                    Text(
                        "Duration", Modifier.weight(1f)
                    )
                }
            }
        }
        times.forEach {
            Card(elevation = 5.dp, modifier = Modifier.fillMaxWidth().padding(10.dp,10.dp)) {
                Row(modifier = Modifier.padding(10.dp)) {
                    val date = it.date.toLocalDateTime()

                    Text(db.getTasksByID(it.TaskID).executeAsOne().Task_name, Modifier.weight(1f))


                    Text(date.date.toString() + ":${date.hour}h:${date.minute}m:${date.second}s", Modifier.weight(2f))
                    val duration = Duration.seconds(it.lenth)

                    duration.toComponents { days, hours, minutes, seconds, nanoseconds ->
                        Text(
                            "Duration: ${hours}h:${minutes}m:${seconds}s", Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}