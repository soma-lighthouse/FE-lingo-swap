package com.lighthouse.android.common_ui.base

import android.content.SharedPreferences
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.lighthouse.android.common_ui.util.Injector
import com.lighthouse.navigation.MainNavigator
import dagger.hilt.android.EntryPointAccessors

abstract class BindingActivity<T : ViewDataBinding>(
    @LayoutRes private val layoutRes: Int,
) : AppCompatActivity() {
    protected lateinit var binding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutRes)
        binding.lifecycleOwner = this
    }

    val mainNavigator: MainNavigator by lazy {
        EntryPointAccessors.fromActivity(
            this,
            Injector.MainNavigatorInjector::class.java
        ).mainNavigator()
    }

    private val sharedPreferences: SharedPreferences by lazy {
        EntryPointAccessors.fromActivity(
            this,
            Injector.SharedPreferencesInjector::class.java
        ).sharedPreferences()
    }

}