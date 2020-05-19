package com.codedeco.lib.ui.widget.menu.context.floating

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codedeco.lib.ui.R
import com.codedeco.lib.ui.databinding.ItemFloatingContextMenuItemBinding
import com.codedeco.lib.ui.widget.menu.context.ContextMenuItem
import java.lang.ref.WeakReference

class FloatingContextMenuAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = ArrayList<ContextMenuItem>()

    var itemClickListener: ItemClickListener? = null
    var itemLongClickListener: ItemLongClickListener? = null
    var itemTouchListener: ItemTouchListener? = null

    fun set(newItems: List<ContextMenuItem>) = apply {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun get(position: Int): ContextMenuItem? {
        return items.getOrNull(position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MenuItemViewHolder(
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_floating_context_menu_item,
                        parent,
                        false
                )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MenuItemViewHolder) {
            items.getOrNull(position)?.run {
                holder.update(this)
            }
        }
    }

    private inner class MenuItemViewHolder(val binding: ItemFloatingContextMenuItemBinding) :
            RecyclerView.ViewHolder(binding.root) {

        private var data: WeakReference<ContextMenuItem>? = null

        init {
            binding.root.apply {
                setOnClickListener {
                    data?.get()?.run {
                        itemClickListener?.onItemClick(this)
                    }
                }
                setOnLongClickListener {
                    data?.get()?.run {
                        itemLongClickListener?.onItemLongClick(this) ?: false
                    } ?: false
                }
                setOnTouchListener { view, event ->
                    data?.get()?.run {
                        itemTouchListener?.onItemTouch(this, view, event) ?: false
                    } ?: false
                }
            }
        }

        fun update(data: ContextMenuItem) {
            this.data = WeakReference(data)
            binding.icon.setImageDrawable(data.iconDrawable)
            binding.text.text = data.string

            if (data.isEnabled) {
                onItemEnabled()
            } else {
                onItemDisabled()
            }
        }

        private fun onItemEnabled() {
            binding.root.alpha = 1f
        }

        private fun onItemDisabled() {
            binding.root.alpha = 0.5f
        }
    }

    interface ItemClickListener {
        fun onItemClick(item: ContextMenuItem)
    }

    interface ItemLongClickListener {
        fun onItemLongClick(item: ContextMenuItem): Boolean
    }

    interface ItemCheckedChangeListener {
        fun onItemCheckChanged(item: ContextMenuItem, checked: Boolean)
    }

    interface ItemTouchListener {
        fun onItemTouch(item: ContextMenuItem, view: View, event: MotionEvent): Boolean
    }
}