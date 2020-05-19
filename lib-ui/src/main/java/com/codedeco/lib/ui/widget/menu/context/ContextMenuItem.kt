package com.codedeco.lib.ui.widget.menu.context

import android.graphics.drawable.Drawable
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class ContextMenuItem(
        val itemId: Int,
        val iconDrawable: @RawValue Drawable,
        val string: String,
        val isEnabled: Boolean = true
) : Parcelable