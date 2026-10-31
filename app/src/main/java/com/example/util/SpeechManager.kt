package com.example.util

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class SpeechManager(context: Context) : TextToSpeech.OnInitListener {
  private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
  private var isReady = false
  private var isMuted = false

  override fun onInit(status: Int) {
    if (status == TextToSpeech.SUCCESS) {
      val result = tts?.setLanguage(Locale.US)
      isReady = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
      tts?.setSpeechRate(0.95f)
    }
  }

  fun setMuted(muted: Boolean) {
    isMuted = muted
    if (muted) {
      tts?.stop()
    }
  }

  fun isMuted(): Boolean = isMuted

  fun speak(text: String, flush: Boolean = false) {
    if (!isReady || isMuted || text.isBlank()) return
    val queueMode = if (flush) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
    tts?.speak(text, queueMode, null, "tts_${System.currentTimeMillis()}")
  }

  fun stop() {
    tts?.stop()
  }

  fun shutdown() {
    tts?.stop()
    tts?.shutdown()
    tts = null
  }
}
