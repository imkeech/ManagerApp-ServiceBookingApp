package com.example.serviceappmanager

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import java.text.DecimalFormat

class SubscriptionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_subscription, container, false)
        val chart: BarChart = view.findViewById(R.id.barChart)

        val db = FirebaseFirestore.getInstance()
        val modelCounts = mutableMapOf<String, Int>()

        db.collection("Service_Booking").get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val modelId = document.getString("modelId") ?: ""

                    db.collection("customerDetails").whereEqualTo("modelId", modelId).get()
                        .addOnSuccessListener { query ->
                            if (query.documents.size < 1) {
                                return@addOnSuccessListener
                            }
                            val modelDocument = query.documents[0]
                            val model = modelDocument.getString("model")
                            if (model != null) {
                                if (modelCounts.containsKey(model)) {
                                    modelCounts[model] = modelCounts[model]!! + 1
                                } else {
                                    modelCounts[model] = 1
                                }
                            }
                            updateChart(chart, modelCounts)
                        }
                }
            }
            .addOnFailureListener { exception ->
                // Handle errors
            }

        return view
    }

    class WholeNumberValueFormatter : ValueFormatter() {
        private val format = DecimalFormat("###") // Format to display values as whole numbers

        override fun getFormattedValue(value: Float): String {
            return format.format(value.toDouble())
        }
    }

    private fun updateChart(chart: BarChart, modelCounts: Map<String, Int>) {
        val entries = ArrayList<BarEntry>()
        val labels = modelCounts.keys.toList()

        modelCounts.values.forEachIndexed { index, value ->
            val barEntry = BarEntry(index.toFloat(), value.toFloat())
            entries.add(barEntry)
        }

        val dataSet = BarDataSet(entries, "Machine Models")
        dataSet.setColors(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW)
        dataSet.valueTextColor = resources.getColor(R.color.white, null)
        dataSet.valueTextSize = 14f
        dataSet.valueFormatter = WholeNumberValueFormatter()
        dataSet.setColors(intArrayOf(R.color.black), context)
        val dataSets: ArrayList<IBarDataSet> = ArrayList()
        dataSets.add(dataSet)


        chart.description.textColor = resources.getColor(R.color.white, null)
        chart.xAxis.textColor = resources.getColor(R.color.white, null)
        chart.axisLeft.textColor = resources.getColor(R.color.white, null)
        chart.axisRight.textColor = resources.getColor(R.color.white, null)
        chart.legend.textColor = resources.getColor(R.color.white, null)

        // Set x-label size to 15dp
        //chart.xAxis.textSize = 15f

        val data = BarData(dataSets)
        chart.data = data
        chart.setFitBars(true)
        chart.description.isEnabled = false

        val xAxis = chart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.labelRotationAngle = 90f
        xAxis.granularity = 1f


        val yAxisLeft = chart.axisLeft
        val yAxisRight = chart.axisRight
        yAxisLeft.axisMinimum = 0f
        yAxisRight.axisMinimum = 0f
        yAxisLeft.granularity = 1f // Set the granularity to display only whole numbers
        yAxisRight.granularity = 1f // Set the granularity to display only whole numbers

        chart.invalidate()
    }
}
