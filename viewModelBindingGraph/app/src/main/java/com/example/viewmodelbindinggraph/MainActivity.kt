package com.example.viewmodelbindinggraph

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.viewmodelbindinggraph.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        // there are two ways to get the view binding
        //val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get the navHostFragment and the navController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.NavControllerView) as NavHostFragment
        val navController: NavController = navHostFragment.navController

        // bind the bottomNavigationView with the navController
        binding.bottomNavigationView.setupWithNavController(navController)

        // remove the ugly background of the bottomNavigationView
        binding.bottomNavigationView.setBackgroundColor(ContextCompat.getColor(applicationContext, android.R.color.transparent));
    }
}