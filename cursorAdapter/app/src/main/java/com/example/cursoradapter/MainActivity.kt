package com.example.cursoradapter

import android.Manifest
import android.Manifest.permission.ACCESS_MEDIA_LOCATION
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.cursoradapter.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


data class MyImg(val name: String, val contentUri: Uri, val thumbnail: Bitmap)

class MainActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityMainBinding
    private val myViewModel: MyViewModel by viewModels {
        MyViewModelFactory((application as App).dataSourcePagin)
    }


    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->

        }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        binding.recyclerView.layoutManager = GridLayoutManager(this, 3)

        val adapter = Adapter(ContextCompat.getDrawable(this, R.drawable.ic_baseline_image_24)!!)


        binding.recyclerView.adapter = adapter

        lifecycleScope.launch {
            myViewModel.flow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }


    }
}