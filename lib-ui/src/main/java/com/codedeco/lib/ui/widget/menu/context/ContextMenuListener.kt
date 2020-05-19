package com.codedeco.lib.ui.widget.menu.context

import android.view.View

interface ContextMenuListener {
    fun onMenuItemSelected(item: ContextMenuItem): Boolean
    fun onContextMenuCreate(callerView: View): Boolean
}