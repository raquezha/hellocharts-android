package net.raquezha.lecho.hellocharts.view;

import android.content.Context;
import android.util.AttributeSet;

import net.raquezha.lecho.hellocharts.listener.DummyLineChartOnValueSelectListener;
import net.raquezha.lecho.hellocharts.listener.LineChartOnValueSelectListener;
import net.raquezha.lecho.hellocharts.model.ChartData;
import net.raquezha.lecho.hellocharts.model.LineChartData;
import net.raquezha.lecho.hellocharts.model.PointValue;
import net.raquezha.lecho.hellocharts.model.SelectedValue;
import net.raquezha.lecho.hellocharts.provider.LineChartDataProvider;
import net.raquezha.lecho.hellocharts.renderer.LineChartRenderer;

/**
 * LineChart, supports cubic lines, filled lines, circle and square points. Point radius and stroke width can be
 * adjusted using LineChartData attributes.
 *
 * @author Leszek Wach
 */
@SuppressWarnings("unused")
public class LineChartView extends AbstractChartView implements LineChartDataProvider {
    protected LineChartData data;
    protected LineChartOnValueSelectListener onValueTouchListener = new DummyLineChartOnValueSelectListener();

    public LineChartView(Context context) {
        this(context, null, 0);
    }

    public LineChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setChartRenderer(new LineChartRenderer(context, this, this));
        setLineChartData(LineChartData.generateDummyData());
    }

    @Override
    public LineChartData getLineChartData() {
        return data;
    }

    @Override
    public void setLineChartData(LineChartData data) {
//        if (BuildConfig.DEBUG) {
//            Log.d(TAG, "Setting data for LineChartView");
//        }

        if (null == data) {
            this.data = LineChartData.generateDummyData();
        } else {
            this.data = data;
        }

        super.onChartDataChange();
    }

    @Override
    public ChartData getChartData() {
        return data;
    }

    @Override
    public void callTouchListener() {
        SelectedValue selectedValue = chartRenderer.getSelectedValue();

        if (selectedValue.isSet()) {
            PointValue point = data.getLines().get(selectedValue.getFirstIndex()).getValues()
                    .get(selectedValue.getSecondIndex());
            onValueTouchListener.onValueSelected(selectedValue.getFirstIndex(), selectedValue.getSecondIndex(), point);
        } else {
            onValueTouchListener.onValueDeselected();
        }
    }

    public LineChartOnValueSelectListener getOnValueTouchListener() {
        return onValueTouchListener;
    }

    public void setOnValueTouchListener(LineChartOnValueSelectListener touchListener) {
        if (null != touchListener) {
            this.onValueTouchListener = touchListener;
        }
    }
}