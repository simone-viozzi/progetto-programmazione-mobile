package com.example.receiptApp.repository.sources

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import com.example.receiptApp.utils.ImageUtils
import com.example.receiptApp.repository.AttachmentRepository
import timber.log.Timber



class GalleryImages(private val contentResolver: ContentResolver)
{
    /**
     * query the system for the images in the gallery
     *
     * @param limit -> how many image should be returned
     * @param offset -> the offset of when i should start returning images
     * @return -> a list of attachments
     */
    fun getImages(limit: Int, offset: Int): List<AttachmentRepository.Attachment>?
    {
        // those are the columns i need
        val projection: Array<String> = arrayOf(
            MediaStore.Images.Media._ID,    // -> needed to get the thumbnail on older versions of SDK
            MediaStore.Images.Media.DISPLAY_NAME, // -> the name of the file
            MediaStore.Images.Media.MIME_TYPE, // -> i need only images to i need this column to filter
            MediaStore.Images.Media.DATE_MODIFIED // -> sort descending
        )

        // depending of the SDK i need to query different places
        val images = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else
        {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        // the mimeTypes i will filter
        val selectionArgs = arrayOf(
            "image/jpeg",
            "image/jpg",
            "image/png"
        )
        val selection = "${MediaStore.Images.Media.MIME_TYPE} IN (?,?,?)"

        // this list will get filled with all the attachments i'm reading
        val list: MutableList<AttachmentRepository.Attachment> = mutableListOf()

        val curr: Cursor? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            // with android 11 you cannot no longer specify query directly but you need to build a bundle
            //  with all the things you want to query
            val bundle = Bundle().apply {
                putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection)
                putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, selectionArgs)
                putString(ContentResolver.QUERY_ARG_SORT_COLUMNS, MediaStore.Images.Media.DATE_MODIFIED)
                putInt(ContentResolver.QUERY_ARG_SORT_DIRECTION, ContentResolver.QUERY_SORT_DIRECTION_DESCENDING)
                putInt(ContentResolver.QUERY_ARG_LIMIT, limit)
                putInt(ContentResolver.QUERY_ARG_OFFSET, offset)
            }
            // and than use the bundle in the query
            contentResolver.query(
                images,
                projection,
                bundle,
                null
            )
        } else
        {
            // with the sort order i can append the limit and offset too
            val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC LIMIT $limit OFFSET $offset"

            // on older versions you can put the arguments directly in the query
            contentResolver.query(
                images,
                projection,  // Which columns to return
                selection,  // Which rows to return (all rows)
                selectionArgs,  // Selection arguments (none)
                sortOrder // Ordering
            )
        }

        curr?.use { cursor ->
            Timber.d("cursor.count = ${cursor.count}")

            // util method to see what is in the cursor
            //DatabaseUtils.dumpCursor(cursor)

            if (cursor.moveToFirst())
            {
                Timber.d("cursor = $cursor")

                val idColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val displayNameColumn = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
                // while the cursor sill have elements i retrive them
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

                    ImageUtils.getThumbnail(contentResolver, contentUri, id)?.let { thumbnail ->
                        list.add(AttachmentRepository.Attachment(
                            name = displayName,
                            uri = contentUri,
                            thumbnail,
                            true,
                            type=AttachmentRepository.TYPE.IMAGE)
                        )
                    }
                } while (cursor.moveToNext())
            }
            return list

        } ?: return null
    }
}