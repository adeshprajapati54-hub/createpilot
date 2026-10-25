package com.example.data.remote

import android.content.Context
import android.media.MediaPlayer
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

object GeminiApiClient {
    private const val TAG = "GeminiApiClient"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta"

    // Supported Models as per requirements & gemini-api skill
    const val MODEL_FLASH = "gemini-3.8-flash"
    const val MODEL_FLASH_LITE = "gemini-3.1-flash-lite-preview"
    const val MODEL_ARGON = "gemini-4-argon"
    const val MODEL_IMAGE_PRO = "gemini-3-pro-image-preview"
    const val MODEL_TTS = "gemini-3.8-flash-tts"
    const val MODEL_OMNI_VIDEO = "gemini-omni-1.1-flash"
    const val MODEL_TRANSCRIBE = "gemini-3.5-transcribe"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private var mediaPlayer: MediaPlayer? = null

    fun getApiKey(): String {
        return try {
            BuildConfig.GEMINI_API_KEY.ifBlank { "" }
        } catch (e: Exception) {
            ""
        }
    }

    fun hasApiKey(): Boolean {
        val key = getApiKey()
        return key.isNotBlank() && key != "MY_GEMINI_API_KEY"
    }

    /**
     * Generate text using Gemini 3.8 Flash, 3.1 Flash Lite, or 4 Argon
     */
    suspend fun generateText(
        prompt: String,
        model: String = MODEL_FLASH,
        systemInstruction: String? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (!hasApiKey()) {
            return@withContext Result.failure(IllegalStateException("No valid API key provided. Using CreatorPilot demo generation."))
        }

        try {
            val jsonBody = JSONObject().apply {
                val contents = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                        }
                        put("parts", parts)
                    }
                    put(contentObj)
                }
                put("contents", contents)

                if (!systemInstruction.isNullOrBlank()) {
                    val sysInstruction = JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().apply { put("text", systemInstruction) })
                        }
                        put("parts", parts)
                    }
                    put("systemInstruction", sysInstruction)
                }

                val genConfig = JSONObject().apply {
                    put("temperature", 0.7)
                }
                put("generationConfig", genConfig)
            }

            val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$BASE_URL/models/$model:generateContent?key=$apiKey")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e(TAG, "Gemini API error ($model): $responseBody")
                return@withContext Result.failure(Exception("HTTP ${response.code}: $responseBody"))
            }

            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            val candidate = candidates?.optJSONObject(0)
            val content = candidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text")

            if (!text.isNullOrBlank()) {
                Result.success(text)
            } else {
                Result.failure(Exception("Empty text in Gemini response"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in generateText: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Generate High-Quality Images using gemini-3-pro-image-preview
     * with resolution affordances (1K, 2K, 4K) and 16:9 aspect ratio
     */
    suspend fun generateImage(
        prompt: String,
        imageSize: String = "2K",
        aspectRatio: String = "16:9"
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (!hasApiKey()) {
            return@withContext Result.failure(IllegalStateException("No valid API key provided."))
        }

        try {
            val jsonBody = JSONObject().apply {
                val contents = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                        }
                        put("parts", parts)
                    }
                    put(contentObj)
                }
                put("contents", contents)

                val genConfig = JSONObject().apply {
                    val imageConfig = JSONObject().apply {
                        put("aspectRatio", aspectRatio)
                        put("imageSize", imageSize)
                    }
                    put("imageConfig", imageConfig)
                    put("responseModalities", JSONArray().apply {
                        put("TEXT")
                        put("IMAGE")
                    })
                }
                put("generationConfig", genConfig)
            }

            val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$BASE_URL/models/$MODEL_IMAGE_PRO:generateContent?key=$apiKey")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e(TAG, "Gemini Image API error: $responseBody")
                return@withContext Result.failure(Exception("HTTP ${response.code}: $responseBody"))
            }

            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            val candidate = candidates?.optJSONObject(0)
            val content = candidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")

            var base64Data: String? = null
            if (parts != null) {
                for (i in 0 until parts.length()) {
                    val part = parts.optJSONObject(i)
                    val inlineData = part?.optJSONObject("inlineData")
                    if (inlineData != null) {
                        base64Data = inlineData.optString("data")
                        if (!base64Data.isNullOrBlank()) break
                    }
                }
            }

            if (!base64Data.isNullOrBlank()) {
                Result.success(base64Data)
            } else {
                Result.failure(Exception("No image returned from Gemini"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error generating image: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Convert Text-to-Speech using gemini-3.8-flash-tts
     * Plays the audio on the device
     */
    suspend fun generateAndPlayTTS(
        context: Context,
        text: String,
        voiceName: String = "Kore"
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (!hasApiKey()) {
            return@withContext Result.failure(IllegalStateException("No valid API key provided for TTS."))
        }

        try {
            val jsonBody = JSONObject().apply {
                val contents = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().apply { put("text", text) })
                        }
                        put("parts", parts)
                    }
                    put(contentObj)
                }
                put("contents", contents)

                val genConfig = JSONObject().apply {
                    put("responseModalities", JSONArray().apply {
                        put("AUDIO")
                    })
                    val speechConfig = JSONObject().apply {
                        val voiceConfig = JSONObject().apply {
                            val prebuiltVoiceConfig = JSONObject().apply {
                                put("voiceName", voiceName)
                            }
                            put("prebuiltVoiceConfig", prebuiltVoiceConfig)
                        }
                        put("voiceConfig", voiceConfig)
                    }
                    put("speechConfig", speechConfig)
                }
                put("generationConfig", genConfig)
            }

            val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$BASE_URL/models/$MODEL_TTS:generateContent?key=$apiKey")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e(TAG, "TTS error: $responseBody")
                return@withContext Result.failure(Exception("HTTP ${response.code}: $responseBody"))
            }

            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            val candidate = candidates?.optJSONObject(0)
            val content = candidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")

            var audioData: String? = null
            var mimeType = "audio/mp3"
            if (parts != null) {
                for (i in 0 until parts.length()) {
                    val part = parts.optJSONObject(i)
                    val inlineData = part?.optJSONObject("inlineData")
                    if (inlineData != null) {
                        audioData = inlineData.optString("data")
                        mimeType = inlineData.optString("mimeType", "audio/mp3")
                        if (!audioData.isNullOrBlank()) break
                    }
                }
            }

            if (!audioData.isNullOrBlank()) {
                val decodedBytes = Base64.decode(audioData, Base64.DEFAULT)
                val tempAudioFile = File(context.cacheDir, "creator_tts_${System.currentTimeMillis()}.mp3")
                FileOutputStream(tempAudioFile).use { it.write(decodedBytes) }

                withContext(Dispatchers.Main) {
                    stopAudio()
                    mediaPlayer = MediaPlayer().apply {
                        setDataSource(tempAudioFile.absolutePath)
                        prepare()
                        start()
                    }
                }
                Result.success("Narration playing ($voiceName)")
            } else {
                Result.failure(Exception("No audio returned in response"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "TTS Exception: ${e.message}", e)
            Result.failure(e)
        }
    }

    fun stopAudio() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping audio: ${e.message}")
        }
    }

    /**
     * Generate Video using Interactions API with gemini-omni-1.1-flash
     */
    suspend fun generateVideo(
        prompt: String,
        aspectRatio: String = "16:9",
        duration: String = "5s",
        onStatusUpdate: (String) -> Unit = {}
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (!hasApiKey()) {
            return@withContext Result.failure(IllegalStateException("No valid API key provided."))
        }

        try {
            onStatusUpdate("Queuing video generation request...")
            val jsonBody = JSONObject().apply {
                put("model", MODEL_OMNI_VIDEO)
                put("input", prompt)
                put("background", false)
                put("store", true)
                put("stream", false)
                val format = JSONObject().apply {
                    put("type", "video")
                    put("aspect_ratio", aspectRatio)
                    put("duration", duration)
                }
                put("response_format", format)
            }

            val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())
            val createReq = Request.Builder()
                .url("$BASE_URL/interactions?key=$apiKey")
                .post(requestBody)
                .build()

            val createRes = client.newCall(createReq).execute()
            val createResBody = createRes.body?.string() ?: ""

            if (!createRes.isSuccessful) {
                return@withContext Result.failure(Exception("Interactions API error: $createResBody"))
            }

            val interactionJson = JSONObject(createResBody)
            val interactionId = interactionJson.optString("id")
            var status = interactionJson.optString("status")

            onStatusUpdate("Synthesizing frames ($status)...")
            var attempts = 0
            var finalResponseJson = interactionJson

            while ((status == "in_progress" || status == "queued") && attempts < 20) {
                delay(4000)
                attempts++
                onStatusUpdate("Rendering video ($status, step $attempts)...")

                val pollReq = Request.Builder()
                    .url("$BASE_URL/interactions/$interactionId?key=$apiKey")
                    .get()
                    .build()
                val pollRes = client.newCall(pollReq).execute()
                val pollBody = pollRes.body?.string() ?: ""
                if (pollRes.isSuccessful) {
                    finalResponseJson = JSONObject(pollBody)
                    status = finalResponseJson.optString("status")
                }
            }

            val steps = finalResponseJson.optJSONArray("steps")
            var videoData: String? = null
            if (steps != null) {
                for (i in 0 until steps.length()) {
                    val step = steps.optJSONObject(i)
                    val contents = step?.optJSONArray("content")
                    if (contents != null) {
                        for (j in 0 until contents.length()) {
                            val content = contents.optJSONObject(j)
                            if (content?.optString("type") == "video") {
                                videoData = content.optString("data")
                                if (videoData.isNullOrBlank()) {
                                    videoData = content.optString("uri")
                                }
                                if (!videoData.isNullOrBlank()) break
                            }
                        }
                    }
                }
            }

            if (!videoData.isNullOrBlank()) {
                onStatusUpdate("Video rendering completed!")
                Result.success(videoData)
            } else {
                Result.failure(Exception("Video synthesis finished with status '$status' but no video data returned."))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in generateVideo: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Transcribe user voice audio using gemini-3.5-transcribe
     */
    suspend fun transcribeAudio(
        audioBytes: ByteArray,
        mimeType: String = "audio/mp4"
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (!hasApiKey()) {
            return@withContext Result.failure(IllegalStateException("No valid API key provided for transcription."))
        }

        try {
            val base64Audio = Base64.encodeToString(audioBytes, Base64.NO_WRAP)
            val jsonBody = JSONObject().apply {
                val contents = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", "Transcribe this audio recording into clear YouTube narration text with punctuation and paragraphs:")
                            })
                            put(JSONObject().apply {
                                val inlineData = JSONObject().apply {
                                    put("mimeType", mimeType)
                                    put("data", base64Audio)
                                }
                                put("inlineData", inlineData)
                            })
                        }
                        put("parts", parts)
                    }
                    put(contentObj)
                }
                put("contents", contents)
            }

            val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$BASE_URL/models/$MODEL_TRANSCRIBE:generateContent?key=$apiKey")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e(TAG, "Transcription error: $responseBody")
                return@withContext Result.failure(Exception("HTTP ${response.code}: $responseBody"))
            }

            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            val candidate = candidates?.optJSONObject(0)
            val content = candidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text")

            if (!text.isNullOrBlank()) {
                Result.success(text)
            } else {
                Result.failure(Exception("Empty transcription result from Gemini"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in transcribeAudio: ${e.message}", e)
            Result.failure(e)
        }
    }
}
