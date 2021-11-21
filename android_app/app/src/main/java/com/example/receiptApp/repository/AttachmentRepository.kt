package com.example.receiptApp.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.provider.OpenableColumns
import com.example.receiptApp.R
import com.example.receiptApp.repository.sources.GalleryImages
import com.example.receiptApp.repository.sources.GalleryImagesPaginated
import com.example.receiptApp.utils.FileUtils
import com.example.receiptApp.utils.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File




class AttachmentRepository(private val applicationContext: Context)
{
    data class Attachment(
        var name: String? = null,
        var uri: Uri,
        var thumbnail: Bitmap? = null,
        var needToCopy: Boolean = false,
        val type: TYPE
    )

    // generate a thumbnail for a given attachment
    //  if the attachment is an image, it will load the thumbnail for that
    //  if it's a pdf just load the pdf icon
    fun generateThumbnail(attachment: Attachment): Bitmap?
    {
        return when(attachment.type)
        {
            TYPE.IMAGE -> ImageUtils.getThumbnail(
                    applicationContext.contentResolver,
                    attachment.uri
                    ).let {
                it?.let { bitmap ->
                    // i don't want portrait images, if an images is in portrait format just rotate it
                    if (bitmap.height  < bitmap.width)
                    {
                        val matrix = Matrix();
                        matrix.postRotate(90F);

                        Bitmap.createBitmap(it, 0, 0, it.width, it.height, matrix, true)
                    }
                    else
                    {
                        it
                    }
                }
            }
            TYPE.PDF -> BitmapFactory.decodeResource(applicationContext.resources, R.drawable.pdf)
        }
    }


    fun generateThumbnailFromUri(uri: Uri?): Bitmap?
    {
        return uri?.let {
            val uriPath = uri.path?.split("/")
            
            val attachment = Attachment(
                uri = uri,
                type = when(uriPath?.get(uriPath.size-2)){
                    "app_images" -> TYPE.IMAGE
                    "app_files" -> TYPE.PDF
                    else -> throw IllegalStateException("unexpected uri type!")
                }
            )
            generateThumbnail(attachment)
        }
    }


    enum class TYPE()
    {
        IMAGE,
        PDF;
    }

    private val galleryImages: GalleryImages = GalleryImages(applicationContext.contentResolver)
    val galleryImagesPaginated = GalleryImagesPaginated(galleryImages)


    suspend fun copyAttachment(attachment: Attachment): Uri? = withContext(Dispatchers.IO) {

        val filesPath = when (attachment.type)
        {
            TYPE.IMAGE ->  "images"
            TYPE.PDF -> "files"
        }

        attachment.name = attachment.name
            ?: FileUtils.getUniqueFilename(getFileName(attachment.uri) ?: attachment.type.name)


        val newFile = File(
            applicationContext.getDir(filesPath, Context.MODE_PRIVATE),
            "/${attachment.name}"
        )

        applicationContext.contentResolver.openInputStream(attachment.uri)?.use { stream ->
            Timber.d("newFile.absolutePath -> ${newFile.absolutePath}")

            return@withContext FileUtils.saveFile(stream, newFile, applicationContext)
        }
    }

    // if i have a content uri i cannot know directly what is the name of the file, so i need to query the system
    //  for that
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
            )?.use { cursor ->

                if (cursor.moveToFirst())
                {
                    result = cursor.getString(
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME).also { if (it <= 0) return null })
                }
            }
        }
        return result
    }

    fun getDefaultBitmap(): Bitmap {
        return BitmapFactory.decodeResource(applicationContext.resources, R.drawable.bill_ico)
    }
}