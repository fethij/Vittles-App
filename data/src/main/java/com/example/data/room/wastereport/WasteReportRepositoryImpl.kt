package com.example.data.room.wastereport

import com.example.data.room.product.ProductDao
import com.example.domain.wasteReport.WasteReportRepository
import com.example.domain.wasteReport.WasteReportProduct
import io.reactivex.Completable
import io.reactivex.Single

/**
 * This is the implementation of the WasteReportRepository in the Domain layer.
 *
 *
 * @property productDao Reference to the ProductDao.
 * @property mapper The mapper used to map the wasteReportProduct data class.
 */
class WasteReportRepositoryImpl(private val productDao: ProductDao,
                                private val mapper: WasteReportModelMapper
) :
    WasteReportRepository {

    /** {@inheritDoc} */
    override fun post(wasteReportProduct: WasteReportProduct): Completable = Completable.fromAction { productDao
        .insertWasteReportProduct(mapper.toEntity(wasteReportProduct))
    }

    /** {@inheritDoc} */
    override fun getCountEatenProducts(date: Long): Single<Int> {
        return productDao.getCountEatenProducts(date)

    }

    /** {@inheritDoc} */
    override fun getCountExpiredProducts(date: Long): Single<Int> {
        return productDao.getCountExpiredProducts(date)
    }

    /** {@inheritDoc} */
    override fun getWasteReportProducts(date: Long): Single<List<WasteReportProduct>> {
        return productDao.getWasteReportProducts(date)
            .map { it.map(mapper::fromEntity) }
    }

}