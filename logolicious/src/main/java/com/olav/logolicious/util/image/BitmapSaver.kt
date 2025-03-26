package com.olav.logolicious.util.image

import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import com.olav.logolicious.util.FileUtil
import com.olav.logolicious.util.GlobalClass
import com.olav.logolicious.util.LogoliciousApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object BitmapSaver {
    const val TAG: String = "BitmapSaver"
    var myCanvasBitmap: Bitmap? = null

    interface SaveBitmapCallback {
        fun onBitmapSaved(path: String)
    }

    @JvmStatic
    fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri, proj, null, null, null)
            if (null != cursor && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                return cursor.getString(column_index)
            }
        } catch (e: Exception) {
            Log.e(TAG, "getRealPathFromURI Exception : $e")
            return contentUri.path
        } finally {
            cursor?.close()
        }
        return ""
    }

    @JvmStatic
    fun getImagePathFromInputStreamUri(context: Context, uri: Uri): String? {
        var inputStream: InputStream? = null
        var filePath: String? = null

        if (uri.authority != null) {
            try {
                inputStream = context.contentResolver.openInputStream(uri) // context needed
                val photoFile = createTemporalFileFrom(inputStream)

                filePath = photoFile!!.path
            } catch (e: FileNotFoundException) {
                // log
            } catch (e: IOException) {
                // log
            } finally {
                try {
                    inputStream!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        return filePath
    }

    @Throws(IOException::class)
    private fun createTemporalFileFrom(inputStream: InputStream?): File? {
        var targetFile: File? = null

        if (inputStream != null) {
            var read: Int
            val buffer = ByteArray(8 * 1024)

            targetFile = createTemporalFile()
            val outputStream: OutputStream = FileOutputStream(targetFile)

            while ((inputStream.read(buffer).also { read = it }) != -1) {
                outputStream.write(buffer, 0, read)
            }
            outputStream.flush()

            try {
                outputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return targetFile
    }

    private fun createTemporalFile(): File {
        return File(
            GlobalClass.getAppContext().externalCacheDir,
            "LogoLiciousTempFile.jpg"
        ) // context needed
    }

    @JvmStatic
    fun exifBitmapOrientationCorrector(context: Context, uri: Uri): Bitmap? {
        var realImage: Bitmap? = null
        var `is`: InputStream? = null
        try {
            `is` = context.contentResolver.openInputStream(uri)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        if (`is` != null) {
            realImage = BitmapFactory.decodeStream(`is`)
        }

        var exif: ExifInterface? = null
        try {
            var strRealPath = getRealPathFromURI(context, uri)
            //Safe check if the retrieving of image fail for cloud apps
            if (null == strRealPath) {
                strRealPath = getImagePathFromInputStreamUri(context, uri)
                realImage = BitmapFactory.decodeFile(strRealPath)
            }
            Log.i("xxx", "xxx extracting ExifData $strRealPath")
            exif = ExifInterface(strRealPath!!)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        Log.d(
            "EXIF value",
            exif!!.getAttribute(ExifInterface.TAG_ORIENTATION)!!
        )
        LogoliciousApp.sharedPreferenceSet(context, "BaseImgOrientation", 0)
        if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equals("6", ignoreCase = true)) {
            realImage = ImageHelper.rotateImage(realImage, 90)
            LogoliciousApp.sharedPreferenceSet(context, "BaseImgOrientation", 90)
            Log.i("xxx", "xxx exifBitmapOrientationCorrector 90")
        } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
                .equals("8", ignoreCase = true)
        ) {
            realImage = ImageHelper.rotateImage(realImage, 270)
            LogoliciousApp.sharedPreferenceSet(context, "BaseImgOrientation", 270)
            Log.i("xxx", "xxx exifBitmapOrientationCorrector 270")
        } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
                .equals("3", ignoreCase = true)
        ) {
            realImage = ImageHelper.rotateImage(realImage, 180)
            LogoliciousApp.sharedPreferenceSet(context, "BaseImgOrientation", 180)
            Log.i("xxx", "xxx exifBitmapOrientationCorrector 180")
        }
        return realImage
    }

    /**
     *
     * @param context
     * @param path
     * @return Orientation corrector for Photo from Camera. It will decrease quality to avoid memory issue
     */
    @JvmStatic
    fun exifBitmapOrientationCorrector(context: Context?, path: String): Bitmap? {
        var realImage =
            ImageHelper.decodeBitmapPath(path) //this will decrease memory issue if OOM error

        var exif: ExifInterface? = null
        try {
            exif = ExifInterface(path)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if (null == exif) return realImage

        Log.d(
            "EXIF value",
            exif.getAttribute(ExifInterface.TAG_ORIENTATION)!!
        )
        LogoliciousApp.sharedPreferenceSet(context, "BaseImgOrientation", 0)
        if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equals("6", ignoreCase = true)) {
            realImage = ImageHelper.rotateImage(realImage, 90)
            LogoliciousApp.sharedPreferenceSet(context, "BaseImgOrientation", 90)
            Log.i("xxx", "xxx exifBitmapOrientationCorrector 90")
        } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
                .equals("8", ignoreCase = true)
        ) {
            realImage = ImageHelper.rotateImage(realImage, 270)
            LogoliciousApp.sharedPreferenceSet(context, "BaseImgOrientation", 270)
            Log.i("xxx", "xxx exifBitmapOrientationCorrector 270")
        } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
                .equals("3", ignoreCase = true)
        ) {
            realImage = ImageHelper.rotateImage(realImage, 180)
            LogoliciousApp.sharedPreferenceSet(context, "BaseImgOrientation", 180)
            Log.i("xxx", "xxx exifBitmapOrientationCorrector 180")
        }
        return realImage
    }

    //same function of exifBitmapOrientationCorrector
    @JvmStatic
    fun exifLogoBitmapOrientationCorrector(context: Context?, path: String): Bitmap? {
        var realImage = ImageHelper.decodeBitmapPath(path)

        var exif: ExifInterface? = null
        try {
            exif = ExifInterface(path)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if (null == exif) return realImage

        Log.d(
            "EXIF value",
            exif.getAttribute(ExifInterface.TAG_ORIENTATION)!!
        )
        if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equals("6", ignoreCase = true)) {
            realImage = ImageHelper.rotateImage(realImage, 90)
        } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
                .equals("8", ignoreCase = true)
        ) {
            realImage = ImageHelper.rotateImage(realImage, 270)
        } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
                .equals("3", ignoreCase = true)
        ) {
            realImage = ImageHelper.rotateImage(realImage, 180)
        }
        return realImage
    }

    @JvmStatic
    fun saveBitmap(sp: SharedPreferences, dir: String, filename: String, bitmap: Bitmap?, scope: CoroutineScope, callback: SaveBitmapCallback) {
        if (null == bitmap) {
            callback.onBitmapSaved("")
            return
        }

        scope.launch(Dispatchers.Main) {
            val result = compressBitmapInBackground(bitmap, sp, dir, filename)
            callback.onBitmapSaved(result)
        }

    }

    private suspend fun compressBitmapInBackground(bitmap: Bitmap, sp: SharedPreferences, dir: String, filename: String): String =
        withContext(Dispatchers.IO) {
        myCanvasBitmap = bitmap
        val myDir = File(dir)
        if (!myDir.exists()) {
            myDir.mkdirs()
        }
        val fname = "Image-$filename.${FileUtil.getImageType(sp)}"
        val file = File(myDir, fname)
        if (file.exists()) {
            file.delete()
        }
        var out: OutputStream? = null
        try {
            out = FileOutputStream(file)
            val compressFormat = FileUtil.getImageCompressType(sp)
            val success = myCanvasBitmap!!.compress(compressFormat, 100, out)
            if (!success) {
                Log.e(TAG, "Bitmap compression failed!")
            }
            out.flush()
            "$dir/$fname"
        } catch (e: IOException) {
            Log.e(TAG, "Error during bitmap compression: ${e.message}")
            e.printStackTrace()
            ""
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during bitmap compression: ${e.message}")
            e.printStackTrace()
            ""
        } finally {
            out?.close()
        }
    }

    fun saveBitmap(filename: String, pathToSave: String, view: View) {
        val copiedBitmap = view.drawingCache.copy(Bitmap.Config.ARGB_8888, false)
        val root = FileUtil.getAppRootFolder().toString()
        val myDir = File(root + pathToSave)
        myDir.mkdirs()
        //	    Random generator = new Random();
//	    int n = 10000;
//	    n = generator.nextInt(n);
        val fname = "Image-$filename.jpg"
        val file = File(myDir, fname)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            copiedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    /**
     *
     * @param pathFile
     * @param bitmap
     * @return This will be use for logo upload to handle logo with transparent background.
     */
    @JvmStatic
    fun saveLogoBitmap(pathFile: String, bitmap: Bitmap?): String {
        myCanvasBitmap = bitmap
        val file = File(pathFile)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            if (pathFile.endsWith(".PNG") || pathFile.endsWith(".png") || pathFile.endsWith(".GIF") || pathFile.endsWith(
                    ".gif"
                )
            ) myCanvasBitmap!!.compress(
                Bitmap.CompressFormat.PNG, 100, out
            )
            else myCanvasBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return pathFile
    }

    @JvmStatic
    fun saveBitmap(
        sp: SharedPreferences?,
        dir: String,
        filename: String,
        bitmap: Bitmap?
    ): String {
        if (null == bitmap) return ""

        myCanvasBitmap = bitmap
        val myDir = File(dir)
        myDir.mkdirs()
        val fname = "Image-" + filename + "." + FileUtil.getImageType(sp)
        val file = File(myDir, fname)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            if (null == sp) return ""
            myCanvasBitmap!!.compress(FileUtil.getImageCompressType(sp), 100, out)
            out.flush()
            out.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        return dir + fname
    }
}
