package com.example.team10.personalbest;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.time.LocalDate;

public class StepSummary extends AppCompatActivity {

    private BarChart barChart10;
    private DataProcessor dp;
    private int[] goal_list = new int[28];//goal_list = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_summary);
        config();

        for (int i = 0; i < 28; i++)
            goal_list[i]=0;

        // from the friends' list, can see the summary char
        barChart10 = findViewById(R.id.summary_bar_chart);

        // Get data for chart
        dp = DataProcessor.getInstance();

        // Display chart
        displayChart();
    }

    private void displayChart() {
        // Obtain sunday
        LocalDate iDate = LocalDate.now();

        // create entries of each day's steps of the week
        ArrayList<BarEntry> entries = new ArrayList<>();

        // Used to get info for each day
        LocalDate dayDate;
        WalkDay day;
        boolean[] goalMet = new boolean[28];
        int goal_max = 0;
        int step_max=0;

        ArrayList<String> labels = new ArrayList<>();
        // Loop over all past 28 days
        for (int i = 27; i >= 0; i++) {

            // Get the day we're processing
            dayDate = iDate.minusDays(i);

            labels.add(dayDate.toString());
            day = dp.retrieveDay(dayDate);

            // Add data for that data to the graph
            if (day != null) {
                entries.add(new BarEntry(i, new float[]
                        {day.getStepCountUnintentional(), day.getStepCountIntentional()}));
                goal_list[i] =day.getGoal();
                if(goal_max<day.getGoal())
                    goal_max = day.getGoal();
                if(step_max<day.getStepCountDailyTotal())
                    step_max = day.getStepCountDailyTotal();
                goalMet[i] = day.getStepCountDailyTotal() >= day.getGoal();
            } else {
                entries.add(new BarEntry(i, new float[]{0, 0}));
                goalMet[i] = false;
            }
        }

        // Gather up the bars into a set
        BarDataSet entrySet = new BarDataSet(entries, "");

        // set the different colors for the vertical bars
        entrySet.setColors(this.getColors());
        entrySet.setBarShadowColor(Color.GRAY);

        // display the bar values, and mark each color with corresponding meaning
        entrySet.setDrawValues(true);
        entrySet.setStackLabels(new String[]{"UnplannedSteps", "PlannedSteps"});

        // Add bars to data set
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(entrySet);

        BarData data = new BarData(dataSets);
        //data.setValueFormatter(new MyValueFormatter());
        data.setValueTextColor(Color.BLACK);


/*
        // test 28 consecutive days
        labels.add("02/02");
        labels.add("02/03");
        labels.add("02/04");
        labels.add("02/05");
        labels.add("02/01");
        labels.add("02/02");
        labels.add("02/03");
        /*labels.add("02/04");
        labels.add("02/05");
        labels.add("02/01");
        labels.add("02/02");
        labels.add("02/03");
        labels.add("02/04");
        labels.add("02/05");
        labels.add("02/01");
        labels.add("02/02");
        labels.add("02/03");
        labels.add("02/04");
        labels.add("02/05");
        labels.add("02/01");
        labels.add("02/02");
        labels.add("02/03");
        labels.add("02/04");
        labels.add("02/05");
        labels.add("02/01");
        labels.add("02/02");
        labels.add("02/03");  */


        XAxis x = barChart10.getXAxis();
        x.setValueFormatter(new IndexAxisValueFormatter(labels));
        //x.setCenterAxisLabels(true);
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setGranularity(1);
        x.setGranularityEnabled(true);

        YAxis leftAxis = barChart10.getAxisLeft();

        if(step_max > goal_max)
            leftAxis.setAxisMaximum(step_max * 1.2f);
        else leftAxis.setAxisMaximum(goal_max * 1.2f);

        //barChart10.setDragEnabled(true);
        //barChart10.setVisibleXRangeMaximum(7);
        //barChart10.moveViewToX();
        //barChart10.setVisibleXRangeMinimum(4);

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
                //int[] days_goal = {1060,2049, 3059, 3589, 5937, 5738, 4826};

                int day_goal = goal_list[day_ind];
                if(day_goal !=0)
                    barChart10.getAxisLeft().addLimitLine(new LimitLine(day_goal, "Goal of the Day"));

                setDataField();
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
        Button back_button = findViewById(R.id.summary_back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setDataField() {
        TextView dataView = findViewById(R.id.summary_data_field);
        String info = "MPH: " + "100 \n" + "AVR Velocity: " + "4.5 m/h\n" + "Total Time: " + "04:59";
        dataView.setText(info);
    }

/*
    private class CustMarkerView extends MarkerView {
        private TextView mContentTv;

        public CustMarkerView(Context context, int layoutResource) {
            super(context, layoutResource);
            mContentTv = (TextView) findViewById(R.id.tv_marker_view);
        }

        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            mContentTv.setText("" + e.getVal());
        }

    }

*/

}
