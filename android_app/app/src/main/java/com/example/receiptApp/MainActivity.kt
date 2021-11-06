package com.example.receiptApp

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.receiptApp.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.math.MathUtils


class MainActivity : AppCompatActivity()
{
    private lateinit var navController: NavController
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)


        ////////////////////// init the navigation view behavior //////////////////////
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.navigationView)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        binding.scrim.visibility = View.GONE


        ////////////////////// set the nav navController //////////////////////
        // TODO there is a better way to bind those?
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        binding.navigationView.setupWithNavController(navController)



        // callBack for then the user click on the hamburger icon
        binding.bottomAppBar.setNavigationOnClickListener {
            // open the navigation drawer
            binding.scrim.visibility = View.VISIBLE
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            binding.fab.hide()
        }


        // callBack for when the user select an item in the navigationView menu.
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            // close the bottom navigation drawer
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            // handle the navigation
            binding.scrim.visibility = View.GONE
            NavigationUI.onNavDestinationSelected(menuItem, navController)
        }


        binding.scrim.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            binding.fab.show()
            binding.scrim.visibility = View.GONE
        }


        // leave this method alone, it's doing it's job
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback()
        {
            override fun onSlide(bottomSheet: View, slideOffset: Float)
            {
                val baseColor = Color.BLACK
                // 60% opacity
                val baseAlpha = ResourcesCompat.getFloat(resources, R.dimen.material_emphasis_medium)
                // Map slideOffset from [-1.0, 1.0] to [0.0, 1.0]
                val offset = (slideOffset - (-1f)) / (1f - (-1f)) * (1f - 0f) + 0f
                val alpha = MathUtils.lerp(0f, 255f, offset * baseAlpha).toInt()
                val color = Color.argb(alpha, baseColor.red, baseColor.green, baseColor.blue)

                binding.scrim.setBackgroundColor(color)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int)
            {
            }
        })
    }

    override fun onBackPressed()
    {
        Toast.makeText(this, "halooo", Toast.LENGTH_SHORT).show()
    }




}