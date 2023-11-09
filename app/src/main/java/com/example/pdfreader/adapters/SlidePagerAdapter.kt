package com.example.pdfreader.adapters

import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.viewpager.widget.PagerAdapter

class SlidePagerAdapter(private val htmlContentList: List<String>) : PagerAdapter() {

    override fun getCount(): Int {
        return htmlContentList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val webView = WebView(container.context)
        webView.settings.javaScriptEnabled = true
        webView.loadDataWithBaseURL(null, htmlContentList[position], "text/html", "utf-8", null)
        container.addView(webView)
        return webView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}