package com.fiap.ariachallenge.data.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
class AvatarStorage @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    suspend fun saveFromContentUri(
        userId: String,
        contentUri: String,
        previousPath: String? = null,
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            deleteFile(previousPath)
            val uri = Uri.parse(contentUri)
            val dir = File(context.filesDir, AVATARS_DIR).apply { mkdirs() }
            val destination = File(dir, "$userId-${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                val bitmap = BitmapFactory.decodeStream(input)
                    ?: error("Could not decode image")
                val scaled = scaleDown(bitmap, MAX_EDGE_PX)
                FileOutputStream(destination).use { output ->
                    scaled.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, output)
                }
                if (scaled !== bitmap) {
                    bitmap.recycle()
                    scaled.recycle()
                }
            } ?: error("Could not open image")
            destination.absolutePath
        }
    }

    fun deleteFile(path: String?) {
        if (path.isNullOrBlank()) return
        runCatching { File(path).delete() }
    }

    fun exists(path: String?): Boolean =
        !path.isNullOrBlank() && File(path).exists()

    private fun scaleDown(source: Bitmap, maxEdge: Int): Bitmap {
        val largest = maxOf(source.width, source.height)
        if (largest <= maxEdge) return source
        val ratio = maxEdge.toFloat() / largest
        val width = (source.width * ratio).toInt().coerceAtLeast(1)
        val height = (source.height * ratio).toInt().coerceAtLeast(1)
        return Bitmap.createScaledBitmap(source, width, height, true)
    }

    companion object {
        private const val AVATARS_DIR = "avatars"
        private const val MAX_EDGE_PX = 512
        private const val JPEG_QUALITY = 85
    }
}
