package com.dindamaylan.storyapp.utils

import android.animation.ObjectAnimator
import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import java.io.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object Helper {
    fun ImageView.setImage(context: Context, url: String) {
        Glide
            .with(context)
            .load(url)
            .into(this)
    }

    fun TextView.dateFormat(timestamp: String) {
        val dateFormatYMD = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        val date = dateFormatYMD.parse(timestamp) as Date

        val dateFormatted = DateFormat.getDateInstance(DateFormat.FULL).format(date)
        this.text = dateFormatted
    }

    fun View.margin(
        left: Float? = null,
        top: Float? = null,
        right: Float? = null,
        bottom: Float? = null
    ) {
        layoutParams<ViewGroup.MarginLayoutParams> {
            left?.run { leftMargin = dpToPx(this) }
            top?.run { topMargin = dpToPx(this) }
            right?.run { rightMargin = dpToPx(this) }
            bottom?.run { bottomMargin = dpToPx(this) }
        }
    }

    private inline fun <reified T : ViewGroup.LayoutParams> View.layoutParams(block: T.() -> Unit) {
        if (layoutParams is T) block(layoutParams as T)
    }

    private fun View.dpToPx(dp: Float): Int = context.dpToPx(dp)
    private fun Context.dpToPx(dp: Float): Int =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()

    fun createFile(app: Application): File {
        val mediaDir = app.externalMediaDirs.firstOrNull()?.let {
            File(it, "StoryApp").apply { mkdirs() }
        }

        val outputDirectory = if (
            mediaDir != null && mediaDir.exists()
        ) mediaDir else app.filesDir

        return File(outputDirectory, "$timeStamp.jpg")
    }

    private const val DATE_FORMAT = "dd-MMM-yyyy"

    private val timeStamp: String = SimpleDateFormat(
        DATE_FORMAT,
        Locale.US
    ).format(System.currentTimeMillis())

    private fun createTempFile(prefix: Context): File {
        return File.createTempFile(
            SimpleDateFormat("dd-MM-yyyy", Locale.US).format(System.currentTimeMillis()),
            ".jpg",
            prefix.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )
    }

    fun uriToFile(selectedImage: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val myFiles = createTempFile(context)

        val inputStream = contentResolver.openInputStream(selectedImage) as InputStream
        val outputStream: OutputStream = FileOutputStream(myFiles)
        val buf = ByteArray(1024)
        var its: Int

        while (inputStream.read(buf).also {
                its = it
            } > 0) outputStream.write(buf, 0, its)
        outputStream.close()
        inputStream.close()

        return myFiles
    }

    fun rotateBitmap(bitmap: Bitmap, isBackCamera: Boolean = false): Bitmap {
        val matrix = Matrix()
        return if (isBackCamera) {
            matrix.postRotate(90f)
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } else {
            matrix.postRotate(-90f)
            matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }
    }

    fun reduceFileImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)

        var compressQuality = 100
        var streamLength: Int

        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > 1000000)

        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }

    fun Bitmap.bitmapToFile(file: File): File {
        this.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))
        return file
    }

    fun View.animVisibility(isVisible: Boolean, duration: Long = 500) {
        ObjectAnimator
            .ofFloat(this, View.ALPHA, if (isVisible) 2f else 0f)
            .setDuration(duration)
            .start()
    }
}