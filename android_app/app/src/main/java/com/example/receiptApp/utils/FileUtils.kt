package com.example.receiptApp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class FileUtils
{
    companion object
    {
        fun saveFile(inStream: InputStream, file: File, context: Context): Uri
        {
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

            return file.toUri()
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
}