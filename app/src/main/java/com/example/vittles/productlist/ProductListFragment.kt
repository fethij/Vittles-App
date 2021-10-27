package com.example.vittles.productlist

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.domain.product.Product
import com.example.vittles.R
import com.example.vittles.enums.DeleteType
import com.example.vittles.services.popups.PopupBase
import com.example.vittles.services.popups.PopupButton
import com.example.vittles.services.popups.PopupManager
import com.example.vittles.services.sorting.SortMenu
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_productlist.*
import javax.inject.Inject

/**
 * Fragment class for the main activity. This is the fragment that shows the list of products.
 *
 */
class ProductListFragment : DaggerFragment(), ProductListContract.View {

    /**
     * The presenter of the fragment.
     */
    @Inject
    lateinit var presenter: ProductListPresenter

    /** The vibration manager used for vibration when a product is eaten or removed. */
    private lateinit var vibrator: Vibrator

    /** Arguments passed to the fragment */
    private val productArgs: ProductListFragmentArgs by navArgs()
    /** @suppress */
    private lateinit var itemTouchHelper: ItemTouchHelper
    /** @suppress */
    private lateinit var undoSnackbar: Snackbar
    /** @suppress */
    private lateinit var deletedProduct: Product
    /** @suppress */
    private lateinit var deletedProductDeleteType: DeleteType
    /** @suppress */
    private var deletedProductIndex: Int = 0
    /** @suppress */
    private var products = mutableListOf<Product>()
    /** @suppress */
    private var filteredProducts = products
    /** @suppress */
    private val productAdapter = ProductAdapter(products, this::onItemViewClicked, this::onRemoveButtonClicked)
    /** @suppress */
    private val sortMenu = SortMenu(products, productAdapter)

    /** {@inheritDoc} */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        with(presenter) { start(this@ProductListFragment) }
        vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        return inflater.inflate(R.layout.fragment_productlist, container, false)
    }

    /** {@inheritDoc} */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onSearchBarClosed()
        initViews()
    }

    /** {@inheritDoc} */
    override fun onDestroy() {
        onSearchBarClosed()
        presenter.destroy()
        super.onDestroy()
    }

    /**
     * Initializes the RecyclerView and sets EventListeners.
     *
     */
    override fun initViews() {
        setListeners()

        rvProducts.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvProducts.adapter = productAdapter

        // Set searchView textColor
        val id =
            svSearch.context.resources.getIdentifier("android:id/search_src_text", null, null)
        val textView = svSearch.findViewById(id) as TextView
        textView.setTextColor(Color.BLACK)

        setListeners()

        initUndoSnackbar()

        setItemTouchHelper()

        if (productArgs.withSearch) {
            onSearchBarOpened()
        }
    }

    /**
     * Called when the mainActivity starts.
     * Re-populates the RecyclerView.
     *
     */
    override fun onResume() {
        super.onResume()
        onPopulateRecyclerView()

        // Set sortBtn text to currentSortingType
        btnSort.text = getString(sortMenu.currentSortingType.textId)
    }

    /**
     * Sets all necessary event listeners on ui elements.
     *
     */
    override fun setListeners() {
        sortLayout.setOnClickListener { onSortMenuOpened() }

        ibtnSearch.setOnClickListener { onSearchBarOpened() }

        svSearch.setOnCloseListener { onSearchBarClosed(); false }

        svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                filter(newText); return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                filter(query); return false
            }

        })

        imgbtnCloseSearch.setOnClickListener { onSearchBarClosed() }
    }


    /**
     * Deletes product and shows a toast to undo the deletion.
     *
     * @param product The product to delete.
     * @param deleteType The deleteType: eaten, thrown_away or removed.
     */
    // Deprecation suppressed because we use an old API version
    @Suppress("DEPRECATION")
    override fun onSafeDeleteProduct(product: Product, deleteType: DeleteType) {

        if (undoSnackbar.isShown) {
            presenter.deleteProduct(deletedProduct, deletedProductDeleteType)
        }
        //set deleted product
        deletedProduct = product
        deletedProductIndex = products.indexOf(product)
        deletedProductDeleteType = deleteType

        products.remove(product)

        //checks the vibration setting then if it will give vibration feedback
        if (vibrator.hasVibrator() && presenter.getVibrationSetting()) {
            vibrator.vibrate(50)
        }

        //It crashes when you use notifyItemRemoved(0). This has been a known issue for quit a while.
        if (deletedProductIndex == 0) {
            productAdapter.notifyDataSetChanged()
        } else {
            productAdapter.notifyItemRemoved(deletedProductIndex)

            //Makes sure the divider on the element above is drawn
            if(deletedProductIndex != 0) {
                productAdapter.notifyItemChanged(deletedProductIndex - 1)
            }
        }

        setAllNoResultStates()

        onShowUndoSnackbar()
    }

    /**
     * Initializes the undo snackbar.
     *
     */
    override fun initUndoSnackbar(){
        undoSnackbar = Snackbar.make(
            content,
            "",
            Snackbar.LENGTH_LONG)

        undoSnackbar.setAction("UNDO") {}
        undoSnackbar.setActionTextColor(Color.WHITE)
        undoSnackbar.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {

            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)

                if(event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT){
                    presenter.deleteProduct(deletedProduct, deletedProductDeleteType)
                }
                else{
                    products.add(index = deletedProductIndex, element = deletedProduct)
                    productAdapter.notifyItemInserted(deletedProductIndex)

                    if (deletedProductIndex == products.count() - 1){
                        productAdapter.notifyItemChanged(deletedProductIndex - 1)
                    }

                    setAllNoResultStates()
                }
            }
        })
    }

    /**
     * Shows the undo snackbar and sets the text.
     *
     */
    @SuppressLint("DefaultLocale")
    override fun onShowUndoSnackbar(){
        undoSnackbar.setText(deletedProduct.productName + " has been " + deletedProductDeleteType
            .toString()
            .toLowerCase()
            .replace("_", " ")
        )

        undoSnackbar.show()
    }

    /**
     * Handles the action of the remove button on a product.
     *
     */
    @SuppressLint("DefaultLocale")
    override fun onRemoveButtonClicked(product: Product) {
        PopupManager.instance.showPopup(context!!,
            PopupBase(
                getString(R.string.remove_product_header),
                getString(R.string.remove_product_subText)
            ),
            PopupButton(getString(R.string.btn_no).toUpperCase()),
            PopupButton(getString(R.string.btn_yes).toUpperCase()) { onSafeDeleteProduct(product, DeleteType.REMOVED) })
    }

    /**
     * Handles the item view being clicked, opens the product info page.
     *
     */
    override fun onItemViewClicked(product: Product) {
        NavHostFragment.
            findNavController(fragmentHost).
            navigate(ProductListFragmentDirections.actionProductListFragmentToProductInfoFragment(ParcelableProductMapper.toParcelable(product)))
    }

    /**
     * Attaches the ItemTouchHelper to the RecyclerView.
     *
     */
    override fun setItemTouchHelper() {
        val callback = ProductItemTouchHelper(products, context!!, this::onSafeDeleteProduct)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(rvProducts)
    }

    /**
     * Checks if emptyView should be visible based on the itemCount.
     *
     */
    override fun setEmptyView() {
        if (productAdapter.itemCount == 0) {
            tvAddNewVittle.visibility = View.VISIBLE
        } else {
            tvAddNewVittle.visibility = View.GONE
        }
    }

    /**
     * Called after filtering products array to show or hide no results text view.
     *
     */
    override fun onNoResults() {
        if (productAdapter.itemCount == 0) {
            tvNoResults.visibility = View.VISIBLE
        } else {
            tvNoResults.visibility = View.INVISIBLE
        }
    }

    /**
     * Enables the empty view and the no result view.
     *
     */
    override fun setAllNoResultStates() {
        setEmptyView()
        onNoResults()
    }

    /**
     * Populates the RecyclerView with items from the local database.
     *
     */
    override fun onPopulateRecyclerView() {
        products.clear()
        presenter.startPresenting()
    }

    /**
     * When products are loaded, this method will get the products to the product list.
     *
     * @param products Products to be added to the product list.
     */
    override fun onShowProducts(products: List<Product>) {
        this.products.addAll(products)
        presenter.loadIndicationColors(this.products)
        productAdapter.products = products
        productAdapter.notifyDataSetChanged()
        filteredProducts = this.products
        sortMenu.sortFilteredList(filteredProducts)
        setEmptyView()
        onNoResults()

        checkForDeletedProduct()
    }

    /**
     * Checks if a product was deleted in another fragment.
     * If it is deleted it will safeDelete the product.
     *
     */
    override fun checkForDeletedProduct() {
        Handler().postDelayed( {
            if (productArgs.ProductToDelete != null &&
                productArgs.ProductToDelete!!.deleteType != null &&
                getProductToDelete(ParcelableProductMapper.fromParcelable(productArgs.ProductToDelete!!)) != null){
                    val productToDelete = getProductToDelete(ParcelableProductMapper.fromParcelable(productArgs.ProductToDelete!!))!!
                    onSafeDeleteProduct(productToDelete, productArgs.ProductToDelete!!.deleteType!!)
            }
        }, 300)    }

    /**
     * Gets the product that should be deleted.
     *
     * @param product The product that you want to delete.
     * @return The product inside of the list that should be deleted.
     */
    override fun getProductToDelete(product: Product): Product? {
            return products.find{ it.uid == product.uid }
    }

    /**
     * If product could not be deleted, this method will create a feedback Snackbar for the error.
     *
     */
    override fun onShowProductDeleteError() {
        Snackbar.make(rvProducts, getString(R.string.product_deleted_failed), Snackbar.LENGTH_LONG)
            .show()
    }

    /**
     * Method that is called when text is entered in search view.
     *
     * @param query entered string used as search query.
     */
    @SuppressLint("DefaultLocale")
    override fun filter(query: String) {
        filteredProducts = products.filter { product ->
            product.productName.toLowerCase().contains(query.toLowerCase())
        } as MutableList<Product>

        productAdapter.products = filteredProducts
        productAdapter.notifyDataSetChanged()
        sortMenu.sortFilteredList(filteredProducts)

        onNoResults()
    }

    /**
     * Called when the sort button is clicked.
     *
     */
    override fun onSortMenuOpened() {
        sortMenu.openMenu(context!!, btnSort, filteredProducts)
    }

    /**
     * Method to show the search bar and hide the toolbar.
     *
     */
    override fun onSearchBarOpened() {
        svSearch?.setQuery("", true)
        llSearch?.visibility = View.VISIBLE
        svSearch?.isIconified = false
        toolbar?.visibility = View.GONE
    }

    /**
     * Method to hide the search bar and show the toolbar.
     *
     */
    override fun onSearchBarClosed() {
        svSearch?.setQuery("", true)
        llSearch?.visibility = View.GONE
        toolbar?.visibility = View.VISIBLE
    }
}
