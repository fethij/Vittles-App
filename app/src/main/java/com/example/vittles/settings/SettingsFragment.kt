package com.example.vittles.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.lifecycle.Observer
import com.example.domain.settings.model.NotificationSchedule
import android.widget.Toast
import androidx.core.view.ViewCompat.animate
import androidx.core.view.ViewPropertyAnimatorListener
import com.example.domain.settings.model.PerformanceSetting
import com.example.vittles.R
import com.example.vittles.services.popups.PopupBase
import com.example.vittles.services.popups.PopupButton
import com.example.vittles.services.popups.PopupManager
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_settings.*
import javax.inject.Inject


/**
 * Fragment class for the Settings. This is the fragment that shows the settings.
 *
 */
class SettingsFragment : DaggerFragment(), SettingsContract.View {

    /**
     * The presenter of the fragment
     */
    @Inject
    lateinit var presenter: SettingsPresenter

    /** {@inheritDoc} */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        presenter.start(this)
        presenter.startPresenting(requireContext())
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    /** {@inheritDoc} */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    override fun onDestroy() {
        presenter.destroy()
        super.onDestroy()
    }

    /**
     * Sets EventListeners
     * sets default values to the notification toggle
     * shows/keeps the last selected value of notification time
     *
     */
    override fun initViews() {

        setListeners()

        presenter.notificationEnabled.observe(this, Observer {
            notification_toggle.isChecked = it
        })

        presenter.notificationSchedule.observe(this, Observer {
            notification_timer.setSelection(it.ordinal)
        })

        presenter.vibrationEnabled.observe(this, Observer {
            vibration_toggle.isChecked = it
        })

        presenter.performanceSetting.observe(this, Observer {
            performance_picker.setSelection(it.ordinal)
        })
    }

    /**
     * Sets all necessary event listeners on ui elements
     *
     */
    override fun setListeners() {

        /*
         * called when the vibration switch is switched
         *
         */
        vibration_toggle.setOnCheckedChangeListener { _, isChecked ->
            presenter.onVibrationEnabledChanged(isChecked)
        }

        /*
         * called when the notification switch is switched
         *
         */
        notification_toggle.setOnCheckedChangeListener { _, isChecked ->
            presenter.onNotificationEnabledChanged(isChecked)
        }

        /*
         * called when time is selected from the spinner
         *
         */
        notification_timer.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val notificationSchedule = NotificationSchedule.values()[position]

                presenter.onNotificationScheduleChanged(notificationSchedule)
            }

            override fun onNothingSelected(parent: AdapterView<*>) { }
        }

        performance_picker.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val performanceSetting = PerformanceSetting.values()[position]

                    presenter.onPerformanceSettingChanged(performanceSetting)

                }
                override fun onNothingSelected(parent: AdapterView<*>) { }
        }

        llAdvanced.setOnClickListener { onAdvancedClick() }

        ibRemoveSavedProducts.setOnClickListener { onRemoveSavedProductsClick() }
    }

    /**
     * Method to show successful deletion of product names
     *
     */
    override fun onProductDictionaryClearSuccess() {
        Toast.makeText(context, getString(R.string.settings_remove_products_success), Toast.LENGTH_SHORT).show()
    }

    /**
     * Method to show unsuccessful deletion of product names
     *
     */
    override fun onProductDictionaryClearFail() {
        Toast.makeText(context, getString(R.string.settings_remove_products_fail), Toast.LENGTH_SHORT).show()
    }

    override fun onRemoveSavedProductsClick() {
        PopupManager.instance.showPopup(this.context!!, PopupBase(
            getString(R.string.settings_popup_confirm_header),
            getString(R.string.settings_popup_confirm_subtext)),
            PopupButton(getString(R.string.btn_no)),
            PopupButton(getString(R.string.btn_yes)) { removeSavedProducts() })
    }

    /**
     * Method to call presenter to remove all saved products
     *
     */
    override fun removeSavedProducts() {
        presenter.clearProductDictionary()
    }

    /**
     * Method called when clicked on advanced settings item
     *
     */
    override fun onAdvancedClick() {
        expandAdvancedSettings()
    }

    /**
     * Method for toggling the advanced settings
     *
     */
    override fun expandAdvancedSettings() {
        if (ibAdvanced.rotation == 0f) {
            ibAdvanced.animate().rotation(180f).start()
            fadeInAnim(llAdvancedSettings)
        } else {
            ibAdvanced.animate().rotation(0f).start()
            fadeOutAnim(llAdvancedSettings)

        }
    }

    /**
     * Animation for fade out and translate
     *
     * @param elem Element to animate
     */
    override fun fadeOutAnim(elem: View) {
        animate(elem).apply {
            interpolator = AccelerateInterpolator()
            alpha(0f)
            translationY(0f)
            duration = 500
            setListener(object : ViewPropertyAnimatorListener {
                override fun onAnimationEnd(view: View?) { elem.visibility = View.GONE }
                override fun onAnimationCancel(view: View?) { }
                override fun onAnimationStart(view: View?) { }
            })
            start()
        }
    }

    /**
     * Animation for fade in and translate
     *
     * @param elem Element to animate
     */
    override fun fadeInAnim(elem: View) {
        animate(elem).apply {
            interpolator = AccelerateInterpolator()
            translationY(llAdvanced.height.toFloat())
            alpha(1f)
            duration = 250
            setListener(object : ViewPropertyAnimatorListener {
                override fun onAnimationEnd(view: View?) { }
                override fun onAnimationCancel(view: View?) { }
                override fun onAnimationStart(view: View?) { elem.visibility = View.VISIBLE }
            })
            start()
        }
    }
}