package com.reset.repository

import com.reset.model.DomainPlaceholder
import javax.inject.Inject

/**
 * A placeholder implementation to verify data module dependencies and compilation.
 */
class DataPlaceholder @Inject constructor() : DomainPlaceholder {
    override fun execute(): String {
        return "Hello from Data module!"
    }
}
