package com.rezarashidi.common.network

import com.rezarashidi.common.TodoDatabaseQueries
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@Serializable
data class userinfo(
    var uuid: String,
    var age: Long,
    val ac: Long,
    val sex: Long,
    var work: Long,
    var remote: Long,
    var id: Long? = null,
)
@Serializable
data class leaderbord(
    var uuid: String,
    var totalpoint: Long,
    val totaltime: Long,
    val totaltimeget: Long,
    var taskcount: Long,
    var username: String? = null,
    var g: Long = 1,
    var id: Long? = null,
)
@Serializable
data class Task(
    var uuid: String,
    var taskid: Long,
    var timestamp: Long,
    var Task_name: String,
    var Difficulty: Long,
    var Urgency: Long,
    var timeInHour: Long,
    var timeInMinute: Long,
    var dailyRepeat: Long,
    var reward: Long,
    var addTime: Long,
    var Donedate: Long?,
    var del: Long?,
    var g: Long = 1,
    var id: Long? = null,
)
@Serializable
data class diliy(
    var uuid: String,
    var time: Long,
    var timeget: Long,
    var reward: Long,
    var rewardget: Long,
    var donecount: Int,
    var donunecount: Int,
    var date: String,
    var g: Long = 1,
    var id: Long? = null,
)

class networktasks(val db: TodoDatabaseQueries, val scope: CoroutineScope) {
    val baseurl = "http://127.0.0.1:8080"
    val nowdate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date//.minus(DatePeriod(0, 0, 1))
    val client = HttpClient() {
        install(ContentNegotiation) {
            json()
        }
    }

    private fun clienthander(c: suspend () -> Unit) =
        scope.launch {
            try {
                c()
            } catch (ex: RedirectResponseException) {
                // 3xx - responses
                println("FError1: ${ex.response.status.description}")
            } catch (ex: ClientRequestException) {
                // 4xx - responses
                println("FError2: ${ex.response.status.description}")
            } catch (ex: ServerResponseException) {
                // 5xx - response
                println("FError3: ${ex.response.status.description}")
            } catch (ex: IOException) {
                // 5xx - response
                println("FError4: ${ex.message}")
            } catch (e: java.lang.Exception) {
                println("FError5:${e.message}")
            }
        }

    fun createuser() {
        if (db.getuser().executeAsList().isEmpty()) {
            db.insertuser(null, null, UUID.randomUUID().toString())
        }
    }

    fun getuuid(): String {
        return if (db.getuser().executeAsList().isEmpty()) {
            val x = UUID.randomUUID().toString()
            db.insertuser(null, null, x)
            x
        } else {
            return db.getuser().executeAsList().first().uuid
        }
    }

    fun getusername(): String? {
        val x = db.getuser().executeAsList()
        return if (x.isEmpty()) {
            null
        } else {
            x.first().username
        }
    }

    fun changeusername(username: String) {
        if (db.getuser().executeAsList().isNotEmpty()) {
            db.updateusername(username)
        }
    }

    fun sendTaskrecoed() {
        val alltask = db.getAllTasks().executeAsList()
        val yesterdaytaskadd = alltask.filter {
            (it.sentadd == null) and (it.isdone == 0L)
        }
            .filter {
                val x =
                    Instant.fromEpochMilliseconds(it.addTime).toLocalDateTime(TimeZone.currentSystemDefault()).date



                nowdate.minus(x).days > 0
            }
        val yesterdaytaskdone = alltask.filter {
            (it.sentdone == null) and (it.isdone == 1L)
        }
            .filter {
                val x =
                    Instant.fromEpochMilliseconds(it.Donedate!!).toLocalDateTime(TimeZone.currentSystemDefault()).date

                nowdate.minus(x).days > 0
            }
        val yesterdaytaskdel = alltask.filter {
            (it.sendel == null) and (it.Del > 1L)
        }
            .filter {
                val x =
                    Instant.fromEpochMilliseconds(it.Del).toLocalDateTime(TimeZone.currentSystemDefault()).date

                nowdate.minus(x).days > 0
            }
        val uuid = getuuid()
        val timest = System.currentTimeMillis()
        val ytaskaddmap = yesterdaytaskadd.map {
            Task(
                uuid,
                it.id,
                timest,
                it.Task_name,
                it.Difficulty,
                it.Urgency,
                it.timeInHour,
                it.timeInMinute,
                it.dailyRepeat,
                it.reward,
                it.addTime,
                it.Donedate, it.Del
            )
        }
        val ytaskdonemap = yesterdaytaskdone.map {
            Task(
                uuid,
                it.id,
                timest,
                it.Task_name,
                it.Difficulty,
                it.Urgency,
                it.timeInHour,
                it.timeInMinute,
                it.dailyRepeat,
                it.reward,
                it.addTime,
                it.Donedate, it.Del
            )
        }
        val ytaskdelmap = yesterdaytaskdel.map {
            Task(
                uuid,
                it.id,
                timest,
                it.Task_name,
                it.Difficulty,
                it.Urgency,
                it.timeInHour,
                it.timeInMinute,
                it.dailyRepeat,
                it.reward,
                it.addTime,
                it.Donedate, it.Del
            )
        }

        clienthander {
            val response: HttpResponse = client.post("$baseurl/Taskrecoed") {
                contentType(ContentType.Application.Json)
                setBody(ytaskaddmap)
            }
            if (response.status.value == 200) {
                yesterdaytaskadd.forEach {
                    db.sendaddtask(it.id)
                }
            }
            val response2: HttpResponse = client.post("$baseurl/Taskrecoed") {
                contentType(ContentType.Application.Json)
                setBody(ytaskdonemap)
            }


            if (response2.status.value == 200) {
                yesterdaytaskdone.forEach {
                    db.senddonetask(it.id)
                }
            }
            val response3: HttpResponse = client.post("$baseurl/Taskrecoed") {
                contentType(ContentType.Application.Json)
                setBody(ytaskdelmap)
            }


            if (response3.status.value == 200) {
                yesterdaytaskdel.forEach {
                    db.sendeltask(it.id)
                }
            }

            if ((response.status.value == 200) and (response2.status.value == 200) and (response3.status.value == 200)) {
                db.insertsendlist(null, nowdate.toString(), 1)
            }
        }
    }

    fun senddiliy() {
        val ydailis = db.getAlldailiess().executeAsList().filter {
            Instant.fromEpochMilliseconds(it.DateAdd).toLocalDateTime(TimeZone.currentSystemDefault()).date == nowdate
        }.map {
            db.getTasksByID(it.TaskID).executeAsOne()
        }
        val ytime = ydailis.filter {
            it.isdone == 0L
        }.sumOf { (it.timeInHour * 60) + it.timeInMinute }
        val ytimeget = ydailis.filter {
            it.isdone == 1L
        }.sumOf { (it.timeInHour * 60) + it.timeInMinute }
        val yreward = ydailis.filter {
            it.isdone == 0L
        }.sumOf { it.reward }
        val yrewardget = ydailis.filter {
            it.isdone == 1L
        }.sumOf { it.reward }
        val ydonecount = ydailis.filter {
            it.isdone == 1L
        }.count()
        val yundonecount = ydailis.filter {
            it.isdone == 0L
        }.count()
        val uuid = getuuid()
        var Diliy: diliy? = null
        if (ydailis.isNotEmpty()) {
            Diliy = diliy(uuid, ytime, ytimeget, yreward, yrewardget, ydonecount, yundonecount, nowdate.toString())
        }

        if (Diliy != null) {
            clienthander {
                val response: HttpResponse = client.post("$baseurl/diliy") {
                    contentType(ContentType.Application.Json)
                    setBody(Diliy)
                }
                if (response.status.value == 200) {
                    db.insertsendlist(null, nowdate.toString(), 2)
                }
            }
        }
    }

    fun sendleaderbord() {
        val alltaskdone = db.getAllTasks().executeAsList().filter {
            it.isdone == 1L
        }
        val points = alltaskdone.sumOf { it.reward }
        val time = alltaskdone.sumOf { (it.timeInHour * 60) + it.timeInMinute }
        val timeget = alltaskdone.map {
            db.getTimerecordByTaskID(it.id).executeAsList().sumOf { it.lenth }
        }.sum()
        val data = leaderbord(getuuid(), points, time, timeget, alltaskdone.count().toLong(), getusername())
//            print(data)
        clienthander {
            val response: HttpResponse = client.post("$baseurl/leaderbord") {
                contentType(ContentType.Application.Json)
                setBody(data)
            }

            if (response.status.value == 200) {
//                    db.insertsendlist(null,nowdate.toString(),3)
            }
        }
    }

    suspend fun getleaderbord(): List<leaderbord> {
        val leaderbord = emptyList<leaderbord>().toMutableList()

        clienthander {
            val x: List<leaderbord> = client.get("$baseurl/leaderbord").body()
            leaderbord.addAll(x)
        }.join()

        return leaderbord
    }

    suspend fun getleaderbordranking(){
        val a = getleaderbord()
        val x = a.sortedBy { it.totalpoint }
        val c = x.count()
        val index = x.indexOfFirst { it.uuid == getuuid() } + 1
        val rank = a.sortedByDescending {
            it.totalpoint
        }.indexOfFirst { it.uuid == getuuid() } + 1
        val q=index / c.toFloat()

        db.insertleaderbordranking(1,rank.toLong(),q)


    }

    fun senduerinfo(info: userinfo) {
        clienthander {
            val response: HttpResponse = client.post("$baseurl/saveuserinfo") {
                contentType(ContentType.Application.Json)
                setBody(info)
            }

            if (response.status.value == 200) {
                val x = db.getuserinfo().executeAsList().first()
                db.insertuserinfo(x.id, x.age, x.ac, x.sex, x.work, x.remote, "ok")
            }
        }
    }

    suspend fun update(): Boolean {
        var update = false
        clienthander {
            val respons = client.get("$baseurl/update")

            if (respons.bodyAsText() == "true") {
                update = true
            }
            if (respons.status.value == 200) {
                db.insertsendlist(null, nowdate.toString(), 4)
            }
            if (update) {
                db.insertupdates(1, 1)
            } else {
                db.insertupdates(1, 0)
            }
        }.join()
        return update
    }
}