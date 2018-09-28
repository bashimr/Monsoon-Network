package com.apptellect.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.apptellect.R;
import com.apptellect.utilities.WeatherReport_Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WeatherForecastFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WeatherForecastFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeatherForecastFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1 = "";
    private String mParam2 = "";


    View view;
    TextView selectCity, cityField, detailsField, currentTemperatureField, weatherIcon, updatedField;
    ProgressBar loader;
    Typeface weatherFont;
    // OpenWeatherMap API KEY here
    String OPEN_WEATHER_MAP_API = "c027e6c5878c58dcc9fcacccb117b58a";

    TextView date_lastone, weather_icon_lastone, current_temperature_lastone;
    TextView date_lasttwo, weather_icon_lasttwo, current_temperature_lasttwo;
    TextView date_lastthree, weather_icon_lastthree, current_temperature_lastthree;
    TextView date_lastfour, weather_icon_lastfour, current_temperature_lastfour;
    TextView date_lastfive, weather_icon_lastfive, current_temperature_lastfive;
    TextView date_lastsix, weather_icon_lastsix, current_temperature_lastsix;


    public WeatherForecastFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeatherForecastFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeatherForecastFragment newInstance(String param1, String param2) {
        WeatherForecastFragment fragment = new WeatherForecastFragment();
        Bundle args = new Bundle();
        if (param1 != null)
            args.putString(ARG_PARAM1, param1);
        if (param2 != null)
            args.putString(ARG_PARAM2, param2);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().getString(ARG_PARAM1) != null || getArguments().getString(ARG_PARAM2) != null) {
                mParam1 = getArguments().getString(ARG_PARAM1);
                mParam2 = getArguments().getString(ARG_PARAM2);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_weather_forecast, container, false);


        cityField = (TextView) view.findViewById(R.id.city_field);
        updatedField = (TextView) view.findViewById(R.id.updated_field);
        detailsField = (TextView) view.findViewById(R.id.details_field);
        currentTemperatureField = (TextView) view.findViewById(R.id.current_temperature_field);
        weatherIcon = (TextView) view.findViewById(R.id.weather_icon);
        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weathericons-regular-webfont.ttf");
        weatherIcon.setTypeface(weatherFont);

        onloadViews();

        if (!mParam1.equals("") || !mParam2.equals(""))
            taskLoadUp(mParam1, mParam2);

        return view;

    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


    public void taskLoadUp(String lat, String longi) {
        if (WeatherReport_Utils.isNetworkAvailable(getActivity())) {
            DownloadWeather task = new DownloadWeather();
            String latlong = "lat=" + lat + "&lon=" + longi;
            task.execute(latlong);
        } else {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }


    class DownloadWeather extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected String doInBackground(String... args) {
            String xml = WeatherReport_Utils.excuteGet("http://api.openweathermap.org/data/2.5/weather?" + args[0] +
                    "&units=metric&appid=" + OPEN_WEATHER_MAP_API);
            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {

            System.out.println("MyJSON " + xml);
            try {
                JSONObject json = new JSONObject(xml);
                if (json != null) {
                    JSONObject details = json.getJSONArray("weather").getJSONObject(0);
                    JSONObject main = json.getJSONObject("main");
                    DateFormat df = DateFormat.getDateTimeInstance();

                    cityField.setText(json.getString("name").toUpperCase(Locale.US) + ", " + json.getJSONObject("sys").getString("country"));
                    detailsField.setText(details.getString("description").toUpperCase(Locale.US));
                    currentTemperatureField.setText(String.format("%.2f", main.getDouble("temp")) + "Â° C");
                    updatedField.setText(df.format(new Date(json.getLong("dt") * 1000)));
                    weatherIcon.setText(Html.fromHtml(WeatherReport_Utils.setWeatherIcon(details.getInt("id"),
                            json.getJSONObject("sys").getLong("sunrise") * 1000,
                            json.getJSONObject("sys").getLong("sunset") * 1000)));


                }
            } catch (JSONException e) {
                Toast.makeText(getActivity(), "Error, Check City", Toast.LENGTH_SHORT).show();
            }

        }

    }

    public void onloadViews() {

        date_lastone = (TextView) view.findViewById(R.id.date_lastone);
        weather_icon_lastone = (TextView) view.findViewById(R.id.weather_icon_lastone);
        current_temperature_lastone = (TextView) view.findViewById(R.id.current_temperature_lastone);

        date_lasttwo = (TextView) view.findViewById(R.id.date_lasttwo);
        weather_icon_lasttwo = (TextView) view.findViewById(R.id.weather_icon_lasttwo);
        current_temperature_lasttwo = (TextView) view.findViewById(R.id.current_temperature_lasttwo);

        date_lastthree = (TextView) view.findViewById(R.id.date_lastthree);
        weather_icon_lastthree = (TextView) view.findViewById(R.id.weather_icon_lastthree);
        current_temperature_lastthree = (TextView) view.findViewById(R.id.current_temperature_lastthree);

        date_lastfour = (TextView) view.findViewById(R.id.date_lastfour);
        weather_icon_lastfour = (TextView) view.findViewById(R.id.weather_icon_lastfour);
        current_temperature_lastfour = (TextView) view.findViewById(R.id.current_temperature_lastfour);

        date_lastfive = (TextView) view.findViewById(R.id.date_lastfive);
        weather_icon_lastfive = (TextView) view.findViewById(R.id.weather_icon_lastfive);
        current_temperature_lastfive = (TextView) view.findViewById(R.id.current_temperature_lastfive);

        date_lastsix = (TextView) view.findViewById(R.id.date_lastsix);
        weather_icon_lastsix = (TextView) view.findViewById(R.id.weather_icon_lastsix);
        current_temperature_lastsix = (TextView) view.findViewById(R.id.current_temperature_lastsix);

        date_lastone.setText(getYesterdayDateString(-1));
        weather_icon_lastone.setTypeface(weatherFont);
        weather_icon_lastone.setText(Html.fromHtml("&#xf01e;"));
        current_temperature_lastone.setText("28Â° C");

        date_lasttwo.setText(getYesterdayDateString(-2));
        weather_icon_lasttwo.setTypeface(weatherFont);
        weather_icon_lasttwo.setText(Html.fromHtml("&#xf01e;"));
        current_temperature_lasttwo.setText("26Â° C");

        date_lastthree.setText(getYesterdayDateString(-3));
        weather_icon_lastthree.setTypeface(weatherFont);
        weather_icon_lastthree.setText(Html.fromHtml("&#xf01c;"));
        current_temperature_lastthree.setText("28Â° C");

        date_lastfour.setText(getYesterdayDateString(-4));
        weather_icon_lastfour.setTypeface(weatherFont);
        weather_icon_lastfour.setText(Html.fromHtml("&#xf014;"));
        current_temperature_lastfour.setText("31Â° C");

        date_lastfive.setText(getYesterdayDateString(-5));
        weather_icon_lastfive.setTypeface(weatherFont);
        weather_icon_lastfive.setText(Html.fromHtml("&#xf013;"));
        current_temperature_lastfive.setText("29Â° C");

        date_lastsix.setText(getYesterdayDateString(-6));
        weather_icon_lastsix.setTypeface(weatherFont);
        weather_icon_lastsix.setText(Html.fromHtml("&#xf01b;"));
        current_temperature_lastsix.setText("32Â° C");


    }

    private String getYesterdayDateString(int value) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MMM/yy");
        return dateFormat.format(yesterday(value));
    }

    private Date yesterday(int value) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, value);
        return cal.getTime();
    }
}