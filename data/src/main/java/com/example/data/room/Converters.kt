package com.example.data.room

import androidx.room.TypeConverter
import org.joda.time.DateTime

/**
 * The converters in this class are used to convert non-persistable types in data models
 * to persistable types.
 *

 */
class Converters {
    /**
     * Converts long value (time in ms) to a DateTime object.
     *
     * @param value Long value (time in ms).
     * @return DateTime object.
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): DateTime? {
        return DateTime(value)
    }

    /**
     * Converts a DateTime object to a long value (time in ms).
     *
     * @param date DateTime object.
     * @return Long value (time in ms).
     */
    @TypeConverter
    fun dateToTimestamp(date: DateTime?): Long? {
        return date!!.millis
    }
}