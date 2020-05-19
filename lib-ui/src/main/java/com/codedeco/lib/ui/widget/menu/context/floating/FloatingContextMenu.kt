package com.codedeco.lib.ui.widget.menu.context.floating

import android.animation.Animator
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.codedeco.lib.ui.R
import com.codedeco.lib.ui.databinding.WidgetFloatingContextMenuBinding
import com.codedeco.lib.ui.widget.menu.context.ContextMenuItem
import java.lang.ref.WeakReference

class FloatingContextMenu @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: WidgetFloatingContextMenuBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.widget_floating_context_menu,
            this,
            true
    )

    private val adapter = FloatingContextMenuAdapter()

    private val contextMenuAnimator = FloatingContextMenuAnimator(context).apply {
        init(binding, adapter,
                object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        listener?.get()?.onMenuShow()
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationStart(animation: Animator?) {
                        (context as Activity).window.decorView
                                .findViewById<ViewGroup>(android.R.id.content)
                                .addView(this@FloatingContextMenu, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    }
                },
                object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        (parent as ViewGroup?)?.removeView(this@FloatingContextMenu)
                        listener?.get()?.onMenuDismiss()
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationStart(animation: Animator?) {
                    }
                }
        )
    }

    private var listener: WeakReference<FloatingContextMenuListener>? = null

    init {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@FloatingContextMenu.adapter
        }
    }

    private fun setListener(listener: FloatingContextMenuListener) = apply {
        this.listener = WeakReference(listener)
        adapter.itemClickListener = object : FloatingContextMenuAdapter.ItemClickListener {
            override fun onItemClick(item: ContextMenuItem) {
                listener.onMenuItemClick(item)
                dismiss()
            }
        }
    }

    private fun setCancelable(cancelable: Boolean) = apply {
        contextMenuAnimator.cancelable = cancelable
    }

    fun show() = apply {
        contextMenuAnimator.show(binding)
    }

    fun dismiss() = apply {
        contextMenuAnimator.dismiss(binding)
    }

    fun setItems(items: List<ContextMenuItem>) = apply {
        adapter.set(items)
    }

    companion object {
        @Synchronized
        fun make(context: Context, items: List<ContextMenuItem>, listener: FloatingContextMenuListener, cancelable: Boolean = true): FloatingContextMenu {
            return FloatingContextMenu(context)
                    .setItems(items)
                    .setListener(listener)
                    .setCancelable(cancelable)
        }
    }
}