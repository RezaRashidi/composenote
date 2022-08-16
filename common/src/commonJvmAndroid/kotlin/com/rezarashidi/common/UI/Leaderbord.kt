package com.rezarashidi.common.UI

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.rezarashidi.common.TodoDatabaseQueries
import com.rezarashidi.common.network.leaderbord
import com.rezarashidi.common.network.networktasks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

enum class sortbyL {
    Default, totaltime, totaltimeget, totaltask, reward
}
@OptIn(ExperimentalTime::class, ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun Leaderbord(db: TodoDatabaseQueries) {
    val scope: CoroutineScope = rememberCoroutineScope()
    val network = networktasks(db, scope)
    var selectedIndex by remember { mutableStateOf(0) }
    val items = listOf(sortbyL.Default, sortbyL.totaltask, sortbyL.reward, sortbyL.totaltimeget, sortbyL.totaltime)
    var list: MutableList<leaderbord>? by remember() {
        mutableStateOf(null)
    }
    var username by remember { mutableStateOf(network.getusername() ?: "") }
    val Dialog = remember { mutableStateOf(username == "") }
    var expanded by remember { mutableStateOf(false) }
    val newtask by
    derivedStateOf {
        list?.sortedByDescending {
            when (items[selectedIndex]) {
                sortbyL.Default -> it.totalpoint
                sortbyL.totaltask -> it.taskcount
                sortbyL.totaltimeget -> it.totaltimeget
                sortbyL.totaltime -> it.totaltime
                sortbyL.reward -> it.totalpoint
            }
        }
    }


val uuid = network.getuuid()
LaunchedEffect(true) {
    network.sendleaderbord()
    list = network.getleaderbord().toMutableList()
    network. getleaderbordranking()
}

val column1Weight = .3f // 30%
val column2Weight = .7f // 70%
Box(Modifier.background(MaterialTheme.colors.background)) {
    if (Dialog.value) {
        AlertDialog(
            onDismissRequest = {
                Dialog.value = false
            },
            title = {
//                    Text(text = "Enter username")
            },
            text = {
                OutlinedTextField(username, onValueChange = {
                    username = it
                }, label = {
                    Text("Enter username")
                })
            },
            confirmButton = {
                OutlinedButton(
                    onClick = {
                        Dialog.value = false
                        network.changeusername(username)
                    }) {
                    Text("Save")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        Dialog.value = false
                    }) {
                    Text("dismiss")
                }
            }
        )
    }
    newtask?.let {
        LazyColumn {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(10.dp, 0.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = {
                        scope.launch {
                        }
                    }) {
                        Icon(Icons.Filled.ArrowBack, "backIcon")
                    }
                    if (username != "") {
                        Text(username, modifier = Modifier.clickable {
                            Dialog.value = true
                        })
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
            item {
                Card(Modifier.fillMaxWidth().padding(8.dp), elevation = 5.dp) {
                    Row(Modifier.fillMaxWidth().padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("#", Modifier.weight(0.5f))
                        Text("Name", Modifier.weight(2f))
                        Text("Total time", Modifier.weight(2f))
                        Text("Spend time", Modifier.weight(2f))
                        Text("Task Count", Modifier.weight(2f))
                        Text("Reward", Modifier.weight(2f))
                    }
                }
            }
            itemsIndexed(it, key = { index, item -> item.uuid }) { index, it ->
                var color = Color.White
                if (it.uuid == uuid) {
                    color = MaterialTheme.colors.primary
                }
                Card(
                    Modifier.fillMaxWidth().padding(8.dp).animateItemPlacement(),
                    elevation = 5.dp,
                    backgroundColor = color
                ) {
                    var totaltimeD = ""

                    Duration.minutes(it.totaltime).toComponents { days, hours, minutes, seconds, nanoseconds ->
                        totaltimeD = "${days}d:${hours}h:${minutes}m:${seconds}s"
                    }
                    var spendtimeD = ""
                    Duration.minutes(it.totaltimeget).toComponents { days, hours, minutes, seconds, nanoseconds ->
                        spendtimeD = "${days}d:${hours}h:${minutes}m:${seconds}s"
                    }


                    Row(Modifier.fillMaxWidth().padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text((index + 1).toString(), Modifier.weight(0.5f))
                        Text(it.username ?: "unknown", Modifier.weight(2f))
                        Text(totaltimeD.toString(), Modifier.weight(2f))
                        Text(spendtimeD.toString(), Modifier.weight(2f))
                        Text(it.taskcount.toString(), Modifier.weight(2f))
                        Text(it.totalpoint.toString() + "px", Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
}