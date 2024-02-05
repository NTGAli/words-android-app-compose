package com.ntg.vocabs.playback

import java.io.File

interface AudioPlayer {
    fun playFile(file: File)
    fun stop()
    fun isPlaying(): Boolean
}