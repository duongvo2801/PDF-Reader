package com.example.pdfreader.data

interface ItemClickListener<T> {
    fun onItemClick(position: Int, item: T)
}
