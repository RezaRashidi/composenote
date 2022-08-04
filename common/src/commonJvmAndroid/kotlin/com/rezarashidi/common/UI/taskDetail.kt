package com.rezarashidi.common.UI

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.rezarashidi.common.Tasks
import com.rezarashidi.common.TodoDatabaseQueries
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.launch
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterialApi::class, ExperimentalTime::class)
@Composable
fun taskDetail(sheetState: MutableState<Boolean>, db: TodoDatabaseQueries, task: Tasks? = null) {
    val taskName = remember { mutableStateOf(TextFieldValue(task?.Task_name ?: "")) }
    val descreption = remember { mutableStateOf(TextFieldValue(task?.Descreption ?: "")) }
    val tag = remember { mutableStateOf(TextFieldValue()) }
    var isTagDelete by remember { mutableStateOf(false) }
    val tags by remember() { mutableStateOf(mutableListOf<String>()) }

    LaunchedEffect(true) {
        if (task != null) {
            task.tags?.let { tags.addAll(it.split(",")) }
        }
    }
    val timeM = remember { mutableStateOf(TextFieldValue(task?.timeInHour?.toString() ?: "0")) }
    val timeH = remember { mutableStateOf(TextFieldValue(task?.timeInHour?.toString() ?: "0")) }
    val difficulty = listOf<String>("Low", "Medium", "High")
    val Urgency = listOf<String>("Low", "Medium", "High")
    var sliderPositionDifficulty by remember { mutableStateOf(task?.Difficulty?.toFloat() ?: 1F) }
    var sliderPositionUrgency by remember { mutableStateOf(task?.Urgency?.toFloat() ?: 1F) }
    var reward = remember { mutableStateOf(task?.reward?.toInt() ?: 0) }
    val timeHH = timeH.value.text.toIntOrNull()?.times(60) ?: 0
    val timeMM = timeM.value.text.toIntOrNull() ?: 0
    val rewardWithtime = reward.value + timeHH + timeMM
    var dailyRepeat by remember { mutableStateOf((task?.dailyRepeat == 1L)) }
    var isdone by remember { mutableStateOf((task?.isdone == 1L)) }
    val scope = rememberCoroutineScope()
    val tabDialog = remember { mutableStateOf(false) }
    var Dropdownexpanded by remember { mutableStateOf(false) }
    var showtimes = remember { mutableStateOf(false) }
    val projectlist = remember {
        val x: MutableList<Pair<String, Long?>> = db.getAllProjects().executeAsList().map {
            it.Project_name to it.id
        }.toMutableList()

        x.add(0, "select project" to null)
        return@remember x
    }
    var selectedProjectIndex by remember {
        mutableStateOf(task?.ProjectID?.let { id ->
            val x = projectlist.first {
                it.second == id
            }.first
            projectlist.indexOf(x to id)
        } ?: 0)
    }



    if (tabDialog.value) {
        AlertDialog(
            onDismissRequest = {
                tabDialog.value = false
            },
            title = {
                Text(text = "Add tag")
            },
            text = {
                OutlinedTextField(tag.value, onValueChange = {
                    tag.value = it
                })
            },
            confirmButton = {
                OutlinedButton(
                    onClick = {
                        tabDialog.value = false
                        tags.add(tag.value.text)
                        tag.value = TextFieldValue("")
                    }) {
                    Text("Save")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        tabDialog.value = false
                    }) {
                    Text("dismiss")
                }
            }
        )
    }


    Column(
        modifier = Modifier.fillMaxWidth().padding(10.dp, 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        val scrollState = rememberScrollState(0)
        Box(Modifier.fillMaxSize().weight(10f).verticalScroll(scrollState)) {
            if (!showtimes.value) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedTextField(taskName.value, onValueChange = {
                        taskName.value = it
                    }, label = {
                        Text("Task Name")
                    }, modifier = Modifier.fillMaxWidth().padding(10.dp))
                    OutlinedTextField(descreption.value, onValueChange = {
                        descreption.value = it
                    }, label = {
                        Text("Description")
                    }, modifier = Modifier.fillMaxWidth().padding(10.dp))

                    Box(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                        Text(
                            projectlist[selectedProjectIndex].first, modifier = Modifier.clickable {
                                Dropdownexpanded = true
                            }.fillMaxWidth().border(color = Color.Gray, width = 1.dp, shape = RoundedCornerShape(4.dp))
                                .padding(15.dp)
                        )



                        DropdownMenu(
                            expanded = Dropdownexpanded,
                            onDismissRequest = { Dropdownexpanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            projectlist.forEachIndexed { index, s ->
                                DropdownMenuItem(onClick = {
                                    selectedProjectIndex = index
                                    Dropdownexpanded = false
                                }) {
                                    Text(text = s.first)
                                }
                            }
                        }
                    }
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(10.dp)
                    ) {
                        Text(text = "Estimated Time:", modifier = Modifier.align(alignment = Alignment.Start))

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            OutlinedTextField(
                                timeH.value,
                                onValueChange = {
                                    timeH.value = it
                                },
                                label = {
                                    Text("hour")
                                },
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )

                            OutlinedTextField(
                                timeM.value,
                                onValueChange = {
                                    timeM.value = it
                                },
                                label = {
                                    Text("minute")
                                },
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(10.dp)
                    ) {
                        Row(modifier = Modifier.align(alignment = Alignment.Start)) {
                            Text(text = "Difficulty: ")
                            Text(text = difficulty[sliderPositionDifficulty.toInt() - 1])
                        }

                        Slider(
                            value = sliderPositionDifficulty,
                            onValueChange = {
                                sliderPositionDifficulty = it
                                reward.value = (sliderPositionDifficulty * sliderPositionUrgency).toInt() * 10
                            },
                            steps = 1,
                            valueRange = 1f..3f
                        )
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth().padding(10.dp)
                    ) {
                        Row(modifier = Modifier.align(alignment = Alignment.Start)) {
                            Text(text = "Urgency: ")
                            Text(text = Urgency[sliderPositionUrgency.toInt() - 1])
                        }


                        Slider(
                            value = sliderPositionUrgency,
                            onValueChange = {
                                sliderPositionUrgency = it
                                reward.value = (sliderPositionDifficulty * sliderPositionUrgency).toInt() * 10
                            },
                            steps = 1,
                            valueRange = 1f..3f
                        )
                    }
                    ////////////////
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    )
                    {
                        Row(
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Repeat:")
                            Checkbox(modifier = Modifier.height(intrinsicSize = IntrinsicSize.Max),
                                checked = dailyRepeat,
                                onCheckedChange = { dailyRepeat = it }
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("done:")
                            Checkbox(modifier = Modifier.height(intrinsicSize = IntrinsicSize.Max),
                                checked = isdone,
                                onCheckedChange = { isdone = it }
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "reward:")
                            Text(text = rewardWithtime.toString())
                        }
                    }
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(10.dp)
                    ) {
                        Text(text = "Tags:", modifier = Modifier.align(alignment = Alignment.Start))
                        val scrollState1 = rememberScrollState(0)
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(10.dp).horizontalScroll(scrollState1),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                { tabDialog.value = true },
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.padding(0.dp),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text("ADD", modifier = Modifier.padding(0.dp))
                            }


                            tags.forEach {
                                key(isTagDelete) {
                                    OutlinedButton({}, shape = RoundedCornerShape(20.dp)) {
                                        Text(it)
                                        Text("x", Modifier.clickable {
                                            tags.remove(it)
                                            isTagDelete = !isTagDelete
                                        }, color = Color.Red)
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (task != null) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    val times = db.getTimerecordByTaskID(task.id).executeAsList()

                    if (times.isEmpty()) {
                        Card(elevation = 5.dp, modifier = Modifier.padding(10.dp)) {
                            Text(
                                "there is no time record",
                                style = MaterialTheme.typography.h5,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    times.forEach {
                        Card(elevation = 5.dp, modifier = Modifier.padding(10.dp)) {
                            Row(modifier = Modifier.padding(10.dp)) {
                                val date = it.date.toLocalDateTime()
                                Text("Date:" + date.date.toString())
                                Text("          Start Time:${date.hour}h:${date.minute}m:${date.second}s          ")
                                val duration = Duration.seconds(it.lenth)

                                duration.toComponents { days, hours, minutes, seconds, nanoseconds ->
                                    Text(
                                        " Duration: ${hours}h:${minutes}m:${seconds}s"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Row(
            Modifier.fillMaxSize().weight(1f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                {
                    sheetState.value = false
                },
                border = BorderStroke(2.dp, color = MaterialTheme.colors.primary),
                shape = RoundedCornerShape(10.dp),
                ) {
                Text("close", style = MaterialTheme.typography.h6)
            }

            task?.let {
                OutlinedButton(
                    {
                        showtimes.value = !showtimes.value
                    },
                    border = BorderStroke(2.dp, color = MaterialTheme.colors.primary),
                    shape = RoundedCornerShape(10.dp),
                    ) {
                    Text("recorded times", style = MaterialTheme.typography.h6)
                }
            }

            OutlinedButton(
                {
                    db.insertTasks(
                        task?.id,
                        taskName.value.text,
                        descreption.value.text,
                        projectlist[selectedProjectIndex].second,
                        sliderPositionDifficulty.toLong(),
                        sliderPositionUrgency.toLong(),
                        timeH.value.text.toLong(),
                        timeM.value.text.toLong(),
                        dailyRepeat.let { if (it) return@let 1L else 0L },
                        if (tags.isNotEmpty()) tags.joinToString(",") else null, rewardWithtime.toLong(),
                        if (isdone) 1 else 0,
                        System.nanoTime(),
                        0
                    )

                    sheetState.value = false
                },
                border = BorderStroke(2.dp, color = MaterialTheme.colors.primary),
                shape = RoundedCornerShape(10.dp),
                ) {
                Text("Save", style = MaterialTheme.typography.h6)
            }
        }
    }
}




