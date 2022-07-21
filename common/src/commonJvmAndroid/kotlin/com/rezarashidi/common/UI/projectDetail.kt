package com.rezarashidi.common.UI

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.rezarashidi.common.Projects
import com.rezarashidi.common.Tasks
import com.rezarashidi.common.TodoDatabaseQueries
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.launch
@Composable
fun projectDetail(sheetState: MutableState<Boolean>, db: TodoDatabaseQueries, Project: MutableState<Projects?>? = null) {

    val projectName = remember { mutableStateOf(TextFieldValue(Project?.value?.Project_name ?: "")) }
    val descreption = remember { mutableStateOf(TextFieldValue(Project?.value?.Descreption ?: "")) }
    val date = remember { mutableStateOf(Project?.value?.date ?: "") }
    val pritoritynames = listOf<String>("Low", "Medium", "High")
    var sliderPosition by remember { mutableStateOf(Project?.value?.Pritority?.toFloat() ?: 1f) }
    val dialogState = rememberMaterialDialogState()
    var isdone by remember { mutableStateOf((Project?.value?.isdone == 1L)) }
    val scope = rememberCoroutineScope()
//     val db= Projects()
    Column(
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        OutlinedTextField(projectName.value, onValueChange = {
            projectName.value = it
        }, label = {
            Text("Project Name")
        }, modifier = Modifier.fillMaxWidth().padding(10.dp))
        OutlinedTextField(descreption.value, onValueChange = {
            descreption.value = it
        }, label = {
            Text("Description")
        }, modifier = Modifier.fillMaxWidth().padding(10.dp))
        Text(text = "Pritority:", modifier = Modifier.align(alignment = Alignment.Start))
        Text(text = pritoritynames[sliderPosition.toInt() - 1])
        Slider(value = sliderPosition, onValueChange = { sliderPosition = it }, steps = 1, valueRange = 1f..3f)
        Text(text = "Due Date:", modifier = Modifier.align(alignment = Alignment.Start))

        MaterialDialog(dialogState = dialogState, buttons = {
            positiveButton("Ok")
            negativeButton("Cancel")
        }) {
            datepicker {
                date.value = it.toString()
            }
        }

        OutlinedTextField(date.value, onValueChange = {

        }, label = {
            Text("date")
        }, readOnly = true, enabled = false, modifier = Modifier.fillMaxWidth().padding(10.dp).clickable {
            scope.launch {
                dialogState.show()

            }
        })

        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Is done:")
            Checkbox(modifier = Modifier.height(intrinsicSize = IntrinsicSize.Max),
                checked = isdone,
                onCheckedChange = { isdone = it }
            )
        }

        Row {

            OutlinedButton(
                {

                    scope.launch {


                    }

                    db.insertProject(
                        Project?.value?.id,
                        projectName.value.text,
                        descreption.value.text,
                        sliderPosition.toLong(),
                        date.value,
                        if (isdone) 1 else 0
                    )

                    sheetState.value = false
                    Project?.value=null

                },
                border = BorderStroke(2.dp, color = MaterialTheme.colors.primary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.width(IntrinsicSize.Max).padding(10.dp)
            ) {
                Text("Save", style = MaterialTheme.typography.h4)
            }

            OutlinedButton(
                {

                    scope.launch {


                    }

                    sheetState.value = false
                    Project?.value=null

                },
                border = BorderStroke(2.dp, color = MaterialTheme.colors.primary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.width(IntrinsicSize.Max).padding(10.dp)
            ) {
                Text("close", style = MaterialTheme.typography.h4)
            }

        }
    }


}