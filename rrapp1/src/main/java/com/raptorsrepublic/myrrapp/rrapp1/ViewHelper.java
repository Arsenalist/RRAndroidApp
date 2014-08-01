package com.raptorsrepublic.myrrapp.rrapp1;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TableRow;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by Zarar_Siddiqi on 6/9/2014.
 */
public class ViewHelper {

    private static String TAG = "ViewHelper";


    private static String MAIN_TEAM_ABBR = "TOR";
    private static String MAIN_TEAM_ID = "5";

    private static final String FEED_GAME_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss Z";
    private static SimpleDateFormat gameDateFormatter = new SimpleDateFormat(FEED_GAME_DATE_FORMAT);

    private static final String FEED_GAME_DATE_FORMAT_DISPLAY = "EEE MMM d";
    private static SimpleDateFormat gameDateDisplay = new SimpleDateFormat(FEED_GAME_DATE_FORMAT_DISPLAY);

    private static final String FEED_GAME_TIME_FORMAT_DISPLAY = "h:mm a";
    private static SimpleDateFormat gameTimeDisplay = new SimpleDateFormat(FEED_GAME_TIME_FORMAT_DISPLAY);

    public static TableRow.LayoutParams tableRowLayoutParams = new
            TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);

    static {
        gameDateFormatter.setTimeZone(TimeZone.getDefault());
        tableRowLayoutParams.setMargins(5, 5, 5, 5);
    }

    public static String httpGet(String url) {
        Request request = new Request.Builder().url(url).build();
        try {
            return new OkHttpClient().newCall(request).execute().body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static TextView textColumn(Context context, String text) {
        TextView t = new TextView(context);
        t.setPadding(6, 6, 6, 6);
        t.setText(text);
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        return t;
    }

    public static TextView addSmallTextColumn(Context context, TableRow tr, String text, boolean isHeaderCell) {
        TextView t = new TextView(context);
        t.setPadding(6, 6, 6, 6);
        t.setText(text);
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        tr.addView(t);
        if (isHeaderCell) {
            //t.setBackgroundColor(Color.DKGRAY);
            t.setTypeface(null, Typeface.BOLD);
        }
        return t;
    }

    public static TextView addTextColumn(Context context, TableRow tr, String text, boolean isHeaderCell) {
        TextView t = new TextView(context);
        t.setPadding(30, 30, 30, 30);
        t.setText(text);
        tr.addView(t);
        if (isHeaderCell) {
            t.setBackgroundColor(Color.DKGRAY);
            t.setTypeface(null, Typeface.BOLD);
        }
        return t;
    }

    public static String getOpponent(JSONObject game) throws JSONException {
        return game.getJSONObject("away_team").getString("abbreviation").equals(MAIN_TEAM_ABBR) ?
                game.getJSONObject("home_team").getString("name") : game.getJSONObject("away_team").getString("name");
    }

    public static String getLocation(JSONObject game) throws JSONException {
        return game.getJSONObject("away_team").getString("abbreviation").equals(MAIN_TEAM_ABBR) ? "@" : "v";
    }

    public static String formatTime(String date) {
        try {
            return gameTimeDisplay.format(gameDateFormatter.parse(date));
        } catch (ParseException e) {
            Log.d(TAG, "Date could not be formatted " + date);
            return date;
        }
    }

    public static String formatDate(String date) {
        try {
            return gameDateDisplay.format(gameDateFormatter.parse(date));
        } catch (ParseException e) {
            Log.d(TAG, "Date could not be formatted " + date);
            return date;
        }
    }


    public static String getScore(JSONObject row) throws JSONException {
        JSONObject score = row.getJSONObject("box_score").getJSONObject("score");
        String points = score.getJSONObject("home").getString("score") + "-"
                + score.getJSONObject("away").getString("score");
        boolean isWinner = score.getString("winning_team").equals("/nba/teams/" + MAIN_TEAM_ID);
        return (isWinner ? "W " : "L ") + points;
    }
}
