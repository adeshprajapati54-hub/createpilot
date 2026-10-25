package com.example.util

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import java.io.File
import java.io.FileInputStream

class AudioRecorderHelper(private val context: Context) {
    private var recorder: MediaRecorder? = null
    private var currentFile: File? = null

    val isRecording: Boolean
        get() = recorder != null

    fun startRecording(): Boolean {
        try {
            stopRecording()
            val outputFile = File(context.cacheDir, "voice_input_${System.currentTimeMillis()}.m4a")
            currentFile = outputFile

            val mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            mediaRecorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setOutputFile(outputFile.absolutePath)
                prepare()
                start()
            }

            recorder = mediaRecorder
            return true
        } catch (e: Exception) {
            Log.e("AudioRecorderHelper", "Failed to start recording: ${e.message}", e)
            recorder = null
            return false
        }
    }

    fun stopRecording(): ByteArray? {
        val mr = recorder ?: return null
        return try {
            mr.stop()
            mr.release()
            recorder = null
            val file = currentFile
            if (file != null && file.exists() && file.length() > 0) {
                file.readBytes()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("AudioRecorderHelper", "Error stopping recording: ${e.message}", e)
            recorder = null
            null
        }
    }
}
