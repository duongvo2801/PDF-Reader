package com.example.pdfreader.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pdfreader.R
import com.example.pdfreader.entities.RecentItem

class HistoryAdapter(private val recentItems: MutableList<RecentItem>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    fun clear() {
        recentItems.clear()
        notifyDataSetChanged()
    }

    fun addAll(newRecentItems: List<RecentItem>) {
        recentItems.addAll(newRecentItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val historyItem = recentItems[position]
        // Gán thông tin từ historyItem vào các View trong ViewHolder
    }

    override fun getItemCount(): Int {
        return recentItems.size
    }

    fun updateHistoryList(newRecentItems: List<RecentItem>) {
        // Cập nhật danh sách lịch sử và thông báo cho RecyclerView
        recentItems.clear()
        recentItems.addAll(newRecentItems)
        notifyDataSetChanged()
    }
}

