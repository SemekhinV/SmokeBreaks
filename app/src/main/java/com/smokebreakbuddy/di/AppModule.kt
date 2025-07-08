package com.smokebreakbuddy.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.smokebreakbuddy.data.local.SmokeBreakBuddyDatabase
import com.smokebreakbuddy.data.local.dao.BreakInvitationDao
import com.smokebreakbuddy.data.local.dao.BreakSessionDao
import com.smokebreakbuddy.data.local.dao.GroupDao
import com.smokebreakbuddy.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseMessaging(): FirebaseMessaging = FirebaseMessaging.getInstance()

    @Provides
    @Singleton
    fun provideSmokeBreakBuddyDatabase(
        @ApplicationContext context: Context
    ): SmokeBreakBuddyDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            SmokeBreakBuddyDatabase::class.java,
            SmokeBreakBuddyDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideUserDao(database: SmokeBreakBuddyDatabase): UserDao = database.userDao()

    @Provides
    fun provideGroupDao(database: SmokeBreakBuddyDatabase): GroupDao = database.groupDao()

    @Provides
    fun provideBreakInvitationDao(database: SmokeBreakBuddyDatabase): BreakInvitationDao = database.breakInvitationDao()

    @Provides
    fun provideBreakSessionDao(database: SmokeBreakBuddyDatabase): BreakSessionDao = database.breakSessionDao()
}
