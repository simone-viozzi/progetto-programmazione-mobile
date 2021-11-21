package com.example.receiptApp.utils

import android.annotation.SuppressLint
import android.net.Uri
import androidx.core.net.toUri
import timber.log.Timber
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class FileUtils
{
    companion object
    {
        /**
         * Save file
         *  utility so copy a file
         *
         * @param inStream -> to avoid the need  of context i pass directly the stream
         * @param outFile -> the file that will be written
         * @return
         */
        suspend fun saveFile(inStream: InputStream, outFile: File): Uri
        {
            Timber.d("$outFile")

            if (outFile.exists()) {
                outFile.delete()
            }
            outFile.createNewFile()

            val outStream = FileOutputStream(outFile)
            val buff = ByteArray(5 * 1024)

            var len: Int
            while(inStream.read(buff).also { len = it } >= 0)
            {
                outStream.write(buff, 0, len)
            }

            outStream.flush()
            outStream.close()
            inStream.close()

            Timber.e("file -> $outFile")
            Timber.e("file -> ${outFile.toUri()}")

            // if everything went of i can return the uri of the newly created file
            return outFile.toUri()
        }

        /**
         * Get unique filename
         *  utility to make a filename unique
         *
         * @param fileName
         * @return
         */
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