//package com.rezarashidi.common.UI
//
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import me.bytebeats.views.charts.pie.PieChart
//import me.bytebeats.views.charts.pie.PieChartData
//import me.bytebeats.views.charts.pie.render.SimpleSliceDrawer
//import me.bytebeats.views.charts.simpleChartAnimation
//import kotlin.random.Random
//private val colors = mutableListOf<Color>(
//    Color(0XFFF44336),
//    Color(0XFFE91E63),
//    Color(0XFF9C27B0),
//    Color(0XFF673AB7),
//    Color(0XFF3F51B5),
//    Color(0XFF03A9F4),
//    Color(0XFF009688),
//    Color(0XFFCDDC39),
//    Color(0XFFFFC107),
//    Color(0XFFFF5722),
//    Color(0XFF795548),
//    Color(0XFF9E9E9E),
//    Color(0XFF607D8B)
//)
//private fun randomLength(): Float = Random.Default.nextInt(10, 30).toFloat()
//private fun randomColor(): Color {
//    val randomIndex = Random.Default.nextInt(colors.size)
//    return colors.removeAt(randomIndex)
//}
//@Composable
//fun PieChartView() {
//    PieChart(
//        pieChartData = PieChartData(
//            slices = listOf(
//                PieChartData.Slice(
//                    randomLength(),
//                    randomColor()
//                ),
//                PieChartData.Slice(randomLength(), randomColor()),
//                PieChartData.Slice(randomLength(), randomColor())
//            )
//        ),
//        // Optional properties.
//        modifier = Modifier.fillMaxSize(),
//        animation = simpleChartAnimation(),
//        sliceDrawer = SimpleSliceDrawer()
//    )
//}
//@Composable
//fun charts() {
//    PieChartView()
//}