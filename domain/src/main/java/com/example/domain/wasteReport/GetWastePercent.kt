package com.example.domain.wasteReport

import io.reactivex.Single
import javax.inject.Inject
import kotlin.math.ceil

/**
 * This class handles te business logic of getting waste report.
 *
 *
 */
class GetWastePercent @Inject constructor() {

    /**
     * This method is used to get the percent value of eaten vittles
     *
     * @param vittlesEaten Amount of eaten vittles
     * @param vittlesExpired Amount of expired vittles
     * @return percent value of eaten vittles
     */
    operator fun invoke(vittlesEaten: Int, vittlesExpired: Int): Single<Int> {
        var percent = 0
        if((vittlesEaten + vittlesExpired) != 0 ) {
            percent =
                ceil((vittlesEaten.toDouble() / (vittlesEaten + vittlesExpired)) * 100).toInt()
        }
        return Single.just(percent)
    }

}