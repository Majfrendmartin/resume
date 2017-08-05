package com.wec.resume.injection.module;


import android.app.Application;
import android.content.SharedPreferences;

import com.wec.resume.injection.scope.PerApplication;
import com.wec.resume.model.repository.Repository;
import com.wec.resume.model.repository.RepositoryImpl;

import org.greenrobot.eventbus.EventBus;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetworkModule {

    private static final String BASE_URL = "https://majfrendmartin.github.io";

    @Provides
    @PerApplication
    Repository provideRepository(Application application, SharedPreferences preferences,
                                 Retrofit retrofit, EventBus eventBus) {
        return new RepositoryImpl(application, preferences, retrofit, eventBus);
    }

    @Provides
    @PerApplication
    Retrofit provideRetrofit() {
        return new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
    }

}
