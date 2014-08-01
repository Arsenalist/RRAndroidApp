package com.raptorsrepublic.myrrapp.rrapp1;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BoxScoreActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box_score);
        Intent intent = getIntent();
        String boxScoreApi = intent.getStringExtra("box_score_api");

        new BoxScoreRequest().execute(boxScoreApi);

    }

    class BoxScoreRequest extends AsyncTask<String, String, Map<String, String>> {

        @Override
        protected void onPostExecute(Map<String, String> map) {
            super.onPostExecute(map);
            try {
                JSONObject event = new JSONObject(map.get("event"));
                String awayTeam = event.getJSONObject("event").getJSONObject("away_team").getString("abbreviation");
                String homeTeam = event.getJSONObject("event").getJSONObject("away_team").getString("abbreviation");
                String awayScore = event.getJSONObject("score").getJSONObject("home").getString("score");
                String homeScore = event.getJSONObject("score").getJSONObject("home").getString("score");
                getActionBar().setTitle(awayTeam + " " + awayScore + ", " + homeTeam + " " + homeScore);

                TableLayout tableLayout = (TableLayout) findViewById(R.id.line_scores);
                tableLayout.addView(createLineScoreRow(event, "away"));
                tableLayout.addView(createLineScoreRow(event, "home"));

                JSONArray players = new JSONArray(map.get("players"));
                Map<String, List<JSONObject>> playerMap = new HashMap<String, List<JSONObject>>();
                for (int i =0; i < players.length(); i++) {
                    JSONObject player = players.getJSONObject(i);
                    String alignment = player.getString("alignment");
                    if (!playerMap.containsKey(alignment)) {
                        playerMap.put(alignment, new ArrayList<JSONObject>());
                    }
                    playerMap.get(alignment).add(player);
                }

                TableLayout boxLayout = (TableLayout) findViewById(R.id.player_lines);
                addPlayerLines(playerMap, event, boxLayout, "away");
                addPlayerLines(playerMap, event, boxLayout, "home");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        private void addPlayerLines(Map<String, List<JSONObject>> playerMap, JSONObject event, TableLayout boxLayout, String alignment) throws JSONException {

            TableRow teamNameLine = new TableRow(BoxScoreActivity.this);
            teamNameLine.setLayoutParams(ViewHelper.tableRowLayoutParams);
            String teamName = event.getJSONObject("event").getJSONObject(alignment + "_team").getString("full_name");
            TextView textView = ViewHelper.addSmallTextColumn(BoxScoreActivity.this, teamNameLine, teamName, true);
            TableRow.LayoutParams params = (TableRow.LayoutParams) textView.getLayoutParams();
            params.span = 11;
            textView.setLayoutParams(params);
            boxLayout.addView(teamNameLine);

            TableRow headerLine = new TableRow(BoxScoreActivity.this);
            headerLine.setLayoutParams(ViewHelper.tableRowLayoutParams);
            ViewHelper.addSmallTextColumn(BoxScoreActivity.this, headerLine, "Name (MIN)", true);
            ViewHelper.addSmallTextColumn(BoxScoreActivity.this, headerLine, "PT", true);
            ViewHelper.addSmallTextColumn(BoxScoreActivity.this, headerLine, "FG", true);
            ViewHelper.addSmallTextColumn(BoxScoreActivity.this, headerLine, "3FG", true);
            ViewHelper.addSmallTextColumn(BoxScoreActivity.this, headerLine, "FT", true);
            ViewHelper.addSmallTextColumn(BoxScoreActivity.this, headerLine, "REB", true);
            ViewHelper.addSmallTextColumn(BoxScoreActivity.this, headerLine, "AS", true);
            ViewHelper.addSmallTextColumn(BoxScoreActivity.this, headerLine, "ST", true);
            ViewHelper.addSmallTextColumn(BoxScoreActivity.this, headerLine, "BL", true);
            ViewHelper.addSmallTextColumn(BoxScoreActivity.this, headerLine, "TO", true);
            ViewHelper.addSmallTextColumn(BoxScoreActivity.this, headerLine, "PF", true);
            boxLayout.addView(headerLine);
            for (JSONObject player : playerMap.get(alignment)) {
                if ("null".equals(player.getString("dnp_type")) && player.getInt("total_seconds") != 0) {
                    TableRow playerLine = new TableRow(BoxScoreActivity.this);
                    playerLine.setLayoutParams(ViewHelper.tableRowLayoutParams);
                    ViewHelper.addSmallTextColumn(BoxScoreActivity.this, playerLine, player.getJSONObject("player").getString("first_initial_and_last_name") + " (" + player.getString("minutes")  + ")", false);
                    ViewHelper.addSmallTextColumn(BoxScoreActivity.this, playerLine, player.getString("points"), false);
                    ViewHelper.addSmallTextColumn(BoxScoreActivity.this, playerLine, player.getString("field_goals_made") + "/" + player.getString("field_goals_attempted"), false);
                    ViewHelper.addSmallTextColumn(BoxScoreActivity.this, playerLine, player.getString("three_point_field_goals_made") + "/" + player.getString("three_point_field_goals_attempted"), false);
                    ViewHelper.addSmallTextColumn(BoxScoreActivity.this, playerLine, player.getString("free_throws_made") + "/" + player.getString("free_throws_attempted"), false);
                    ViewHelper.addSmallTextColumn(BoxScoreActivity.this, playerLine, player.getString("rebounds_total") + "/" + player.getString("rebounds_offensive"), false);
                    ViewHelper.addSmallTextColumn(BoxScoreActivity.this, playerLine, player.getString("assists"), false);
                    ViewHelper.addSmallTextColumn(BoxScoreActivity.this, playerLine, player.getString("steals"), false);
                    ViewHelper.addSmallTextColumn(BoxScoreActivity.this, playerLine, player.getString("blocked_shots"), false);
                    ViewHelper.addSmallTextColumn(BoxScoreActivity.this, playerLine, player.getString("turnovers"), false);
                    ViewHelper.addSmallTextColumn(BoxScoreActivity.this, playerLine, player.getString("personal_fouls"), false);
                    boxLayout.addView(playerLine);
                }
            }
            TableRow totals = new TableRow(BoxScoreActivity.this);
            totals.setLayoutParams(ViewHelper.tableRowLayoutParams);
            JSONObject teamRecord = event.getJSONObject("team_records").getJSONObject(alignment);
            ViewHelper.addSmallTextColumn(BoxScoreActivity.this, totals, "", false);
            ViewHelper.addSmallTextColumn(BoxScoreActivity.this, totals, teamRecord.getString("points"), true);
            ViewHelper.addSmallTextColumn(BoxScoreActivity.this, totals, teamRecord.getString("field_goals_made") + "/" + teamRecord.getString("field_goals_attempted") + "\n" + teamRecord.getString("field_goals_percentage"), true);
            ViewHelper.addSmallTextColumn(BoxScoreActivity.this, totals, teamRecord.getString("three_point_field_goals_made") + "/" + teamRecord.getString("three_point_field_goals_attempted") + "\n" + teamRecord.getString("three_point_field_goals_percentage"), true);
            ViewHelper.addSmallTextColumn(BoxScoreActivity.this, totals, teamRecord.getString("free_throws_made") + "/" + teamRecord.getString("free_throws_attempted") + "\n" + teamRecord.getString("free_throws_percentage"), true);
            ViewHelper.addSmallTextColumn(BoxScoreActivity.this, totals, teamRecord.getString("rebounds_total") + "/" + teamRecord.getString("rebounds_offensive"), true);
            ViewHelper.addSmallTextColumn(BoxScoreActivity.this, totals, teamRecord.getString("assists"), true);
            ViewHelper.addSmallTextColumn(BoxScoreActivity.this, totals, teamRecord.getString("steals"), true);
            ViewHelper.addSmallTextColumn(BoxScoreActivity.this, totals, teamRecord.getString("blocked_shots"), true);
            ViewHelper.addSmallTextColumn(BoxScoreActivity.this, totals, teamRecord.getString("turnovers"), true);
            ViewHelper.addSmallTextColumn(BoxScoreActivity.this, totals, teamRecord.getString("personal_fouls"), true);
            boxLayout.addView(totals);


        }
        private TableRow createLineScoreRow(JSONObject json, String alignment) throws JSONException {
            TableRow rowLineScore = new TableRow(BoxScoreActivity.this);
            rowLineScore.setLayoutParams(ViewHelper.tableRowLayoutParams);
            rowLineScore.addView(ViewHelper.textColumn(BoxScoreActivity.this,
                    json.getJSONObject("event").getJSONObject(alignment + "_team").getString("abbreviation")));
            JSONArray lineScores = json.getJSONObject("line_scores").getJSONArray(alignment);
            for (int i = 0; i < lineScores.length(); i++) {
                JSONObject lineScore = lineScores.getJSONObject(i);
                ViewHelper.addSmallTextColumn(BoxScoreActivity.this, rowLineScore, lineScore.getString("score"), false);
            }
            rowLineScore.addView(ViewHelper.textColumn(BoxScoreActivity.this,
                    json.getJSONObject("score").getJSONObject(alignment).getString("score")));
            return rowLineScore;
        }

        @Override
        protected Map<String, String> doInBackground(String... strings) {
            String boxScoreUrl = "http://api.thescore.com" + strings[0];
            String boxScorePlayerDataUrl = "http://api.thescore.com" + strings[0] + "/player_records";
            Map<String, String> map = new HashMap<String, String>();
            map.put("event", ViewHelper.httpGet(boxScoreUrl));
            map.put("players", ViewHelper.httpGet(boxScorePlayerDataUrl ));
            return map;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.box_score, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
