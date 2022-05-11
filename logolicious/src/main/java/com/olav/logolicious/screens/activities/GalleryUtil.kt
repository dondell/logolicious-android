package com.olav.logolicious.screens.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import java.io.File

class GalleryUtil : Activity() {
    var mCurrentPhotoPath: String? = null
    var photoFile: File? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            //Pick Image From Gallery
            val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(i, RESULT_SELECT_IMAGE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RESULT_SELECT_IMAGE -> if (resultCode == RESULT_OK && data != null && data.data != null) {
                try {
                    val selectedImage = data.data
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    val cursor =
                        contentResolver.query(selectedImage!!, filePathColumn, null, null, null)
                    cursor!!.moveToFirst()
                    val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                    val picturePath = cursor.getString(columnIndex)
                    cursor.close()

                    //return Image Path to the Main Activity
                    val returnFromGalleryIntent = Intent()
                    returnFromGalleryIntent.putExtra("picturePath", picturePath)
                    setResult(RESULT_OK, returnFromGalleryIntent)
                    finish()
                } catch (e: Exception) {
                    e.printStackTrace()
                    val returnFromGalleryIntent = Intent()
                    setResult(RESULT_CANCELED, returnFromGalleryIntent)
                    finish()
                }
            } else {
                Log.i(TAG, "RESULT_CANCELED")
                val returnFromGalleryIntent = Intent()
                setResult(RESULT_CANCELED, returnFromGalleryIntent)
                finish()
            }
        }
    }

    companion object {
        private const val RESULT_SELECT_IMAGE = 100
        const val MEDIA_TYPE_IMAGE = 1
        private const val TAG = "GalleryUtil"
    }
}