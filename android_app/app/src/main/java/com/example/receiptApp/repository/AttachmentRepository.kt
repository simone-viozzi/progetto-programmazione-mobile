package com.example.receiptApp.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.receiptApp.Utils.FileUtils
import com.example.receiptApp.repository.sources.GalleryImages
import com.example.receiptApp.repository.sources.GalleryImagesPaginated
import timber.log.Timber
import java.io.File


data class Attachment(val name: String, val contentUri: Uri, val thumbnail: Bitmap)

class AttachmentRepository(private val applicationContext: Context)
{

    enum class TYPE(val type: String)
    {
        IMAGE(""),
        PDF("pdf");
    }


    private val galleryImages: GalleryImages = GalleryImages(applicationContext.contentResolver)
    val galleryImagesPaginated = GalleryImagesPaginated(galleryImages)

    suspend fun copyAttachment(uri: Uri, type: TYPE)
    {
        val filesPath = File(applicationContext.filesDir, "files/")


        //if (!filesPath.mkdirs()) throw FileNotFoundException()

        val newFile = File(filesPath, "${FileUtils.getUniqueFilename()}.${type.type}")


        val contentUri: Uri = FileProvider.getUriForFile(
            applicationContext,
            "com.example.receiptApp",
            newFile
        )


        val resolver = applicationContext.contentResolver

        resolver.openInputStream(uri).use { stream ->
            if (stream != null)
            {
                Timber.d("newFile.absolutePath -> ${newFile.absolutePath}")
                FileUtils.saveFile(stream, newFile.absolutePath)
            }
        }
    }


}