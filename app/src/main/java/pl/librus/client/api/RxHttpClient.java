package pl.librus.client.api;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pl.librus.client.LibrusUtils;

public class RxHttpClient {

    private final ConnectivityManager connectivityManager;

    public RxHttpClient(ConnectivityManager connectivityManager) {
        this.connectivityManager = connectivityManager;
    }

    public Single<String> executeCall(Request request) {
        return Single.<String>create(observer -> {
            try {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if(networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
                    observer.onError(new OfflineException(request.url().toString()));
                    return;
                }
                LibrusUtils.log("start fetching data from " + request.url());
                Response response = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .build()
                        .newCall(request).execute();
                String message = response.body().string();
                if(response.isSuccessful()) {
                    LibrusUtils.log("data fetched succesfully from " + request.url());
                    observer.onSuccess(message);
                } else {
                    observer.onError(new HttpException(response.code(), message, request.url().toString()));
                }
            } catch (Exception e) {
                observer.onError(new HttpException(e, request.url().toString()));
            }
        }).subscribeOn(Schedulers.io());
    }
}
