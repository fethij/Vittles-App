package com.example.vittles.services.popups

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import com.example.vittles.R
import kotlinx.android.synthetic.main.popup_base.view.tvHeader
import kotlinx.android.synthetic.main.popup_base.view.tvsSubtext
import kotlinx.android.synthetic.main.popup_button_one.view.*
import kotlinx.android.synthetic.main.popup_button_two.view.*
import java.util.*
import kotlin.concurrent.schedule

/**
 * This class is used to show custom popups.
 *
 *
 * @property alertDialog This is the alerDialog used to show popups.
 */
@SuppressLint("InflateParams")
internal class PopupManager {

    /** @suppress */
    private var alertDialog: AlertDialog? = null

    companion object{
        /** Instance of the pop up manager. */
        private var INSTANCE: PopupManager? = null

        val instance: PopupManager
            get(){
                if(INSTANCE == null){
                    INSTANCE = PopupManager()
                }
                return INSTANCE!!
            }
    }

    /**
     * Sets the AlertDialog and makes sure the popup xml is inflated.
     *
     * @param context The context of the current activity.
     * @param popupBase An implementation of the IPopupBase.
     * @param view The view that should be displayed.
     */
    private fun showPopup(context: Context, popupBase: IPopupBase, view: View){
        closePopup()

        view.tvHeader.text = popupBase.header
        view.tvsSubtext.text = popupBase.subText

        val builder = AlertDialog.Builder(context).setView(view)
        alertDialog = builder.show()

        //This is done so the default window of the alertDialog is not shown. If we don't do this, de design won't match.
        alertDialog!!.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        autoClosePopup(popupBase.popupDuration)
    }

    /**
     * Displays a popup with a header and subText.
     *
     * @param context The context of current activity.
     * @param popupBase The PopupBase which contains a header and subText string.
     */
    internal fun showPopup(context: Context, popupBase: IPopupBase){
        val view = LayoutInflater.from(context).inflate(R.layout.popup_base, null)

        showPopup(context, popupBase, view)
}

    /**
     * Displays a popup with the header, subText and one button.
     *
     * @param context The context of current activity.
     * @param popupBase The PopupBase which contains a header and subText string.
     * @param button The PopupButton which contains a string and a Unit.
     */
    internal fun showPopup(context: Context, popupBase: IPopupBase, button: IPopupButton){
        val view = LayoutInflater.from(context).inflate(R.layout.popup_button_one, null)
        view.btn.text = button.text
        view.btn.setOnClickListener { buttonAction(button.action) }

        showPopup(context, popupBase, view)
    }

    /**
     * Displays a popup with the header, subText and two buttons.
     *
     * @param context The context of current activity.
     * @param popupBase The PopupBase which contains a header and subText string.
     * @param buttonLeft The PopupButton for the left button which contains a string and a Unit.
     * @param buttonRight The PopupButton for the right button which contains a string and a Unit.
     */
    internal fun showPopup(context: Context, popupBase: IPopupBase, buttonLeft: IPopupButton, buttonRight: IPopupButton){

        val view = LayoutInflater.from(context).inflate(R.layout.popup_button_two, null)
        view.btnLeft.text = buttonLeft.text
        view.btnLeft.setOnClickListener { buttonAction(buttonLeft.action) }
        view.btnRight.text = buttonRight.text
        view.btnRight.setOnClickListener { buttonAction( buttonRight.action) }

        showPopup(context, popupBase, view)
    }

    /**
     * Closes the active popup.
     *
     */
    private fun closePopup(){
        if (alertDialog != null) {
            alertDialog!!.dismiss()
            alertDialog = null
        }
    }

    /**
     * Handles a button action.
     *
     * @param action The action to handle.
     */
    private fun buttonAction(action: (() ->Unit)?){
        action?.invoke()
        closePopup()
    }

    /**
     * Closes the active popup after a certain amount of milliseconds.
     *
     * @param duration The amount of milliseconds the popup is active. If it's null <= 0 the popup won't close automatically.
     */
    private fun autoClosePopup(duration: Long?) {
        if (duration == null || duration <= 0 || alertDialog == null){
            return
        }

        Timer().schedule(duration) {
            closePopup()
        }
    }
}