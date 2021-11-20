package com.example.receiptApp.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import com.example.receiptApp.THUMBNAIL_SIZE
import timber.log.Timber
import java.io.FileNotFoundException

class ImageUtils
{
    companion object {
        /**
         * Get thumbnail from the uri of an image
         *
         * @param contentResolver
         * @param uri
         * @param id -> if SDK < Q need to use old method
         * @return -> the bitmap or null if not found
         */
        fun getThumbnail(contentResolver: ContentResolver, uri: Uri, id: Long? = null): Bitmap?
        {
            return try
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                {
                    contentResolver.loadThumbnail(
                        uri, Size(THUMBNAIL_SIZE, THUMBNAIL_SIZE), null
                    )
                } else
                {
                    when (uri.scheme)
                    {
                        "content" ->
                        {
                            MediaStore.Images.Thumbnails.getThumbnail(
                                contentResolver,
                                id ?: ContentUris.parseId(uri),
                                MediaStore.Images.Thumbnails.MINI_KIND,
                                BitmapFactory.Options()
                            )
                        }
                        "file" ->
                        {
                            MediaStore.Images.Media.getBitmap(contentResolver, uri)
                        }
                        else -> throw IllegalArgumentException("uri.scheme -> ${uri.scheme} is not yet supported")
                    }
                }
            } catch (e: FileNotFoundException)
            {
                Timber.e("$uri not found")
                null
            }
        }

    }
}