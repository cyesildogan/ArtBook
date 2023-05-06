package com.example.udemyartbooktesting.repo

import androidx.lifecycle.LiveData
import com.example.udemyartbooktesting.model.ImageResponse
import com.example.udemyartbooktesting.roomdb.Art
import com.example.udemyartbooktesting.util.Resource

interface ArtRepositoryInterface {

    suspend fun insertArt(art: Art)

    suspend fun deleteArt(art : Art)

    fun getArt() : LiveData<List<Art>>

    suspend fun searchImage(imageString : String) : Resource<ImageResponse>

}