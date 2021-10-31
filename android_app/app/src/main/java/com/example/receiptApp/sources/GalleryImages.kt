package com.example.receiptApp.sources

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.database.DatabaseUtils
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Size
import com.example.receiptApp.THUMBNAIL_SIZE
import com.example.receiptApp.Utils.ImageUtils
import timber.log.Timber
import java.io.FileNotFoundException


data class Attachment(val name: String, val contentUri: Uri, val thumbnail: Bitmap)

class GalleryImages(private val contentResolver: ContentResolver)
{

    fun getImages(limit: Int, offset: Int): List<Attachment>?
    {
        val projection: Array<String> = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.DATE_MODIFIED
        )


        val images = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else
        {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        Timber.d("images = $images")

        val selection = "${MediaStore.Images.Media.MIME_TYPE} IN (?,?,?)"

        val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC LIMIT $limit OFFSET $offset"

        val selectionArgs = arrayOf(
            "image/jpeg",
            "image/jpg",
            "image/png"
        )

        val list: MutableList<Attachment> = mutableListOf()

        val curr: Cursor? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            val bundle = Bundle().apply {
                putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection)
                putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, selectionArgs)
                putString(ContentResolver.QUERY_ARG_SORT_COLUMNS, MediaStore.Images.Media.DATE_MODIFIED)
                putInt(ContentResolver.QUERY_ARG_SORT_DIRECTION, ContentResolver.QUERY_SORT_DIRECTION_DESCENDING)
                putInt(ContentResolver.QUERY_ARG_LIMIT, limit)
                putInt(ContentResolver.QUERY_ARG_OFFSET, offset)
            }

            contentResolver.query(
                images,
                projection,
                bundle,
                null
            )
        } else
        {
            contentResolver.query(
                images,
                projection,  // Which columns to return
                selection,  // Which rows to return (all rows)
                selectionArgs,  // Selection arguments (none)
                sortOrder // Ordering
            )
        }

        Timber.d("curr = $curr")

        curr?.use { cursor ->
            Timber.d("cursor.count = ${cursor.count}")

            //DatabaseUtils.dumpCursor(cursor)

            if (cursor.moveToFirst())
            {
                Timber.d("cursor = $cursor")


                val idColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val displayNameColumn = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
                do
                {
                    // Get the field values
                    val id = cursor.getLong(idColumn)
                    val displayName = cursor.getString(displayNameColumn)

                    // Do something with the values.
                    //Timber.i("id=$id   displayNameColumn=$displayName")

                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    //Timber.d("contentUri=$contentUri")

                    ImageUtils.getThumbnail(contentResolver, contentUri, id)?.let { thumbnail ->
                        list.add(Attachment(displayName, contentUri, thumbnail))
                    }

                } while (cursor.moveToNext())
            }
            return list

        } ?: return null
    }
}