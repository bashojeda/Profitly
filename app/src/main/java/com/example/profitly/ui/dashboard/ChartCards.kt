package com.example.profitly.ui.dashboard

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.profitly.domain.model.ChartPoint
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

@Composable
fun SalesChartCard(points: List<ChartPoint>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = "Sales Over Time",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp)
        )
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(12.dp),
            factory = { context -> BarChart(context) },
            update = { chart ->
                configureBarChart(chart, points)
            }
        )
    }
}

@Composable
fun ProfitChartCard(points: List<ChartPoint>) {
    val primaryColor = MaterialTheme.colorScheme.primary.toArgb()
    val accentColor = MaterialTheme.colorScheme.secondary.toArgb()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = "Profit Trend",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp)
        )
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(12.dp),
            factory = { context -> LineChart(context) },
            update = { chart ->
                configureLineChart(chart, points, primaryColor, accentColor)
            }
        )
    }
}

private fun configureBarChart(chart: BarChart, points: List<ChartPoint>) {
    val labels = points.map { it.label }
    val entries = points.mapIndexed { idx, point -> BarEntry(idx.toFloat(), point.value.toFloat()) }
    val set = BarDataSet(entries, "Sales").apply {
        color = android.graphics.Color.parseColor("#4CAF50")
        valueTextSize = 10f
    }
    chart.data = BarData(set)
    chart.description.isEnabled = false
    chart.legend.isEnabled = false
    chart.axisRight.isEnabled = false
    chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
    chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
    chart.xAxis.granularity = 1f
    chart.xAxis.labelRotationAngle = -25f
    chart.animateY(500)
    chart.invalidate()
}

private fun configureLineChart(
    chart: LineChart,
    points: List<ChartPoint>,
    lineColor: Int,
    pointColor: Int
) {
    val labels = points.map { it.label }
    val entries = points.mapIndexed { idx, point -> Entry(idx.toFloat(), point.value.toFloat()) }
    val set = LineDataSet(entries, "Profit").apply {
        color = lineColor
        lineWidth = 2.5f
        setCircleColor(pointColor)
        circleRadius = 4f
        valueTextSize = 10f
    }
    chart.data = LineData(set)
    chart.description.isEnabled = false
    chart.legend.isEnabled = false
    chart.axisRight.isEnabled = false
    chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
    chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
    chart.xAxis.granularity = 1f
    chart.xAxis.labelRotationAngle = -25f
    chart.animateX(500)
    chart.invalidate()
}
