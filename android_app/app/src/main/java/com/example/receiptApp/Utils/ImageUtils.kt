package com.example.receiptApp.Utils

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Size
import com.example.receiptApp.THUMBNAIL_SIZE
import timber.log.Timber
import java.io.*
import java.net.URI

class ImageUtils
{
    companion object {

        /**
         * Get thumbnail from the uri of an image
         *
         * @param contentUri
         * @param id -> if SDK < Q need to use old method
         * @return -> the bitmap
         */
        fun getThumbnail(contentResolver: ContentResolver, contentUri: Uri, id: Long): Bitmap?
        {
            return try
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                {
                    contentResolver.loadThumbnail(
                        contentUri, Size(THUMBNAIL_SIZE, THUMBNAIL_SIZE), null
                    )
                } else
                {
                    MediaStore.Images.Thumbnails.getThumbnail(
                        contentResolver,
                        id,
                        MediaStore.Images.Thumbnails.MINI_KIND,
                        BitmapFactory.Options()
                    )
                }
            } catch (e: FileNotFoundException)
            {
                Timber.e("$contentUri not found")
                null
            }
        }

    }
}