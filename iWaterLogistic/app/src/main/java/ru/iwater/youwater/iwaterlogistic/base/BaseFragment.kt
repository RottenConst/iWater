package ru.iwater.youwater.iwaterlogistic.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import ru.iwater.youwater.iwaterlogistic.util.Logger

open class BaseFragment : Fragment() {

    private lateinit var logger: Logger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger = Logger()
        lifecycle.addObserver(logger)
    }
}