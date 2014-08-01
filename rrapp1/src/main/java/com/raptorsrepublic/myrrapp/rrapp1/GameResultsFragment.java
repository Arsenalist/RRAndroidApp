package com.raptorsrepublic.myrrapp.rrapp1;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Zarar_Siddiqi on 5/26/2014.
 */
public class GameResultsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.activity_upcoming_games_fragment, container, false);
        String url = "https://api.thescore.com/nba/teams/5/events/previous?rpp=10";
        new RequestTask(rootView).execute(url);
        return rootView;
    }

    class RequestTask extends AsyncTask<String, String, String>{

        private final ViewGroup context;

        public RequestTask(ViewGroup context) {
            this.context = context;        }

        @Override
        protected String doInBackground(String... uri) {
            return ViewHelper.httpGet(uri[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            TableLayout tableLayout = (TableLayout) context.findViewById(R.id.game_table);
            JSONArray json = null;
            try {
                json = new JSONArray(result);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            TableRow header = new TableRow(getActivity());
            header.setLayoutParams(ViewHelper.tableRowLayoutParams);
            ViewHelper.addTextColumn(getActivity(), header, "", true);
            ViewHelper.addTextColumn(getActivity(), header, getString(R.string.opponent), true);
            ViewHelper.addTextColumn(getActivity(), header, getString(R.string.score), true);
            ViewHelper.addTextColumn(getActivity(), header, getString(R.string.date), true);
            tableLayout.addView(header);
            for (int i = 0; i < json.length(); i++) {
                try {
                    JSONObject row = json.getJSONObject(i);
                    TableRow tr = new TableRow(getActivity());
                    tr.setLayoutParams(ViewHelper.tableRowLayoutParams);
                    tr.setTag(row.getJSONObject("box_score").getString("api_uri"));
                    tr.setClickable(true);
                    final String opponent = ViewHelper.getOpponent(row);
                    final String location = ViewHelper.getLocation(row);

                    tr.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
/*
                            Intent intent = new Intent(getActivity(), GenericWebViewActivity.class);
                            intent.putExtra("url", "http://www.thescore.com" + view.getTag() + "/box_score");
                            intent.putExtra("title", location + " " + opponent + " Box Score");
                            startActivity(intent);
*/
                            Intent intent = new Intent(getActivity(), BoxScoreActivity.class);
                            intent.putExtra("box_score_api", (String) view.getTag());
                            startActivity(intent);
                        }
                    });
                    ViewHelper.addTextColumn(getActivity(), tr, location, false);
                    ViewHelper.addTextColumn(getActivity(), tr, opponent, false);
                    ViewHelper.addTextColumn(getActivity(), tr, ViewHelper.getScore(row), false);
                    ViewHelper.addTextColumn(getActivity(), tr, ViewHelper.formatDate(row.getString("game_date")), false);
                    tableLayout.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
