package com.example.util

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer

object AudioExtractor {
  private const val TAG = "AudioExtractor"

  /**
   * Extracts audio track from [sourceFile] into [destAudioFile] using Android MediaExtractor and MediaMuxer.
   * If sourceFile is already audio-only, or if muxing cannot proceed, it safely copies the file so playback works.
   * Returns true on success.
   */
  fun extractAudioOrCopy(sourceFile: File, destAudioFile: File): Boolean {
    if (!sourceFile.exists() || sourceFile.length() <= 0L) {
      Log.e(TAG, "Source file is empty or does not exist: ${sourceFile.absolutePath}")
      return false
    }

    // Attempt hardware-accelerated lossless bitstream extraction
    val extractionSuccess = tryExtractAudioTrack(sourceFile, destAudioFile)
    if (extractionSuccess && destAudioFile.exists() && destAudioFile.length() > 0L) {
      Log.d(TAG, "Successfully extracted pure audio track to: ${destAudioFile.absolutePath} (${destAudioFile.length()} bytes)")
      return true
    }

    // Fallback: Copy the file directly
    Log.w(TAG, "Extraction failed or not required. Falling back to direct stream copy.")
    return try {
      FileInputStream(sourceFile).use { input ->
        FileOutputStream(destAudioFile).use { output ->
          input.copyTo(output)
        }
      }
      destAudioFile.exists() && destAudioFile.length() > 0L
    } catch (e: Exception) {
      Log.e(TAG, "Fallback file copy failed", e)
      false
    }
  }

  private fun tryExtractAudioTrack(sourceFile: File, destAudioFile: File): Boolean {
    val extractor = MediaExtractor()
    var muxer: MediaMuxer? = null
    var isMuxerStarted = false

    try {
      extractor.setDataSource(sourceFile.absolutePath)
      val numTracks = extractor.trackCount
      var audioTrackIndex = -1
      var audioFormat: MediaFormat? = null

      for (i in 0 until numTracks) {
        val format = extractor.getTrackFormat(i)
        val mime = format.getString(MediaFormat.KEY_MIME) ?: ""
        if (mime.startsWith("audio/")) {
          audioTrackIndex = i
          audioFormat = format
          break
        }
      }

      if (audioTrackIndex == -1 || audioFormat == null) {
        Log.w(TAG, "No audio track found in media container.")
        return false
      }

      extractor.selectTrack(audioTrackIndex)

      if (destAudioFile.exists()) {
        destAudioFile.delete()
      }

      muxer = MediaMuxer(destAudioFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
      val dstAudioTrackIndex = muxer.addTrack(audioFormat)
      muxer.start()
      isMuxerStarted = true

      val maxBufferSize = if (audioFormat.containsKey(MediaFormat.KEY_MAX_INPUT_SIZE)) {
        val size = audioFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE)
        if (size > 0) size else 256 * 1024
      } else {
        256 * 1024
      }

      val buffer = ByteBuffer.allocateDirect(maxBufferSize)
      val bufferInfo = MediaCodec.BufferInfo()

      while (true) {
        bufferInfo.offset = 0
        bufferInfo.size = extractor.readSampleData(buffer, 0)
        if (bufferInfo.size < 0) {
          break
        }
        bufferInfo.presentationTimeUs = extractor.sampleTime
        bufferInfo.flags = extractor.sampleFlags
        muxer.writeSampleData(dstAudioTrackIndex, buffer, bufferInfo)
        extractor.advance()
      }

      return true
    } catch (e: Exception) {
      Log.e(TAG, "Audio track extraction threw exception", e)
      return false
    } finally {
      try {
        if (isMuxerStarted) {
          muxer?.stop()
        }
      } catch (e: Exception) {
        Log.w(TAG, "Muxer stop threw exception: ${e.message}")
      }
      try {
        muxer?.release()
      } catch (_: Exception) {}
      try {
        extractor.release()
      } catch (_: Exception) {}
    }
  }
}
