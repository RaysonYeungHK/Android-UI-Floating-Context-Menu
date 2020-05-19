package com.codedeco.lib.floatingcontextmenu

import android.R
import android.app.Application
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.AndroidViewModel
import com.codedeco.lib.ui.widget.menu.context.ContextMenuItem

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    val example1Items = listOf(
            ContextMenuItem(1, ResourcesCompat.getDrawable(application.resources, R.drawable.ic_delete, null)!!, "Delete", true)
    )

    val example2Items = listOf(
            ContextMenuItem(1, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_delete, null)!!, "Delete", true),
            ContextMenuItem(2, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_dialog_alert, null)!!, "Alert", true),
            ContextMenuItem(3, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_menu_edit, null)!!, "Edit", true),
            ContextMenuItem(4, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_dialog_email, null)!!, "Email", true),
            ContextMenuItem(5, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_menu_share, null)!!, "Share", true)
    )

    val example3Items = listOf(
            ContextMenuItem(1, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_delete, null)!!, "Delete", true),
            ContextMenuItem(2, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_dialog_alert, null)!!, "Alert", true),
            ContextMenuItem(3, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_menu_edit, null)!!, "Edit", true),
            ContextMenuItem(4, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_dialog_email, null)!!, "Email", true),
            ContextMenuItem(5, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_menu_share, null)!!, "Share", true),
            ContextMenuItem(6, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_delete, null)!!, "Delete", true),
            ContextMenuItem(7, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_dialog_alert, null)!!, "Alert", true),
            ContextMenuItem(8, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_menu_edit, null)!!, "Edit", true),
            ContextMenuItem(9, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_dialog_email, null)!!, "Email", true),
            ContextMenuItem(10, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_menu_share, null)!!, "Share", true),
            ContextMenuItem(11, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_delete, null)!!, "Delete", true),
            ContextMenuItem(12, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_dialog_alert, null)!!, "Alert", true),
            ContextMenuItem(13, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_menu_edit, null)!!, "Edit", true),
            ContextMenuItem(14, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_dialog_email, null)!!, "Email", true),
            ContextMenuItem(15, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_menu_share, null)!!, "Share", true),
            ContextMenuItem(16, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_delete, null)!!, "Delete", true),
            ContextMenuItem(17, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_dialog_alert, null)!!, "Alert", true),
            ContextMenuItem(18, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_menu_edit, null)!!, "Edit", true),
            ContextMenuItem(19, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_dialog_email, null)!!, "Email", true),
            ContextMenuItem(20, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_menu_share, null)!!, "Share", true),
            ContextMenuItem(21, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_delete, null)!!, "Delete", true),
            ContextMenuItem(22, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_dialog_alert, null)!!, "Alert", true),
            ContextMenuItem(23, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_menu_edit, null)!!, "Edit", true),
            ContextMenuItem(24, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_dialog_email, null)!!, "Email", true),
            ContextMenuItem(25, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_menu_share, null)!!, "Share", true),
            ContextMenuItem(26, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_delete, null)!!, "Delete", true),
            ContextMenuItem(27, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_dialog_alert, null)!!, "Alert", true),
            ContextMenuItem(28, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_menu_edit, null)!!, "Edit", true),
            ContextMenuItem(29, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_dialog_email, null)!!, "Email", true),
            ContextMenuItem(30, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_menu_share, null)!!, "Share", true)
    )

    val example4Items5 = listOf(
            ContextMenuItem(1, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_delete, null)!!, "Delete", true),
            ContextMenuItem(2, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_dialog_alert, null)!!, "Alert", true),
            ContextMenuItem(3, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_menu_edit, null)!!, "Edit", true),
            ContextMenuItem(4, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_dialog_email, null)!!, "Email", true),
            ContextMenuItem(5, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_menu_share, null)!!, "Share", true)
    )

    val example4Items2 = listOf(
            ContextMenuItem(1, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_delete, null)!!, "Delete", true),
            ContextMenuItem(2, ResourcesCompat.getDrawable(application.resources, android.R.drawable.ic_dialog_alert, null)!!, "Alert", true)
    )
}