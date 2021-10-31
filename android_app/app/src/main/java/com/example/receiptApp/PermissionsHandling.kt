package com.example.receiptApp

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

class PermissionsHandling(frag: Fragment,
                          permissions: Array<String>,
                          granted: (() -> Unit),
                          denied: (()-> Unit)
)
{
    private val requestPermissionLauncher = frag.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {

        if (it.values.reduce{ b1, b2 -> b1 && b2 })
        {
           granted.invoke()
        } else
        {
            denied.invoke()
        }
    }

    init
    {
        frag.context?.let{ context ->
            val permToAsk = permissions.filter {
                ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
            }

            if (permToAsk.isEmpty())
            {
                granted.invoke()
            }
            else
            {
                requestPermissionLauncher.launch(permToAsk.toTypedArray())
            }
        }
    }
}