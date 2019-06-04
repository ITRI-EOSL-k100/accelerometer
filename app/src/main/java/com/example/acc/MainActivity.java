package com.example.acc;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

//    private static final String TAG = "MainActivity";
    private SensorManager mSensorManager;
    private Sensor mAccelerometer,sensor;
    //private Sensor sensor;

    private LineChart mChart;
    private Thread thread;
    private boolean plotData = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        List<Sensor> sensor = mSensorManager.getSensorList(Sensor.TYPE_ALL);


//        for (int i =0; i<sensor.size();i++){
//
//            Log.d(TAG, "onCreate: Sensor" + i + " :" + sensor.get(i).toString());
//        }

        if (mAccelerometer != null){

            mSensorManager.registerListener(this,mAccelerometer,SensorManager.SENSOR_DELAY_GAME);
        }

        mChart = (LineChart)findViewById(R.id.chart1);
        mChart.getDescription().setEnabled(false);
//        mChart.getDescription().setText("Real Time EMG Signal");
        mChart.getDescription().setTextColor(Color.RED);

        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(true);
        mChart.setPinchZoom(true);
        mChart.setBackgroundColor(Color.BLACK);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);
        mChart.setData(data);

        Legend l = mChart.getLegend();

        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis x1 = mChart.getXAxis();
        x1.setTextColor(Color.WHITE);
        x1.setDrawGridLines(true);
        x1.setAvoidFirstLastClipping(true);
        x1.setEnabled(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(-100f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        mChart.getAxisLeft().setDrawGridLines(true);
        mChart.getXAxis().setDrawGridLines(true);
        mChart.setDrawBorders(true);

        startPlot();
    }

    private  void startPlot(){
        if (thread != null){
            thread.interrupt();
        }
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    plotData = true;
                    try{
                        Thread.sleep(10);

                    }catch (InterruptedException e){
                        e.printStackTrace();

                    }
                }
            }
        });
        thread.start();

    }

    private void addEntry1(SensorEvent event){
        LineData data = mChart.getData();
        if(data != null){
            LineDataSet set  = (LineDataSet) data.getDataSetByIndex(0);
            if (set == null){
                set = createSet();
                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount(),event.values[0]+5),0);
            data.notifyDataChanged();

            mChart.notifyDataSetChanged();

            mChart.setVisibleXRange(150,150);
            //mChart.setMaxVisibleValueCount(150);

            //mChart.moveViewToX(data.getEntryCount()-100);
            mChart.moveViewToX(data.getEntryCount());

        }
    }

    private LineDataSet createSet(){
        LineDataSet set  = new LineDataSet(null,"Real Time EMG Signal");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(Color.MAGENTA);
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.001f);
        return set;
    }

    public void onSensorChanged(SensorEvent sensorEvent){

        if(plotData){
            addEntry1(sensorEvent);
            plotData = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (thread != null){
            thread.interrupt();
        }
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        mSensorManager.registerListener(this,mAccelerometer,SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        thread.interrupt();
        super.onDestroy();
    }
}
