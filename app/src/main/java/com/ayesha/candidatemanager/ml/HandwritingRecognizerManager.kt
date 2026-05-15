package com.ayesha.candidatemanager.ml

import com.google.mlkit.vision.digitalink.DigitalInkRecognition
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModel
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModelIdentifier
import com.google.mlkit.vision.digitalink.DigitalInkRecognizer
import com.google.mlkit.vision.digitalink.DigitalInkRecognizerOptions
import com.google.mlkit.vision.digitalink.Ink
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import kotlinx.coroutines.tasks.await

class HandwritingRecognizerManager {

    private var recognizer: DigitalInkRecognizer? = null

    suspend fun initModel(): Boolean {
        val modelIdentifier = try {
            DigitalInkRecognitionModelIdentifier.fromLanguageTag("en-US")
        } catch (e: Exception) {
            null
        } ?: return false

        val model = DigitalInkRecognitionModel.builder(modelIdentifier).build()
        val remoteModelManager = RemoteModelManager.getInstance()
        
        return try {
            remoteModelManager.download(model, DownloadConditions.Builder().build()).await()
            recognizer = DigitalInkRecognition.getClient(
                DigitalInkRecognizerOptions.builder(model).build()
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun recognize(ink: Ink): String {
        return try {
            val result = recognizer?.recognize(ink)?.await()
            result?.candidates?.firstOrNull()?.text ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    fun close() {
        recognizer?.close()
    }
}
