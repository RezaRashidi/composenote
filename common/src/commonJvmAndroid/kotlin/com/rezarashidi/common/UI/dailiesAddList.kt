package com.rezarashidi.common.UI

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rezarashidi.common.TodoDatabaseQueries
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

@Composable
fun dailiesAddList(sheetState: MutableState<Boolean>, db: TodoDatabaseQueries) {
    val listoftask = db.getAllTasks().executeAsList()
    Column(Modifier.width(IntrinsicSize.Max), horizontalAlignment = Alignment.CenterHorizontally) {

        listoftask.forEach {


            Card(Modifier.fillMaxWidth().padding(20.dp), elevation = 5.dp, shape = RoundedCornerShape(7)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {

                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(it.Task_name, modifier = Modifier.padding(5.dp), style = MaterialTheme.typography.h6)
                        if (it.ProjectID != null) {
                            Text(("Project:" + it.ProjectID.let { it1 ->
                                db.getProjectByID(it1).executeAsOne().Project_name
                            }) , modifier = Modifier.padding(5.dp))
                        }
                        Text("Reward: "+it.reward, modifier = Modifier.padding(5.dp))
                        Text("time: "+it.timeInHour+" : "+it.timeInMinute, modifier = Modifier.padding(5.dp))
                        if (it.ProjectID != null) {
                            Text(("Project:" + it.ProjectID.let { it1 ->
                                db.getProjectByID(it1).executeAsOne().Project_name
                            }) , modifier = Modifier.padding(5.dp))
                        }
                    }
                    OutlinedButton(
                        {
                            db.insertdailiess(null, it.id)
                        }, border = BorderStroke(2.dp, color = MaterialTheme.colors.primary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.padding(10.dp)
                    ) {

                        Text("add", style = MaterialTheme.typography.h5)
                    }
                }


            }
        }
        OutlinedButton(
            {
                sheetState.value=false
            }, border = BorderStroke(2.dp, color = MaterialTheme.colors.primary),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.padding(10.dp)
        ) {

            Text("close", style = MaterialTheme.typography.h5)
        }
    }


}