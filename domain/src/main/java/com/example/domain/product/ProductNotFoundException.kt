package com.example.domain.product

/**
 * Exception thrown when a product could not be found in an API.
 *
 *
 * @property barcode GTIN of the product that could not be found in the API.
 */
class ProductNotFoundException(val barcode: String) : Exception()