package ru.kapuchinka.coffeeweather.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NotificationsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public NotificationsViewModel() {
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