package com.codedeco.lib.ui.widget.menu.context.floating

import com.codedeco.lib.ui.widget.menu.context.ContextMenuItem

interface FloatingContextMenuListener {
    fun onMenuShow()
    fun onMenuDismiss()
    fun onMenuItemClick(menuItem: ContextMenuItem)
}