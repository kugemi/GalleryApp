package com.example.galleryapp.api

import com.example.galleryapp.model.GalleryItem
import com.google.gson.annotations.SerializedName

class PhotoResponse{
    @SerializedName("photo")
    lateinit var galleryItems: List<GalleryItem>
}