package com.ntg.mywords.playback

import java.io.File

interface AudioPlayer {
    fun playFile(file: File)
    fun stop()
    fun isPlaying(): Boolean
}