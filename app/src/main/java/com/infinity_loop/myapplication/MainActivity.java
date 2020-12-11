package com.infinity_loop.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    ArrayList<Entry> x;
    ArrayList<Entry> k;
    ArrayList<String> y;
    private LineChart mChart;

    private static final String TAG = "MainActivity";
    private Object Menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        x = new ArrayList<Entry>();
        k = new ArrayList<Entry>();
        y = new ArrayList<String>();
        mChart = (LineChart) findViewById(R.id.chart1);
        mChart.setDrawGridBackground(false);


        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setPinchZoom(true);
        mChart.setHighlightPerTapEnabled(true);
        CustomMarkerView mv = new CustomMarkerView(this, R.layout.custom_marker_view_layout);
        mChart.setMarkerView(mv);


        XAxis xl = mChart.getXAxis();
        xl.setAvoidFirstLastClipping(true);
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setInverted(false);
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
        Legend l = mChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);

        drawChart();

    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.item1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                restartActivity();
                return true;
            case R.id.item2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                restartActivity();
                return true;
            case R.id.item3:
                restartActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void restartActivity()
    {
        Intent intent= new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    private void drawChart() {

        String tag_string_req = "req_chart";

        StringRequest strReq = new StringRequest(Request.Method.POST, "https://moneyfit.000webhostapp.com/",
                new Response.Listener<String>() {

                    @SuppressLint("ResourceType")
                    @Override
                    public void onResponse(String response) {

                        Log.d(TAG, "Response: " + response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
//                            String id = jsonObject.getString("id");
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject c = jsonArray.getJSONObject(i);

                                float value = (float)c.getDouble("investment_current_value");
                                float value2 = (float)c.getDouble("investment_invest_value");
                                String date = c.getString("date");
                                x.add(new Entry(value, i));
                                k.add(new Entry(value2, i));
                                y.add(date);

                            }
                            LineDataSet set1 = new LineDataSet(x, "Current Value");
                            LineDataSet set2 = new LineDataSet(k, "Investment Value");
                            set1.setLineWidth(3f);
//                            set1.setCircleRadius(0f);
//                            set1.setDrawFilled(true);
                            set1.setColor(getResources().getColor(R.color.green));
                            set1.setDrawFilled(true);
                            set1.setDrawCircles(false);
                            set1.setValueTextSize(0f);
                            set1.setFillColor(getResources().getColor(R.color.green));
//                            set1.setFillAlpha(R.drawable.fade_green);
//                            if (android.os.Build.VERSION.SDK_INT >= 18) {
//                                set1.setFillColor(getResources().getColor(R.drawable.fade_green));
//                            }
//                            else {
//
//                            }





                            set2.setLineWidth(3f);
//                            set2.setCircleRadius(0f);
//                            set2.setCircleColor(getResources().getColor(R.color.orange));
                            set2.setColor(getResources().getColor(R.color.orange));
//                            set2.setValueTextColors(Collections.singletonList(getResources().getColor(R.color.orange)));
//                            set2.setValueTextSize(10f);
                            set2.setDrawFilled(true);
                            set2.setValueTextSize(0f);
                            set2.setFillColor(getResources().getColor(R.color.orange));
                            set2.setDrawCircles(false);

                            LineData data = new LineData(y, set1);

                            data.addDataSet(set2);

                            mChart.setData(data);
                            mChart.invalidate();
                            mChart.animateX(3000);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
            }
        });
        strReq.setRetryPolicy(new RetryPolicy() {

            @Override
            public void retry(VolleyError arg0) throws VolleyError {
            }

            @Override
            public int getCurrentTimeout() {
                return 0;
            }

            @Override
            public int getCurrentRetryCount() {
                return 0;
            }
        });
        strReq.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}

