package com.example.camera_and_files

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.example.camera_and_files.databinding.ActivityMainBinding
import timber.log.Timber
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION

import android.provider.MediaStore
import java.lang.IllegalArgumentException
import java.security.AccessController.getContext
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity()
{
    lateinit var binding: ActivityMainBinding

    var uri: Uri? = null
    var type: String? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val perm = PermissionsHandling(this)

        perm.setCallbacksAndAsk(
            permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_MEDIA_LOCATION,
                    Manifest.permission.CAMERA,
                )
            } else
            {
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                )
            },
            granted = {
                Timber.d("granted")
            },
            denied = {
                Timber.d("denied")
            }
        )

        binding.camera.setOnClickListener{
            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, "take_picture")
            values.put(MediaStore.Images.Media.DESCRIPTION, "take_picture_description")
            uri = contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

            getCamera.launch(uri)
        }

        binding.file.setOnClickListener{
            getFile.launch("application/pdf")
        }

        binding.copy.setOnClickListener{
            copyFile(uri!!)
        }

    }

    private val getFile = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        Timber.d("$uri")
        this.uri = uri
        type = "pdf"
    }

    private val getCamera = registerForActivityResult(object : ActivityResultContracts.TakePicture()
    {
        override fun createIntent(
            context: Context,
            input: Uri
        ): Intent
        {
            val intent = super.createIntent(context, input)
            intent.addFlags(
                FLAG_GRANT_WRITE_URI_PERMISSION or
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
            )
            return intent
        }
    }) {
        if (it) Timber.d("got camera") else Timber.e("no camera")

        type = "img"
        Timber.d("$uri")
    }

    private fun copyFile(uri: Uri)
    {
        val inStream = contentResolver.openInputStream(uri)!!

        val (dir, filename) = when (type)
        {
            "img" -> Pair("Images", "${getUniqueFilename("test")}.jpg")
            "pdf" -> Pair("Files", "${getUniqueFilename("test")}.pdf")
            else -> throw IllegalArgumentException()
        }

        val file = File(this.getDir(dir, Context.MODE_PRIVATE), "/$filename")

        Timber.d("$file")

        if (file.exists()) {
            file.delete()
        }
        file.createNewFile()

        val outStream = FileOutputStream(file)
        val buff = ByteArray(5 * 1024)

        var len: Int
        while(inStream.read(buff).also { len = it } >= 0)
        {
            outStream.write(buff, 0, len)
        }

        outStream.flush()
        outStream.close()
        inStream.close()
    }

    @SuppressLint("SimpleDateFormat")
    fun getUniqueFilename(fileName: String): String
    {
        val name = getFilenameWithoutExt(fileName)
        val ext = getTypeExtension(fileName)

        val format = SimpleDateFormat("ddMMyy-hhmmss");
        return "$name-${format.format( Date() )}.$ext"
    }

    private fun getTypeExtension(name: String): String
    {
        return File(name).extension
    }

    private fun getFilenameWithoutExt(name: String): String
    {
        return File(name).nameWithoutExtension
    }
}