package com.example.ch10

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// Titik masuk graph dependency Hilt — semua module di package di/ dirakit di sini.
@HiltAndroidApp
class MainApplication : Application()
