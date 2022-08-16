package com.rezarashidi.common.UI

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Sort
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rezarashidi.common.Projects
import com.rezarashidi.common.TodoDatabaseQueries
import com.rezarashidi.common.network.networktasks
import kotlinx.coroutines.launch

enum class sortbyP {
    Default, Pritority, Urgency, totaltime
}
@OptIn(
    ExperimentalMaterialApi::class, ExperimentalAnimationApi::class, ExperimentalGraphicsApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun projectList(db: TodoDatabaseQueries, projectListState: LazyListState, network: networktasks) {
    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    val scrollState = rememberScrollState()
    val scrollStatetask = rememberScrollState()
    val scope = rememberCoroutineScope()
    val openDialog: MutableState<Boolean> = remember { mutableStateOf(false) }
    val ScrollState = rememberScrollState()
    val alpha: Float by animateFloatAsState(if (!openDialog.value) 0f else 0.5f)
    val selectproject: MutableState<Projects?> = remember { mutableStateOf<Projects?>(null) }
    val items = listOf(sortbyP.Default, sortbyP.Pritority, sortbyP.totaltime, sortbyP.Urgency)
    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(0) }
    val taskListState = rememberLazyListState()
    val allprojects = db.getAllProjects().executeAsList().sortedByDescending { it ->
        when (items[selectedIndex]) {
            sortbyP.Default -> it.addtime
            sortbyP.Urgency -> it.Urgency
            sortbyP.totaltime -> db.getTasksByProjectID(it.id).executeAsList()
                .sumOf { itx -> itx.timeInHour * 60 + itx.timeInMinute }

            sortbyP.Pritority -> it.Pritority
            else -> it.addtime
        }
    }
    @Composable
    fun allprojects() {
        Box {
            Scaffold(
                scaffoldState = scaffoldState,
                drawerGesturesEnabled = false,
//                drawerContent = { drawerContent(db) },
//                drawerShape = RoundedCornerShape(0),
//            topBar = { TopAppBar(title = {  }, backgroundColor = Color.White) },
                floatingActionButtonPosition = FabPosition.Center,
                floatingActionButton = {
                    FloatingActionButton(onClick = {
                        openDialog.value = true
                        scope.launch {
//                        bottomSheetState.show()
//                        dialogState.show()
                        }
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
                content = {
                    LazyColumn(state = projectListState) {
                        item {

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth().padding(10.dp, 0.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                IconButton(onClick = {
                                    scope.launch {
                                        scaffoldState.drawerState.open()
                                    }
                                }) {
                                    Icon(Icons.Filled.Menu, "backIcon")
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
//            val x=openDialog.value //for recomposition
                        items(allprojects, key = { it.addtime }) { it ->
                            val dismissState = rememberDismissState(initialValue = DismissValue.Default)
                            SwipeToDismiss(
                                state = dismissState,
                                modifier = Modifier.animateItemPlacement(),
                                /***  create dismiss alert Background */
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
//                                    Text("Delete", color = Color.Red, style = MaterialTheme.typography.h4)
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = "Localized description",
                                                    tint = Color.hsl(40F, 1F, 0.5F),
                                                    modifier = Modifier.scale(2f)
                                                )
                                            }

                                        if (dismissState.isDismissed(DismissDirection.EndToStart))
                                            db.deleteProject(it.id)
                                    }
                                },
                                /**** Dismiss Content */
                                /**** Dismiss Content */
                                dismissContent = {
                                    if (!dismissState.isDismissed(DismissDirection.EndToStart))
                                        projectItem(it, db, selectproject, openDialog)
                                },
                                /*** Set Direction to dismiss */
                                /*** Set Direction to dismiss */
                                directions = setOf(DismissDirection.EndToStart),
                            )
                        }
                    }
                },
//        bottomBar = { BottomAppBar() { Text("BottomAppBar") } }
            )

            if (openDialog.value) {
                Box(modifier = Modifier.background(color = Color.Black.copy(alpha = alpha)).fillMaxSize().clickable {
                    openDialog.value = false
                    selectproject.value = null
                })
                Box(
                    modifier = Modifier.clickable {
                    }.align(alignment = Alignment.Center).padding(20.dp).clip(shape = RoundedCornerShape(3))
                        .background(color = Color.White).verticalScroll(ScrollState)
                ) {
                    projectDetail(sheetState = openDialog, db, selectproject)
                }
            }
        }
    }

    if (!openDialog.value and (selectproject.value != null)) {
        taskList(db, taskListState, selectproject, network)
    } else {
        allprojects()
    }
//}
}
