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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rezarashidi.common.Projects
import com.rezarashidi.common.TodoDatabaseQueries
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import java.lang.Math.floor
import kotlin.math.floor
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun projectItem(
    Project: Projects,
    db: TodoDatabaseQueries,
    selectproject: MutableState<Projects?>,
    openDialog: MutableState<Boolean>,
) {
//    val scope = rememberCoroutineScope()
    var rewardall = remember { db.getTasksByProjectID(Project.id).executeAsList().sumOf { it.reward } }
    var reward =
        remember { db.getTasksByProjectID(Project.id).executeAsList().filter { it.isdone == 1L }.sumOf { it.reward } }
    var totaltime = remember {
        db.getTasksByProjectID(Project.id).executeAsList().sumOf { it.timeInHour * 60 + it.timeInMinute }
    } * 60
    var spendtime = remember {
        db.getTasksByProjectID(Project.id).executeAsList().map { it.id }
            .sumOf { id -> db.getTimerecordByTaskID(id).executeAsList().sumOf { it.lenth } }
    }
//    var totaltimex = Instant.fromEpochSeconds(totaltime).toLocalDateTime(TimeZone.UTC)
//    var spendtimex = Instant.fromEpochSeconds(spendtime).toLocalDateTime(TimeZone.UTC)
    val startdate = Project.Startdate.toLocalDate()
    val Enddate = Project.Enddate?.toLocalDate()
    val nowdate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val pritoritynames = listOf<String>("Low", "Medium", "High")
    val Urgency = listOf<String>("Low", "Medium", "High")
    @Composable
    fun showProgress(progress: Float = 0.2F, percent: String = "5%") {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            LinearProgressIndicator(
                modifier = Modifier.weight(4f).height(7.dp),
                progress = progress
            )
            Text(
                floor(progress * 100).toString() + "%",
                modifier = Modifier.padding(15.dp, 0.dp, 0.dp, 0.dp),
                style = MaterialTheme.typography.subtitle2,
            )
        }
    }


    Card(
        Modifier.fillMaxWidth().padding(15.dp).shadow(5.dp, RoundedCornerShape(15), true),
        elevation = 5.dp,
        backgroundColor = Color.White
    ) {
        BoxWithConstraints(contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier.padding(20.dp, 20.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Row (modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween){
                    Column(modifier = Modifier.padding(0.dp)) {
                        Text(Project.Project_name, style = MaterialTheme.typography.h4)
                        Text(
                            pritoritynames[Project.Pritority.toInt() - 1] + " Pritority",
                            style = MaterialTheme.typography.subtitle1
                        )
                        Text(
                            Urgency[Project.Urgency.toInt() - 1] + " Urgency",
                            style = MaterialTheme.typography.subtitle1
                        )
                    }

                    Column(modifier = Modifier.width(IntrinsicSize.Max).padding(0.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        OutlinedButton(
                            onClick = {
                                selectproject.value = Project
                            },
                            border = BorderStroke(2.dp, MaterialTheme.colors.primary),
                            modifier = Modifier
                        ) {
                            Text(
                                "Tasks", style = MaterialTheme.typography.h6
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                selectproject.value = Project
                                openDialog.value = true
                            },
                            border = BorderStroke(2.dp, MaterialTheme.colors.primary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Edit" ,style = MaterialTheme.typography.h6
                            )
                        }
                    }
                }



                Column(Modifier.fillMaxWidth().padding(0.dp, 5.dp)) {
                    Text(
                        "reward: ${reward} / ${rewardall}",
                        modifier = Modifier,
                        style = MaterialTheme.typography.subtitle1
                    )
                    rewardall.let {
                        if (it == 0L) {
                            0
                        } else {
                            reward.toFloat() / rewardall
                        }
                    }.toFloat()
                    showProgress(rewardall.let {
                        if (it == 0L) {
                            0
                        } else {
                            reward.toFloat() / rewardall
                        }
                    }.toFloat())
                    var totaltimeD = ""

                    Duration.seconds(totaltime).toComponents { days, hours, minutes, seconds, nanoseconds ->
                        totaltimeD = "${days}d:${hours}h:${minutes}m:${seconds}s"
                    }
                    var spendtimeD = ""
                    Duration.seconds(spendtime).toComponents { days, hours, minutes, seconds, nanoseconds ->
                        spendtimeD = "${days}d:${hours}h:${minutes}m:${seconds}s"
                    }

                    Text(
                        "Time: ${totaltimeD} / ${spendtimeD}",
                        modifier = Modifier,
                        style = MaterialTheme.typography.subtitle1
                    )

                    showProgress(totaltime.let {
                        if (it == 0L) {
                            0
                        } else
                            spendtime / totaltime.toFloat()
                    }.toFloat())



                    if (startdate.until(nowdate, DateTimeUnit.DAY) > 0) {
//                    Enddate?.minus(startdate).days/Enddate?.minus(nowdate).days
                        Enddate?.let {
                            Text(
                                "Date: " + startdate.until(Enddate, DateTimeUnit.DAY) + " day left",
                                modifier = Modifier,
                                style = MaterialTheme.typography.subtitle1
                            )
                            showProgress(
                                nowdate.until(Enddate, DateTimeUnit.DAY) / startdate.until(
                                    Enddate,
                                    DateTimeUnit.DAY
                                ).toFloat()
                            )
                        }
                    }
                }
            }
        }
    }
}


