package com.reset.model.domain

import com.reset.model.domain.model.EyeFact
import javax.inject.Inject
import kotlin.random.Random

/** Picks a random eye-science card to surface on the Home screen. */
class EyeFactProvider @Inject constructor(
    private val random: Random,
) {
    fun random(): EyeFact = EyeFact(index = random.nextInt(EyeFact.COUNT))
}
