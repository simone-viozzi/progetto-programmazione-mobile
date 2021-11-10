package com.example.receiptApp.utils

import android.annotation.SuppressLint
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class FileUtils
{
    companion object
    {
        fun saveFile(bis: InputStream, destinationFile: File): Uri?
        {
            var contentUri: Uri? = null
            var bos: BufferedOutputStream? = null
            try
            {
                bos = BufferedOutputStream(FileOutputStream(destinationFile.absolutePath, false))
                val buf = ByteArray(1024)
                bis.read(buf)
                do
                {
                    bos.write(buf)
                } while (bis.read(buf) != -1)
            } catch (e: IOException)
            {
                e.printStackTrace()
            } finally
            {
                try
                {
                    bis.close()
                    bos?.close()
                    contentUri = Uri.fromFile(destinationFile)
                } catch (e: IOException)
                {
                    e.printStackTrace()
                }
            }
            return contentUri
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