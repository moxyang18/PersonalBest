package com.example.team10.personalbest;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

public class BarChartActivity extends AppCompatActivity {

    private BarChart barChart10;
    private HashMap<String,WalkDay> user_WalkDays;
    //private DataProcessor dp;
    private String userEmail;
    private String userDisplayName;

    private int[] goal_list = {0,0,0,0,0,0,0};
    private static String TAG = "BarChartActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);
        config();
        user_WalkDays = ActivityMediator.getUserWalkDays();
//        userEmail = ActivityMediator.getInstance().getUserEmail();
//        userDisplayName = ActivityMediator.getInstance().getUserDisplayName();

        barChart10 =  findViewById(R.id.bar_chart);


        // Get data for chart
        //dp = DataProcessor.getInstance();

        // Display chart
        displayChart();
    }

    private void displayChart() {

        // Determine the day of the week so we can start on Sunday
        String dayOfWeek = LocalDate.now().getDayOfWeek().toString();

        // How many days must we subtract to get to sunday?
        int minDays = 0;
        switch (dayOfWeek) {
            case "SUNDAY": minDays = 0; break;
            case "MONDAY": minDays = 1; break;
            case "TUESDAY": minDays = 2; break;
            case "WEDNESDAY": minDays = 3; break;
            case "THURSDAY": minDays = 4; break;
            case "FRIDAY": minDays = 5; break;
            case "SATURDAY": minDays = 6; break;
        }

        // Obtain sunday
        LocalDate sundayDate = LocalDate.now().minusDays(minDays);

        // create entries of each day's steps of the week
        ArrayList<BarEntry> entries = new ArrayList<>();

        // Used to get info for each day
        LocalDate dayDate;
        WalkDay day;
        boolean[] goalMet = new boolean[7];
        int goal_max = 0;
        int step_max=0;
        // Loop over all 7 days of the week
        for (int i = 0; i < 7; i++) {

            // Get the day we're processing
            dayDate = sundayDate.plusDays(i);

            //since we update local walkdays when calling savelocal, no need to read from cloud
            /*
            if(dayDate.isEqual(LocalDate.now()))
                day = day = CloudProcessor.retrieveDay(dayDate,userEmail);

            else
            */
                day = user_WalkDays.get(dayDate.toString());
            //day = CloudProcessor.retrieveDay(dayDate,userEmail);

            // Add data for that data to the graph
            if (day != null) {
                entries.add(new BarEntry(i, new float[]
                        {day.getStepCountUnintentional(), day.getStepCountIntentional()}));

                Log.i(TAG+"'s Intentional Steps: ", Integer.toString(day.getStepCountIntentional()));
                Log.i(TAG+"'s Total Steps: ", Integer.toString(day.getStepCountDailyTotal()));
                goal_list[i] =day.getGoal();
                if(goal_max<day.getGoal())
                    goal_max = day.getGoal();

                if(step_max<day.getStepCountDailyTotal())
                    step_max = day.getStepCountDailyTotal();

                goalMet[i] = day.getGoalMet();

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

        // create labels representing the x-axis days
        ArrayList<String> labels = new ArrayList<>();
        //String curDay;

        // Determine if check mark should be present
        labels.add((goalMet[0])? "Sun(???)":"Sun");
        labels.add((goalMet[1])? "Mon(???)":"Mon");
        labels.add((goalMet[2])? "Tue(???)":"Tue");
        labels.add((goalMet[3])? "Wed(???)":"Wed");
        labels.add((goalMet[4])? "Thu(???)":"Thu");
        labels.add((goalMet[5])? "Fri(???)":"Fri");
        labels.add((goalMet[6])? "Sat(???)":"Sat");

        XAxis x = barChart10.getXAxis();
        x.setValueFormatter(new IndexAxisValueFormatter(labels));
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setGranularity(1);
        x.setGranularityEnabled(true);

        YAxis leftAxis = barChart10.getAxisLeft();

        if(step_max > goal_max)
            leftAxis.setAxisMaximum(step_max * 1.2f);
        else leftAxis.setAxisMaximum(goal_max * 1.2f);

        // put data onto the bar chart
        barChart10.setData(data); //stepData);
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
                Log.i(TAG + "'s Daily Goal is displayed:", Integer.toString(day_goal));
                if(day_goal !=0)
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