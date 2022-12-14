package com.dindamaylan.storyapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.dindamaylan.storyapp.databinding.ActivityMainBinding
import com.dindamaylan.storyapp.ui.login.LoginViewModel
import com.dindamaylan.storyapp.utils.Helper.margin
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navigation: BottomNavigationView = binding.navigationBottom

        val navHost = supportFragmentManager.findFragmentById(R.id.fcw_main) as NavHostFragment
        val navControl = navHost.navController
        navigation.setupWithNavController(navControl)

        loginViewModel.getAuthToken.observe(this) { token ->
            if (token.isBlank()) {
                navToLogin(navControl)
            }

            else{
                navToDashboard(navControl)
            }
        }

        btmNavVisible(navControl)
    }

    private fun navToLogin(navController: NavController) {
        val navOptions = NavOptions.Builder().setPopUpTo(R.id.navigation_main, true).build()
        navController.navigate(R.id.loginFragment, null, navOptions)
    }

    private fun navToDashboard(navController: NavController) {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.navigation_main, true)
            .setLaunchSingleTop(true)
            .build()
        navController.navigate(R.id.dashboardFragment, null, navOptions)
    }

    private fun btmNavVisible(navController: NavController) {
        val btmNav = binding.navigationBottom
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.dashboardFragment,
                R.id.othersFragment -> {
                    binding.fcwMain.margin(bottom = 56f)
                    btmNav.isVisible = true
                }
                else -> {
                    binding.fcwMain.margin(bottom = 0f)
                    btmNav.isVisible = false
                }
            }
        }
    }

    fun askPermissionCamerax(code: Int = 10): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                code
            )
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            10 -> {
                if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    if (askPermissionCamerax())
                        Toast.makeText(
                            this,
                            getString(R.string.camera_denied),
                            Toast.LENGTH_SHORT
                        ).show()
                }
            }
        }
    }
}