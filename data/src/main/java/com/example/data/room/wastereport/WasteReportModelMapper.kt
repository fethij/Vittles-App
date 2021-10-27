package com.example.data.room.wastereport

import com.example.data.room.wastereport.WasteReportEntity
import com.example.domain.wasteReport.WasteReportProduct
import javax.inject.Inject

/**
 * Maps between Room database entity and model.
 *
 */
class WasteReportModelMapper @Inject constructor() {

    /**
     * Maps wasteProduct entity to product model.
     *
     * @param from The WasteProduct entity.
     */
    fun fromEntity(from: WasteReportEntity) = WasteReportProduct(from.uid, from.creationDate, from.wasteType)

    /**
    * Maps wasteProduct model to wasteProduct entity.
    *
    * @param from The wasteProduct model.
    */
    fun toEntity(from: WasteReportProduct) =
        WasteReportEntity(
            from.uid,
            from.creationDate,
            from.wasteType
        )
}