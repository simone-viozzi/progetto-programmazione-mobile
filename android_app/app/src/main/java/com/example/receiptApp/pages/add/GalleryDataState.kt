package com.example.receiptApp.pages.add

import androidx.paging.PagingData
import com.example.receiptApp.sources.Attachment
import kotlinx.coroutines.flow.Flow

sealed class GalleryDataState
{
    object Idle : GalleryDataState()
    object Loading : GalleryDataState()
    data class Data(val tasks: PagingData<Attachment>) : GalleryDataState()
    data class Error(val error: Int) : GalleryDataState()
}