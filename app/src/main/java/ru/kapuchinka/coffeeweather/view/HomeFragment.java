package ru.kapuchinka.coffeeweather.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.kapuchinka.coffeeweather.R;
import ru.kapuchinka.coffeeweather.databinding.FragmentHomeBinding;
import ru.kapuchinka.coffeeweather.viewmodel.HomeViewModel;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ImageButton searchButton;
    private EditText fieldSearchCity;
    private TextView searchResult;

    private String mCity;
    private String key = "c50ba949b50e3b521271fb2b6a25f0e5";
    private String url;

    private LocationManager locationManager;
    private LocationListener locationListener;

    HomeViewModel homeViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        searchButton = binding.searchButton;
        fieldSearchCity = binding.fieldSearchCity;
        searchResult = binding.searchResult;

        searchButton.setOnClickListener(v -> {
            if (fieldSearchCity.getText().toString().trim().equals("")) {
                Toast.makeText(getActivity(), R.string.empty_field, Toast.LENGTH_SHORT).show();
            } else {
                String city = fieldSearchCity.getText().toString().trim();
                key = "c50ba949b50e3b521271fb2b6a25f0e5";
                url = String.format("https://api.openweathermap.org/data/2.5/weather?q=%s&APPID=%s&units=metric&lang=ru", city, key);

                new GetData().execute(url);
            }
        });

        // Инициализация службы местоположения
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Получение города на основе координат
                String city = getCityFromLocation(location.getLatitude(), location.getLongitude());
                if (city != null) {
                    fieldSearchCity.setText(city);
                    mCity = city;
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };

        // Запрос разрешения на доступ к местоположению
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            startLocationUpdates();
        }

        if (mCity != null) {
            url = String.format("https://api.openweathermap.org/data/2.5/weather?q=%s&APPID=%s&units=metric&lang=ru", mCity, key);
            new GetData().execute(url);
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
    }

    private String getCityFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                mCity = address.getLocality();
                return address.getLocality();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("StaticFieldLeak")
    private class GetData extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            homeViewModel.setText("Ожидание...");
            homeViewModel.getText().observe(getViewLifecycleOwner(), searchResult::setText);
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
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }

                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
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

                homeViewModel.setText(resultWeather);
                homeViewModel.getText().observe(getViewLifecycleOwner(), searchResult::setText);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

