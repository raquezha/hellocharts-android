package net.raquezha.hellocharts.kotlin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import lecho.lib.hellocharts.gesture.ZoomType
import lecho.lib.hellocharts.listener.ViewportChangeListener
import lecho.lib.hellocharts.model.Axis
import lecho.lib.hellocharts.model.Line
import lecho.lib.hellocharts.model.LineChartData
import lecho.lib.hellocharts.model.PointValue
import lecho.lib.hellocharts.model.Viewport
import lecho.lib.hellocharts.util.ChartUtils
import lecho.lib.hellocharts.util.ChartUtils.pickColor
import net.raquezha.hellocharts.kotlin.databinding.FragmentPreviewLineChartBinding

class PreviewLineChartActivity : HelloChartsActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_line_chart)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.container, PlaceholderFragment())
            }
        }
    }

    /**
     * A fragment containing a line chart and preview line chart.
     */
    class PlaceholderFragment : HelloChartsFragmentMenu() {

        private val binding: FragmentPreviewLineChartBinding by lazy {
            FragmentPreviewLineChartBinding.inflate(layoutInflater)
        }
        private lateinit var data: LineChartData
        /**
         * Deep copy of data.
         */
        private lateinit var previewData: LineChartData
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            // Generate data for previewed chart and copy of that data for preview chart.
            generateDefaultData()
            binding.chart.lineChartData = data
            // Disable zoom/scroll for previewed chart, visible chart ranges depends on preview chart viewport so
            // zoom/scroll is unnecessary.
            binding.chart.isZoomEnabled = false
            binding.chart.isScrollEnabled = false
            binding.previewChart.lineChartData = previewData
            binding.previewChart.setViewportChangeListener(ViewportListener())
            previewX(false)
            return binding.root
        }

        override fun getMenu() = R.menu.preview_line_chart

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            val id = menuItem.itemId
            if (id == R.id.action_reset) {
                generateDefaultData()
                binding.chart.lineChartData = data
                binding.previewChart.lineChartData = previewData
                previewX(true)
                return true
            }
            if (id == R.id.action_preview_both) {
                previewXY()
                binding.previewChart.zoomType = ZoomType.HORIZONTAL_AND_VERTICAL
                return true
            }
            if (id == R.id.action_preview_horizontal) {
                previewX(true)
                return true
            }
            if (id == R.id.action_preview_vertical) {
                previewY()
                return true
            }
            if (id == R.id.action_change_color) {
                var color = pickColor()
                while (color == binding.previewChart.previewColor) {
                    color = pickColor()
                }
                binding.previewChart.previewColor = color
                return true
            }
            return false
        }

        private fun generateDefaultData() {
            val numValues = 50
            val values: MutableList<PointValue> = ArrayList()
            for (i in 0 until numValues) {
                values.add(PointValue(i.toFloat(), Math.random().toFloat() * 100f))
            }
            val line = Line(values)
            line.color = ChartUtils.COLOR_GREEN
            line.setHasPoints(false) // too many values so don't draw points.
            val lines: MutableList<Line> = ArrayList()
            lines.add(line)
            data = LineChartData(lines)
            data.axisXBottom = Axis()
            data.axisYLeft = Axis().setHasLines(true)

            // prepare preview data, is better to use separate deep copy for preview chart.
            // Set color to grey to make preview area more visible.
            previewData = LineChartData(data)
            previewData.lines[0].color = ChartUtils.DEFAULT_DARKEN_COLOR
        }

        private fun previewY() {
            val tempViewport = Viewport(binding.chart.maximumViewport)
            val dy = tempViewport.height() / 4
            tempViewport.inset(0f, dy)
            binding.previewChart.setCurrentViewportWithAnimation(tempViewport)
            binding.previewChart.zoomType = ZoomType.VERTICAL
        }

        private fun previewX(animate: Boolean) {
            val tempViewport = Viewport(binding.chart.maximumViewport)
            val dx = tempViewport.width() / 4
            tempViewport.inset(dx, 0f)
            if (animate) {
                binding.previewChart.setCurrentViewportWithAnimation(tempViewport)
            } else {
                binding.previewChart.currentViewport = tempViewport
            }
            binding.previewChart.zoomType = ZoomType.HORIZONTAL
        }

        private fun previewXY() {
            // Better to not modify viewport of any chart directly so create a copy.
            val tempViewport = Viewport(binding.chart.maximumViewport)
            // Make temp viewport smaller.
            val dx = tempViewport.width() / 4
            val dy = tempViewport.height() / 4
            tempViewport.inset(dx, dy)
            binding.previewChart.setCurrentViewportWithAnimation(tempViewport)
        }

        /**
         * Viewport listener for preview chart(lower one).
         * in [onViewportChanged] method change viewport of upper chart.
         */
        private inner class ViewportListener : ViewportChangeListener {
            override fun onViewportChanged(newViewport: Viewport) {
                // don't use animation, it is unnecessary when using preview chart.
                binding.chart.currentViewport = newViewport
            }
        }
    }
}