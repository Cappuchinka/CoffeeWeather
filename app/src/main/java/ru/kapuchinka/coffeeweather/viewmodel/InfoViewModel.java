package ru.kapuchinka.coffeeweather.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InfoViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public InfoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("CoffeeWeather\n" +
                "API: Open Weather Map\n" +
                "База Данных: Sqlite\n" +
                "\n" +
                "Автор: Kapućinka");
    }

    public LiveData<String> getText() {
        return mText;
    }
}