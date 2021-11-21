package com.example.autocompletetextview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.os.Build

import android.annotation.TargetApi
import android.content.Context
import android.view.View
import android.widget.ListAdapter


class MainActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        disableAutofill()

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, COUNTRIES);

        val textView = findViewById<AutoCompleteTextView>(R.id.countries_list)
        textView.setAdapter(adapter);

        textView.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
            {
                textView.showDropDown()
            }
        }

    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun disableAutofill()
    {
        window.decorView.importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
    }

    private val COUNTRIES =  listOf("Belgium", "Balbe", "Balbus", "France", "Italy", "Germany", "Spain")

    class MyAdapter(context: Context, resource: Int) : ArrayAdapter<String>(context, resource)
    {

    }

}
