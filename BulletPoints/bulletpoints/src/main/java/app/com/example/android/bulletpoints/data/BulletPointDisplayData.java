package app.com.example.android.bulletpoints.data;

import android.view.View;

/**
 * Class to hold the bulletpoint and paragraph data that is read from the database, and the view
 * that will hold the bulletpoint text
 */
public class BulletPointDisplayData {
    private String bulletpoint;
    private String paragraph;
    private View textView;

    public BulletPointDisplayData(String bPoint, String pGraph, View tView) {
        this.bulletpoint = bPoint;
        this.paragraph = pGraph;
        this.textView = tView;
    }

    public String getBulletpoint() {
        return bulletpoint;
    }

    public void setBulletpoint(String bulletpoint) {
        this.bulletpoint = bulletpoint;
    }

    public String getParagraph() {
        return paragraph;
    }

    public void setParagraph(String paragraph) {
        this.paragraph = paragraph;
    }

    public View getTextView() {
        return textView;
    }

    public void setTextView(View textView) {
        this.textView = textView;
    }
}
