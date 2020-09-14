package com.example.galleryapp.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface FlickrApi{
    @GET("rest/?method=flickr.interestingness.getList")
    fun fetchPhotos(): Call<FlickrResponse> //в объекте Call указываем, что хотим десериализовать ответ в объект FlickrResponse

    @GET
    fun fetchUrlBytes(@Url url: String): Call<ResponseBody> //@Url переопределяет базоый url Retrofit

    @GET("rest?method=flickr.photos.search")
    fun searchPhotos(@Query("text") query: String): Call<FlickrResponse> //Query позволяет динамически добавлять параметр запроса к url
}