package com.example.receiptApp.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.viewModelScope
import com.example.receiptApp.repository.sources.GalleryImages
import com.example.receiptApp.repository.sources.GalleryImagesPaginated
import com.example.receiptApp.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File




class AttachmentRepository(private val applicationContext: Context)
{
    data class Attachment(
        val name: String,
        var uri: Uri,
        var thumbnail: Bitmap?,
        var needToCopy: Boolean = false,
        val type: TYPE
    )

    enum class TYPE(val type: String)
    {
        IMAGE(""),
        PDF("pdf");
    }

    private val galleryImages: GalleryImages = GalleryImages(applicationContext.contentResolver)
    val galleryImagesPaginated = GalleryImagesPaginated(galleryImages)

    suspend fun copyAttachment(attachment: Attachment): Uri? = withContext(Dispatchers.IO) {

        val filesPath = when (attachment.type)
        {
            TYPE.IMAGE -> File(applicationContext.filesDir, "images/")
            TYPE.PDF -> File(applicationContext.filesDir, "files/")
        }

        val newFile = File(filesPath, FileUtils.getUniqueFilename(attachment.name))

        applicationContext.contentResolver.openInputStream(attachment.uri)?.use { stream ->
            Timber.d("newFile.absolutePath -> ${newFile.absolutePath}")

            return@use FileUtils.saveFile(stream, newFile)
        }

        return@withContext null
    }

    fun getFileName(uri: Uri): String?
    {
        var result: String? = null
        if (uri.scheme == "content")
        {
            applicationContext.contentResolver.query(
                uri,
                null,
                null,
                null,
                null
            )
                ?.use { cursor ->

                    if (cursor.moveToFirst())
                    {
                        result = cursor.getString(
                            cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME).also { if (it <= 0) return null })
                    }
                }
        }
        return result ?: uri.lastPathSegment
    }

}