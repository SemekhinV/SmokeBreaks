package com.smokebreakbuddy.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.smokebreakbuddy.data.local.dao.BreakInvitationDao
import com.smokebreakbuddy.data.local.dao.BreakSessionDao
import com.smokebreakbuddy.data.local.dao.GroupDao
import com.smokebreakbuddy.data.local.dao.UserDao
import com.smokebreakbuddy.data.local.converters.Converters
import com.smokebreakbuddy.data.model.BreakInvitation
import com.smokebreakbuddy.data.model.BreakSession
import com.smokebreakbuddy.data.model.Group
import com.smokebreakbuddy.data.model.User

@Database(
    entities = [
        User::class,
        Group::class,
        BreakInvitation::class,
        BreakSession::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SmokeBreakBuddyDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun groupDao(): GroupDao
    abstract fun breakInvitationDao(): BreakInvitationDao
    abstract fun breakSessionDao(): BreakSessionDao
    
    companion object {
        const val DATABASE_NAME = "smoke_break_buddy_db"
        
        @Volatile
        private var INSTANCE: SmokeBreakBuddyDatabase? = null
        
        fun getDatabase(context: Context): SmokeBreakBuddyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SmokeBreakBuddyDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
