package com.rezarashidi.common.UI

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.rezarashidi.common.Tasks
import com.rezarashidi.common.TodoDatabaseQueries
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.math.roundToInt

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun taskItem(
    db: TodoDatabaseQueries,
    Task: Tasks,
    showbuttom1: MutableState<Boolean>,
    openDialog: MutableState<Boolean>,
    selecttask: MutableState<Tasks?>,
) {
//    val state = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val taskName by remember() { mutableStateOf(Task.Task_name) }
    var reward by remember() { mutableStateOf(Task.reward) }
    val timeM = remember() { Task.timeInMinute }
    val timeH = remember() { Task.timeInHour }
    val levels = listOf<String>("Low", "Medium", "High")
    val showbuttom: MutableState<Boolean> = remember {
        mutableStateOf(true)
    }
    val spendtime by remember(showbuttom.value) {
        mutableStateOf(
            Instant.fromEpochSeconds(db.getTimerecordByTaskID(Task.id).executeAsList().sumOf { it.lenth })
                .toLocalDateTime(
                    TimeZone.UTC
                )
        )
    }
    val spendtimeH = spendtime.hour
    val spendtimeM = spendtime.minute
    val spendtimeS = spendtime.second
    val scope = rememberCoroutineScope()
    var showtimer by remember {
        mutableStateOf(false)
    }
    if (showbuttom.value) {
        showbuttom1.value = !showbuttom1.value
    }
    var showselectTime by remember {
        mutableStateOf(false)
    }
    var min by remember {
        mutableStateOf(0)
    }
    val alpha: Float by animateFloatAsState(if (!showselectTime) 1f else 0f)

    Card(Modifier.fillMaxWidth().padding(15.dp), elevation = 5.dp) {
        BoxWithConstraints(modifier = Modifier.clickable {
            selecttask.value = Task
            openDialog.value = true
            scope.launch {
                showselectTime = false
            }
        }, contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier.fillMaxWidth().graphicsLayer(alpha = alpha),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(20.dp).fillMaxWidth()
                ) {
                    Column {
                        Text(taskName, style = MaterialTheme.typography.h4)


                        Text(
                            "$timeH:$timeM:0 / " + "${spendtimeH}:${spendtimeM}:${spendtimeS}",
                            modifier = Modifier.padding(0.dp,5.dp),
                            style = MaterialTheme.typography.subtitle2
                        )
                        Row (
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.width(IntrinsicSize.Max)
                        ){
                            Text(
                                "urgency:${levels[Task.Urgency.toInt() - 1]} ",
                                modifier = Modifier.padding(0.dp,0.dp,10.dp,0.dp),
                                style = MaterialTheme.typography.subtitle2
                            )
                            Text(
                                "Diffculty: ${levels[Task.Difficulty.toInt() - 1]}",
                                modifier = Modifier.padding(0.dp,0.dp,10.dp,0.dp),
                                style = MaterialTheme.typography.subtitle2
                            )
                            Text(
                                "reward:${Task.reward}xp",
                                modifier = Modifier.padding(0.dp,0.dp,0.dp,0.dp),
                                style = MaterialTheme.typography.subtitle2
                            )
                        }
                    }


                    if (showbuttom.value) {
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    showselectTime = true
                                }
                            },
                            border = BorderStroke(2.dp, MaterialTheme.colors.primary)
                        ) {
                            Text(
                                "Start", style = MaterialTheme.typography.h5
                            )
                        }
                    } else {
                        TimerView(min, db, showbuttom, Task.id)
                    }
                }
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().height(7.dp),
                    progress = if (spendtime.toInstant(TimeZone.UTC).epochSeconds == 0L) 0F else (spendtime.toInstant(
                        TimeZone.UTC
                    ).epochSeconds) / ((timeH * 360) + (timeM * 60)).toFloat()
                )
            }
            AnimatedVisibility(showselectTime, enter = fadeIn() + scaleIn()) {
                Row(modifier = Modifier.fillMaxWidth().clickable {
                    scope.launch {
                        showselectTime = false
                    }
                }, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    OutlinedButton(modifier = Modifier.padding(5.dp), onClick = {
                        showbuttom.value = false
                        min = 15
                        showselectTime = false
                    }) {
                        Text("15M", style = MaterialTheme.typography.h5)
                    }
                    OutlinedButton(modifier = Modifier.padding(5.dp), onClick = {
                        showbuttom.value = false
                        min = 25
                        showselectTime = false
                    }) {
                        Text("25M", style = MaterialTheme.typography.h5)
                    }
                    OutlinedButton(modifier = Modifier.padding(5.dp), onClick = {
                        showbuttom.value = false
                        min = 35
                        showselectTime = false
                    }) {
                        Text("35M", style = MaterialTheme.typography.h5)
                    }
                    OutlinedButton(modifier = Modifier.padding(5.dp), onClick = {
                        showbuttom.value = false
                        showselectTime = false
                    }) {
                        Text("start", style = MaterialTheme.typography.h5)
                    }
                }
            }
        }
    }
}