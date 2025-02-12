package lecho.lib.hellocharts.renderer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.provider.ColumnChartDataProvider;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.Chart;

/**
 * Renderer for preview chart based on ColumnChart. In addition to drawing chart data it also draw current viewport as
 * preview area.
 */
public class PreviewColumnChartRenderer extends ColumnChartRenderer {
    private static final int DEFAULT_PREVIEW_TRANSPARENCY = 64;
    private static final int FULL_ALPHA = 255;
    private static final int DEFAULT_PREVIEW_STROKE_WIDTH_DP = 2;

    private final Paint previewPaint = new Paint();

    public PreviewColumnChartRenderer(Context context, Chart chart, ColumnChartDataProvider dataProvider) {
        super(context, chart, dataProvider);
        previewPaint.setAntiAlias(true);
        previewPaint.setColor(Color.LTGRAY);
        previewPaint.setStrokeWidth(ChartUtils.dp2px(density, DEFAULT_PREVIEW_STROKE_WIDTH_DP));
    }

    @Override
    public void drawUnClipped(Canvas canvas) {
        super.drawUnClipped(canvas);
        final Viewport currentViewport = computator.getCurrentViewport();
        final float left = computator.computeRawX(currentViewport.left);
        final float top = computator.computeRawY(currentViewport.top);
        final float right = computator.computeRawX(currentViewport.right);
        final float bottom = computator.computeRawY(currentViewport.bottom);
        previewPaint.setAlpha(DEFAULT_PREVIEW_TRANSPARENCY);
        previewPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(left, top, right, bottom, previewPaint);
        previewPaint.setStyle(Paint.Style.STROKE);
        previewPaint.setAlpha(FULL_ALPHA);
        canvas.drawRect(left, top, right, bottom, previewPaint);
    }

    public int getPreviewColor() {
        return previewPaint.getColor();
    }

    public void setPreviewColor(int color) {
        previewPaint.setColor(color);
    }

}
