package com.smokebreakbuddy.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.smokebreakbuddy.data.model.BreakResponse
import com.smokebreakbuddy.data.model.UserPreferences
import java.util.Date

class Converters {
    
    private val gson = Gson()
    
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }
    
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
    
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }
    
    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }
    
    @TypeConverter
    fun fromIntList(value: List<Int>): String {
        return gson.toJson(value)
    }
    
    @TypeConverter
    fun toIntList(value: String): List<Int> {
        val listType = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }
    
    @TypeConverter
    fun fromBreakResponseList(value: List<BreakResponse>): String {
        return gson.toJson(value)
    }
    
    @TypeConverter
    fun toBreakResponseList(value: String): List<BreakResponse> {
        val listType = object : TypeToken<List<BreakResponse>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }
    
    @TypeConverter
    fun fromUserPreferences(preferences: UserPreferences): String {
        return gson.toJson(preferences)
    }
    
    @TypeConverter
    fun toUserPreferences(preferencesJson: String): UserPreferences {
        return gson.fromJson(preferencesJson, UserPreferences::class.java) ?: UserPreferences()
    }
}
