package com.example.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.room.productdictionary.BarcodeDao
import com.example.data.room.productdictionary.ProductDictionaryEntity
import com.example.data.room.product.ProductDao
import com.example.data.room.product.ProductEntity
import com.example.data.room.wastereport.WasteReportEntity

/**
 * Creates the ProductDao.
 *
 * @param context The application context.
 * @return The ProductDao.
 */
fun createProductDaoImpl(context: Context): ProductDao {
    return AppDatabase.getDatabase(context).productDao()
}

/**
 * Creates the BarcodeDao.
 *
 * @param context The application context.
 * @return The BarcodeDao.
 */
fun createBarcodeDaoImpl(context: Context): BarcodeDao {
    return AppDatabase.getDatabase(context).barcodeDao()
}

/**
 * Room database singleton implementation. Used for CRUD in the database. Use the
 * getDatabase method to retrieve the singleton instance to select the needed dao.
 *

 */
@Database(entities = [ProductEntity::class, WasteReportEntity::class,  ProductDictionaryEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Retrieves the ProductDao
     *
     * @return The ProductDao.
     */
    abstract fun productDao(): ProductDao

    abstract fun barcodeDao(): BarcodeDao

    companion object {

        /**
         * Instance of the database.
         */
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * If instance is not created, creates a new database instance. Otherwise it will
         * return the existing instance.
         *
         * @param context Application context.
         * @return Singleton instance of database.
         */
        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vittles_database"
                ).allowMainThreadQueries().build()
                INSTANCE = instance
                return instance
            }
        }

    }
}
