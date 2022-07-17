package com.rezarashidi.common.UI

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rezarashidi.common.Projects
import com.rezarashidi.common.Tasks
import com.rezarashidi.common.TodoDatabaseQueries
import kotlinx.coroutines.launch
import java.util.logging.XMLFormatter

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@Composable
fun taskList(db: TodoDatabaseQueries) {
    val selecttask: MutableState<Tasks?> = remember { mutableStateOf(null) }
    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val openDialog = remember() { mutableStateOf(false) }
    val alpha: Float by animateFloatAsState(if (!openDialog.value) 0f else 0.5f)
    var tagPress by remember { mutableStateOf(false) }
    var tagSelect by remember { mutableStateOf("") }
    var dailyreaptSelect by remember { mutableStateOf(false) }
    val ScrollState = rememberScrollState()
    val showbuttom: MutableState<Boolean> = remember {
        mutableStateOf(true)
    }
    val alltask  by remember(openDialog.value) {  mutableStateOf( db.getAllTasks().executeAsList() )}

    Box {
        Scaffold(
            scaffoldState = scaffoldState,
            drawerGesturesEnabled = false,
            topBar = { TopAppBar(title = { Text("To do") }, backgroundColor = Color.White) },
            floatingActionButtonPosition = FabPosition.Center,
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    selecttask.value = null
                    openDialog.value = true
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
                Column(modifier = Modifier.verticalScroll(scrollState)) {
//                    val x = openDialog.value //for recomposition
//                    val x1 = selecttask.value //for recomposition
                    val tag = alltask.mapNotNull { tasks ->
                        if (tasks.tags == null) null else tasks.tags.split(",")
                    }.flatten().toSet()

                    Row(
                        modifier = Modifier.fillMaxSize().padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
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

                    if (tagPress) {

                        alltask.filter {
                            if (it.tags == null) false else it.tags.split(",").contains(tagSelect)
                        }.forEach {
                            val dismissState = rememberDismissState(initialValue = DismissValue.Default)
                            SwipeToDismiss(
                                state = dismissState,
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
                                                Text("Delete", color = Color.Red, style = MaterialTheme.typography.h4)
                                            }

                                        if (dismissState.isDismissed(DismissDirection.EndToStart))
                                            db.deleteTask(it.id)
                                    }

                                    if (direction == DismissDirection.StartToEnd) {
                                        if (!dismissState.isDismissed(DismissDirection.StartToEnd))
                                            Row(
                                                modifier = Modifier.padding(20.dp)
                                                    .fillMaxSize(),
                                                horizontalArrangement = Arrangement.Start,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("Done", color = Color.Cyan, style = MaterialTheme.typography.h4)
                                            }

                                        if (dismissState.isDismissed(DismissDirection.StartToEnd))
                                            db.getTaskdoneByid(it.id)
                                    }
                                },
                                /**** Dismiss Content */
                                dismissContent = {
                                    if (!dismissState.isDismissed(DismissDirection.EndToStart) && !openDialog.value) {
                                        taskItem(db = db, it, showbuttom, openDialog, selecttask)
                                    }
                                },
                                /*** Set Direction to dismiss */
                                directions = setOf(DismissDirection.EndToStart, DismissDirection.StartToEnd),
                            )
                        }
                    } else if (dailyreaptSelect) {
                        alltask.filter {
                            it.dailyRepeat == 1L
                        }.forEach {
                            val dismissState = rememberDismissState(initialValue = DismissValue.Default)
                            SwipeToDismiss(
                                state = dismissState,
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
                                                Text("Delete", color = Color.Red, style = MaterialTheme.typography.h4)
                                            }

                                        if (dismissState.isDismissed(DismissDirection.EndToStart))
                                            db.deleteTask(it.id)
                                    }

                                    if (direction == DismissDirection.StartToEnd) {
                                        if (!dismissState.isDismissed(DismissDirection.StartToEnd))
                                            Row(
                                                modifier = Modifier.padding(20.dp)
                                                    .fillMaxSize(),
                                                horizontalArrangement = Arrangement.Start,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("Done", color = Color.Cyan, style = MaterialTheme.typography.h4)
                                            }

                                        if (dismissState.isDismissed(DismissDirection.StartToEnd))
                                            db.getTaskdoneByid(it.id)
                                    }
                                },
                                /**** Dismiss Content */
                                dismissContent = {
                                    if (!dismissState.isDismissed(DismissDirection.EndToStart) && !openDialog.value) {
                                        taskItem(db = db, it, showbuttom, openDialog, selecttask)
                                    }
                                },
                                /*** Set Direction to dismiss */
                                directions = setOf(DismissDirection.EndToStart, DismissDirection.StartToEnd),
                            )
                        }
                    } else {
                        alltask.forEach {
                            val dismissState = rememberDismissState(initialValue = DismissValue.Default)
                            SwipeToDismiss(
                                state = dismissState,
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
                                                Text("Delete", color = Color.Red, style = MaterialTheme.typography.h4)
                                            }

                                        if (dismissState.isDismissed(DismissDirection.EndToStart))
                                            db.deleteTask(it.id)
                                    }

                                    if (direction == DismissDirection.StartToEnd) {
                                        if (!dismissState.isDismissed(DismissDirection.StartToEnd))
                                            Row(
                                                modifier = Modifier.padding(20.dp)
                                                    .fillMaxSize(),
                                                horizontalArrangement = Arrangement.Start,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("Done", color = Color.Cyan, style = MaterialTheme.typography.h4)
                                            }

                                        if (dismissState.isDismissed(DismissDirection.StartToEnd))
                                            db.getTaskdoneByid(it.id)
                                    }
                                },
                                /**** Dismiss Content */
                                dismissContent = {
                                    if (!dismissState.isDismissed(DismissDirection.EndToStart) && !openDialog.value) {
                                        taskItem(db = db, it, showbuttom, openDialog, selecttask)
                                    }
                                },
                                /*** Set Direction to dismiss */
                                directions = setOf(DismissDirection.EndToStart, DismissDirection.StartToEnd),
                            )
                        }
                    }
                }
            },
        )

        if (openDialog.value) {
            Box(modifier = Modifier.background(color = Color.Black.copy(alpha = alpha)).fillMaxSize().clickable {
                selecttask.value = null
                openDialog.value = false
            })
            Box(
                modifier = Modifier.clickable {
                }.align(alignment = Alignment.Center).padding(20.dp)
                    .clip(shape = RoundedCornerShape(3)).background(color = Color.White).verticalScroll(ScrollState)
            ) {
                taskDetail(openDialog, db, selecttask.value)
            }
        }
    }
//}
}
