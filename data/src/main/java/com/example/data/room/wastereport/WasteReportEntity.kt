package com.example.data.room.wastereport

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.DateTime

/**
 * WasteReportEntity data model.
 *

 *
 * @property uid unique id used as surrogate key.
 * @property creationDate date of when product was added to database.
 * @property wasteType eaten or deleted
 */
@Entity
data class WasteReportEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int?,
    @ColumnInfo(name = "creation_date") val creationDate: DateTime,
    @ColumnInfo(name = "waste_type") val wasteType: String
)