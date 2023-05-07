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

import ru.kapuchinka.coffeeweather.MainActivity;
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
            return null;
        }
    }
}