package ru.kapuchinka.coffeeweather.ui.home;

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

import ru.kapuchinka.coffeeweather.R;
import ru.kapuchinka.coffeeweather.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ImageButton searchButton;
    private EditText fieldSearchCity;
    private TextView searchResult;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        searchButton = binding.searchButton;
        fieldSearchCity = binding.fieldSearchCity;
        searchResult = binding.searchResult;

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fieldSearchCity.getText().toString().trim().equals(""))
                    Toast.makeText(getActivity(), R.string.empty_search, Toast.LENGTH_SHORT).show();
                else {
                    String city = fieldSearchCity.getText().toString();
                    String key = "c50ba949b50e3b521271fb2b6a25f0e5";
                    String url = String.format("https://api.openweathermap.org/data/2.5/weather?q=%s&APPID=%s&units=metric&lang=ru", city, key);

                    new GetData().execute(url);
                }
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private class GetData extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            searchResult.setText("Ожидание...");
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

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);
                Double temp = jsonObject.getJSONObject("main").getDouble("temp");
                searchResult.setText(temp.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}