package com.example.data.room.productdictionary

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Product dictionary data model.
 *
 * @property barcode Barcode of the product.
 * @property productName Name of the product.
 */
@Entity
data class ProductDictionaryEntity (
    @PrimaryKey @ColumnInfo(name ="gtin") val barcode: String,
    @ColumnInfo(name = "product_name") val productName: String?
)