package com.example.ch10

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.ch10.ui.theme.Ch10Theme
import dagger.hilt.android.AndroidEntryPoint

// @AndroidEntryPoint membuat Hilt siap menyuntik dependency ke Activity ini.
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Ch10Theme { KabarKiniApp() }
        }
    }
}
