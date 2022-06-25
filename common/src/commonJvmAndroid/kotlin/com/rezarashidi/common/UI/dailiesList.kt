package com.rezarashidi.common.UI

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rezarashidi.common.Tasks
import com.rezarashidi.common.TodoDatabaseQueries
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@Composable
fun dailiesList(db: TodoDatabaseQueries) {
    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val openaddDialog = remember { mutableStateOf(false) }
    val openDialog = remember { mutableStateOf(false) }
    val alpha: Float by animateFloatAsState(if (!openDialog.value) 0f else 0.5f)
    val alpha1: Float by animateFloatAsState(if (!openaddDialog.value) 0f else 0.5f)
    var tagPress by remember { mutableStateOf(false) }
    var tagSelect by remember { mutableStateOf("") }
    var dailyreaptSelect by remember { mutableStateOf(false) }
    val ScrollState = rememberScrollState()
    val showbuttom: MutableState<Boolean> = remember {
        mutableStateOf(true)
    }
    var selecttask: MutableState<Tasks?> = remember { mutableStateOf(null) }


    Box {
        Scaffold(
            scaffoldState = scaffoldState,
            drawerGesturesEnabled = false,
            topBar = { TopAppBar(title = { Text("To do") }, backgroundColor = Color.White) },
            floatingActionButtonPosition = FabPosition.Center,
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    openaddDialog.value = true
                    scope.launch {}
                }, backgroundColor = Color.White) {
                    Text(
                        "+",
                        modifier = Modifier.padding(2.dp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.primary,
                        style = MaterialTheme.typography.h4
                    )
                }
            },
            drawerContent = { Text(text = "drawerContent") },
            content = {
                Column(modifier = Modifier.verticalScroll(scrollState), horizontalAlignment = Alignment.CenterHorizontally) {
                    val tasklist = db.getAlldailiess().executeAsList().map { db.getTasksByID(it.TaskID).executeAsOne() }
                    val totaltime =
                        kotlinx.datetime.Instant.fromEpochSeconds(tasklist.sumOf { it.timeInHour * 60 + it.timeInMinute } * 60)
                            .toLocalDateTime(
                                TimeZone.UTC
                            )
                    val totalSpenTimeinSecend = tasklist.map {
                        db.getTimerecordByTaskID(it.id).executeAsList().sumOf { it.lenth }
                    }.sum()
                    val totalspendtime = remember(showbuttom.value) {
                        kotlinx.datetime.Instant.fromEpochSeconds(totalSpenTimeinSecend).toLocalDateTime(
                            TimeZone.UTC
                        )
                    }
                    val x = openDialog.value //for recomposition
                    val tag = tasklist.mapNotNull { tasks ->
                        if (tasks.tags == null) null else tasks.tags.split(",")
                    }.flatten().toSet()


                    Row(
                        modifier = Modifier.fillMaxSize().padding(10.dp), verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "tags:"
                        )

                        OutlinedButton({
                            dailyreaptSelect = !dailyreaptSelect
                        }, modifier = Modifier.padding(5.dp), shape = RoundedCornerShape(50.dp)) {
                            Text("Daily Repeat")
                        }
                        tag.forEach {
                            OutlinedButton({
                                tagPress = !tagPress
                                tagSelect = it
                            }, modifier = Modifier.padding(5.dp), shape = RoundedCornerShape(50.dp)) {
                                Text(it)
                            }
                        }
                    }

                    Column(
                        modifier = Modifier.width(IntrinsicSize.Max).padding(20.dp)
                            .border(2.dp, MaterialTheme.colors.primary, shape = RoundedCornerShape(10.dp))
                            .padding(20.dp), horizontalAlignment = Alignment.Start
                    ) {

                            Text("total number: ${tasklist.count()}", modifier = Modifier.padding(5.dp))
                            Text(
                                "total reward: ${tasklist.sumOf { it.reward }}",
                                modifier = Modifier.padding(5.dp)
                            )


                            Text(
                                "total time: ${totaltime.hour}H : ${totaltime.minute}M : ${totaltime.second}S",
                                modifier = Modifier.padding(5.dp)
                            )
                            Text(
                                "total spend time:  ${totalspendtime.hour}H : ${totalspendtime.minute}M : ${totalspendtime.second}S",
                                modifier = Modifier.padding(5.dp)
                            )

                    }

                    if (tagPress) {
                        tasklist.filter {
                            if (it.tags == null) false else it.tags.split(",").contains(tagSelect)
                        }.forEach {
                            val dismissState = rememberDismissState(initialValue = DismissValue.Default)
                            SwipeToDismiss(
                                state = dismissState,
                                /***  create dismiss alert Background */
                                background = {
                                    val direction = dismissState.dismissDirection
                                    if (direction == DismissDirection.EndToStart) {
                                        if (!dismissState.isDismissed(DismissDirection.EndToStart)) Row(
                                            modifier = Modifier.padding(20.dp).fillMaxSize(),
                                            horizontalArrangement = Arrangement.End,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Delete", color = Color.Red, style = MaterialTheme.typography.h4)
                                        }

                                        if (dismissState.isDismissed(DismissDirection.EndToStart)) db.deletedailiess(it.id)
                                    }

                                    if (direction == DismissDirection.StartToEnd) {
                                        if (!dismissState.isDismissed(DismissDirection.StartToEnd)) Row(
                                            modifier = Modifier.padding(20.dp).fillMaxSize(),
                                            horizontalArrangement = Arrangement.Start,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Done", color = Color.Cyan, style = MaterialTheme.typography.h4)
                                        }

                                        if (dismissState.isDismissed(DismissDirection.StartToEnd)) db.getTaskdoneByid(it.id)
                                    }
                                },
                                /**** Dismiss Content */
                                dismissContent = {
                                    if (!dismissState.isDismissed(DismissDirection.EndToStart) && !openDialog.value)
                                        taskItem(db = db, it, showbuttom, openDialog, selecttask)
                                },
                                /*** Set Direction to dismiss */
                                directions = setOf(DismissDirection.EndToStart, DismissDirection.StartToEnd),
                            )
                        }
                    } else if (dailyreaptSelect) {
                        tasklist.filter {
                            it.dailyRepeat == 1L
                        }.forEach {
                            val dismissState = rememberDismissState(initialValue = DismissValue.Default)
                            SwipeToDismiss(
                                state = dismissState,
                                /***  create dismiss alert Background */
                                background = {
                                    val direction = dismissState.dismissDirection
                                    if (direction == DismissDirection.EndToStart) {
                                        if (!dismissState.isDismissed(DismissDirection.EndToStart)) Row(
                                            modifier = Modifier.padding(20.dp).fillMaxSize(),
                                            horizontalArrangement = Arrangement.End,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Delete", color = Color.Red, style = MaterialTheme.typography.h4)
                                        }

                                        if (dismissState.isDismissed(DismissDirection.EndToStart)) db.deletedailiess(it.id)
                                    }

                                    if (direction == DismissDirection.StartToEnd) {
                                        if (!dismissState.isDismissed(DismissDirection.StartToEnd)) Row(
                                            modifier = Modifier.padding(20.dp).fillMaxSize(),
                                            horizontalArrangement = Arrangement.Start,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Done", color = Color.Cyan, style = MaterialTheme.typography.h4)
                                        }

                                        if (dismissState.isDismissed(DismissDirection.StartToEnd)) db.getTaskdoneByid(it.id)
                                    }
                                },
                                /**** Dismiss Content */
                                dismissContent = {
                                    if (!dismissState.isDismissed(DismissDirection.EndToStart) && !openDialog.value)
                                        taskItem(db = db, it, showbuttom, openDialog, selecttask)
                                },
                                /*** Set Direction to dismiss */
                                directions = setOf(DismissDirection.EndToStart, DismissDirection.StartToEnd),
                            )
                        }
                    } else {
                        tasklist.forEach {
                            val dismissState = rememberDismissState(initialValue = DismissValue.Default)
                            SwipeToDismiss(
                                state = dismissState,
                                /***  create dismiss alert Background */
                                background = {
                                    val direction = dismissState.dismissDirection
                                    if (direction == DismissDirection.EndToStart) {
                                        if (!dismissState.isDismissed(DismissDirection.EndToStart)) Row(
                                            modifier = Modifier.padding(20.dp).fillMaxSize(),
                                            horizontalArrangement = Arrangement.End,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Delete", color = Color.Red, style = MaterialTheme.typography.h4)
                                        }

                                        if (dismissState.isDismissed(DismissDirection.EndToStart)) db.deletedailiess(it.id)
                                    }

                                    if (direction == DismissDirection.StartToEnd) {
                                        if (!dismissState.isDismissed(DismissDirection.StartToEnd)) Row(
                                            modifier = Modifier.padding(20.dp).fillMaxSize(),
                                            horizontalArrangement = Arrangement.Start,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Done", color = Color.Cyan, style = MaterialTheme.typography.h4)
                                        }

                                        if (dismissState.isDismissed(DismissDirection.StartToEnd)) db.getTaskdoneByid(it.id)
                                    }
                                },
                                /**** Dismiss Content */
                                dismissContent = {
                                    if (!dismissState.isDismissed(DismissDirection.EndToStart) && !openDialog.value)
                                        taskItem(db = db, it, showbuttom, openDialog, selecttask)
                                },
                                /*** Set Direction to dismiss */
                                directions = setOf(DismissDirection.EndToStart, DismissDirection.StartToEnd),
                            )
                        }
                    }
                }
            },
        )

        if (openaddDialog.value) {
            Box(modifier = Modifier.background(color = Color.Black.copy(alpha = alpha1)).fillMaxSize().clickable {
                openaddDialog.value = false
            })
            Box(
                modifier = Modifier.clickable {
                }.align(alignment = Alignment.Center).padding(20.dp).clip(shape = RoundedCornerShape(3))
                    .background(color = Color.White).verticalScroll(ScrollState)
            ) {
                dailiesAddList(openaddDialog, db)
            }
        }



        if (openDialog.value) {
            Box(modifier = Modifier.background(color = Color.Black.copy(alpha = alpha)).fillMaxSize().clickable {
                openDialog.value = false
                selecttask.value = null
            })
            Box(
                modifier = Modifier.clickable {
                }.align(alignment = Alignment.Center).padding(20.dp).clip(shape = RoundedCornerShape(3))
                    .background(color = Color.White).verticalScroll(ScrollState)
            ) {
                taskDetail(openDialog, db, selecttask.value)
            }
        }
    }
//}
}
