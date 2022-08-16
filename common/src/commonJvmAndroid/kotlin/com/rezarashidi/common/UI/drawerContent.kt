package com.rezarashidi.common.UI

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import com.rezarashidi.common.TodoDatabaseQueries

@Composable
fun drawerContent(db: TodoDatabaseQueries, scaffoldState: ScaffoldState, showleaderbor: MutableState<Boolean>){


    Box(Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {

        if (scaffoldState.drawerState.isOpen){
//        timerecordlist(db)
            if(showleaderbor.value){
                Leaderbord(db)



            }else{

                diliyrecord(db)
            }


        }


    }


//Column {
//    Text("time record")
//    Text("chart and static")
//    Text("leadership")
//    Text("setting")
//}



}
