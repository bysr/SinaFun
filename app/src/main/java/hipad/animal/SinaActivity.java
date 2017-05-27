package hipad.animal;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import hipad.unittest.R;

public class SinaActivity extends AppCompatActivity {
    private LineChart mChart;

    private Typeface tf;

    private SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sina);
        tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        init();

        initSeekBar();

    }

    private void initSeekBar() {
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(127);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                addEntry(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    /**
     * 改变数据
     *
     * @param progress
     */
    private void addEntry(int progress) {

        LineData data = mChart.getLineData();


        if (data != null) {

            LineDataSet lastSet = (LineDataSet) data.getDataSetByIndex(1);
            if (lastSet == null) {


            } else {

                data.removeDataSet(1);
                mChart.invalidate(); // refresh

                data.addDataSet(getDateLine(getValue(progress)));

            }



        }



    }


    public double getValue(int progress) {

        return 2 * Math.PI * progress / 127;

    }

    private void init() {


        mChart = (LineChart) findViewById(R.id.lineChart1);

        mChart.getDescription().setEnabled(false);
        /*是否可以触摸*/
        mChart.setTouchEnabled(true);
        /*是否显示表格颜色*/
        mChart.setDrawGridBackground(false);

        mChart.setDragEnabled(true);// 是否可以拖拽
        mChart.setScaleEnabled(true);// 是否可以缩放

        mChart.setData(generateLineData());
        mChart.animateX(3000);
        // 是否在折线图上添加边框
        mChart.setDrawBorders(false);


        Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");

        setLabel(tf);

        setYAxis(tf);

        setXAxis(tf);


    }

    /**
     * 设置标签说明
     *
     * @param tf
     */
    private void setLabel(Typeface tf) {
        Legend l = mChart.getLegend();
        l.setTypeface(tf);
        l.setTextColor(getResources().getColor(R.color.write));
    }

    /**
     * 设置Y轴
     *
     * @param tf
     */
    private void setYAxis(Typeface tf) {
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(tf);
        leftAxis.setAxisMaximum(1.2f);
        leftAxis.setAxisMinimum(-1.2f);
        leftAxis.setTextColor(getResources().getColor(R.color.write));

        leftAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return "" + value;
            }
        });

       /*设置右侧显示坐标*/
        mChart.getAxisRight().setEnabled(false);
    }

    /**
     * 设置X轴
     *
     * @param tf
     */
    private void setXAxis(Typeface tf) {
        XAxis xAxis = mChart.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setTextColor(getResources().getColor(R.color.write));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(6);
        xAxis.setTypeface(tf);
        xAxis.setDrawGridLines(true);
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(540);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return (int) value / 10 + "m";
            }
        });
    }


    public double Sin(int i, double k) {
        double result = 0;
        //在这里我是写sin函数，其实可以用cos，tan等函数的，不信大家尽管试试
        //result = Math.cos(i * Math.PI / 180);
        result = Math.sin((i * Math.PI / 180) + k);
        //result = Math.tan(i * Math.PI / 180);
        return result;
    }

    /**
     * 默认曲线
     *
     * @return
     */
    protected LineDataSet getDefaultLine() {

        List<Entry> yVals = new ArrayList<>();
        for (int i = 0; i < 540; i++) {
            yVals.add(new Entry(i, (float) Sin(i, 0)));
        }
        LineDataSet ds1 = new LineDataSet(yVals, "sinA");
        ds1.setLineWidth(2f);
        ds1.setDrawCircles(false);
        ds1.setValueTextColor(getResources().getColor(R.color.write));
        ds1.setColor(Color.YELLOW);
        return ds1;
    }

    /**
     * 可变化的函数曲线
     *
     * @return
     */
    protected LineDataSet getDateLine(double x) {
        List<Entry> yVals = new ArrayList<>();
        for (int i = 0; i < 540; i++) {
            yVals.add(new Entry(i, (float) Sin(i, x)));
        }
        LineDataSet ds2 = new LineDataSet(yVals, "sinB");
        //设置线条宽度
        ds2.setLineWidth(2f);
        //设置有原点
        ds2.setDrawCircles(false);
        ds2.setColor(Color.GREEN);
        return ds2;

    }

    /**
     * 添加两条曲线的数据曲线数据
     *
     * @return
     */
    protected LineData generateLineData() {
        ArrayList<ILineDataSet> sets = new ArrayList<>();
        // load DataSets from textfiles in assets folders
        sets.add(getDefaultLine());
        sets.add(getDateLine(1));
        LineData d = new LineData(sets);
        d.setValueTypeface(tf);
        return d;
    }

}
