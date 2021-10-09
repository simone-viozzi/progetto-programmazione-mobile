/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.navigation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import com.example.android.navigation.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private val TAG: String = "Android Trivia"
    private val app_class: String = "MainActivity"
    private var num: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        val navController = this.findNavController(R.id.myNavHostFragment)

        //Setta un'action bar
        drawerLayout = binding.drawerLayout
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)

        // Setta il Navigation Drawer
        NavigationUI.setupWithNavController(binding.navView, navController)

        Log.d(TAG, "$app_class onCreate() launched")
    }

    override fun onRestart() {
        super.onRestart()

        Log.d(TAG, "$app_class onRestart() launched")
        Log.d(TAG, "$app_class restarted with num = $num")
    }

    override fun onStart() {
        super.onStart()

        Log.d(TAG, "$app_class onStart() launched")
    }

    override fun onResume() {
        super.onResume()

        Log.d(TAG, "$app_class onResume() launched")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {

        Log.d(TAG, "$app_class onRestoreInstanceState() launched")
        num = savedInstanceState.getInt("num")
        Log.d(TAG, "$app_class onRestoreInstanceState() restored $num")

        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onPause() {
        super.onPause()

        Log.d(TAG, "$app_class onPause() launched")
    }

    override fun onStop() {
        super.onStop()

        Log.d(TAG, "$app_class onStop() launched")
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d(TAG, "$app_class onDestroy() launched")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d(TAG, "$app_class onSaveInstanceState() launched")
        num = (1..10).random()
        outState.putInt("num", num)
        Log.d(TAG, "$app_class onSaveInstanceState() saved $num")
        super.onSaveInstanceState(outState)
    }

    // Setta una app bar con il bottone Up
    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return navController.navigateUp(drawerLayout)
    }
}
