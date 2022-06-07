package com.rezarashidi.common.UI

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp



@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun projectList(){
    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    val scrollState=rememberScrollState()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopAppBar(title = {Text("TopAppBar")})  },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = { FloatingActionButton(onClick = {}){
            Text("+", modifier = Modifier.padding(2.dp), fontWeight = FontWeight.Bold, color = MaterialTheme.colors.onPrimary, style = MaterialTheme.typography.h4)
        } },
        drawerContent = { Text(text = "drawerContent") },
        content = {
            Column(modifier = Modifier.verticalScroll(scrollState)){
                projectItem()
                projectItem()
                projectItem()
                projectItem()
                projectItem()

            }

                  },
//        bottomBar = { BottomAppBar() { Text("BottomAppBar") } }
    )
}
