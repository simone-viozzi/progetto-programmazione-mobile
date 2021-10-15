package com.example.sharedpref

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import androidx.databinding.DataBindingUtil
import com.example.sharedpref.databinding.ActivityMainBinding


fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)


class MainActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        val sharedPref = getPreferences(Context.MODE_PRIVATE)

        with(binding) {
            editTextString.text = sharedPref.getString(getString(R.string.string_pref), "")?.toEditable()
            editTextInt.text = sharedPref.getInt(getString(R.string.int_pref), 0).toString().toEditable()
            editTextFloat.text = sharedPref.getFloat(getString(R.string.float_pref), 0.0F).toString().toEditable()
            toggleButtonBoolean.isChecked = sharedPref.getBoolean(getString(R.string.boolean_pref), false)
        }

        binding.button.setOnClickListener {
            with (sharedPref.edit()) {
                putString(getString(R.string.string_pref), binding.editTextString.text.toString())
                putInt(getString(R.string.int_pref), binding.editTextInt.text.toString().toInt())
                putFloat(getString(R.string.float_pref), binding.editTextFloat.text.toString().toFloat())
                putBoolean(getString(R.string.boolean_pref), binding.toggleButtonBoolean.isChecked)

                apply()
            }

        }
    }
}