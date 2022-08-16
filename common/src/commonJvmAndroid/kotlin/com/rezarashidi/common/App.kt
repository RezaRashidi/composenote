package com.rezarashidi.common

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import com.rezarashidi.common.UI.*
import com.rezarashidi.common.network.networktasks
import kotlinx.coroutines.CoroutineScope

typealias Content = @Composable () -> Unit

val Cyan = Color(0xff03DAC5)
val mycolor = lightColors(primary = Cyan, background = Color(0xffF6F7FB), surface = Color.White)
@Composable
fun App(db: TodoDatabaseQueries) {
    val scope: CoroutineScope = rememberCoroutineScope()
    val network = networktasks(db, scope)
    LaunchedEffect(true) {
        network.createuser()
        val sendlist = db.getsendlistBydate(network.nowdate.toString()).executeAsList()
        if (sendlist.isEmpty()) {
            network.sendTaskrecoed()
            network.senddiliy()
            network.sendleaderbord()
            network.update()
        } else if ((sendlist.count() < 3) ) {
            if (sendlist.filter {
                    it.type == 1L
                }.isEmpty()) {
                network.sendTaskrecoed()
            }
            if (
                sendlist.filter {
                    it.type == 2L
                }.isEmpty()
            ) {
                network.senddiliy()
            }

            if (
                sendlist.filter {
                    it.type == 3L
                }.isEmpty()
            ) {
                network.sendleaderbord()
            }
            if (
                sendlist.filter {
                    it.type == 4L
                }.isEmpty()
            ) {
                network.update()
            }
        }
    }
    MaterialTheme(
        colors = mycolor,
        shapes = Shapes(RoundedCornerShape(15), RoundedCornerShape(15), RoundedCornerShape(15))
    ) {
        val taskListState = rememberLazyListState()
        val projectListState = rememberLazyListState()
        val dailiesListState = rememberLazyListState()
//        charts()

        TabLayout(
            {
//                uinfo(db,network)
//                diliyrecord(db)
                projectList(db, projectListState,network)
//                                Leaderbord(db)
            },
            {
//                timerecordlist(db)
                taskList(db, taskListState,null,network)
            },
            { dailiesList(db, dailiesListState, network) },
            db
        )
    }
}



