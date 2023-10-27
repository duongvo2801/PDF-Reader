package com.example.pdfreader

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.pdfreader.activities.ChangeLanguageActivity
import com.example.pdfreader.activities.ConvertPdfActivity
import com.example.pdfreader.activities.PremiumActivity
import com.example.pdfreader.activities.SearchActivity
import com.example.pdfreader.adapters.ViewPagerAdapter
import com.example.pdfreader.data.Libs
import com.example.pdfreader.databinding.ActivityMainBinding
import com.example.pdfreader.fragments.PdfFragment
import com.example.pdfreader.fragments.WordFragment
import com.example.pptreader.fragments.ExcelFragment
import com.example.pptreader.fragments.PptFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    lateinit var addFAB: FloatingActionButton
    lateinit var imageToPdfFAB: FloatingActionButton
    lateinit var scanFAB: FloatingActionButton
    var fabVisible = false

    private val pdfFragment by lazy { PdfFragment() }
    private val wordFragment by lazy { WordFragment() }
    private val excelFragment by lazy { ExcelFragment() }
    private val pptFragment by lazy { PptFragment() }
    private val pagerAdapter by lazy {
        ViewPagerAdapter(supportFragmentManager, lifecycle).apply {
            this.addFragments(pdfFragment, wordFragment, excelFragment, pptFragment)
        }
    }

    fun checkPermission() {
        Dexter.withContext(this)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    // Permission granted, you can now proceed to set up your RecyclerView

                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    // Handle permission denied, for example, show a message or request again
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    // Optionally show a rationale to the user before the permission request
                    token?.continuePermissionRequest()
                }
            })
            .check()
    }

    override fun onResume() {
        super.onResume()

        // Change language
        val prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE)
        val lang = prefs.getString("Language", "en") ?: "en"
        Log.e("DEBUG", lang)
        Libs.loadLocale(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val toolBar = findViewById<LinearLayout>(R.id.toolbar)
        clickToolbar()

        checkPermission()
        setFAB()


        binding.tabLayoutView.addTab(binding.tabLayoutView.newTab().setText("PDF"))
        binding.tabLayoutView.addTab(binding.tabLayoutView.newTab().setText("WORD"))
        binding.tabLayoutView.addTab(binding.tabLayoutView.newTab().setText("EXCEL"))
        binding.tabLayoutView.addTab(binding.tabLayoutView.newTab().setText("PPT"))

        toolBar.setBackgroundColor(Color.parseColor("#b30b00"))
        binding.tabLayoutView.setBackgroundColor(Color.parseColor("#b30b00"))
        window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.pdf)

        binding.viewpage.apply {
            adapter = pagerAdapter
            offscreenPageLimit = pagerAdapter.itemCount
        }


        binding.tabLayoutView.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.position?.let {
                    binding.viewpage.currentItem = it
                    when(it) {
                        0 -> {

                            toolBar.setBackgroundColor(Color.parseColor("#b30b00"))
                            binding.tabLayoutView.setBackgroundColor(Color.parseColor("#b30b00"))
                            window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.pdf)
                        }
                        1 -> {
                            toolBar.setBackgroundColor(Color.parseColor("#185abd"))
                            binding.tabLayoutView.setBackgroundColor(Color.parseColor("#185abd"))
                            window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.word)
                        }
                        2 -> {
                            toolBar.setBackgroundColor(Color.parseColor("#107a40"))
                            binding.tabLayoutView.setBackgroundColor(Color.parseColor("#107a40"))
                            window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.excel)
                        }
                        3 -> {
                            toolBar.setBackgroundColor(Color.parseColor("#c43f1d"))
                            binding.tabLayoutView.setBackgroundColor(Color.parseColor("#c43f1d"))
                            window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.ppt)

                        }
                        else -> {
                            toolBar.setBackgroundColor(Color.parseColor("#b30b00"))
                            binding.tabLayoutView.setBackgroundColor(Color.parseColor("#b30b00"))
                            window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.pdf)
                        }

                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        binding.navigationmenu.setOnItemSelectedListener {
            when(it.itemId){
                R.id.documentation -> {
                    when(binding.viewpage.currentItem) {
                        0 -> {
                            pdfFragment.loadAllFilePDF()
                            true
                        }
                        1 -> {
                            wordFragment.loadAllFileWord()
                            true
                        }
                        2 -> {
                            excelFragment.loadAllFileExcel()
                            true
                        }
                        3 -> {
                            pptFragment.loadAllFilePPT()
                            true
                        }
                    }

                    true
                }
                R.id.favor -> {
                    when(binding.viewpage.currentItem) {
                        0 -> {
                            //pdf
                            pdfFragment.loadFavoriteFilePdf()
                            true
                        }
                        1 -> {
                            wordFragment.loadFavoriteFileWord()
                            true
                        }
                        2 -> {
                            //excel
                            excelFragment.loadFavoriteFileExcel()
                            true
                        }
                        3 -> {
                            //ppt
                            pptFragment.loadFavoriteFilePpt()
                            true
                        }
                    }

                    true
                }
                R.id.recent -> {

                    true
                }

                else -> {
                    true
                }
            }
        }

        imageToPdfFAB.setOnClickListener {
            startActivity(Intent(this, ConvertPdfActivity::class.java))
        }
        scanFAB.setOnClickListener {
            startActivity(Intent(this, ConvertPdfActivity::class.java))
        }
    }

    private fun clickToolbar() {
        val getPremium = findViewById<ImageView>(R.id.get_premium)
        val changeLanguage = findViewById<ImageView>(R.id.change_language)
        val search = findViewById<ImageView>(R.id.search)
        val sort = findViewById<ImageView>(R.id.sort)

        getPremium.setOnClickListener {
            startActivity(Intent(this, PremiumActivity::class.java))
        }
        changeLanguage.setOnClickListener {
            startActivity(Intent(this, ChangeLanguageActivity::class.java))
        }
        search.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
        sort.setOnClickListener {
            val popupMenu: PopupMenu = PopupMenu(this, sort)
            popupMenu.menuInflater.inflate(R.menu.popup_toolbar, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.toolbarName ->
                        Toast.makeText(this@MainActivity, "" + item.title, Toast.LENGTH_SHORT).show()
                    R.id.toolbarEdit ->
                        Toast.makeText(this@MainActivity, "" + item.title, Toast.LENGTH_SHORT).show()
                    R.id.toolbarSize ->
                        Toast.makeText(this@MainActivity, "" + item.title, Toast.LENGTH_SHORT).show()
                }
                true
            })
            popupMenu.show()
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        val inflater : MenuInflater = menuInflater
//        inflater.inflate(R.menu.menu_top, menu)
//        return true
//    }
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when(item.itemId) {
//            R.id.sort -> {
//                softDialog()
//            }
//            R.id.search -> {
//                startActivity(Intent(this, SearchActivity::class.java))
//            }
//            R.id.change_language -> {
//                intent = Intent(this, ChangeLanguageActivity::class.java)
//                startActivity(intent)
//            }
//            R.id.get_premium -> {
//                Toast.makeText(this, getString(R.string.toolbar_coming_soon), Toast.LENGTH_SHORT).show()
//
//            }
//        }
//
//        return super.onOptionsItemSelected(item)
//    }

//    private fun softDialog() {
//        var options = arrayOf(getString(R.string.sort_asc), getString(R.string.sort_desc))
//        val dialog = AlertDialog.Builder(this)
//        dialog.setTitle(getString(R.string.dialog_title))
//            .setItems(options){
//                dialogIntetface, i ->
//                if(i == 0) {
//                    dialogIntetface.dismiss()
//
////                    itemList.sortBy{it.title}
//                    // refresh adapter after sort
//                    adapter.notifyDataSetChanged()
//                } else if(i == 1) {
//                    dialogIntetface.dismiss()
//
////                    itemList.sortByDescending {it.title}
//                    // refresh adapter after sort
//                    adapter.notifyDataSetChanged()
//                }
//            }.show()
//    }


    fun setFAB() {
        addFAB = findViewById(R.id.FABAdd);
        imageToPdfFAB = findViewById(R.id.FABImageToPdf);
        scanFAB = findViewById(R.id.FABScan);

        fabVisible = false
        addFAB.setOnClickListener {
            if (!fabVisible) {
                imageToPdfFAB.show()
                scanFAB.show()

                imageToPdfFAB.visibility = View.VISIBLE
                scanFAB.visibility = View.VISIBLE
                addFAB.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_close_24))
                fabVisible = true
            } else {
                imageToPdfFAB.hide()
                scanFAB.hide()

                imageToPdfFAB.visibility = View.GONE
                scanFAB.visibility = View.GONE
                addFAB.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_add_24))
                fabVisible = false
            }
        }

        imageToPdfFAB.setOnClickListener {
            Toast.makeText(this@MainActivity, getString(R.string.image_to_pdf), Toast.LENGTH_LONG).show()
        }

        scanFAB.setOnClickListener {
            Toast.makeText(this@MainActivity, getString(R.string.scan_document), Toast.LENGTH_LONG).show()
        }
    }

}