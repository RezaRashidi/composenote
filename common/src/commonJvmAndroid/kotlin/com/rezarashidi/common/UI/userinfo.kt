import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.rezarashidi.common.TodoDatabaseQueries
import com.rezarashidi.common.network.networktasks
import com.rezarashidi.common.network.userinfo

@Composable
fun uinfo(db: TodoDatabaseQueries, network: networktasks, form: MutableState<Boolean>) {
    var age by remember { mutableStateOf(TextFieldValue("")) }
    var work by remember { mutableStateOf(false) }
    var remote by remember { mutableStateOf(false) }
    var remotlist= listOf<String?>(null,"yes","no")
    var remoteindex  by remember { mutableStateOf(0) }
    var sex by remember { mutableStateOf(0) }
    var sexDropdownexpanded by remember { mutableStateOf(false) }
    var sexlist = listOf<String>("male", "female")
    var acindex by remember { mutableStateOf(0) }
    var acDropdownexpanded by remember { mutableStateOf(false) }
    var aclist = listOf<String>("Associate degree", "Bachelor’s degree", "Master’s degree", "Doctoral degree")

    Column {
        Row(Modifier.fillMaxWidth().padding(10.dp), horizontalArrangement = Arrangement.Center) {
            Text("Please fill the form", style = MaterialTheme.typography.h4)
        }

        OutlinedTextField(age, onValueChange = {
            age = it
        }, label = {
            Text("age")
        }, modifier = Modifier.fillMaxWidth().padding(10.dp) ,keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number))

        Box(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
            Column {
                Text("degree:")
                Text(
                    aclist[acindex], modifier = Modifier.clickable {
                        acDropdownexpanded = true
                    }.fillMaxWidth().border(color = Color.Gray, width = 1.dp, shape = RoundedCornerShape(4.dp))
                        .padding(15.dp)
                )
            }

            DropdownMenu(
                expanded = acDropdownexpanded,
                onDismissRequest = { acDropdownexpanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                aclist.forEachIndexed { index, s ->
                    DropdownMenuItem(onClick = {
                        acindex = index
                        acDropdownexpanded = false
                    }) {
                        Text(s)
                    }
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
            Column {
                Text("sex:")
                Text(
                    sexlist[sex], modifier = Modifier.clickable {
                        sexDropdownexpanded = true
                    }.fillMaxWidth().border(color = Color.Gray, width = 1.dp, shape = RoundedCornerShape(4.dp))
                        .padding(15.dp)
                )
                DropdownMenu(
                    expanded = sexDropdownexpanded,
                    onDismissRequest = { sexDropdownexpanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    sexlist.forEachIndexed { index, s ->
                        DropdownMenuItem(onClick = {
                            sex = index
                            sexDropdownexpanded = false
                        }) {
                            Text(s)
                        }
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("do yo hava job?")
            Checkbox(modifier = Modifier.height(intrinsicSize = IntrinsicSize.Max),
                checked = work,
                onCheckedChange = { work = it }
            )
        }



        Box(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
            Column {
                Text("Remote working?")
                Text(
                    remotlist[remoteindex]?:"", modifier = Modifier.clickable {
                        remote = true
                    }.fillMaxWidth().border(color = Color.Gray, width = 1.dp, shape = RoundedCornerShape(4.dp))
                        .padding(15.dp)
                )
                DropdownMenu(
                    expanded = remote,
                    onDismissRequest = { remote = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    remotlist.forEachIndexed { index, s ->
                        DropdownMenuItem(onClick = {
                            remoteindex = index
                            remote = false
                        }) {
                            Text(s?:"")
                        }
                    }
                }
            }
        }






        Row(modifier = Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Remote working?")
            Checkbox(modifier = Modifier.height(intrinsicSize = IntrinsicSize.Max),
                checked = remote,
                onCheckedChange = { remote = it }
            )
        }

        Row(Modifier.fillMaxWidth().padding(10.dp), horizontalArrangement = Arrangement.Center) {
            OutlinedButton(
                {


                    db.insertuserinfo(
                        null,
                        age.text.toLongOrNull() ?: 0L,
                        acindex.toLong(),
                        sex.toLong(),
                        if (work) 1L else 0L,
                        remoteindex.toLong(),
                        null
                    )
                    network.senduerinfo(
                        userinfo(
                            network.getuuid(),
                            age.text.toLongOrNull() ?: 0,
                            acindex.toLong(),
                            sex.toLong(),
                            if (work) 1 else 0,
                            remoteindex.toLong()
                        )
                    )
                    form.value=!form.value
                }, enabled = (age.text!="") and (remotlist[remoteindex]!=null),
                border = BorderStroke(2.dp, color = MaterialTheme.colors.primary),
                shape = RoundedCornerShape(10.dp),
            ) {
                Text("Save", style = MaterialTheme.typography.h6)
            }
        }
    }
}