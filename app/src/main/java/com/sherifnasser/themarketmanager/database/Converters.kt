package com.sherifnasser.themarketmanager.database

import androidx.room.TypeConverter
import java.util.Date

class Converters{
    @TypeConverter
    fun fromDateToTimestamp(date:Date)=date.time

    @TypeConverter
    fun fromTimestampToDate(dateLong:Long)=Date(dateLong)
}