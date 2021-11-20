package com.example.receiptApp.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import com.example.receiptApp.THUMBNAIL_SIZE
import timber.log.Timber
import java.io.FileNotFoundException
import java.lang.IllegalArgumentException

class ImageUtils
{
    companion object {
        /**
         * Get thumbnail from the uri of an image
         *
         * @param contentResolver
         * @param contentUri
         * @param id -> if SDK < Q need to use old method
         * @return -> the bitmap or null if not found
         */
        fun getThumbnail(contentResolver: ContentResolver, contentUri: Uri, id: Long? = null): Bitmap?
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
                    if (contentUri.scheme == "content")
                    {
                        Timber.d("$contentUri")
                        Timber.d("contentUri.lastPathSegment -> ${ContentUris.parseId(contentUri)}")
                        MediaStore.Images.Thumbnails.getThumbnail(
                            contentResolver,
                            id ?: ContentUris.parseId(contentUri),
                            MediaStore.Images.Thumbnails.MINI_KIND,
                            BitmapFactory.Options()
                        )
                    }
                    else
                    {
                        null
                    }
                }

            } catch (e: FileNotFoundException)
            {
                Timber.e("$contentUri not found")
                null
            }
        }

    }
}