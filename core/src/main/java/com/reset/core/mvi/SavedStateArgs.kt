package com.reset.core.mvi

import androidx.lifecycle.SavedStateHandle
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Read an Activity/Fragment argument that was forwarded into the [SavedStateHandle].
 *
 * Mirrors the sharechat `view-binder` helpers referenced by the feature guide: a
 * `BaseViewModel` reads its launch arguments with `by argument(KEY)` /
 * `by argumentNullable(KEY)` instead of touching the handle directly.
 */
inline fun <reified T> SavedStateHandle.argumentNullable(
    key: String? = null,
    initialValue: T? = null,
): ReadWriteProperty<Any, T?> = object : ReadWriteProperty<Any, T?> {
    override fun getValue(thisRef: Any, property: KProperty<*>): T? {
        val stateKey = key ?: property.name
        return this@argumentNullable[stateKey] ?: initialValue
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
        val stateKey = key ?: property.name
        this@argumentNullable[stateKey] = value
    }
}

inline fun <reified T> SavedStateHandle.argument(
    key: String? = null,
    initialValue: T? = null,
): ReadWriteProperty<Any, T> = object : ReadWriteProperty<Any, T> {
    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        val stateKey = key ?: property.name
        return this@argument[stateKey]
            ?: initialValue
            ?: throw NullPointerException("value is null, use argumentNullable")
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        val stateKey = key ?: property.name
        this@argument[stateKey] = value
    }
}
