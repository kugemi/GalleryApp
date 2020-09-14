package com.example.galleryapp

import android.app.Application
import androidx.lifecycle.*
import com.example.galleryapp.api.FlickrFetchr
import com.example.galleryapp.model.GalleryItem
import com.example.galleryapp.model.QueryPreferences

class GalleryAppViewModel (private val app: Application):  AndroidViewModel(app){
    val galleryItemLiveData: LiveData<List<GalleryItem>>

    private val flickrFetchr = FlickrFetchr()
    private val mutableSearchTerm = MutableLiveData<String>()

    init {
        mutableSearchTerm.value = QueryPreferences.getStoredQuery(app)
        galleryItemLiveData =
            Transformations.switchMap(mutableSearchTerm) {searchTerm ->
                if (searchTerm.isBlank()) {
                    flickrFetchr.fetchPhotos()
                } else {
                    flickrFetchr.searchPhotos(searchTerm)
                }
            }
    }

    fun fetchPhotos(query: String = "") {
        QueryPreferences.setStorageQuery(app, query)
        mutableSearchTerm.value = query
    }
}