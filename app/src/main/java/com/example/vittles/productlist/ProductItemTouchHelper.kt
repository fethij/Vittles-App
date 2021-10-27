package com.example.vittles.productlist

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.vittles.R
import kotlinx.android.synthetic.main.item_product.view.*
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.example.domain.product.Product
import com.example.vittles.enums.DeleteType


/**
 * Creates an ItemTouchHelper and defines what happens on swiping.
 *
 *
 * @param products The List of products from the recycler View.
 * @param context Application Context.
 * @param safeDelete Safe delete method callback.
 */
class ProductItemTouchHelper(private val products: List<Product>, var context: Context,
                             private val safeDelete: (Product, DeleteType) -> Unit
): ItemTouchHelper.Callback() {


    /**
     * Enum to determine where the swipe icon should be located.
     *
     */
    enum class IconLocation {
        /** Left side of the swipe bar */
        LEFT,
        /** Right side of the swipe bar */
        RIGHT
    }

    /**
     * Called when ItemTouchHelper wants to move the dragged item from its old position to the new position.
     *
     * @param recyclerView The RecyclerView to which ItemTouchHelper is attached to.
     * @param viewHolder The ViewHolder which is being dragged by the user.
     * @param target The ViewHolder over which the currently active item is being dragged.
     * @return 	True if the viewHolder has been moved to the adapter position of target.
     */
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    /**
     * Return a composite flag which defines the enabled move directions in each state (idle, swiping, dragging).
     *
     * @param recyclerView The RecyclerView to which ItemTouchHelper is attached.
     * @param viewHolder The ViewHolder for which the movement information is necessary.
     * @return Flags specifying which movements are allowed on this ViewHolder.
     */
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {

        return makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
    }

    /**
     * Called when a ViewHolder is swiped by the user.
     *
     * @param viewHolder The ViewHolder which has been swiped by the user.
     * @param direction  The direction to which the ViewHolder is swiped (8 = to the left and 4 = to the right).
     */
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val product = products[viewHolder.adapterPosition]

        //Must be var. For some reason it will always be THROWN_AWAY when it's val.
        val deleteType =  if (direction == 8){
            DeleteType.EATEN
        }else{
            DeleteType.THROWN_AWAY
        }

        safeDelete(product, deleteType)
    }

    /**
     * Called by the ItemTouchHelper when the user interaction with an element is over.
     *
     * @param recyclerView  The RecyclerView which is controlled by the ItemTouchHelper.
     * @param viewHolder The View that was interacted by the user.
     */
    override fun clearView(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ) {
        removeSwipeLines(viewHolder,recyclerView)
        super.clearView(recyclerView, viewHolder)
    }


    /**
     * Customize how your View's respond to user interactions.
     *
     * @param c The canvas which RecyclerView is drawing its children.
     * @param recyclerView The RecyclerView to which ItemTouchHelper is attached to.
     * @param viewHolder  The ViewHolder which is being interacted by the User.
     * @param dX The amount of horizontal displacement caused by user's action.
     * @param dY The amount of vertical displacement caused by user's action.
     * @param actionState The type of interaction on the View.
     * @param isCurrentlyActive True if this view is currently being controlled by the user or false it is simply animating back to its original state.
     */
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        val isCanceled = dX == 0f && !isCurrentlyActive

        if (isCanceled) {
            removeSwipeLines(viewHolder, recyclerView)
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            removeDecoratorOnSwipedProduct(viewHolder.itemView)
            return
        }

        showSwipeLines(viewHolder, recyclerView)

        if (dX < 0) {
            setBackgroundColor(c, viewHolder, ContextCompat.getColor(context, R.color.red))
            if (dX > -viewHolder.itemView.width/3) {
                drawIcon(c, viewHolder, context.getDrawable(R.drawable.ic_delete_1)!!, IconLocation.RIGHT)
            } else if (dX < -viewHolder.itemView.width/3 && dX > -viewHolder.itemView.width/2) {
                drawIcon(c, viewHolder, context.getDrawable(R.drawable.ic_delete_2)!!, IconLocation.RIGHT)
            } else if (dX < -viewHolder.itemView.width/2) {
                drawIcon(c, viewHolder, context.getDrawable(R.drawable.ic_delete_3)!!, IconLocation.RIGHT)
            }
        }else{
            setBackgroundColor(c, viewHolder, ContextCompat.getColor(context, R.color.green))
            if (dX < viewHolder.itemView.width/3) {
                drawIcon(c, viewHolder, context.getDrawable(R.drawable.ic_eaten_1)!!, IconLocation.LEFT)
            } else if (dX > viewHolder.itemView.width/3 && dX < viewHolder.itemView.width/2) {
                drawIcon(c, viewHolder, context.getDrawable(R.drawable.ic_eaten_2)!!, IconLocation.LEFT)
            } else if (dX > viewHolder.itemView.width/2) {
                drawIcon(c, viewHolder, context.getDrawable(R.drawable.ic_eaten_3)!!, IconLocation.LEFT)
            }
        }
        
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    /**
     * Checks if swiped product is last product and removes the decorator
     */
    private fun removeDecoratorOnSwipedProduct(view: View){
        val itemView: View = view
        val product: Product? = products.find { product -> product.uid == view.productId.text.toString().toInt() }

        if(products[products.lastIndex] == product) {
            itemView.borderDecorator.visibility = View.INVISIBLE
        } else {
            itemView.borderDecorator.visibility = View.VISIBLE
        }
    }

    /**
     * Set the animation background color of a ViewHolder.
     *
     * @param c The canvas which RecyclerView is drawing its children.
     * @param viewHolder The ViewHolder which is being interacted by the User.
     * @param color The color the background should have.
     */
    private fun setBackgroundColor(c:Canvas, viewHolder: RecyclerView.ViewHolder, color: Int) {
        val background = ColorDrawable()
        background.color = color
        background.setBounds(viewHolder.itemView.left, viewHolder.itemView.top,
            viewHolder.itemView.right, viewHolder.itemView.bottom)
        background.draw(c)
    }

    /**
     * Set an icon to the animation of a ViewHolder.
     *
     * @param c The canvas which RecyclerView is drawing its children.
     * @param viewHolder The ViewHolder which is being interacted by the User.
     * @param icon icon the animation should have.
     */
    private fun drawIcon(c: Canvas, viewHolder: RecyclerView.ViewHolder, icon: Drawable, iconLocation: IconLocation){

        val itemView = viewHolder.itemView

        val itemHeight = itemView.bottom - itemView.top
        val iconTop = itemView.top + (itemHeight - icon.intrinsicHeight) / 2
        val iconMargin = (itemHeight - icon.intrinsicHeight) / 2
        val iconBottom = iconTop + icon.intrinsicHeight
        var iconLeft = 0
        var iconRight = 0

        when(iconLocation){
            IconLocation.RIGHT -> {
                iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
                iconRight = itemView.right - iconMargin
            }
            IconLocation.LEFT -> {
                iconLeft = itemView.left + iconMargin
                iconRight = itemView.left + iconMargin + icon.intrinsicWidth
            }
        }

        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
        icon.draw(c)
    }

    /**
     * Set the lines back to the original state.
     *
     * @param viewHolder The ViewHolder which is being interacted by the User.
     * @param recyclerView The RecyclerView to which ItemTouchHelper is attached to.
     */
    private fun removeSwipeLines(viewHolder: RecyclerView.ViewHolder, recyclerView: RecyclerView) {
        if(viewHolder.layoutPosition > 0) {
            val viewHolderAbove =
                recyclerView.findViewHolderForLayoutPosition(viewHolder.layoutPosition - 1)
            viewHolderAbove!!.itemView.borderDecorator.visibility = View.VISIBLE

        }
        viewHolder.itemView.borderDecorator.visibility = View.VISIBLE
        viewHolder.itemView.ivTest.visibility = View.INVISIBLE
    }


    /**
     *
     * Customize the lines of the ItemView while swiping.
     *
     * @param viewHolder The ViewHolder which is being interacted by the User.
     * @param recyclerView The RecyclerView to which ItemTouchHelper is attached to.
     */
    private fun showSwipeLines(viewHolder: RecyclerView.ViewHolder, recyclerView: RecyclerView){
        if (viewHolder.layoutPosition != 0) {
            val viewHolderAbove =
                recyclerView.findViewHolderForLayoutPosition(viewHolder.layoutPosition - 1)
            viewHolderAbove!!.itemView.borderDecorator.visibility = View.INVISIBLE
        }
        viewHolder.itemView.borderDecorator.visibility = View.INVISIBLE
        viewHolder.itemView.ivTest.visibility = View.VISIBLE
    }
}