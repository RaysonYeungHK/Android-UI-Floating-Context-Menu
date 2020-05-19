package com.codedeco.lib.ui.widget.menu.context.floating

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.codedeco.lib.ui.R
import com.codedeco.lib.ui.databinding.ItemFloatingContextMenuItemBinding
import com.codedeco.lib.ui.widget.menu.context.ContextMenuItem

class FloatingContextMenuExpandedView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val binding: ItemFloatingContextMenuItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.item_floating_context_menu_item,
            this,
            true
    )

    var isHidden: Boolean = true

    fun setMenuItem(menuItem: ContextMenuItem) {
        binding.apply {
            decorationBorder.visibility = View.VISIBLE
            icon.setImageDrawable(menuItem.iconDrawable)
            text.text = menuItem.string
        }
    }

    companion object {
        private val viewPool: MutableList<FloatingContextMenuExpandedView> = ArrayList()

        fun getExpandedView(context: Context): FloatingContextMenuExpandedView {
            synchronized(viewPool) {
                for (view in viewPool) {
                    if (view.parent == null) {
                        return view
                    }
                }
                val newView = FloatingContextMenuExpandedView(context)
                viewPool.add(newView)
                return newView
            }
        }
    }
}