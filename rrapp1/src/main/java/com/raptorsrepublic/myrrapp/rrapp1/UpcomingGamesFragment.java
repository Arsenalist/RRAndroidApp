package com.raptorsrepublic.myrrapp.rrapp1;

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
public class UpcomingGamesFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.activity_upcoming_games_fragment, container, false);
        String url = "https://api.thescore.com/nba/teams/25/events/upcoming?rpp=5";
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
            ViewHelper.addTextColumn(getActivity(), header, getString(R.string.date), true);
            ViewHelper.addTextColumn(getActivity(), header, getString(R.string.time), true);
            tableLayout.addView(header);
            for (int i = 0; i < json.length(); i++) {
                try {
                    JSONObject row = json.getJSONObject(i);
                    TableRow tr = new TableRow(getActivity());
                    tr.setLayoutParams(ViewHelper.tableRowLayoutParams);
                    ViewHelper.addTextColumn(getActivity(), tr, ViewHelper.getLocation(row), false);
                    ViewHelper.addTextColumn(getActivity(), tr, ViewHelper.getOpponent(row), false);
                    ViewHelper.addTextColumn(getActivity(), tr, ViewHelper.formatDate(row.getString("game_date")), false);
                    ViewHelper.addTextColumn(getActivity(), tr, ViewHelper.formatTime(row.getString("game_date")), false);
                    tableLayout.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
