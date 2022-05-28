package com.rezarashidi.common.UI

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch


@ExperimentalMaterialApi
@Composable
fun projectItem(projectname: String = "test", reward: Int = 50, totaltime: Int = 100, spendtime: Int = 70) {
    val state = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    Box(){




    Card(Modifier.fillMaxWidth().padding(15.dp), elevation = 5.dp) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(20.dp).fillMaxWidth()
            ) {

                Column() {
                    Text(projectname, style = MaterialTheme.typography.h4)
                    Text(
                        "reward = ${reward.toString()}", modifier = Modifier, style = MaterialTheme.typography.subtitle1
                    )
                    Text(
                        "${totaltime.toString()} / ${spendtime.toString()}",
                        modifier = Modifier,
                        style = MaterialTheme.typography.subtitle2
                    )

                }
                OutlinedButton(
                    onClick = { scope.launch { state.show() } },
                    border = BorderStroke(2.dp, MaterialTheme.colors.primary)
                ) {
                    Text(
                        "Start", style = MaterialTheme.typography.h5
                    )
                }



            }
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth().height(7.dp), progress = spendtime.toFloat() / totaltime.toFloat()
            )
        }
    }
        ModalBottomSheetLayout(sheetState = state,sheetElevation=1.dp, sheetContent = {
            LazyColumn {
                items(50) {
                    ListItem(text = { Text("Item $it") }, icon = {
                        Icon(
                            Icons.Default.Favorite, contentDescription = "Localized description"
                        )
                    })
                }
            }
        }) {}

    }
}