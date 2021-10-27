package com.example.vittles.mvp

/**
 * Interface of the BasePresenter.
 *
 */
interface MvpPresenter {
    /**
     * Stop presenting.
     *
     */
    fun stop()

    /**
     * Destroy content of presenter.
     *
     */
    fun destroy()
}