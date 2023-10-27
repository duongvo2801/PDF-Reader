package com.example.pdfreader.model

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pdfreader.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MyBottomSheetDialogFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.bottom_dialog, container, false)
        return view
    }
}

