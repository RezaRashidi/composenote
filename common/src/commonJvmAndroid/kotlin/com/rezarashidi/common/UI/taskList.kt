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
import androidx.compose.material.icons.filled.Sort
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rezarashidi.common.Tasks
import com.rezarashidi.common.TodoDatabaseQueries

enum class sortby {
    Default, Reward, Difficulty, Urgency, Time
}
@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun taskList(db: TodoDatabaseQueries, listState: LazyListState) {
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
    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(0) }
    val alltask by remember(openDialog.value) { mutableStateOf(db.getAllTasks().executeAsList()) }
    val items = listOf(sortby.Default, sortby.Difficulty, sortby.Reward, sortby.Time, sortby.Urgency)
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
    val tag = alltask.mapNotNull { tasks ->
        if (tasks.tags == null) null else tasks.tags.split(",")
    }.flatten().toSet()


    Box {
        Scaffold(
            scaffoldState = scaffoldState,
            drawerGesturesEnabled = false,
            topBar = {
                TopAppBar(title = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Filled.Sort, "backIcon")
                    }
//                    Text("Sort by "+items[selectedIndex].toString(),modifier = Modifier.fillMaxWidth().clickable(onClick = { expanded = true }), textAlign = TextAlign.End)
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
//                        modifier = Modifier.fillMaxWidth()
                    )
                    {
                        items.forEachIndexed { index, s ->
                            DropdownMenuItem(onClick = {
                                selectedIndex = index
                                expanded = false
                            }) {
                                Text(text = s.name)
                            }
                        }
                    }
                }, backgroundColor = Color.White)
            },
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

                LazyColumn(state = listState) {
                    item {
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



                    items(newtasks, key = {it.addTime}) { it ->
                        val dismissState = rememberDismissState(initialValue = DismissValue.Default)
                        SwipeToDismiss(
                            state = dismissState,
                            modifier = Modifier.animateItemPlacement(),
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
