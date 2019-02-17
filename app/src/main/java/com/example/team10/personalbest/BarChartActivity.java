package com.example.team10.personalbest;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;

public class BarChartActivity extends AppCompatActivity {

    private BarChart barChart10;
    private ArrayList<ArrayList> stepList;
    private DataProcessor dp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);
        config();

        barChart10 = (BarChart) findViewById(R.id.bar_chart);
        /* first load the steps in stepList, which will store an array of lists each containing the
           planned steps and unplanned steps
        stepList = xxx;*/

        // Get data for chart
        dp = DataProcessor.getInstance();

        // Display chart
        displayChart();
    }

    private void displayChart() {
         /* first load the steps in stepList, which will store an array of lists each containing the
           planned steps and unplanned steps
        stepList = xxx;
        int xInd = 2;
        for (ArrayList<int> day: stepList)
            entries.add(new BarEntry(xInd++, new float[]{day[0], day[1]}));
        */

        // create entries of each day's steps of the week
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, new float[]{1000, 200}));
        entries.add(new BarEntry(1, new float[]{2060, 1000}));
        entries.add(new BarEntry(2, new float[]{1000, 200}));
        entries.add(new BarEntry(3, new float[]{2000, 1000}));
        entries.add(new BarEntry(4, new float[]{2000, 1000}));
        entries.add(new BarEntry(5, new float[]{500, 5000}));
        entries.add(new BarEntry(6, new float[]{700, 1400}));

        BarDataSet entrySet = new BarDataSet(entries, "DailySteps"); //"DailySteps");

        // set the different colors for the vertical bars
        entrySet.setColors(this.getColors());
        entrySet.setBarShadowColor(Color.GRAY);

        // display the bar values, and mark each color with corresponding meaning
        entrySet.setDrawValues(true);
        entrySet.setStackLabels(new String[]{"UnplannedSteps", "PlannedSteps"});

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(entrySet);

        BarData data = new BarData(dataSets);
        //data.setValueFormatter(new MyValueFormatter());
        data.setValueTextColor(Color.BLACK);

        // create labels representing the x-axis days
        ArrayList<String> labels = new ArrayList<>();
        //String curDay;
        //(planned steps != 0) as condition
        labels.add("Sun");
        labels.add("Mon");
        labels.add("Tue");
        labels.add("Wed");
        labels.add("Thu");
        labels.add("Fri");
        labels.add("Sat");

        XAxis x = barChart10.getXAxis();
        x.setValueFormatter(new IndexAxisValueFormatter(labels));
        //x.setCenterAxisLabels(true);
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setGranularity(1);
        x.setGranularityEnabled(true);

        //barChart10.setDragEnabled(true);

        // initialize the BarData with the entries and the labels
        //BarData stepData = new BarData(entrySet);

        // put data onto the bar chart
        barChart10.setData(data); //stepData);
        //barChart10.setFitBars(true);
        barChart10.animateY(1000);

        // display the daily goal limit lines based on which bar of the day gets clicked
        barChart10.setHighlightPerDragEnabled(false);
        barChart10.setHighlightPerTapEnabled(true);
        barChart10.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                barChart10.getAxisLeft().removeAllLimitLines();
                int day_ind = (int)e.getX();
                int[] days_goal = {1060,2049, 3059, 3589, 5937, 5738, 4826};

                int day_goal = days_goal[day_ind];
                barChart10.getAxisLeft().addLimitLine(new LimitLine(day_goal, "Goal of the Day"));
            }

            @Override
            public void onNothingSelected() {

            }
        });

        barChart10.invalidate();
    }

    private int[] getColors() {
        int stackSize = 2;
        // set 2 colors for unplanned and planned steps
        int[] colors = new int[stackSize];
        colors[0] = Color.parseColor("#2cb5d9");
        colors[1] = Color.parseColor("#d34a26");
        return colors;
    }

    private void config() {
        Button back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
