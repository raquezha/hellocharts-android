package lecho.lib.hellocharts.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import lecho.lib.hellocharts.listener.ComboLineColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.listener.DummyCompoLineColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.ChartData;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.ComboLineColumnChartData;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.model.SelectedValue.SelectedValueType;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.provider.ColumnChartDataProvider;
import lecho.lib.hellocharts.provider.ComboLineColumnChartDataProvider;
import lecho.lib.hellocharts.provider.LineChartDataProvider;
import lecho.lib.hellocharts.renderer.ColumnChartRenderer;
import lecho.lib.hellocharts.renderer.ComboLineColumnChartRenderer;
import lecho.lib.hellocharts.renderer.LineChartRenderer;

/**
 * ComboChart, supports ColumnChart combined with LineChart. Lines are always drawn on top.
 *
 * @author Leszek Wach
 */
@SuppressWarnings("unused")
public class ComboLineColumnChartView extends AbstractChartView implements ComboLineColumnChartDataProvider {
    protected ComboLineColumnChartData data;
    protected ColumnChartDataProvider columnChartDataProvider = new ComboColumnChartDataProvider();
    protected LineChartDataProvider lineChartDataProvider = new ComboLineChartDataProvider();
    protected ComboLineColumnChartOnValueSelectListener onValueTouchListener = new
            DummyCompoLineColumnChartOnValueSelectListener();

    public ComboLineColumnChartView(Context context) {
        this(context, null, 0);
    }

    public ComboLineColumnChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ComboLineColumnChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setChartRenderer(new ComboLineColumnChartRenderer(context, this, columnChartDataProvider,
                lineChartDataProvider));
        setComboLineColumnChartData(ComboLineColumnChartData.generateDummyData());
    }

    @NonNull
    @Override
    public ComboLineColumnChartData getComboLineColumnChartData() {
        return data;
    }

    @Override
    public void setComboLineColumnChartData(@NonNull ComboLineColumnChartData data) {
        this.data = data;// generateDummyData();
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

            if (SelectedValueType.COLUMN.equals(selectedValue.type)) {

                SubcolumnValue value = data.getColumnChartData().getColumns().get(selectedValue.firstIndex)
                        .getValues().get(selectedValue.secondIndex);
                onValueTouchListener.onColumnValueSelected(selectedValue.firstIndex,
                        selectedValue.secondIndex, value);

            } else if (SelectedValueType.LINE.equals(selectedValue.type)) {

                PointValue value = data.getLineChartData().getLines().get(selectedValue.firstIndex).getValues()
                        .get(selectedValue.secondIndex);
                onValueTouchListener.onPointValueSelected(selectedValue.firstIndex, selectedValue.secondIndex,
                        value);

            } else {
                if (selectedValue.type != null) {
                    throw new IllegalArgumentException("Invalid selected value type " + selectedValue.type.name());
                } else {
                    throw new IllegalArgumentException("Invalid selected value type " + selectedValue);
                }
            }
        } else {
            onValueTouchListener.onValueDeselected();
        }
    }

    public ComboLineColumnChartOnValueSelectListener getOnValueTouchListener() {
        return onValueTouchListener;
    }

    public void setOnValueTouchListener(ComboLineColumnChartOnValueSelectListener touchListener) {
        if (null != touchListener) {
            this.onValueTouchListener = touchListener;
        }
    }

    public void setColumnChartRenderer(Context context, ColumnChartRenderer columnChartRenderer){
        setChartRenderer(new ComboLineColumnChartRenderer(context, this , columnChartRenderer, lineChartDataProvider));
    }

    public void setLineChartRenderer(Context context, LineChartRenderer lineChartRenderer){
        setChartRenderer(new ComboLineColumnChartRenderer(context, this, columnChartDataProvider, lineChartRenderer));
    }

    private class ComboLineChartDataProvider implements LineChartDataProvider {

        @NonNull
        @Override
        public LineChartData getLineChartData() {
            return ComboLineColumnChartView.this.data.getLineChartData();
        }

        @Override
        public void setLineChartData(@NonNull LineChartData data) {
            ComboLineColumnChartView.this.data.setLineChartData(data);

        }

    }

    private class ComboColumnChartDataProvider implements ColumnChartDataProvider {

        @NonNull
        @Override
        public ColumnChartData getColumnChartData() {
            return ComboLineColumnChartView.this.data.getColumnChartData();
        }

        @Override
        public void setColumnChartData(@NonNull ColumnChartData data) {
            ComboLineColumnChartView.this.data.setColumnChartData(data);

        }

    }

}
