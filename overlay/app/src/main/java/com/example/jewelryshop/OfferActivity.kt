package com.example.jewelryshop

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.example.jewelryshop.data.HttpOfferApi
import com.example.jewelryshop.data.OfferRepository
import com.example.jewelryshop.data.OfferUseCase
import com.example.jewelryshop.data.ShopDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CoroutineStart
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class OfferActivity : Activity() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = intent.getIntExtra("product_index", 0).coerceIn(0, 4) + 1
        val panel = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(32, 32, 32, 32) }
        val status = TextView(this).apply { text = "Учебное API: не реальный склад JewelryShop. Результаты кэшируются в Room."; textSize = 18f }
        val load = Button(this).apply { text = "Обновить демо-наличие" }
        panel.addView(status); panel.addView(load); setContentView(panel)
        load.setOnClickListener {
            load.isEnabled = false
            OperationTracker.begin()
            scope.launch(start = CoroutineStart.UNDISPATCHED) {
                try {
                    val app = applicationContext
                    val repository = withContext(Dispatchers.IO) {
                        val api = HttpOfferApi(OkHttpClient.Builder().callTimeout(8, TimeUnit.SECONDS).build(), "https://dummyjson.com/".toHttpUrl())
                        OfferRepository(api, ShopDatabase.get(app).offers())
                    }
                    OfferUseCase(repository).observe(id).collect { result ->
                        val offer = result.offer
                        status.text = if (offer == null) "Нет сети и кэша" else "Демо-остаток: ${offer.stock}; источник: ${if (result.cached) "Room" else "HTTP"}"
                        ShopLog.i("JS_NET", if (result.cached) "Cache/fallback" else "HTTP offer loaded")
                    }
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (error: Exception) {
                    status.text = "Ошибка загрузки данных"
                    ShopLog.e("JS_NET", "Offer load failed", error)
                } finally {
                    load.isEnabled = true
                    OperationTracker.end()
                }
            }
        }
    }
    override fun onDestroy() { scope.cancel(); super.onDestroy() }
}
