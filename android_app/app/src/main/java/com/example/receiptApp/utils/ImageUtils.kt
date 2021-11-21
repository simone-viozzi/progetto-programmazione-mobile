package com.example.receiptApp.utils

import android.R.attr
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
import java.io.File
import java.io.FileNotFoundException
import android.R.attr.path
import android.database.Cursor


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
                    when (uri.scheme)
                    {
                        "content" ->
                        {
                            contentResolver.loadThumbnail(
                                uri, Size(THUMBNAIL_SIZE, THUMBNAIL_SIZE), null
                            )
                        }
                        "file" ->
                        {
                            uri.path?.let {
                                val image = File(it)
                                val bounds = BitmapFactory.Options()
                                bounds.inJustDecodeBounds = true
                                BitmapFactory.decodeFile(image.path, bounds)
                                if (bounds.outWidth == -1 || bounds.outHeight == -1) return null
                                val originalSize = if (bounds.outHeight > bounds.outWidth) bounds.outHeight else bounds.outWidth
                                val opts = BitmapFactory.Options()
                                opts.inSampleSize = originalSize / THUMBNAIL_SIZE
                                return BitmapFactory.decodeFile(image.path, opts)
                            }
                        }
                        else -> throw IllegalArgumentException("uri.scheme -> ${uri.scheme} is not yet supported")
                    }
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
                Timber.e(e)
                Timber.e("$uri not found")
                null
            }
        }




    }
}