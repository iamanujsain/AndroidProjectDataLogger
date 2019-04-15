package samrat.androiddataloggerproject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import samrat.androiddataloggerproject.R;import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.LinkedList;

//from github
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.List;
//end of from github
//test code
import android.widget.EditText;
import android.widget.TextView;
//end of test code


@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class MainActivity extends AppCompatActivity implements SensorEventListener2 {

    SensorManager manager;
    Button buttonStart;
    Button buttonStop;
    boolean isRunning;
    final String TAG = "SensorLog";
    FileWriter writer;

    //Code from github
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;
    private  Sensor sensors;

    private LineChart mChart;
    private LineChart mChart2;
    private Thread thread;
    private boolean plotData = true;
    //End of from github

    //test code file name
    EditText fileName;
    //end of test code file name
    //testcode toggle button
    ToggleButton toggleGyro = (ToggleButton) findViewById(R.id.toggleGyro); // initiate a toggle button
    Boolean toggleGyroState = toggleGyro.isChecked();
    //end of test code toggle button


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isRunning = false;

        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        buttonStart = (Button)findViewById(R.id.buttonStart);
        buttonStop = (Button)findViewById(R.id.buttonStop);

        //test code file name
        fileName  = (EditText) findViewById(R.id.fileName);
        final String fileNameValue = fileName.getText().toString();
        //end of test code file name

        buttonStart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                buttonStart.setEnabled(false);
                buttonStop.setEnabled(true);

                Log.d(TAG, "Writing to " + getStorageDir());
                try {
                    writer = new FileWriter(new File(getStorageDir(), "sensors_" + fileNameValue + System.currentTimeMillis() + ".csv"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                manager.registerListener(MainActivity.this, manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 0);
                manager.registerListener(MainActivity.this, manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), 0);
                /*manager.registerListener(MainActivity.this, manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED), 0);
                manager.registerListener(MainActivity.this, manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), 0);
                manager.registerListener(MainActivity.this, manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED), 0);
                manager.registerListener(MainActivity.this, manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), 0);
                manager.registerListener(MainActivity.this, manager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR), 0);*/

                isRunning = true;
                return true;
            }
        });
        buttonStop.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                buttonStart.setEnabled(true);
                buttonStop.setEnabled(false);
                isRunning = false;
                manager.flush(MainActivity.this);
                manager.unregisterListener(MainActivity.this);
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });

        //From github
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //Test Code
        /*
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        */
        //End of Test Code

        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        for(int i=0; i<sensors.size(); i++){
            Log.d(TAG, "onCreate: Sensor "+ i + ": " + sensors.get(i).toString());
        }

        if (mAccelerometer != null) {
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        }
        //TestCode:
        /*
        if (mGyroscope != null) {
            mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_FASTEST);
        }
        */
        //End of Test Code

        mChart = (LineChart) findViewById(R.id.chart1);

        // enable description text
        mChart.getDescription().setEnabled(true);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.WHITE);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        mChart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMaximum(50f);
        leftAxis.setAxisMinimum(-50f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.setDrawBorders(false);

        feedMultiple();
        //End of from github

        //Test Code
        /*
        mChart2 = (LineChart) findViewById(R.id.chart2);

        // enable description text
        mChart2.getDescription().setEnabled(true);

        // enable touch gestures
        mChart2.setTouchEnabled(true);

        // enable scaling and dragging
        mChart2.setDragEnabled(true);
        mChart2.setScaleEnabled(true);
        mChart2.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart2.setPinchZoom(true);

        // set an alternative background color
        mChart2.setBackgroundColor(Color.WHITE);

        LineData data2 = new LineData();
        data2.setValueTextColor(Color.WHITE);

        // add empty data
        mChart2.setData(data2);

        // get the legend (only possible after setting data)
        Legend l2 = mChart2.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis xl2 = mChart2.getXAxis();
        xl2.setTextColor(Color.WHITE);
        xl2.setDrawGridLines(true);
        xl2.setAvoidFirstLastClipping(true);
        xl2.setEnabled(true);

        YAxis leftAxis2 = mChart2.getAxisLeft();
        leftAxis2.setTextColor(Color.WHITE);
        leftAxis2.setDrawGridLines(false);
        leftAxis2.setAxisMaximum(50f);
        leftAxis2.setAxisMinimum(-50f);

        YAxis rightAxis2 = mChart2.getAxisRight();
        rightAxis2.setEnabled(false);

        mChart2.getAxisLeft().setDrawGridLines(false);
        mChart2.getXAxis().setDrawGridLines(false);
        mChart2.setDrawBorders(false);

        feedMultiple();
        */
        //End Of Test Code
    }

    private String getStorageDir() {
        return this.getExternalFilesDir(null).getAbsolutePath();
        //  return "/storage/emulated/0/Android/data/";
    }

    @Override
    public void onFlushCompleted(Sensor sensor) {

    }

    //From Github
    public void addEntry(SensorEvent event) {

        LineData data = mChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

//            data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 80) + 10f), 0);
            data.addEntry(new Entry(set.getEntryCount(), event.values[0] ), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(150);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            mChart.moveViewToX(data.getEntryCount());

        }
        //TestCode
        /*
        LineData data2 = mChart2.getData();

        if (data2 != null) {

            ILineDataSet set = data2.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data2.addDataSet(set);
            }

//            data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 80) + 10f), 0);
            data2.addEntry(new Entry(set.getEntryCount(), event.values[0] + 5), 0);
            data2.notifyDataChanged();

            // let the chart know it's data has changed
            mChart2.notifyDataSetChanged();

            // limit the number of visible entries
            mChart2.setVisibleXRangeMaximum(150);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            mChart2.moveViewToX(data.getEntryCount());

        }
        */
        //End of Test COde
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(1f);
        set.setColor(Color.BLACK);
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }

    private void feedMultiple() {

        if (thread != null){
            thread.interrupt();
        }

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true){
                    plotData = true;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (thread != null) {
            thread.interrupt();
        }
        mSensorManager.unregisterListener(this);

    }
    //End of from github

    @Override
    public void onSensorChanged(SensorEvent evt) {

        if(plotData){
            addEntry(evt);
            plotData = false;
        }

        if(isRunning) {
            try {
                float xA=0, yA=0, zA=0, xG=0, yG=0, zG=0;
                switch(evt.sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER:
                        xA = evt.values[0];
                        yA =  evt.values[1];
                        zA = evt.values[2];
                    case Sensor.TYPE_GYROSCOPE:
                        xG = evt.values[0];
                        yG =  evt.values[1];
                        zG = evt.values[2];
                        break;

                    /*case Sensor.TYPE_ACCELEROMETER:
                        writer.write(String.format("%d; ACC; %f; %f; %f; %f; %f; %f\n", evt.timestamp, evt.values[0], evt.values[1], evt.values[2], 0.f, 0.f, 0.f));
                        break;
                    case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                        writer.write(String.format("%d; GYRO_UN; %f; %f; %f; %f; %f; %f\n", evt.timestamp, evt.values[0], evt.values[1], evt.values[2], evt.values[3], evt.values[4], evt.values[5]));
                        break;
                    case Sensor.TYPE_GYROSCOPE:
                        writer.write(String.format("%d; GYRO; %f; %f; %f; %f; %f; %f\n", evt.timestamp, evt.values[0], evt.values[1], evt.values[2], 0.f, 0.f, 0.f));
                        break;
                    case Sensor.TYPE_MAGNETIC_FIELD:
                        writer.write(String.format("%d; MAG; %f; %f; %f; %f; %f; %f\n", evt.timestamp, evt.values[0], evt.values[1], evt.values[2], 0.f, 0.f, 0.f));
                        break;
                    case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                        writer.write(String.format("%d; MAG_UN; %f; %f; %f; %f; %f; %f\n", evt.timestamp, evt.values[0], evt.values[1], evt.values[2], 0.f, 0.f, 0.f));
                        break;
                    case Sensor.TYPE_ROTATION_VECTOR:
                        writer.write(String.format("%d; ROT; %f; %f; %f; %f; %f; %f\n", evt.timestamp, evt.values[0], evt.values[1], evt.values[2], evt.values[3], 0.f, 0.f));
                        break;
                    case Sensor.TYPE_GAME_ROTATION_VECTOR:
                        writer.write(String.format("%d; GAME_ROT; %f; %f; %f; %f; %f; %f\n", evt.timestamp, evt.values[0], evt.values[1], evt.values[2], evt.values[3], 0.f, 0.f));
                        break;*/
                }
                if(toggleGyroState = true)
                    writer.write("" + xA + "," + yA + "," + zA + "," + xG + "," + yG + "," + zG + "," + evt.timestamp + "\n");
                else
                    writer.write("" + xA + "," + yA + "," + zA + "," + evt.timestamp + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    //From github
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onDestroy() {
        mSensorManager.unregisterListener(MainActivity.this);
        thread.interrupt();
        super.onDestroy();
    }
    //End of from github
}
