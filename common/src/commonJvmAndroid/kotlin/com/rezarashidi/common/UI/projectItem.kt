package com.rezarashidi.common.UI

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rezarashidi.common.Projects
import com.rezarashidi.common.TodoDatabaseQueries

import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun projectItem(Project: Projects,db: TodoDatabaseQueries) {
    val scope = rememberCoroutineScope()


    var showselectTime by  remember {mutableStateOf(false) }
    var reward =  remember {db.getTasksByProjectID(Project.id).executeAsList().sumOf {it.reward} }
    var totaltime = remember {db.getTasksByProjectID(Project.id).executeAsList().sumOf {it.timeInHour*60 + it.timeInMinute}}
    var spendtime =  remember {
        db.getTasksByProjectID(Project.id).executeAsList().map { it.id }
            .sumOf { id -> db.getTimerecordByTaskID(id).executeAsList().sumOf { it.lenth } }
    }
    var totaltimex= Instant.fromEpochSeconds(totaltime).toLocalDateTime( TimeZone.UTC  )
    var spendtimex= Instant.fromEpochSeconds(spendtime).toLocalDateTime( TimeZone.UTC  )

    var min by  remember {
        mutableStateOf(0)
    }




    val alpha: Float by animateFloatAsState(if (!showselectTime) 1f else 0f)

    Card(Modifier.fillMaxWidth().padding(15.dp).shadow(5.dp,RoundedCornerShape(15),true), elevation = 5.dp, backgroundColor = Color.White ){
        BoxWithConstraints (contentAlignment=Alignment.Center){
            Column(modifier = Modifier.fillMaxWidth().graphicsLayer(alpha = alpha), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(20.dp).fillMaxWidth()
                ) {

                    Column {
                        Text(Project.Project_name, style = MaterialTheme.typography.h4)
                        Text(
                            "reward : $reward",
                            modifier = Modifier,
                            style = MaterialTheme.typography.subtitle1
                        )
                        Text(
                            "${totaltimex.hour}:${totaltimex.minute}:${totaltimex.second} / ${spendtimex.hour}:${spendtimex.minute}:${spendtimex.second} ",
                            modifier = Modifier,
                            style = MaterialTheme.typography.subtitle2
                        )
                    }

//
                }
                Row(modifier = Modifier.fillMaxWidth().padding(20.dp,5.dp), verticalAlignment = Alignment.CenterVertically){

                    LinearProgressIndicator(
                        modifier = Modifier.weight(4f).height(7.dp),
                        progress = if (spendtime==0L) 0F else (spendtime/totaltime.toFloat())
                    )
                    Text(
                        "5%",
                        modifier = Modifier.padding(15.dp,0.dp,0.dp,0.dp),
                        style = MaterialTheme.typography.subtitle2,

                    )
                }

            }


    }


    }
}