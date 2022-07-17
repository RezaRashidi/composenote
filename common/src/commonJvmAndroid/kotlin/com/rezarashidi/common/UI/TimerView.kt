package com.rezarashidi.common.UI

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rezarashidi.common.TodoDatabaseQueries
import kotlinx.coroutines.delay
import kotlinx.datetime.*


class TimerData(val min: Int = 0) {
    private var startTime = Clock.System.now()

    init {

        startTime = Clock.System.now()
    }
    fun passtime(): DateTimePeriod {


        return Clock.System.now().minus(startTime).toDateTimePeriod()


    }
    fun getSecend() = passtime().seconds
    fun getMinute() = passtime().minutes
    fun gethour() = passtime().hours
}

class Countdwontimer(val min: Int = 0) {
     var startTime = 0L
    var lasttime= Instant.fromEpochSeconds(startTime).toLocalDateTime(TimeZone.UTC)
    init {
        startTime= min * 60L

    }

    fun passtime(): LocalDateTime {

        lasttime=Instant.fromEpochSeconds(startTime).toLocalDateTime(TimeZone.UTC)
        startTime -= 1L
        return lasttime
    }
    fun getSecend() = passtime().second
    fun getMinute() = lasttime.minute
    fun gethour() = lasttime.hour
}

@Composable
fun TimerView(min: Int = 0, db: TodoDatabaseQueries, showbuttom: MutableState<Boolean>,taskid:Long) {
    var puase by remember {
        mutableStateOf(true)
    }
    val timer = remember { TimerData() }
    val countdwontimer = remember { Countdwontimer(min) }

    val Secend by produceState(timer.getSecend(),puase) {

        while (puase) {

            if (min > 0) {
                value = countdwontimer.getSecend()
            } else {
                value = timer.getSecend()
            }
            delay(1000)
        }

    }
    val minute by produceState(timer.getMinute(),puase) {

        while (puase) {




            if (min > 0) {
                value = countdwontimer.getMinute()
            } else {
                value = timer.getMinute()
            }

            delay(1000 * 60)

        }

    }
    val hour by produceState(timer.gethour(),puase) {

        while (puase) {

            if (min > 0) {
                value = countdwontimer.gethour()
            } else {
                value = timer.gethour()
            }
            delay(1000 * 60 * 60)
        }

    }




    Text("$hour : $minute : $Secend", style = MaterialTheme.typography.h5, modifier = Modifier.border(
        3.dp, MaterialTheme.colors.primary,
        AbsoluteRoundedCornerShape(8.dp)
    ).padding(8.dp).clickable {
        val passtime= if(min>0){
            min *60  - ((countdwontimer.getMinute()*60) +countdwontimer.getSecend() )

        }else{
            (timer.gethour() * 60*60) + (timer.getMinute()*60) +timer.getSecend()
        }

        db.insertTimerecord(null,taskid,passtime.toLong(),Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString())
        puase=!puase
        showbuttom.value=true
    })


}