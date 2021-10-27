package com.example.vittles

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ActionMenuView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.size
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.vittles.productlist.ProductListFragment
import com.example.vittles.productlist.ProductListFragmentDirections
import com.example.vittles.wastereport.WasteReportFragmentDirections
import com.google.android.material.bottomappbar.BottomAppBar
import kotlinx.android.synthetic.main.content_main.*
import androidx.navigation.findNavController as findNavSetup
import android.content.Intent
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.camera.core.CameraX
import com.example.vittles.enums.PreviousFragmentIndex
import com.example.vittles.settings.SettingsFragment


/**
 * Main activity that only controls the navigation.
 *

 */
class MainActivity : AppCompatActivity() {
    /**
     * The Navigation Controller of the application.
     */
    private lateinit var navController: NavController

    private val topLevelDestinations =
        arrayOf(R.id.productListFragment, R.id.wasteReportFragment, R.id.settingsFragment)

    /** {@inheritDoc}*/
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar) // Finish splash screen
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(mainToolbar)
        initViews()
    }

    /**
     * Initializes the view elements.
     *
     */
    private fun initViews() {
        initNavigation()
        setListeners()
    }

    /**
     * Initializes the bottom navigation.
     *
     */
    private fun initNavigation() {
        navController = findNavSetup(R.id.fragmentHost)

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        // Make reports top-level so that the back button disables
        appBarConfiguration.topLevelDestinations.addAll(topLevelDestinations)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        // Initialize navigation visibility
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.productListFragment -> {
                    showBottomNavigationBar(barVisibility = true, fabVisibility = true)
                    getMenuItemByTitle(R.string.menu_home)?.let { setMenuItemIconColor(it) }
                }
                R.id.wasteReportFragment -> {
                    showBottomNavigationBar(barVisibility = true, fabVisibility = true)
                    getMenuItemByTitle(R.string.menu_reports)?.let { setMenuItemIconColor(it) }
                }
                R.id.scannerFragment -> {
                    showBottomNavigationBar(barVisibility = false, fabVisibility = false)
                }
                R.id.settingsFragment -> {
                    showBottomNavigationBar(barVisibility = true, fabVisibility = true)
                    getMenuItemByTitle(R.string.menu_settings)?.let { setMenuItemIconColor(it) }
                }
                R.id.productInfoFragment -> {
                    showBottomNavigationBar(barVisibility = false, fabVisibility = false)
                }
            }
        }

        // Distribute the menu items evenly
        if (navView.childCount > 0) {
            val actionMenuView = navView.getChildAt(0) as ActionMenuView
            actionMenuView.layoutParams.width = ActionMenuView.LayoutParams.MATCH_PARENT
        }
    }

    /**
     * Will hide/show the navigation bar and/or the floating action button.
     *
     * @param barVisibility Boolean value that represents if the navigation should be visible.
     * @param fabVisibility Boolean value that represents if the FAB should be visible.
     */
    private fun showBottomNavigationBar(barVisibility: Boolean, fabVisibility: Boolean) {
        navView.visibility = if (barVisibility) BottomAppBar.VISIBLE else BottomAppBar.GONE
        if (fabVisibility) fab.show() else fab.hide()
    }

    /**
     * Sets all necessary event listeners on UI elements.
     *
     */
    private fun setListeners() {
        fab.setOnClickListener { onAddButtonClick() }

        navView.menu.getItem(0).setOnMenuItemClickListener { onNavigateHomeButtonClick() }
        navView.menu.getItem(1).setOnMenuItemClickListener { onNavigateSearchButtonClick() }
        navView.menu.getItem(4).setOnMenuItemClickListener { onNavigateReportsButtonClick() }
        navView.menu.getItem(5).setOnMenuItemClickListener { onNavigateSettingsButtonClick() }
    }

    /**
     * Sets the color of menu icon that is pressed
     *
     * @param menuItem Menu item of which to color icon of
     */
    private fun setMenuItemIconColor(menuItem: MenuItem) {
        val wrappedDrawable = setDrawableTint(menuItem.icon, R.color.colorPrimary)

        for (x in 0 until navView.menu.size) {
            if (navView.menu.getItem(x).isEnabled && navView.menu.getItem(x).title != menuItem.title) {
                val wrappedDrawableToReset =
                    setDrawableTint(navView.menu.getItem(x).icon, R.color.black)
                navView.menu.getItem(x).icon = wrappedDrawableToReset
            }
        }

        menuItem.icon = wrappedDrawable
    }

    /**
     * Sets the tint of a drawable and returns the drawable.
     *
     * @param drawable Drawable to modify tint of.
     * @param color Color to modify tint of drawable to.
     * @return Returns modified drawable.
     */
    private fun setDrawableTint(drawable: Drawable, color: Int): Drawable? {
        val wrappedDrawable = DrawableCompat.wrap(drawable)

        DrawableCompat.setTint(
            wrappedDrawable,
            ContextCompat.getColor(applicationContext, color)
        )
        return wrappedDrawable
    }

    /**
     * Gets a menu item based on its title.
     *
     * @param title Title to search MenuItem on.
     * @return Returns found MenuItem of null if not found.
     */
    private fun getMenuItemByTitle(title: Int): MenuItem? {
        for (x in 0 until navView.menu.size) {
            if (navView.menu.getItem(x).isEnabled && navView.menu.getItem(x).title == getString(
                    title
                )
            ) {
                return navView.menu.getItem(x)
            }
        }
        return null
    }

    /**
     * Customized navigate up flow. Necessary for passing values on the navigate up flow.
     *
     * @return Boolean that represents if the action succeeded.
     */
    override fun onSupportNavigateUp(): Boolean {
        return when (navController.currentDestination?.id) {
            R.id.scannerFragment, R.id.productInfoFragment -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onSupportNavigateUp()
        }
    }

    /**
     * Customized back button flow. Necessary for closing the app on top level destinations
     * and passing values on the navigate up flow.
     *
     */
    override fun onBackPressed() {
        /*
        If current destination is a top level destination, close the app. Otherwise follow the
        navigate up flow.
        */
        if (topLevelDestinations.contains(navController.currentDestination?.id)) {
            // Closes the app (returns to home screen) instead of quitting it with finish()
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        } else {
            onSupportNavigateUp()
        }
    }

    /**
     * Navigate to the SettingsFragment.
     *
     * @return Boolean value that represents if the navigation has succeeded.
     */
    private fun onNavigateSettingsButtonClick(): Boolean {
        findNavController(fragmentHost).navigate(NavigationGraphDirections.actionGlobalSettingsFragment())
        return true
    }

    /**
     * Navigate to the ProductListFragment with the search bar opened.
     *
     * @return Boolean value that represents if the navigation has succeeded.
     */
    private fun onNavigateSearchButtonClick(): Boolean {
        findNavController(fragmentHost).navigate(
            NavigationGraphDirections.actionGlobalProductListFragment(
                null,
                true
            )
        )
        return true
    }

    /**
     * Navigate to the ProductListFragment.
     *
     * @return Boolean value that represents if the navigation has succeeded.
     */
    private fun onNavigateHomeButtonClick(): Boolean {
        return if (navController.currentDestination?.id != R.id.productListFragment) {
            findNavController(fragmentHost).navigate(NavigationGraphDirections.actionGlobalProductListFragment())
            true
        } else {
            false
        }
    }

    /**
     * Called when the add button is clicked.
     * It starts the addProduct activity.
     *
     */
    private fun onAddButtonClick() {
        val currentFragment =
            supportFragmentManager.primaryNavigationFragment!!.childFragmentManager.fragments.first()
        val previousFragment: PreviousFragmentIndex = when (currentFragment) {
            is ProductListFragment -> PreviousFragmentIndex.PRODUCT_LIST
            is SettingsFragment -> PreviousFragmentIndex.SETTINGS
            else -> PreviousFragmentIndex.PRODUCT_LIST
        }
        findNavController(fragmentHost).navigate(NavigationGraphDirections.actionGlobalScannerFragment(previousFragment() as Int))
    }

    /**
     * Navigate to the ReportsFragment.
     *
     * @return Boolean value that represents if the navigation has succeeded.
     */
    private fun onNavigateReportsButtonClick(): Boolean {
        return if (navController.currentDestination?.id != R.id.wasteReportFragment) {
            findNavController(fragmentHost).navigate(NavigationGraphDirections.actionGlobalReportsFragment())
            true
        } else {
            false
        }
    }
}
