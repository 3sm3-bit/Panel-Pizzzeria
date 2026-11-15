package com.pizzzeria.panelpizzzeria.repository.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pizzzeria.panelpizzzeria.repository.network.domain.ModificationGroupResponse

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromPriceMap(map: Map<String, String>?): String? {
        return gson.toJson(map)
    }

    @TypeConverter
    fun toPriceMap(value: String?): Map<String, String>? {
        if (value == null) return null
        val type = object : TypeToken<Map<String, String>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromModificationGroupList(list: List<ModificationGroupResponse>?): String? {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toModificationGroupList(value: String?): List<ModificationGroupResponse>? {
        if (value == null) return null
        val type = object : TypeToken<List<ModificationGroupResponse>>() {}.type
        return gson.fromJson(value, type)
    }
}
