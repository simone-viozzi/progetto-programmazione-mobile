package com.example.receiptApp

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.receiptApp.databinding.ActivityMainBinding
import com.example.receiptApp.home.HomeFragmentDirections
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.math.MathUtils


class MainActivity : AppCompatActivity()
{
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        // TODO this is needed?
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        // TODO it doesn't serve the purpose!
        val theme = resources.newTheme()
        theme.applyStyle(applicationInfo.theme, true)


        val bottomSheetBehavior = BottomSheetBehavior.from(binding.navigationView)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        binding.scrim.visibility = View.GONE

        // TODO there is a better way to bind those?
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        binding.navigationView.setupWithNavController(navController)

        // if i go to a specific route i need to do some specific actions.
        // the action are defined as attributes is the navigation xml
        navController.addOnDestinationChangedListener { navController: NavController,
                                                        navDestination: NavDestination,
                                                        bundle: Bundle? ->

            // handle the fab gravity
            binding.bottomAppBar.fabAlignmentMode =
                bundle?.getInt("FabGravity", R.integer.FabGravityCenter)?.let { resources.getInteger(it) }
                    ?: BottomAppBar.FAB_ALIGNMENT_MODE_CENTER

            // handle the fab hide / show
            if (bundle?.getBoolean("FabShow", true) != false) binding.fab.show() else binding.fab.hide()

            // set the menu of the appbar TODO we most likely want to change how this work and allow to load arbitrary menu
            binding.bottomAppBar.replaceMenu(
                if (bundle?.getBoolean(
                        "MenuShow",
                        true
                    ) != false
                ) R.menu.bottom_bar_menu else R.menu.bottom_bar_menu_hide
            )

            // Display / hide the hamburger menu TODO the theme doesn't work!
            binding.bottomAppBar.navigationIcon = if (bundle?.getBoolean("NavigationIcon", true) == true)
                ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_menu_24, theme) else null

            // set the fab icon based on attributes
            binding.fab.setImageResource(
                bundle?.getInt("FabIcon", R.drawable.ic_baseline_add_24) ?: R.drawable.ic_baseline_add_24
            )
        }

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

        // TODO totally need to do this in another way
        binding.fab.setOnClickListener {
            if (navController.currentDestination == navController.findDestination(R.id.homeFragment))
            {
                val action = HomeFragmentDirections.actionHomeFragmentToAddFragment()
                navController.navigate(action)
            }
        }


        binding.bottomAppBar.setOnMenuItemClickListener {
            when (it.itemId)
            {
                R.id.bottom_bar_menu_about -> {
                    navController.navigate(HomeFragmentDirections.actionHomeFragmentToAboutFragment())
                    true
                }

                R.id.bottom_bar_menu_edit -> {
                    // TODO !!
                    true
                }
                else -> false
            }
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

    override fun onSupportNavigateUp(): Boolean
    {
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed()
    {

    }

}