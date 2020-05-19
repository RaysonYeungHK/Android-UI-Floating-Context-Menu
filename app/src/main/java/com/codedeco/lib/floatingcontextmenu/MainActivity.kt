package com.codedeco.lib.floatingcontextmenu

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.codedeco.lib.floatingcontextmenu.databinding.ActivityMainBinding
import com.codedeco.lib.ui.widget.menu.context.ContextMenuItem
import com.codedeco.lib.ui.widget.menu.context.floating.FloatingContextMenu
import com.codedeco.lib.ui.widget.menu.context.floating.FloatingContextMenuListener
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel

    private var floatingContextMenu: FloatingContextMenu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)

        binding.apply {
            val context = this@MainActivity
            val listener = object : FloatingContextMenuListener {
                override fun onMenuShow() {
                    Snackbar.make(binding.root, "Floating Context Menu is shown", Snackbar.LENGTH_LONG)
                            .show()
                }

                override fun onMenuDismiss() {
                    Snackbar.make(binding.root, "Floating Context Menu is dismissed", Snackbar.LENGTH_LONG)
                            .show()
                    floatingContextMenu = null
                    example1.isEnabled = true
                    example2.isEnabled = true
                    example3.isEnabled = true
                    items5.isEnabled = false
                    items2.isEnabled = false
                }

                override fun onMenuItemClick(menuItem: ContextMenuItem) {
                    Snackbar.make(binding.root, "You just selected $menuItem", Snackbar.LENGTH_LONG)
                            .show()
                }

            }
            example1.setOnClickListener {
                if (floatingContextMenu != null) {
                    return@setOnClickListener
                }
                floatingContextMenu = FloatingContextMenu.make(
                        context = context,
                        items = viewModel.example1Items,
                        listener = listener,
                        cancelable = true
                ).show()
            }

            example2.setOnClickListener {
                if (floatingContextMenu != null) {
                    return@setOnClickListener
                }
                floatingContextMenu = FloatingContextMenu.make(
                        context = context,
                        items = viewModel.example2Items,
                        listener = listener,
                        cancelable = true
                ).show()
            }

            example3.setOnClickListener {
                if (floatingContextMenu != null) {
                    return@setOnClickListener
                }
                floatingContextMenu = FloatingContextMenu.make(
                        context = context,
                        items = viewModel.example3Items,
                        listener = listener,
                        cancelable = true
                ).show()
            }

            example4.setOnClickListener {
                if (floatingContextMenu != null) {
                    return@setOnClickListener
                }
                example1.isEnabled = false
                example2.isEnabled = false
                example3.isEnabled = false
                items5.isEnabled = true
                items2.isEnabled = true
                floatingContextMenu = FloatingContextMenu.make(
                        context = context,
                        items = viewModel.example4Items2,
                        listener = listener,
                        cancelable = false
                ).show()
            }

            items5.setOnClickListener {
                floatingContextMenu?.setItems(viewModel.example4Items5)
            }

            items2.setOnClickListener {
                floatingContextMenu?.setItems(viewModel.example4Items2)
            }
        }
    }

    override fun onBackPressed() {
        if (floatingContextMenu != null) {
            floatingContextMenu?.dismiss()
        } else {
            super.onBackPressed()
        }
    }
}
