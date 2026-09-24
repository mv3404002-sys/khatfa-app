package com.example.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Streaming
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class ServerStatusResponse(
  @Json(name = "status") val status: String? = null,
  @Json(name = "service") val service: String? = null
)

@JsonClass(generateAdapter = true)
data class BackendFormat(
  @Json(name = "format_id") val formatId: String,
  @Json(name = "ext") val ext: String? = null,
  @Json(name = "resolution") val resolution: String? = null,
  @Json(name = "filesize") val filesize: Long? = null,
  @Json(name = "has_video") val hasVideo: Boolean? = null,
  @Json(name = "has_audio") val hasAudio: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class VideoInfoResponse(
  @Json(name = "title") val title: String? = null,
  @Json(name = "thumbnail") val thumbnail: String? = null,
  @Json(name = "duration") val duration: Double? = null,
  @Json(name = "uploader") val uploader: String? = null,
  @Json(name = "platform") val platform: String? = null,
  @Json(name = "formats") val formats: List<BackendFormat>? = null
)

interface KhatfaApiService {
  @GET("/")
  suspend fun checkServer(): ServerStatusResponse

  @GET("info")
  suspend fun getVideoInfo(@Query("url") url: String): VideoInfoResponse

  @Streaming
  @GET("download")
  suspend fun streamDownload(
    @Query("url") url: String,
    @Query("format_id") formatId: String = "best"
  ): Response<ResponseBody>
}

object KhatfaApiClient {
  const val BASE_URL = "https://khatfa-backend.onrender.com/"

  val okHttpClient: OkHttpClient = OkHttpClient.Builder()
    .connectTimeout(45, TimeUnit.SECONDS)
    .readTimeout(120, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .retryOnConnectionFailure(true)
    .build()

  private val moshi: Moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

  val api: KhatfaApiService by lazy {
    Retrofit.Builder()
      .baseUrl(BASE_URL)
      .client(okHttpClient)
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()
      .create(KhatfaApiService::class.java)
  }

  fun buildDownloadUrl(videoUrl: String, formatId: String): String {
    val encodedUrl = URLEncoder.encode(videoUrl, "UTF-8")
    val encodedFormat = URLEncoder.encode(formatId, "UTF-8")
    return "${BASE_URL}download?url=$encodedUrl&format_id=$encodedFormat"
  }

  suspend fun downloadToStream(
    videoUrl: String,
    formatId: String,
    outputStream: OutputStream,
    onProgress: (progressFraction: Float, downloadedBytes: Long, totalBytes: Long) -> Unit
  ): Pair<Boolean, Long> {
    val response = api.streamDownload(videoUrl, formatId)
    if (!response.isSuccessful) {
      return Pair(false, 0L)
    }

    val body = response.body() ?: return Pair(false, 0L)
    val contentLength = body.contentLength()

    var inputStream: InputStream? = null
    var totalRead = 0L

    return try {
      inputStream = body.byteStream()
      val buffer = ByteArray(8192)
      var bytesRead: Int

      while (inputStream.read(buffer).also { bytesRead = it } != -1) {
        outputStream.write(buffer, 0, bytesRead)
        totalRead += bytesRead
        val progress = if (contentLength > 0) {
          (totalRead.toFloat() / contentLength).coerceIn(0f, 1f)
        } else {
          (totalRead.toFloat() / (totalRead + 1024 * 1024 * 5)).coerceIn(0.1f, 0.95f)
        }
        onProgress(progress, totalRead, contentLength)
      }
      outputStream.flush()
      onProgress(1f, totalRead, if (contentLength > 0) contentLength else totalRead)
      Pair(true, totalRead)
    } catch (e: Exception) {
      Pair(false, totalRead)
    } finally {
      try { inputStream?.close() } catch (_: Exception) {}
      try { outputStream.close() } catch (_: Exception) {}
    }
  }

  suspend fun downloadToFile(
    videoUrl: String,
    formatId: String,
    destinationFile: File,
    onProgress: (progressFraction: Float, downloadedBytes: Long, totalBytes: Long) -> Unit
  ): Boolean {
    val response = api.streamDownload(videoUrl, formatId)
    if (!response.isSuccessful) {
      return false
    }

    val body = response.body() ?: return false
    val contentLength = body.contentLength()

    var inputStream: InputStream? = null
    var outputStream: FileOutputStream? = null

    return try {
      inputStream = body.byteStream()
      outputStream = FileOutputStream(destinationFile)

      val buffer = ByteArray(8192)
      var totalRead = 0L
      var bytesRead: Int

      while (inputStream.read(buffer).also { bytesRead = it } != -1) {
        outputStream.write(buffer, 0, bytesRead)
        totalRead += bytesRead
        val progress = if (contentLength > 0) {
          (totalRead.toFloat() / contentLength).coerceIn(0f, 1f)
        } else {
          // indeterminate or unknown length: simulate gradual progression
          (totalRead.toFloat() / (totalRead + 1024 * 1024 * 5)).coerceIn(0.1f, 0.95f)
        }
        onProgress(progress, totalRead, contentLength)
      }
      outputStream.flush()
      onProgress(1f, totalRead, if (contentLength > 0) contentLength else totalRead)
      true
    } catch (e: Exception) {
      if (destinationFile.exists()) {
        destinationFile.delete()
      }
      false
    } finally {
      try { inputStream?.close() } catch (_: Exception) {}
      try { outputStream?.close() } catch (_: Exception) {}
    }
  }
}
