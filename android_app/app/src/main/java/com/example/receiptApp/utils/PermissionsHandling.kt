package com.example.receiptApp.utils

import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import timber.log.Timber

class PermissionsHandling(private val frag: Fragment)
{
    private var granted: (() -> Unit)? = null
    private var denied: (() -> Unit)? = null

    private val requestPermissionLauncher = frag.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {

        Timber.d("it -> $it")

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
            val permToAsk = permissions.filter {
                ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
            }

            if (permToAsk.isEmpty())
            {
                granted?.invoke()
            }
            else
            {
                requestPermissionLauncher.launch(permToAsk.toTypedArray())
            }
        }
    }
}
