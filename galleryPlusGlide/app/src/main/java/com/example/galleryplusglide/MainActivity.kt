package com.example.galleryplusglide

import android.content.ContentUris
import android.database.DatabaseUtils
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Size
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.galleryplusglide.databinding.ActivityMainBinding
import timber.log.Timber


class MainActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val list: List<Pair<Uri, Long>> = getAllShownImagesPath()

        val iter = list.listIterator()

        binding.imageView.setOnClickListener {

            if (iter.hasNext())
            {

                val el: Pair<Uri, Long> = iter.next()

                val thumbnail: Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                {
                    Timber.d("el.first=${el.first}")
                    contentResolver.loadThumbnail(
                        el.first, Size(480, 480), null
                    )
                } else
                {
                    MediaStore.Images.Thumbnails.getThumbnail(
                        contentResolver,
                        el.second,
                        640,
                        BitmapFactory.Options()
                    )
                }

                Glide.with(this)
                    .load(thumbnail)
                    .into(binding.imageView)

            }
        }

    }


    private fun getAllShownImagesPath(): List<Pair<Uri, Long>>
    {

        val projection: Array<String> = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.DATE_MODIFIED
        )
        // content:// style URI for the "primary" external storage volume
        val images = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            MediaStore.Images.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        } else
        {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"

        Timber.d("$images")

        val selection = "${MediaStore.Images.Media.MIME_TYPE} IN (?,?,?)"

        val selectionArgs = arrayOf(
            "image/jpeg",
            "image/jpg",
            "image/png"
        )


        val list: MutableList<Pair<Uri, Long>> = mutableListOf()

        // Make the query.
        contentResolver.query(
            images,
            projection,  // Which columns to return
            selection,  // Which rows to return (all rows)
            selectionArgs,  // Selection arguments (none)
            sortOrder // Ordering
        )?.use { cursor ->

            Timber.i(" query count=" + cursor.count)

            assert(cursor.count != 0)

            if (cursor.moveToFirst())
            {
                DatabaseUtils.dumpCursor(cursor)

                var id: Long
                var displayName: String
                val idColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val displayNameColumn = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
                do
                {
                    // Get the field values
                    id = cursor.getLong(idColumn)
                    displayName = cursor.getString(displayNameColumn)

                    // Do something with the values.
                    Timber.i("id=$id   displayNameColumn=$displayName")

                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    Timber.d("contentUri=$contentUri")

                    list.add(Pair(contentUri, id))

                } while (cursor.moveToNext())
            }
        }
        
        return list
    }


    //    private val pickImage = 100
    //    private var imageUri: Uri? = null

    //    override fun onCreate(savedInstanceState: Bundle?)
    //    {
    //        super.onCreate(savedInstanceState)
    //        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    //
    //        Timber.d("hello logs")
    //        binding.imageView.setOnClickListener {
    //            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
    //            startActivityForResult(gallery, pickImage)
    //        }
    //    }
    //
    //    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    //        super.onActivityResult(requestCode, resultCode, data)
    //        if (resultCode == RESULT_OK && requestCode == pickImage) {
    //            imageUri = data?.data
    //            binding.imageView.setImageURI(imageUri)
    //        }
    //    }

}