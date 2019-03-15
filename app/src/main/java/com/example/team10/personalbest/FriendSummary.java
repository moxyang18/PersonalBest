package com.example.team10.personalbest;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

public class FriendSummary extends AppCompatActivity {

    private BarChart barChart10;
    //private DataProcessor dp;
    private String friendEmail ="";
    private String userEmail = "";
    private TextView header;
    private int[] goal_list = new int[28];
    private static String TAG = "FriendSummary";

    private HashMap<String,WalkDay> user_WalkDays;
    private HashMap<String,WalkDay> friend_WalkDays;
    private String displayName = "";

    /*
    * Populate the DataProcessor with the friend's monthly data stored in the
    * FireBase and draw the chart based on it.
    *
    * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_summary);
        config();

        for (int i = 0; i < 28; i++)
            goal_list[i]=0;

        // get friends friendEmail and set the chart's header
        Bundle extras = this.getIntent().getExtras();
        friendEmail = extras.getString("email");
        if(friendEmail == null){
            Log.d(TAG, "ERROR, didn't get friend name");
        }
            //ActivityMediator.getInstance().preloadFriendWalkDays(friendEmail);


        // from the friends' list, can see the summary char
        barChart10 = findViewById(R.id.friend_bar_chart);
        header = findViewById(R.id.name_title);
        if(this.friendEmail == null)
            header.setText("The Friend's Step Chart");
        else
            header.setText( this.friendEmail + "'s Step Chart");
        userEmail = ActivityMediator.getInstance().getUserEmail();
        user_WalkDays = ActivityMediator.getUserWalkDays();//FIXME currently using user walkDays since friend unimplemented
        friend_WalkDays = ActivityMediator.getFriendWalkDays();
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
            if(dayDate.isEqual(LocalDate.now())){
                day = CloudProcessor.retrieveDay(dayDate,userEmail);
                if(day ==null)
                    day = friend_WalkDays.get(dayDate.toString());
            }

            else{
                day = friend_WalkDays.get(dayDate.toString());
            }


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

                int day_goal =goal_list[27-day_ind];

                //Log.i(TAG, Integer.toString(day_goal));

                if(day_goal !=0) {
                    barChart10.getAxisLeft().addLimitLine(new LimitLine(day_goal, "Goal of the Day"));
                    Log.i(TAG, Integer.toString(day_goal));
                }
                setDataField(user_WalkDays.get((LocalDate.now().minusDays(27-day_ind)).toString()));
                //FIXME currently using user walkDays since friend unimplemented
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
        Button back_button = findViewById(R.id.friend_summary_back);
        back_button.setOnClickListener(v -> finish());

        Button message_button = findViewById(R.id.message_in_chart);
        message_button.setOnClickListener(v -> {
            Intent message = new Intent(this, MessagePage.class);
            message.putExtra("email", friendEmail);
            this.startActivity(message);
        });
    }

    private void setDataField(WalkDay day) {
        TextView dataView = findViewById(R.id.friend_summary_data);

        float mpH = day.getSpeed_average()*2.236f;

        float distance = day.getDistanceRunTotal()*0.000621371f;

        long millis = day.getTime_run_sec_daily();
        String time = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

        // get the calculated MPH, daily distance and the total walk time on that day
        String info = "MPH: " + String.format("%.3f",mpH) +" \n" + "Daily Distance: " + String.format("%.3f",distance) + " \n" + "Total Time: " + time;
        dataView.setText(info);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityMediator.setFriendWalkDays(new HashMap<String, WalkDay>());
    }

}