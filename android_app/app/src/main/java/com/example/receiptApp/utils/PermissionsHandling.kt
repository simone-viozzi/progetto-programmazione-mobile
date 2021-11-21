package com.example.receiptApp.utils

import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import timber.log.Timber


/**
 * Permissions handling
 *  helper class to handle the permissions request
 *
 * @property frag -> the fragment this helper belongs to
 */
class PermissionsHandling(private val frag: Fragment)
{
    // callbacks for when the permissions are granted on denied
    private var granted: (() -> Unit)? = null
    private var denied: (() -> Unit)? = null

    // the callbacks that handle the  response of the user
    private val requestPermissionLauncher = frag.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {

        Timber.d("it -> $it")

        // i only consider permission granted if all permissions are granted
        if (it.values.reduce{ b1, b2 -> b1 && b2 })
        {
           granted?.invoke()
        } else
        {
            denied?.invoke()
        }
    }

    fun setCallbacksAndAsk(permissions: Array<String>, granted: (() -> Unit)? = null, denied: (()-> Unit)? = null)
    {
        this.granted = granted
        this.denied = denied
        askPermissions(permissions)
    }

    private fun askPermissions(permissions: Array<String>)
    {
        frag.context?.let{ context ->
            // before asking i check if the permissions are already granted
            val permToAsk = permissions.filter {
                ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
            }

            if (permToAsk.isEmpty())
            {
                // if so, i will just call the callback
                granted?.invoke()
            }
            else
            {
                // otherwise, i will ask the permissions that are not granted
                requestPermissionLauncher.launch(permToAsk.toTypedArray())
            }
        }
    }
}
