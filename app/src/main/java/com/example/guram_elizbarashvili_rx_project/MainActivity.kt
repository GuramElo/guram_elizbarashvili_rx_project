package com.example.guram_elizbarashvili_rx_project

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);
        var textView = findViewById<TextView>(R.id.centered_view);
        val myObservable = Observable.interval(1, TimeUnit.SECONDS) // Emits a number every second

// Use RxBinding to bind the Observable to the TextView's text property
        myObservable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { number ->
               textView.text = "Count: $number"
            }
    }
}