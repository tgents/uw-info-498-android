package edu.uw.tgents.selftracker;

import java.text.SimpleDateFormat;

/**
 * Created by tgents on 1/25/2016.
 */
public class Activity implements Comparable<Activity>{
    private long time;
    private int quantity;
    private String comment;

    public Activity(long time, int quantity, String comment) {
        this.time = time;
        this.quantity = quantity;
        this.comment = comment;
    }

    public long getTime() {
        return time;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
        String tempTime = sdf.format(time);

        return tempTime + " " + quantity + " " + comment;
    }

    @Override
    public int compareTo(Activity another) {
        return (int) (-time + another.time);
    }
}
