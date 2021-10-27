package com.example.data.room.product

import androidx.room.*
import com.example.data.room.wastereport.WasteReportEntity
import io.reactivex.Single

/**
 * Interface dao for CRUD actions on the product data model.
 *
 */
@Dao
interface ProductDao {

    /**
     * Retrieves all products from the database.
     *
     * @return list of products as a result from the query.
     */
    @Query("SELECT * FROM ProductEntity ORDER BY expiration_date")
    fun getAll(): Single<List<ProductEntity>>

    /**
     * Retrieves all products from the database with the given uid's in the parameters.
     *
     * @param uids unique id's of products.
     * @return list of products as a result from the query.
     */
    @Query("SELECT * FROM ProductEntity WHERE uid IN (:uids)")
    fun loadAllByIds(uids: IntArray): List<ProductEntity>

    /**
     * Retrieve single product from the database with the given product name in
     * the parameters
     *
     * @param name name of the product
     * @return single product as a result from the query.
     */
    @Query("SELECT * FROM ProductEntity WHERE product_name LIKE :name")
    fun findByName(name: String): ProductEntity

    /**
     * Insert the given product from the parameters into the database.
     *
     * @param product product to be inserted.
     * @return the newly generated uid used for checking if the insertion has succeeded.
     */
    @Insert
    fun insert(product: ProductEntity): Long

    /**
     * Update the given product from the parameters.
     *
     * @param product product to be updated
     */
    @Update
    fun update(product: ProductEntity): Int

    /**
     * Delete the given product from the database.
     *
     * @param product product to be deleted.
     */
    @Delete
    fun delete(product: ProductEntity): Int


    /**
     * Insert the given wasteReportProduct into the database
     *
     * @param wasteReportProduct WasteReportProduct to be inserted
     */
    @Insert
    fun insertWasteReportProduct(wasteReportProduct: WasteReportEntity)


    /**
     * Gets amount of eaten vittles
     *
     * @param date from this date up to now the amount is calculated
     * @return amount of eaten vittles
     */
    @Query("SELECT COUNT(waste_type) FROM WasteReportEntity WHERE waste_type = 'EATEN' AND creation_date >= :date")
    fun getCountEatenProducts(date: Long): Single<Int>

    /**
     * Gets amount of expired vittles
     *
     * @param date from this date up to now the amount is calculated
     * @return amount of expired vittles
     * */
    @Query("SELECT COUNT(waste_type) FROM WasteReportEntity WHERE waste_type = 'THROWN_AWAY' AND creation_date >= :date")
    fun getCountExpiredProducts(date: Long): Single<Int>

    /**
    Gets the waste report products
     *
     * @param date From this date up to now the data should be given
     * @return List of vittles
     *
     */
    @Query("SELECT * FROM WasteReportEntity WHERE creation_date >= :date")
    fun getWasteReportProducts(date: Long): Single<List<WasteReportEntity>>
}