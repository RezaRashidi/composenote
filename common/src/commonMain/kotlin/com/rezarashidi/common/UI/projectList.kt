package com.rezarashidi.common.UI

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp



@OptIn(ExperimentalMaterialApi::class)
@Composable
fun projectList(){
    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopAppBar(title = {Text("TopAppBar")})  },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = { FloatingActionButton(onClick = {}){
            Text("+", modifier = Modifier.padding(2.dp), fontWeight = FontWeight.Bold, color = MaterialTheme.colors.onPrimary, style = MaterialTheme.typography.h4)
        } },
        drawerContent = { Text(text = "drawerContent") },
        content = { projectItem() },
//        bottomBar = { BottomAppBar() { Text("BottomAppBar") } }
    )
}
