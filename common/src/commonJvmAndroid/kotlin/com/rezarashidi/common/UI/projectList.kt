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
import com.rezarashidi.common.TodoDatabaseQueries
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun projectList(db: TodoDatabaseQueries) {
    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val openDialog: MutableState<Boolean> = remember { mutableStateOf(false) }
    val ScrollState = rememberScrollState ()
    val alpha: Float by animateFloatAsState(if (!openDialog.value) 0f else 0.5f)
    var selectproject: MutableState<Projects?> = remember { mutableStateOf<Projects? >(null) }

    Box {
        Scaffold(
            scaffoldState = scaffoldState,
            drawerGesturesEnabled = false,
            topBar = { TopAppBar(title = { Text("To do") }, backgroundColor = Color.White) },
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
            drawerContent = { Text(text = "drawerContent") },
            content = {
                Column(modifier = Modifier.verticalScroll(scrollState)) {
                    val x=openDialog.value //for recomposition
                    db.getAllProjects().executeAsList().forEach {
                        val dismissState = rememberDismissState(initialValue = DismissValue.Default)
                        SwipeToDismiss(
                            state = dismissState,
                            /***  create dismiss alert Background */
                            background = {

                                val direction = dismissState.dismissDirection
                                if (direction == DismissDirection.EndToStart ) {
                                    if(!dismissState.isDismissed(DismissDirection.EndToStart))
                                        Row(
                                            modifier = Modifier.padding(20.dp)
                                                .fillMaxSize()
                                            , horizontalArrangement = Arrangement.End, verticalAlignment =Alignment.CenterVertically
                                        ){
                                            Text("Delete", color = Color.Red, style = MaterialTheme.typography.h4)
                                        }

                                    if(dismissState.isDismissed(DismissDirection.EndToStart))
                                        db.deleteProject(it.id)
                                }

                                if (direction == DismissDirection.StartToEnd ) {
                                    if(!dismissState.isDismissed(DismissDirection.StartToEnd))
                                        Row(
                                            modifier = Modifier.padding(20.dp)
                                                .fillMaxSize()
                                            , horizontalArrangement = Arrangement.Start, verticalAlignment =Alignment.CenterVertically
                                        ){
                                            Text("Done", color = Color.Cyan, style = MaterialTheme.typography.h4)
                                        }

                                    if(dismissState.isDismissed(DismissDirection.StartToEnd))
                                        db.getProjectByID(it.id)
                                }

                            },
                            /**** Dismiss Content */
                            dismissContent = {
                                if(!dismissState.isDismissed(DismissDirection.EndToStart))
                                    Box(Modifier.clickable {
                                        selectproject.value=it
                                        openDialog.value=true
                                    }){
                                        projectItem(it,db)
                                    }

                            },
                            /*** Set Direction to dismiss */
                            directions = setOf(DismissDirection.EndToStart, DismissDirection.StartToEnd),
                        )
                    }


                }

            },
//        bottomBar = { BottomAppBar() { Text("BottomAppBar") } }
        )

        if (openDialog.value) {
            Box(modifier = Modifier.background(color = Color.Black.copy(alpha=alpha)).fillMaxSize().clickable {
                openDialog.value=false
                selectproject.value=null
            })
            Box(modifier = Modifier.clickable {

            }.align(alignment = Alignment.Center) .padding(20.dp).clip(shape = RoundedCornerShape(3)).background(color = Color.White).verticalScroll(ScrollState)){


                projectDetail(sheetState = openDialog, db,selectproject)

            }

        }
    }
//}
}
