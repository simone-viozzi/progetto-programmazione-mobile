package com.example.bottom_app_bar_fab

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.example.bottom_app_bar_fab.databinding.ActivityMainBinding
import com.example.bottom_app_bar_fab.home.HomeFragment
import com.example.bottom_app_bar_fab.home.HomeFragmentDirections
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.math.MathUtils


class MainActivity : AppCompatActivity()
{
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        setSupportActionBar(binding.topToolBar)
        supportActionBar?.hide();


        val bottomSheetBehavior = BottomSheetBehavior.from(binding.navigationView)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        binding.navigationView.setupWithNavController(navController)

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            // Handle menu item selected
            binding.navigationView.setCheckedItem(menuItem)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            binding.fab.show()

            NavigationUI.onNavDestinationSelected(menuItem, navController)
        }

        binding.scrim.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            binding.fab.show()
        }


        binding.fab.setOnClickListener {
            if (navController.currentDestination == navController.findDestination(R.id.homeFragment))
            {
                val action = HomeFragmentDirections.actionHomeFragmentToAddFragment()
                navController.navigate(action)
                //binding.fab.hide()
                binding.bottomAppBar.setFabAlignmentModeAndReplaceMenu(
                    BottomAppBar.FAB_ALIGNMENT_MODE_END,
                    R.menu.bottom_bar_menu_hide
                )
                supportActionBar?.show()
                supportActionBar?.setHomeButtonEnabled(true)

                // TODO l'animazione non e' fluida!
                binding.fab.setImageResource(R.drawable.ic_baseline_check_24)
            }
        }

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment, R.id.archiveFragment,  R.id.graphsFragment)
        )

        setupActionBarWithNavController(navController, appBarConfiguration)


        binding.bottomAppBar.setNavigationOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            binding.fab.hide()
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback()
        {
            override fun onSlide(bottomSheet: View, slideOffset: Float)
            {
                val baseColor = Color.BLACK
                // 60% opacity
                val baseAlpha =
                    ResourcesCompat.getFloat(resources, R.dimen.material_emphasis_medium)
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

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }

    override fun onBackPressed() {
        navController.navigateUp(appBarConfiguration)
    }
}