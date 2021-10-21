package com.example.sharedpref

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import androidx.databinding.DataBindingUtil
import com.example.sharedpref.databinding.ActivityMainBinding


/**
 * To editable -> an extension function of string to set the text of an editText in one line
 * (because editText.text is Editable, so i need to convert a String to an Editable)
 */
fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)


class MainActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        val sharedPref = getPreferences(Context.MODE_PRIVATE)

        // take the values auto of the sharedPref and set them in the UI
        with(binding) {
            editTextString.text = sharedPref.getString(getString(R.string.string_pref), "")?.toEditable()
            editTextInt.text = sharedPref.getInt(getString(R.string.int_pref), 0).toString().toEditable()
            editTextFloat.text = sharedPref.getFloat(getString(R.string.float_pref), 0.0F).toString().toEditable()
            toggleButtonBoolean.isChecked = sharedPref.getBoolean(getString(R.string.boolean_pref), false)
        }

        // when the button get pressed take the data out of the UI and set it into the sharedPref
        binding.button.setOnClickListener {
            with (sharedPref.edit()) {
                putString(getString(R.string.string_pref), binding.editTextString.text.toString())
                putInt(getString(R.string.int_pref), binding.editTextInt.text.toString().toInt())
                putFloat(getString(R.string.float_pref), binding.editTextFloat.text.toString().toFloat())
                putBoolean(getString(R.string.boolean_pref), binding.toggleButtonBoolean.isChecked)

                // remember to call apply at the end!
                apply()
            }

        }
    }
}