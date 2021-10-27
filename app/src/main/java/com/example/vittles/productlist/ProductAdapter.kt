package com.example.vittles.productlist

import android.content.Context
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.domain.product.Product
import com.example.vittles.R
import kotlinx.android.synthetic.main.item_product.view.*
import javax.inject.Inject

/**
 * Binds app-specific data to views that are displayed in the RecyclerView.
 *
 *
 * @property products The list of products that should be displayed in the RecyclerView.
 * @property removeClickListener Click listener used for the delete button.
 */
class ProductAdapter @Inject constructor(initialProducts: List<Product>, private val itemClickListener: (Product) -> Unit, private val removeClickListener: (Product) -> Unit) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    /**
     * Context of the activity.
     */
    lateinit var context: Context
    /**
     * List of products in the recycler view.
     */
    var products: List<Product> = initialProducts

    /**
     * Creates a new ViewHolder.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return Returns the new ViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_product, parent, false)
        )
    }

    /**
     * Gets the amount of items in the ListView.
     *
     * @return The amount of items.
     */
    override fun getItemCount(): Int {
        return products.size
    }

    /**
     * Updates the contents of the itemView to reflect the item at the given position.
     *
     * @param holder The ViewHolder that should be updated.
     * @param position The position of the item to be updated.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(products[position], itemClickListener, removeClickListener)
    }

    /**
     * Represents an element in the RecyclerView.
     *
     * @param itemView The xml file that represents an item.
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        /**
         * Sets the values of the itemView to the product values.
         *
         * @param product The product that is bound to the itemView.
         */
        fun bind(product: Product, itemClickListener: (Product) -> Unit, removeClickListener: (Product) -> Unit) {
            itemView.productId.text = product.uid.toString()

            // Assign daysLeft string to 99+ or 99- when greater or smaller than 99 or -99.
            val daysLeft: String = when {
                product.getDaysRemaining() > context.getString(R.string.maxDaysRemaining).toInt() -> context.getString(R.string.maxDaysRemaining) + "+"
                product.getDaysRemaining() < -context.getString(R.string.maxDaysRemaining).toInt() -> context.getString(R.string.maxDaysRemaining) + "-"
                else -> product.getDaysRemaining().toString()
            }

            // LayoutParams for setting margins programmatically
            val lp = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            // Last product in list, remove decorator and add extra bottom-margin
            if(products[products.lastIndex] == product) {
                itemView.borderDecorator.visibility = View.INVISIBLE
                val unbounded = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                itemView.measure(unbounded, unbounded)
                val bottomMargin = itemView.measuredHeight
                lp.setMargins(0, 0, 0, bottomMargin)
            } else {
                itemView.borderDecorator.visibility = View.VISIBLE
                lp.setMargins(0, 0, 0, 0)
            }
            itemView.layoutParams = lp

            // Set values in layout
            itemView.tvName.text = product.productName
            itemView.tvDate.text = context.resources.getString(
                R.string.expiration_format,
                product.expirationDate.dayOfMonth.toString(),
                product.expirationDate.monthOfYear.toString(),
                product.expirationDate.year.toString())
            itemView.tvDaysLeft.text = daysLeft
            itemView.btnRemove.setOnClickListener{ removeClickListener(product) }
            itemView.setOnClickListener{ itemClickListener(product) }

            // Set the colors
            itemView.ivColor.setColorFilter(ContextCompat.getColor(context, product.indicationColor!!), PorterDuff.Mode.MULTIPLY) //Circle
            itemView.tvDaysLeft.setTextColor(ContextCompat.getColor(context, product.indicationColor!!)) //DaysLeft number
        }
    }
}