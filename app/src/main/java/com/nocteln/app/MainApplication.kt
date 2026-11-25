package com.nocteln.app

import android.app.Application
import com.nocteln.app.data.buddy.BuddyRepository
import com.nocteln.app.data.buddy.buddyDataStore

class MainApplication : Application() {
    lateinit var buddyRepository: BuddyRepository

    override fun onCreate() {
        super.onCreate()

        buddyRepository = BuddyRepository(buddyDataStore)
    }
}