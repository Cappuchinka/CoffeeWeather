package ru.kapuchinka.coffeeweather.ui.dashboard;

import android.app.Dialog;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.kapuchinka.coffeeweather.DBHelper;
import ru.kapuchinka.coffeeweather.R;
import ru.kapuchinka.coffeeweather.adapter.CityAdapter;
import ru.kapuchinka.coffeeweather.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    private RecyclerView r_v_cities;
    private DBHelper DB;

    private Dialog dialog;
    private Button addButtonCity;

    CityAdapter adapter;
    ArrayList<String> cities;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        addButtonCity = binding.buttonAddCity;
        DB = new DBHelper(getActivity());
        cities = new ArrayList<>();
        dialog = new Dialog(getContext());

        getCitiesFromDataBases();

        addButtonCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddDialog();
            }
        });

//        adapter = new CityAdapter(getContext(), cities);
//        r_v_cities.setAdapter(adapter);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

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
                    Toast.makeText(getActivity(), "Поле пустое!", Toast.LENGTH_SHORT).show();
                else {
                    String newCity = newName.getText().toString().toUpperCase().trim();
                }
            }
        });
        dialog.show();
    }
}