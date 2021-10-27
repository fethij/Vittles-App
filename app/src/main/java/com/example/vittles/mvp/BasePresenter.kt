package com.example.vittles.mvp

import androidx.fragment.app.Fragment
import io.reactivex.disposables.CompositeDisposable

/**
 * Superclass of all presenters. This class has properties that will be used in all
 * presenters.
 *
 *
 * @param V The activity (V: View) of the presenter.
 */
abstract class BasePresenter<V : Any> : MvpPresenter {

    /** Contains all async calls made. */
    protected val disposables: CompositeDisposable = CompositeDisposable()
    /** The view of the fragment. */
    protected var view: V? = null
        private set

    /**
     * Method to start the presenter, will assign the activity.
     *
     * @param view The activity (V: View) of the presenter.
     */
    fun start(view: V) {
        this.view = view
    }

    /**
     * Method to stop the presenter.
     *
     */
    override fun stop() {
        this.view = null
    }

    /**
     * Method to clear the disposables of the presenter.
     *
     */
    override fun destroy() {
        disposables.clear()
    }
}