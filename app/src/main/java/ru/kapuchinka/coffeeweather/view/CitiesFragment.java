package ru.kapuchinka.coffeeweather.view;

import android.app.Dialog;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.kapuchinka.coffeeweather.utils.db.DBHelper;
import ru.kapuchinka.coffeeweather.R;
import ru.kapuchinka.coffeeweather.adapter.CityAdapter;
import ru.kapuchinka.coffeeweather.databinding.FragmentCitiesBinding;
import ru.kapuchinka.coffeeweather.viewmodel.CitiesViewModel;

public class CitiesFragment extends Fragment {

    private FragmentCitiesBinding binding;

    private RecyclerView r_v_cities;
    private DBHelper DB;

    private Dialog dialog;
    private Button addButtonCity;

    CityAdapter adapter;
    ArrayList<String> cities;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CitiesViewModel citiesViewModel =
                new ViewModelProvider(this).get(CitiesViewModel.class);

        binding = FragmentCitiesBinding.inflate(inflater, container, false);
        addButtonCity = binding.buttonAddCity;
        DB = new DBHelper(getActivity());
        cities = new ArrayList<>();
        dialog = new Dialog(getContext());
        r_v_cities = binding.rVCities;

        getCitiesFromDataBases();

        adapter = new CityAdapter(getContext(), getActivity(), cities, DB);
        r_v_cities.setAdapter(adapter);
        r_v_cities.setLayoutManager(new LinearLayoutManager(getActivity()));

        addButtonCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddDialog();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getCitiesFromDataBases() {
        Cursor cursor = DB.getCities();

        if (cursor.getCount() == 0) {
            Toast.makeText(getActivity(), "No cities", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                cities.add(cursor.getString(1));
            }
        }
    }

    private void openAddDialog() {
        dialog.setContentView(R.layout.add_city);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
        ImageView closeButton = dialog.findViewById(R.id.imageClose);
        EditText newName = dialog.findViewById(R.id.enterCity);
        Button addCity = dialog.findViewById(R.id.buttonAddCityInDB);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        addCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newName.getText().toString().equals(""))
                    Toast.makeText(getActivity(), R.string.empty_field, Toast.LENGTH_SHORT).show();
                else {
                    String newCity = newName.getText().toString().toUpperCase().trim();
                    DB.insertCityData(newCity);
                    Toast.makeText(getActivity(), R.string.added_city, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }




}