package com.example.domain.wasteReport

import org.joda.time.DateTime

/**
 * The WasteReportProduct entity.
 *

 *
 * @property uid The Id of the wasteReportProduct (used as primary key in the database).
 * @property creationDate The date the wasteReportProduct was added to the application.
 * @property wasteType The waste type of the wasteReportProduct
 */
data class WasteReportProduct(
    val uid: Int?,
    val creationDate: DateTime,
    var wasteType: String
)