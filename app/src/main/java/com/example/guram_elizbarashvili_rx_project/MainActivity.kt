package com.example.guram_elizbarashvili_rx_project

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Debug
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

data class UserResponse(
    val data: User,
    val support: Support
)

data class User(
    val id: Int,
    val email: String,
    val first_name: String,
    val last_name: String,
    val avatar: String
)

data class Support(
    val url: String,
    val text: String
)
interface MyApiService {
    @GET("api/users/{user_id}")
    fun fetchData(@Path("user_id") userId: Long): Call<UserResponse>
}

class MainActivity : AppCompatActivity() {
    private val disposables = CompositeDisposable()
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://reqres.in") // Replace with your API's base URL
        .addConverterFactory(GsonConverterFactory.create()) // Or your preferred converter
        .build()

    val apiService = retrofit.create(MyApiService::class.java)

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);
        val textView = findViewById<TextView>(R.id.centered_view);

        val pollingObservable = Observable.interval(5, TimeUnit.SECONDS)
            .switchMap { time ->
                fetchDataFromEndpoint(time + 1)
            }
            .observeOn(AndroidSchedulers.mainThread());

        disposables.add(pollingObservable.subscribe { data ->
            textView.text = "fetchedData: ${data.first.data};\n\n\n\n callCount: ${data.second}"
        })
    }
    override fun onDestroy() {
        super.onDestroy();
        disposables.clear();
    }
    fun fetchDataFromEndpoint(time: Long): Observable<Pair<UserResponse, Long>> {
        return Observable.fromCallable {
            val response = apiService.fetchData(time).execute() // Execute the Retrofit call
            if (response.isSuccessful) {
                Pair(response.body()!!, time);
            } else {
                throw Exception("API request failed with code ${response.code()}") // Handle errors
            }
        }.subscribeOn(Schedulers.io()) // Perform network call on a background thread
    }
}