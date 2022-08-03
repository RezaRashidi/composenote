package com.rezarashidi.common.UI

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rezarashidi.common.Tasks
import com.rezarashidi.common.TodoDatabaseQueries
import kotlinx.coroutines.launch
import kotlinx.datetime.*

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun dailiesList(db: TodoDatabaseQueries, dailiesListState: LazyListState) {
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
    val eventchange = remember { mutableStateOf(false) }
    val items = listOf(sortby.Default, sortby.Difficulty, sortby.Reward, sortby.Time, sortby.Urgency)
    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(0) }
    val alltask by remember(openDialog.value, openaddDialog.value, eventchange.value) {
        mutableStateOf(db.getAlldailiess().executeAsList().map {
            db.getTasksByID(it.TaskID).executeAsOne()
        }.toMutableList())
    }
    var selecttask: MutableState<Tasks?> = remember { mutableStateOf(null) }
    val tag = alltask.mapNotNull { tasks ->
        if (tasks.tags == null) null else tasks.tags.split(",")
    }.flatten().toSet()
//
    val newtasks = alltask.filter {
        if (tagPress) {
            if (it.tags == null) false else it.tags.split(",").contains(tagSelect)
        } else if (dailyreaptSelect) {
            it.dailyRepeat == 1L
        } else {
            true
        }
    }
        .sortedByDescending {
            when (items[selectedIndex]) {
                sortby.Default -> it.addTime
                sortby.Urgency -> it.Urgency
                sortby.Time -> ((it.timeInHour * 60) + it.timeInMinute)
                sortby.Reward -> it.reward
                sortby.Difficulty -> it.Difficulty
            }
        }

    Box {
        Scaffold(
            scaffoldState = scaffoldState,
            drawerGesturesEnabled = false,
//            topBar = { TopAppBar(title = { Text("To do") }, backgroundColor = Color.White) },
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
                LazyColumn(state = dailiesListState) {
                    item {
                        val x = openDialog.value //for recomposition
                        Row(
                            modifier = Modifier.fillMaxSize().padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
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
                    }

                    item(
//                        modifier = Modifier.width(IntrinsicSize.Max).padding(20.dp)
//                            .border(2.dp, MaterialTheme.colors.primary, shape = RoundedCornerShape(10.dp))
//                            .padding(20.dp), horizontalAlignment = Alignment.Start
                    ) {
                        val tasklist = alltask.filter {
                            it.isdone == 0L
                        }
                        val donetasklist = alltask.filter {
                            it.isdone == 1L
                        }.filter {
                            val timeadd =
                                Instant.fromEpochMilliseconds(db.getdailiessBytaskID(it.id).executeAsOne().DateAdd)
                                    .toLocalDateTime(TimeZone.currentSystemDefault()).date
                            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                            now.minus(timeadd).days == 0
                        }
                        val totaltime =
                            kotlinx.datetime.Instant.fromEpochSeconds((tasklist.sumOf { it.timeInHour * 60 + it.timeInMinute } * 60) + (donetasklist.sumOf { it.timeInHour * 60 + it.timeInMinute } * 60))
                                .toLocalDateTime(
                                    TimeZone.UTC
                                )
                        val totalSpenTimeinSecend = tasklist.sumOf {
                            db.getTimerecordByTaskID(it.id).executeAsList().sumOf { it.lenth }
                        } + donetasklist.sumOf {
                            db.getTimerecordByTaskID(it.id).executeAsList().sumOf { it.lenth }
                        }
                        val totalspendtime =
                            kotlinx.datetime.Instant.fromEpochSeconds(totalSpenTimeinSecend).toLocalDateTime(
                                TimeZone.UTC
                            )
                        val xx = tasklist.count() + donetasklist.count()

                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                "Tasks:${donetasklist.count()}/ ${xx}",
                                modifier = Modifier.padding(5.dp)
                            )
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth().height(7.dp),
                                progress = donetasklist.count() / (tasklist.count() + donetasklist.count()).toFloat()
                            )


                            Text(
                                "Reward: ${donetasklist.sumOf { it.reward }}/ ${donetasklist.sumOf { it.reward } + tasklist.sumOf { it.reward }}",
                                modifier = Modifier.padding(5.dp)
                            )
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth().height(7.dp),
                                progress = donetasklist.sumOf { it.reward } / (donetasklist.sumOf { it.reward } + tasklist.sumOf { it.reward }).toFloat()
                            )

                            Text(
                                "Time: ${totalspendtime.hour}h:${totalspendtime.minute}m:${totalspendtime.second}s" + "/" + "${totaltime.hour}h:${totaltime.minute}m:${totaltime.second}s",
                                modifier = Modifier.padding(5.dp)
                            )
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth().height(7.dp),
                                progress = (totalSpenTimeinSecend).toFloat() / ((tasklist.sumOf { it.timeInHour * 60 + it.timeInMinute } * 60) + (donetasklist.sumOf { it.timeInHour * 60 + it.timeInMinute } * 60))
                            )
                        }



                    }



                    items(newtasks.filter {
                        it.isdone != 1L
                    }, key = { it.addTime }) { it ->
                        var newit by mutableStateOf(it)
                        val dismissState = rememberDismissState(
                            initialValue = DismissValue.Default,
                            confirmStateChange = { DismissValueS ->
                                if (DismissValueS == DismissValue.DismissedToEnd) {
                                    if (newit.isdone == 0L) {
                                        db.getTaskdoneByid(it.id)
                                        newit = it.copy(isdone = 1L)
                                        eventchange.value = !eventchange.value
                                    } else {
                                        db.getTaskundoneByid(it.id)
                                        newit = it.copy(isdone = 0L)
                                        eventchange.value = !eventchange.value
                                    }
                                }

                                DismissValueS != DismissValue.DismissedToEnd
                            })
                        SwipeToDismiss(
                            state = dismissState,
                            modifier = Modifier.animateItemPlacement(),
                            directions = setOf(DismissDirection.EndToStart, DismissDirection.StartToEnd),
                            /***  create dismiss alert Background */
                            background = {
                                val direction = dismissState.dismissDirection
                                if (direction == DismissDirection.EndToStart) {
                                    if (!dismissState.isDismissed(DismissDirection.EndToStart))
                                        Row(
                                            modifier = Modifier.padding(20.dp)
                                                .fillMaxSize(),
                                            horizontalArrangement = Arrangement.End,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
//                                            Text("Delete", color = Color.Red, style = MaterialTheme.typography.h4)
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Localized description",
                                                tint = Color.Cyan,
                                                modifier = Modifier.scale(2f)
                                            )
                                        }

                                    if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                                        db.deletedailiess(it.id)
                                        alltask.remove(it)
                                        eventchange.value = !eventchange.value
                                    }
                                }

                                if (direction == DismissDirection.StartToEnd) {
                                    if (!dismissState.isDismissed(DismissDirection.StartToEnd))
                                        Row(
                                            modifier = Modifier.padding(20.dp)
                                                .fillMaxSize(),
                                            horizontalArrangement = Arrangement.Start,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
//                                            Text("Done", color = Color.Cyan, style = MaterialTheme.typography.h4)
                                            Icon(
                                                Icons.Default.Done,
                                                contentDescription = "Localized description",
                                                tint = Color.Cyan,
                                                modifier = Modifier.scale(2f)
                                            )
                                        }
                                }
                            },
                            /**** Dismiss Content */
                            dismissContent = {
                                if (!dismissState.isDismissed(DismissDirection.EndToStart) && !openDialog.value) {
                                    taskItem(db = db, newit, showbuttom, openDialog, selecttask, eventchange)
                                }
                            },
                            /*** Set Direction to dismiss */
                        )
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
