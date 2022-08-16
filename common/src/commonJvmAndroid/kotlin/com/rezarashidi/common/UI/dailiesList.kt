package com.rezarashidi.common.UI

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rezarashidi.common.Tasks
import com.rezarashidi.common.TodoDatabaseQueries
import com.rezarashidi.common.network.networktasks
import com.rezarashidi.common.network.userinfo
import io.ktor.util.*
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import uinfo

@OptIn(
    ExperimentalAnimationApi::class, ExperimentalMaterialApi::class, ExperimentalFoundationApi::class,
    ExperimentalGraphicsApi::class
)
@Composable
fun dailiesList(db: TodoDatabaseQueries, dailiesListState: LazyListState, network: networktasks) {
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
    val form = remember {
        mutableStateOf(false)
    }

    var leaderbordranking by remember {
        mutableStateOf(1F to 1)
    }
    var showleaderbor = remember { mutableStateOf(false) }
    LaunchedEffect(showleaderbor.value) {
        val x = db.getleaderbordranking().executeAsList()

        if (x.isNotEmpty()) {

            val xx = x.first()

            leaderbordranking = xx.in0ex to xx.rank.toInt()
        }
    }
    val username = remember(showleaderbor.value) {
        network.getusername()
    }
    val eventchange = remember { mutableStateOf(false) }
    val items = listOf(sortby.Default, sortby.Difficulty, sortby.Reward, sortby.Time, sortby.Urgency)
    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(0) }
    val alltask by remember(openDialog.value, openaddDialog.value, eventchange.value) {
        mutableStateOf(db.getAlldailiess().executeAsList().filter {
            it.del == null
        }.map {
            db.getTasksByID(it.TaskID).executeAsOne()
        }.filter {
            it.Del == 0L
        }.toMutableList())
    }
    var selecttask: MutableState<Tasks?> = remember { mutableStateOf(null) }
    val tag = alltask.mapNotNull { tasks ->
        if (tasks.tags == null) null else tasks.tags.split(",")
    }.flatten().toSet()
//
    val newtasks = remember(openDialog.value, openaddDialog.value, eventchange.value, tagPress, dailyreaptSelect) {
        alltask.filter {
            (it.isdone != 1L)
        }.filter {
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
                    sortby.Isdone -> it.isdone
                }
            }
    }

    if (scaffoldState.drawerState.isClosed) {
        showleaderbor.value = false
    }

    Box {
        Scaffold(
            scaffoldState = scaffoldState,
            drawerGesturesEnabled = true,
            drawerShape = RoundedCornerShape(0),
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
            drawerContent = { drawerContent(db, scaffoldState, showleaderbor) },
            content = {
                LazyColumn(state = dailiesListState) {
                    item {
                        val lazyrowscrollstate = rememberLazyListState()

                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(10.dp, 0.dp)) {
                            IconButton(onClick = {
                                scope.launch {
                                    scaffoldState.drawerState.open()
                                }
                            }) {
                                Icon(Icons.Filled.Menu, "backIcon")
                            }

                            Box(modifier = Modifier.weight(0.8f)) {
                                LazyRow(
                                    verticalAlignment = Alignment.CenterVertically, state = lazyrowscrollstate
                                ) {
                                    item {
                                    }
                                    item {
                                        OutlinedButton({
                                            dailyreaptSelect = !dailyreaptSelect
                                        }, modifier = Modifier.padding(5.dp), shape = RoundedCornerShape(50.dp)) {
                                            Text("Daily Repeat")
                                        }
                                    }
                                    items(tag.toList()) { it ->
                                        OutlinedButton({
                                            tagPress = !tagPress
                                            tagSelect = it
                                        }, modifier = Modifier.padding(3.dp), shape = RoundedCornerShape(50.dp)) {
                                            Text(it)
                                        }
                                    }
                                }
                            }




                            IconButton(onClick = { expanded = true }) {
                                Icon(Icons.Filled.Sort, "backIcon")
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
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
                            }
                        }
                    }


                    item(
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
                            val x = db.getTimerecordByTaskID(it.id).executeAsList().sumOf { it.lenth }
                            return@sumOf if (x == 0L) {
                                (it.timeInHour * 3600) + (it.timeInMinute * 60)
                            } else {
                                x
                            }
                        }
                        val totalspendtime =
                            kotlinx.datetime.Instant.fromEpochSeconds(totalSpenTimeinSecend).toLocalDateTime(
                                TimeZone.UTC
                            )
                        val xx = tasklist.count() + donetasklist.count()

                        Column(modifier = Modifier.padding(20.dp).clickable {
                            scope.launch {
                                scaffoldState.drawerState.open()
                                showleaderbor.value = true
                            }
                        }) {
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
                            val leaderbordtext   by mutableStateOf (  if (username == null) {
                                "Tap To enable leaderboard"
                            } else {
                                "leaderboard ranking  " + "${leaderbordranking.second}"
                            }

                            )



                            Text(
                                leaderbordtext,
                                modifier = Modifier.padding(5.dp).clickable {

                                }
                            )
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth().height(7.dp),
                                progress = leaderbordranking.first.toFloat()
                            )
                        }
                    }


                    item {
                        val donelist = db.getAllTasks().executeAsList().filter { it.isdone == 1L }
                        if (donelist.count() >= 5) {
                            val userinfo = db.getuserinfo().executeAsList()

                            if (userinfo.isEmpty()) {
                                if (!form.value) {
                                    Card(Modifier.fillMaxWidth().padding(15.dp).clickable {
                                        form.value = !form.value
                                    }, elevation = 5.dp) {
                                        Row(
                                            Modifier.fillMaxWidth().padding(10.dp),
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Text("Please fill the form", style = MaterialTheme.typography.h6)
                                        }
                                    }
                                }
                                AnimatedVisibility(form.value, enter = fadeIn() + scaleIn()) {
                                    Card(
                                        Modifier.fillMaxWidth().padding(15.dp),
                                        elevation = 5.dp,
                                        shape = RoundedCornerShape(5.dp)
                                    ) {
                                        uinfo(db, network, form)
                                    }
                                }
                            } else {
                                val x = userinfo.first().send
                                val a = userinfo.first()

                                if (x == null) {
                                    LaunchedEffect(true) {
                                        network.senduerinfo(
                                            userinfo(
                                                network.getuuid(), a.age, a.ac,
                                                a.sex,
                                                a.work,
                                                a.remote
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                    items(newtasks, key = { it.addTime }) { it ->
                        var newit by mutableStateOf(it)
                        val dismissState = rememberDismissState(
                            initialValue = DismissValue.Default,
                            confirmStateChange = { DismissValueS ->
                                if (DismissValueS == DismissValue.DismissedToEnd) {
                                    if (newit.isdone == 0L) {
                                        db.getTaskdoneByid(System.currentTimeMillis(), it.id)
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
                                                tint = Color.hsl(40F, 1F, 0.5F),
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
            Box(
                modifier = Modifier.background(color = Color.Black.copy(alpha = alpha1)).fillMaxSize()
                    .clickable {
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
            Box(
                modifier = Modifier.background(color = Color.Black.copy(alpha = alpha)).fillMaxSize()
                    .clickable {
                        openDialog.value = false
                        selecttask.value = null
                    })
            Box(
                modifier = Modifier.clickable {
                }.align(alignment = Alignment.Center).padding(20.dp).clip(shape = RoundedCornerShape(3))
                    .background(color = Color.White)
            ) {
                taskDetail(openDialog, db, selecttask.value)
            }
        }
    }
//}
}
