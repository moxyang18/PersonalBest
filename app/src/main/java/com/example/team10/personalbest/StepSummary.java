package com.example.team10.personalbest;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.team10.personalbest.fitness.CloudProcessor;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;
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

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class StepSummary extends AppCompatActivity {

    private BarChart barChart10;
    //private DataProcessor dp;
    private int[] goal_list = new int[28];
    private HashMap<String,WalkDay> user_WalkDays;
    private String userEmail = "";
    private String displayName = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_summary);
        config();

        for (int i = 0; i < 28; i++)
            goal_list[i]=0;

        // from the friends' list, can see the summary char
        barChart10 = findViewById(R.id.summary_bar_chart);
        userEmail = ActivityMediator.getInstance().getUserEmail();
        user_WalkDays = ActivityMediator.getUserWalkDays();
        displayName = ActivityMediator.getInstance().getUserDisplayName();
        // Get data for chart
        //dp = DataProcessor.getInstance();

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

        // create the labels for the x-axis
        ArrayList<String> labels = new ArrayList<>();
        // Loop over all past 28 days
        for (int i = 27; i >= 0; i--) {

            // Get the day we're processing
            dayDate = iDate.minusDays(i);

            labels.add(dayDate.toString().substring(5));
            //since we update local walkdays when calling savelocal, no need to read from cloud
            day = user_WalkDays.get(dayDate.toString());
            //day = dp.retrieveDay(dayDate);

            // Add data for that data to the graph
            if (day != null) {
                entries.add(new BarEntry(27-i, new float[]
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
        data.setValueTextColor(Color.BLACK);

        // create x-axis labels
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

        // put data onto the bar chart
        barChart10.setData(data); //stepData);

        barChart10.setFitBars(true);
        barChart10.animateY(1000);

        // display the daily goal limit lines based on which bar of the day gets clicked
        barChart10.setHighlightPerDragEnabled(false);
        barChart10.setHighlightPerTapEnabled(true);
        barChart10.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                barChart10.getAxisLeft().removeAllLimitLines();
                int day_ind = (int)e.getX();

                int day_goal = goal_list[27-day_ind];
                if(day_goal !=0)
                    barChart10.getAxisLeft().addLimitLine(new LimitLine(day_goal, "Goal of the Day"));

                setDataField(user_WalkDays.get((LocalDate.now().minusDays(27-day_ind)).toString()));
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

    private void setDataField(WalkDay day) {
        TextView dataView = findViewById(R.id.summary_data_field);

        float mpH = day.getSpeed_average()*2.236f;

        float distance = day.getDistanceRunTotal()*0.000621371f;

        long millis = day.getTime_run_sec_daily();
        String time = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

        // get the calculated MPH, daily distance and the total walk time on that day
        String info = "MPH: " + String.format("%.2f",mpH) +" \n" + "Daily Distance: " +  String.format("%.3f",distance) + " \n" + "Total Time: " + time;
        dataView.setText(info);
    }

}
