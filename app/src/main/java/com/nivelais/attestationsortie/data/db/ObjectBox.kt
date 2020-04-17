package com.nivelais.attestationsortie.data.db

import android.content.Context
import io.objectbox.BoxStore

object ObjectBox {

    private const val DB_NAME = "attestationsortie.db"

    /**
     * Store who will contain all of our database
     */
    lateinit var boxStore : BoxStore
        private set

    fun init(context: Context): BoxStore {
        if (ObjectBox::boxStore.isInitialized && !boxStore.isClosed)
            return boxStore

        boxStore = MyObjectBox.builder()
            .androidContext(context.applicationContext)
            .name(DB_NAME)
            .build()

        return boxStore
    }

}