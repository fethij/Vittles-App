package com.example.vittles.services.popups

/**
 * The basePopup implementation.
 *
 *
 * @property header The header of the popup.
 * @property subText The description of the popup.
 * @property popupDuration The amount of milliseconds the popup should stay open. If null or <= 0 it won't automatically close.
 */
class PopupBase(override val header: String,
                override val subText: String,
                override val popupDuration: Long? = null) : IPopupBase

/**
 * The popupButton implementation.
 *
 *
 * @property text The text on the button.
 * @property action A lambda expression with the action that should be performed. If null it will close the popup.
 */
class PopupButton(override val text: String,
                  override val action: (() -> Unit)? = null) : IPopupButton
