package com.example.galleryapp

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.example.galleryapp.api.FlickrFetchr
import java.util.concurrent.ConcurrentHashMap

private const val TAG = "ThumbnailDownloader"
private const val MESSAGE_DOWNLOADED = 0

class ThumbnailDownloader <in T>(
    private val responseHandler: Handler,
    private val onThumbnailDownloaded: (T, Bitmap) -> Unit
) :HandlerThread(TAG){

    val fragmentLifecycleObserver: LifecycleObserver =
        object : LifecycleObserver{
            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            fun setup(){
                Log.i(TAG, "Starting backgroung thread")
                start()
                looper
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun tearDown() {
                Log.i(TAG, "Destroying background thread")
                quit()
            }
        }

    val viewLifecycleObserver : LifecycleObserver =
        object : LifecycleObserver{
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun clearQueue(){
                Log.i(TAG, "Clearing all requests from queue")
                requestHandler.removeMessages(MESSAGE_DOWNLOADED)
                requestMap.clear()
            }
        }

    private var hasQuit = false
    private lateinit var requestHandler: Handler
    private val requestMap = ConcurrentHashMap<T, String>()
    private val flickrFetchr = FlickrFetchr()

    @Suppress("UNCHECKED_CAST") //чтобы кастить obj к T без проверок
    @SuppressLint("HandlerLeak") //могла бы быть утечк внешнего класса, но обработчик прикреплен к циклу фонового потока
    override fun onLooperPrepared() {
        requestHandler = object : Handler() {
            override fun handleMessage(msg: Message) {  //вызывается,когда сообщение о загрузке извлекается из очереди и готово к обработке
                if (msg.what == MESSAGE_DOWNLOADED) { //проверка типа Message
                    val target = msg.obj as T //извлекаем obj типа T (индетификатор запроса)
                    Log.i(TAG, "Got a request for URL: ${requestMap[target]}")
                    handleRequest(target)
                }
            }
        }
    }

    override fun quit(): Boolean {
        hasQuit = true
        return super.quit()
    }

    fun queueThumbnail(target: T, url: String) {
        Log.i(TAG, "Gor a URL: $url")
        requestMap[target] = url
        requestHandler.obtainMessage(MESSAGE_DOWNLOADED, target)
            .sendToTarget()
    }

    private fun handleRequest(target: T) { //вспомогательная функция для загрузки
        val url = requestMap[target] ?: return
        val bitmap = flickrFetchr.fetchPhoto(url) ?: return

        responseHandler.post(Runnable { //код внутри Runnable будет выполняться в основном потоке
            if (requestMap[target] != url || hasQuit){ //гарантия, что каждый photoHolder получит правильное изображение ||
                return@Runnable
            }
            requestMap.remove(target)
            onThumbnailDownloaded(target, bitmap)
        })
    }
}