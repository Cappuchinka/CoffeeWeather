package ru.kapuchinka.coffeeweather.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ru.kapuchinka.coffeeweather.utils.db.DBHelper;
import ru.kapuchinka.coffeeweather.R;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {
    private Context context;
    private Activity activity;
    private ArrayList<String> cities;

    private ImageView closeWeather;
    private TextView infoWeather;

    private Dialog dialog;


    private DBHelper DB;

    public CityAdapter(Context context, Activity activity, ArrayList<String> cities, DBHelper DB) {
        this.context = context;
        this.activity = activity;
        this.cities = cities;
        this.DB = DB;
        this.dialog = new Dialog(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_city, parent,false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int pos = position;
        holder.name.setText(String.valueOf(cities.get(position)));

        holder.deleteCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = String.valueOf(cities.get(pos));
                DB.deleteCity(name);
                cities.remove(pos);
            }
        });

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = "c50ba949b50e3b521271fb2b6a25f0e5";
                String city = String.valueOf(cities.get(pos));
                String url = String.format("https://api.openweathermap.org/data/2.5/weather?q=%s&APPID=%s&units=metric&lang=ru", city, key);

                openWeatherDialog(url);

//                Toast.makeText(activity, cities.get(pos), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private LinearLayout item;
        private ImageButton deleteCity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.cityName);
            item = (LinearLayout) itemView.findViewById(R.id.cityItem);
            deleteCity = (ImageButton) itemView.findViewById(R.id.deleteCity);
        }
    }

    public void openWeatherDialog(String url) {
        dialog.setContentView(R.layout.weather_city);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
        closeWeather = dialog.findViewById(R.id.closeWeather);
        infoWeather = dialog.findViewById(R.id.textViewWeather);

        dialog.show();

        new GetData().execute(url);


        closeWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private class GetData extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            infoWeather.setText("Ожидание...");
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null)
                    buffer.append(line).append("\n");

                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null)
                    connection.disconnect();

                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);
                double temp = jsonObject.getJSONObject("main").getDouble("temp");
                double tempFeelsLike = jsonObject.getJSONObject("main").getDouble("feels_like");
                double tempMin = jsonObject.getJSONObject("main").getDouble("temp_min");
                double tempMax = jsonObject.getJSONObject("main").getDouble("temp_max");
                int humidity = jsonObject.getJSONObject("main").getInt("humidity");
                long sunrise = jsonObject.getJSONObject("sys").getLong("sunrise");
                Date sunriseDate = new Date(sunrise * 1000);
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                String formattedSunrise = sdf.format(sunriseDate);
                long sunset = jsonObject.getJSONObject("sys").getLong("sunset");
                Date sunsetDate = new Date(sunset * 1000);
                String formattedSunset = sdf.format(sunsetDate);
                double pressure = jsonObject.getJSONObject("main").getInt("pressure") * 0.750062;
                String weather = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                String weatherDescription = weather.substring(0, 1).toUpperCase() + weather.substring(1).toLowerCase();
                String city = jsonObject.getString("name");

                @SuppressLint("DefaultLocale")
                String resultWeather = String.format("%s\n" +
                        "%s %.2f°C\n" +
                        "Ощущается как %.2f°C\n" +
                        "Минимальная температура %.2f°C\n" +
                        "Максимальная температура %.2f°C\n" +
                        "Влажность: %d%%\n" +
                        "Давление: %.2f мм.рт.ст\n" +
                        "Восход: %s\n" +
                        "Закат: %s\n", city, weatherDescription, temp, tempFeelsLike, tempMin, tempMax, humidity, pressure, formattedSunrise, formattedSunset);

                infoWeather.setText(resultWeather);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
