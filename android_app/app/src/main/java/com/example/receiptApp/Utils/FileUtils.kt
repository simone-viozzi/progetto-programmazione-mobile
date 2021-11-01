package com.example.receiptApp.Utils

import android.annotation.SuppressLint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class FileUtils
{
    companion object
    {
        suspend fun saveFile(bis: InputStream, destinationFilename: String)
        {
            withContext(Dispatchers.IO)
            {
                var bos: BufferedOutputStream? = null
                try
                {
                    bos = BufferedOutputStream(FileOutputStream(destinationFilename, false))
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
                    } catch (e: IOException)
                    {
                        e.printStackTrace()
                    }
                }
            }
        }


        @SuppressLint("SimpleDateFormat")
        fun getUniqueFilename(): String
        {
            val format = SimpleDateFormat("ddMMyy-hhmmss");
            return "File-${format.format( Date() )}"
        }

    }
}