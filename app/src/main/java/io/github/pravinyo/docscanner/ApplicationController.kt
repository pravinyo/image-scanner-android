package io.github.pravinyo.docscanner

import android.app.Application
import kotlinx.coroutines.IO_PARALLELISM_PROPERTY_NAME
import timber.log.Timber

class ApplicationController : Application(){

    override fun onCreate() {
        super.onCreate()

        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }

        System.setProperty(IO_PARALLELISM_PROPERTY_NAME, Int.MAX_VALUE.toString())
    }
}