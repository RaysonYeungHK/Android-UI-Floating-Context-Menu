package com.codedeco.lib.ui.widget.menu.context.floating

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.graphics.Rect
import android.util.Log
import android.util.SparseArray
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.codedeco.lib.ui.R
import com.codedeco.lib.ui.databinding.WidgetFloatingContextMenuBinding
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.abs

class FloatingContextMenuAnimator(context: Context) {

    private val listScrollThresholds = 3
    private val listScrollSpeed = dpToPx(10)

    private val animationDuration = 200L
    private val collapsedWidth = dpToPx(52).toFloat()
    private val expandedWidth = pxToDp(getDeviceScreenSize(context).x).let {
        it * if (it >= 600) {
            1
        } else {
            2
        } / 3
    }.let {
        dpToPx(it)
    }.toFloat()
    private val dismissTranslationX = expandedWidth
    private val collapsedTranslationX = expandedWidth - collapsedWidth
    private val expandedTranslationX = 0f

    private var isMenuExpanded: Boolean = false

    private lateinit var onShowListener: Animator.AnimatorListener
    private lateinit var onDismissListener: Animator.AnimatorListener

    private var lastTouchId: Int? = null
    private var isScrollingContextMenu = AtomicBoolean(false)
    private var isScrollingRecyclerView = AtomicBoolean(false)

    private var isSettling = AtomicBoolean(false)
    private var isItemExpandMode = AtomicBoolean(false)
    private val expandedItemView = SparseArray<FloatingContextMenuExpandedView?>()

    var cancelable: Boolean = true

    @SuppressLint("ClickableViewAccessibility")
    fun init(binding: WidgetFloatingContextMenuBinding,
             adapter: FloatingContextMenuAdapter,
             onShow: Animator.AnimatorListener,
             onDismiss: Animator.AnimatorListener) {

        onShowListener = onShow
        onDismissListener = onDismiss

        binding.recyclerView.apply {
            layoutManager = object : LinearLayoutManager(context) {
                override fun canScrollVertically(): Boolean {
                    return !isItemExpandMode.get() && super.canScrollVertically()
                }
            }
            layoutParams = layoutParams.apply {
                width = expandedWidth.toInt()
            }
        }

        binding.touchInterceptor.setOnTouchListener(object : View.OnTouchListener {
            private val gestureDetector = GestureDetector(binding.root.context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
                    when (lastTouchId) {
                        R.id.recycler_view -> {
                            if (binding.recyclerView.layoutManager?.canScrollVertically() == true
                                    && abs(distanceY) > abs(distanceX)
                                    && !isScrollingContextMenu.get()
                                    || isScrollingRecyclerView.get()) {
                                isScrollingRecyclerView.set(true)
                                binding.recyclerView.scrollBy(0, distanceY.toInt())
                            }
                        }
                    }
                    if (!isScrollingRecyclerView.get()) {
                        val newTranslationX = binding.content.translationX - distanceX
                        if (isValidRange(newTranslationX)) {
                            isScrollingContextMenu.set(true)
                            binding.content.translationX = newTranslationX
                            setOverlayAlpha(binding)
                        }
                    }
                    lastTouchId = null
                    return true
                }

                override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                    if (!isScrollingRecyclerView.get()) {
                        if (!isSettling.getAndSet(true)) {
                            if (velocityX > 0) {
                                if (isMenuExpanded) {
                                    settleCollapsed(binding)
                                } else {
                                    dismiss(binding)
                                }
                            } else {
                                if (!isMenuExpanded) {
                                    settleExpanded(binding)
                                } else {
                                    isSettling.set(false)
                                }
                            }
                            isScrollingContextMenu.set(false)
                        }
                    } else {
                        binding.recyclerView.fling(velocityX.toInt(), -velocityY.toInt())
                        isScrollingRecyclerView.set(false)
                    }
                    lastTouchId = null
                    return true
                }

                override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
                    when (lastTouchId) {
                        R.id.recycler_view -> {
                            onRecyclerViewClick(binding, event)
                            lastTouchId = null
                        }
                        -1,
                        null -> {
                            dismiss(binding)
                        }
                    }
                    lastTouchId = null
                    return super.onSingleTapConfirmed(event)
                }

                override fun onLongPress(event: MotionEvent) {
                    when (lastTouchId) {
                        R.id.recycler_view -> {
                            if (!isMenuExpanded) {
                                onRecyclerViewLongPress(binding, adapter, event)
                                isItemExpandMode.set(true)
                            }
                        }
                    }
                    lastTouchId = null
                }
            })

            override fun onTouch(view: View, event: MotionEvent): Boolean {
                val viewInCoordinate = (binding.content as ViewGroup).getViewByCoordinates(event.rawX, event.rawY)
                if (!cancelable && !isScrollingContextMenu.get()) {
                    when (viewInCoordinate?.id) {
                        -1,
                        null -> {
                            return false
                        }
                    }
                }
                lastTouchId = viewInCoordinate?.id
                gestureDetector.onTouchEvent(event)
                when (event.action) {
                    MotionEvent.ACTION_UP -> {
                        if (!isSettling.get()) {
                            if (isScrollingContextMenu.getAndSet(false)) {
                                settle(binding)
                            }
                        }
                        if (isItemExpandMode.getAndSet(false)) {
                            onRecyclerViewTouchUp(binding, adapter, event)
                        }
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (isItemExpandMode.get()) {
                            onRecyclerViewTouchMove(binding, adapter, event)
                        }
                    }
                }
                return true
            }
        })
    }

    private fun onRecyclerViewClick(binding: WidgetFloatingContextMenuBinding, event: MotionEvent) {
        val itemView = binding.recyclerView.getViewByCoordinates(event.rawX, event.rawY) ?: return
        itemView.performClick()
    }

    private fun onRecyclerViewLongPress(binding: WidgetFloatingContextMenuBinding, adapter: FloatingContextMenuAdapter, event: MotionEvent) {
        val itemView = binding.recyclerView.getViewByCoordinates(event.rawX, event.rawY) ?: return
        val index = binding.recyclerView.layoutManager?.getPosition(itemView) ?: return
        expandItemView(binding, adapter, index)
    }

    private fun onRecyclerViewTouchMove(binding: WidgetFloatingContextMenuBinding, adapter: FloatingContextMenuAdapter, event: MotionEvent) {
        when (lastTouchId) {
            R.id.recycler_view -> {
                val itemView = binding.recyclerView.getViewByCoordinates(event.rawX, event.rawY) ?: return
                val index = binding.recyclerView.layoutManager?.getPosition(itemView) ?: return
                if (expandedItemView[index] == null) {
                    for (i in 0 until adapter.itemCount) {
                        collapseItemView(binding, i)
                    }
                    expandItemView(binding, adapter, index)
                } else {
                    val layoutManager = (binding.recyclerView.layoutManager as LinearLayoutManager?) ?: return
                    if (layoutManager.canScrollVertically()) {
                        when (index) {
                            0, adapter.itemCount - 1 -> {
                            }
                            in layoutManager.findFirstVisibleItemPosition()..(layoutManager.findFirstVisibleItemPosition() + listScrollThresholds),
                            in layoutManager.findFirstCompletelyVisibleItemPosition()..(layoutManager.findFirstCompletelyVisibleItemPosition() + listScrollThresholds) -> {
                                if (!binding.recyclerView.canScrollVertically(-1)) {
                                    return
                                }
                                binding.recyclerView.scrollBy(0, -listScrollSpeed)
                                for (i in 0 until adapter.itemCount) {
                                    expandedItemView[i]?.run {
                                        translationY += listScrollSpeed
                                    }
                                }
                            }
                            in (layoutManager.findLastVisibleItemPosition() - listScrollThresholds)..layoutManager.findLastVisibleItemPosition(),
                            in (layoutManager.findLastCompletelyVisibleItemPosition() - listScrollThresholds)..layoutManager.findLastCompletelyVisibleItemPosition() -> {
                                if (!binding.recyclerView.canScrollVertically(1)) {
                                    return
                                }
                                binding.recyclerView.scrollBy(0, +listScrollSpeed)
                                for (i in 0 until adapter.itemCount) {
                                    expandedItemView[i]?.run {
                                        translationY -= listScrollSpeed
                                    }
                                }
                            }
                            else -> {
                            }
                        }
                    }
                }
            }
            else -> {
                for (i in 0 until adapter.itemCount) {
                    collapseItemView(binding, i)
                }
            }
        }
    }

    private fun onRecyclerViewTouchUp(binding: WidgetFloatingContextMenuBinding, adapter: FloatingContextMenuAdapter, event: MotionEvent) {
        when (lastTouchId) {
            R.id.recycler_view -> {
                val itemView = binding.recyclerView.getViewByCoordinates(event.rawX, event.rawY) ?: return
                val index = binding.recyclerView.layoutManager?.getPosition(itemView) ?: return
                if (expandedItemView[index] != null) {
                    collapseItemView(binding, index)
                    itemView.performClick()
                }
            }
            else -> {
                for (i in 0 until adapter.itemCount) {
                    collapseItemView(binding, i)
                }
            }
        }
    }

    private fun expandItemView(binding: WidgetFloatingContextMenuBinding, adapter: FloatingContextMenuAdapter, index: Int) {
        if (expandedItemView[index] == null) {
            expandedItemView.put(index, FloatingContextMenuExpandedView.getExpandedView(binding.root.context))
        }
        val itemView = expandedItemView[index] ?: return
        if (itemView.parent == null) {
            binding.expandedViewContainer.addView(itemView)
        }

        val rootBounds = Rect()
        binding.root.getGlobalVisibleRect(rootBounds)
        val bounds = Rect()
        val recyclerViewItemView = binding.recyclerView.layoutManager?.findViewByPosition(index) ?: return
        recyclerViewItemView.getGlobalVisibleRect(bounds)

        itemView.apply {
            adapter.get(index)?.run {
                setMenuItem(this@run)
            }
            translationY = bounds.top.toFloat() - rootBounds.top - if (bounds.height() < recyclerViewItemView.height) {
                if (bounds.top == rootBounds.top) {
                    recyclerViewItemView.height - bounds.height()
                } else {
                    bounds.height() - recyclerViewItemView.height
                }
            } else {
                0
            }
            layoutParams = (layoutParams as FrameLayout.LayoutParams).apply {
                width = expandedWidth.toInt()
            }
        }

        val startX = bounds.left.toFloat() - dpToPx(4)
        val translationX = collapsedTranslationX - expandedTranslationX
        val endX = startX - translationX
        ObjectAnimator.ofFloat(
                itemView,
                View.TRANSLATION_X,
                startX,
                endX
        ).apply {
            interpolator = LinearInterpolator()
            this.duration = animationDuration
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                    itemView.apply {
                        isHidden = false
                    }
                }
            })
            start()
        }
    }

    private fun collapseItemView(binding: WidgetFloatingContextMenuBinding, index: Int) {
        expandedItemView[index]?.let { itemView ->
            if (itemView.isHidden) {
                return@let
            }
            val bounds = Rect()
            binding.recyclerView.getGlobalVisibleRect(bounds)
            val startX = itemView.translationX
            val endX = bounds.left.toFloat() - dpToPx(4)
            ObjectAnimator.ofFloat(
                    itemView,
                    View.TRANSLATION_X,
                    startX,
                    endX
            ).apply {
                interpolator = LinearInterpolator()
                this.duration = animationDuration
                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        (itemView.parent as ViewGroup?)?.removeView(itemView)
                        expandedItemView.remove(index)
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationStart(animation: Animator?) {
                        itemView.isHidden = true
                    }
                })
                start()
            }
        }
    }

    private fun settle(binding: WidgetFloatingContextMenuBinding) {
        val translationX = binding.content.translationX
        when {
            (translationX in expandedTranslationX..(expandedTranslationX + collapsedTranslationX) / 2) -> {
                settleExpanded(binding)
            }
            (translationX in ((expandedTranslationX + collapsedTranslationX) / 2)..((collapsedTranslationX + dismissTranslationX) / 2)) -> {
                settleCollapsed(binding)
            }
            else -> {
                settleDismiss(binding)
            }
        }
    }

    private fun settleExpanded(binding: WidgetFloatingContextMenuBinding) {
        val translationX = binding.content.translationX
        ObjectAnimator.ofFloat(
                binding.content,
                View.TRANSLATION_X,
                translationX,
                expandedTranslationX
        ).apply {
            interpolator = LinearInterpolator()
            this.duration = animationDuration
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    isSettling.set(false)
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }
            })
            addUpdateListener { setOverlayAlpha(binding) }
            start()
        }
        isMenuExpanded = true
    }

    private fun settleCollapsed(binding: WidgetFloatingContextMenuBinding) {
        val translationX = binding.content.translationX
        ObjectAnimator.ofFloat(
                binding.content,
                View.TRANSLATION_X,
                translationX,
                collapsedTranslationX
        ).apply {
            interpolator = LinearInterpolator()
            this.duration = animationDuration
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    isSettling.set(false)
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }
            })
            addUpdateListener { setOverlayAlpha(binding) }
            start()
        }
        isMenuExpanded = false
    }

    private fun settleDismiss(binding: WidgetFloatingContextMenuBinding) {
        val translationX = binding.content.translationX
        ObjectAnimator.ofFloat(
                binding.content,
                View.TRANSLATION_X,
                translationX,
                dismissTranslationX
        ).apply {
            interpolator = LinearInterpolator()
            this.duration = animationDuration
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    isSettling.set(false)
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }
            })
            addListener(onDismissListener)
            addUpdateListener { setOverlayAlpha(binding) }
            start()
        }
    }

    private fun isValidRange(translationX: Float): Boolean {
        return translationX in 0f..expandedWidth
    }

    private fun setOverlayAlpha(binding: WidgetFloatingContextMenuBinding) {
        if (!cancelable) {
            return
        }
        val alpha = abs((binding.content.width.toFloat() - binding.content.translationX) / binding.content.width.toFloat()) * 0.6f
        if (alpha in 0.0..0.6) {
            binding.overlay.alpha = alpha
        }
    }

    fun show(binding: WidgetFloatingContextMenuBinding) {
        ObjectAnimator.ofFloat(
                binding.content,
                View.TRANSLATION_X,
                dismissTranslationX,
                collapsedTranslationX
        ).apply {
            interpolator = AccelerateDecelerateInterpolator()
            this.duration = animationDuration
            addListener(onShowListener)
            addUpdateListener { setOverlayAlpha(binding) }
            start()
        }
    }

    fun dismiss(binding: WidgetFloatingContextMenuBinding) {
        val translationX = binding.content.translationX
        ObjectAnimator.ofFloat(
                binding.content,
                View.TRANSLATION_X,
                translationX,
                dismissTranslationX
        ).apply {
            interpolator = AccelerateDecelerateInterpolator()
            this.duration = animationDuration
            addListener(onDismissListener)
            addUpdateListener { setOverlayAlpha(binding) }
            start()
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    private fun pxToDp(px: Int): Int {
        return (px / Resources.getSystem().displayMetrics.density).toInt()
    }

    private fun getDeviceScreenSize(context: Context): Point {
        val display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val size = Point()
        display.getSize(size)
        return size
    }
}

fun ViewGroup.getViewByCoordinates(x: Float, y: Float): View? {
    (childCount - 1 downTo 0)
            .map { this.getChildAt(it) }
            .forEach {
                val bounds = Rect()
                it.getGlobalVisibleRect(bounds)
                if (bounds.contains(x.toInt(), y.toInt())) {
                    return it
                }
            }
    return null
}