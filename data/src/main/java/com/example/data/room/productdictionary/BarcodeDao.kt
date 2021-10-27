package com.example.data.room.productdictionary

import androidx.room.*
import io.reactivex.Maybe

/**
 * Interface dao for CRUD actions on the product dictionary data model.
 *
 */
@Dao
interface BarcodeDao {

    /**
     * Retrieves entity by searching the barcode.
     *
     * @param barcode The barcode query to search.
     */
    @Query("SELECT * FROM ProductDictionaryEntity WHERE gtin LIKE :barcode")
    fun getProductDictionary(barcode: String): Maybe<ProductDictionaryEntity>

    /**
     * Inserts entity into local database.
     *
     * @param productDictionaryEntity The entity to insert.
     * @return Long value that returns the barcode if succeeded, otherwise it returns a negative value.
     */
    @Insert
    fun insertProductDictionary(productDictionaryEntity: ProductDictionaryEntity)

    /**
     * Updates entity in the local database.
     *
     * @param productDictionaryEntity The entity to update.
     * @return Long value that returns the barcode if succeeded, otherwise it returns a negative value.
     */
    @Update
    fun updateProductDictionary(productDictionaryEntity: ProductDictionaryEntity)

    /**
     * Deletes all entities in the local database.
     *
     * @return Long value that returns the barcode if succeeded, otherwise it returns a negative value.
     */
    @Query("DELETE FROM ProductDictionaryEntity WHERE 1")
    fun emptyProductDictionary()
}