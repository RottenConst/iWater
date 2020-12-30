package ru.iwater.youwater.iwaterlogistic.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.iwater.youwater.iwaterlogistic.util.Logger

/**
 * Базовая активность приложения от которой наследуются все остальные
 **/
open class BaseActivity : AppCompatActivity() {

    private lateinit var logger: Logger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger = Logger()
        lifecycle.addObserver(logger)
    }
}