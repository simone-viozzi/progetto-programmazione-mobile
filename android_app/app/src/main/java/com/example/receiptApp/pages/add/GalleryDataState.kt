package com.example.receiptApp.pages.add

import androidx.paging.PagingData
import com.example.receiptApp.repository.AttachmentRepository

sealed class GalleryDataState
{
    object Idle : GalleryDataState()
    object Loading : GalleryDataState()
    data class Data(val tasks: PagingData<AttachmentRepository.Attachment>) : GalleryDataState()
    data class Error(val error: Int) : GalleryDataState()
}