package com.example.pdfreader

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.pdfreader.activities.ChangeLanguageActivity
import com.example.pdfreader.activities.ConvertPdfActivity
import com.example.pdfreader.activities.ExcelActivity
import com.example.pdfreader.activities.PremiumActivity
import com.example.pdfreader.activities.SearchActivity
import com.example.pdfreader.adapters.FileAdapter
import com.example.pdfreader.adapters.ViewPagerAdapter
import com.example.pdfreader.data.Languages
import com.example.pdfreader.databinding.ActivityMainBinding
import com.example.pdfreader.fragments.PdfFragment
import com.example.pdfreader.fragments.WordFragment
import com.example.pptreader.fragments.ExcelFragment
import com.example.pptreader.fragments.PptFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {
    //
    private val pdfFragment = PdfFragment()
    private val wordFragment = WordFragment()
    private val excelFragment = ExcelFragment()
    private val pptFragment = PptFragment()

    private lateinit var binding: ActivityMainBinding
    private var doubleClickToExit: Long = 0

    lateinit var addFAB: FloatingActionButton
    lateinit var imageToPdfFAB: FloatingActionButton
    lateinit var scanFAB: FloatingActionButton
    lateinit var toolBar: LinearLayout
    private lateinit var adapter: FileAdapter
    var fabVisible = false

    private var currentNavItem = R.id.documentation


    private val pagerAdapter by lazy {
        ViewPagerAdapter(supportFragmentManager, lifecycle).apply {
            this.addFragments(pdfFragment, wordFragment, excelFragment, pptFragment)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolBar = findViewById(R.id.toolbar)
        clickToolbar()


        adapter = FileAdapter(ArrayList(), this)

        if (ContextCompat.checkSelfPermission(this, manageExternalStoragePermission) == PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this@MainActivity, getString(R.string.access_granted), Toast.LENGTH_SHORT).show()
        } else {
            requestPermission()
        }


        setupTablayout()
        setupViewPager()
        setupBottomNavigition()

        setFAB()

    }


    override fun onResume() {
        super.onResume()

        // Change language
        val prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE)
        val lang = prefs.getString("Language", "en") ?: "en"
        Log.e("DEBUG", lang)
        Languages.loadLocale(this)
    }
    private fun setupTablayout() {
        binding.tabLayoutView.addTab(binding.tabLayoutView.newTab().setText("PDF"))
        binding.tabLayoutView.addTab(binding.tabLayoutView.newTab().setText("WORD"))
        binding.tabLayoutView.addTab(binding.tabLayoutView.newTab().setText("EXCEL"))
        binding.tabLayoutView.addTab(binding.tabLayoutView.newTab().setText("PPT"))

        binding.tabLayoutView.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.position?.let {
                    binding.viewpage.currentItem = it
                    when (currentNavItem) {
                        R.id.documentation -> {
                            when (it) {
                                0 -> pdfFragment.loadAllFilePDF()
                                1 -> wordFragment.loadAllFileWord()
                                2 -> excelFragment.loadAllFileExcel()
                                3 -> pptFragment.loadAllFilePPT()
                            }
                        }
                        R.id.favor -> {
                            when (it) {
                                0 -> pdfFragment.loadFavoriteFilePdf()
                                1 -> wordFragment.loadFavoriteFileWord()
                                2 -> excelFragment.loadFavoriteFileExcel()
                                3 -> pptFragment.loadFavoriteFilePpt()
                            }
                        }
                        R.id.recent -> {
                            when (it) {
                                0 -> pdfFragment.loadPdfFileByPath()
                                1 -> wordFragment.loadPdfFileByPath()
                                2 -> excelFragment.loadPdfFileByPath()
                                3 -> pptFragment.loadPdfFileByPath()
                            }
                        }
                    }


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
    }
    private fun setupViewPager() {
        binding.viewpage.apply {
            adapter = pagerAdapter
            offscreenPageLimit = pagerAdapter.itemCount
        }
    }

    private fun setupBottomNavigition() {
        binding.navigationmenu.setOnItemSelectedListener {item ->
            currentNavItem = item.itemId
            when(item.itemId){
                R.id.documentation -> {
                    when(binding.viewpage.currentItem) {
                        0 -> pdfFragment.loadAllFilePDF()
                        1 -> wordFragment.loadAllFileWord()
                        2 -> excelFragment.loadAllFileExcel()
                        3 -> pptFragment.loadAllFilePPT()
                    }
                    true
                }
                R.id.favor -> {
                    when(binding.viewpage.currentItem) {
                        0 -> pdfFragment.loadFavoriteFilePdf()
                        1 -> wordFragment.loadFavoriteFileWord()
                        2 -> excelFragment.loadFavoriteFileExcel()
                        3 -> pptFragment.loadFavoriteFilePpt()
                    }
                    true
                }
                R.id.recent -> {
                    when(binding.viewpage.currentItem) {
                        0 -> pdfFragment.loadPdfFileByPath()
                        1 -> wordFragment.loadPdfFileByPath()
                        2 -> excelFragment.loadPdfFileByPath()
                        3 -> pptFragment.loadPdfFileByPath()
                    }
                    true
                }

                else -> true
            }
        }
    }

    private val manageExternalStoragePermission = Manifest.permission.MANAGE_EXTERNAL_STORAGE

    private val requestPermissionLauncher: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(this@MainActivity, getString(R.string.access_granted), Toast.LENGTH_SHORT).show()
        } else {
            showPermissionDeniedDialog()
        }
    }
    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, manageExternalStoragePermission)) {
            showPermissionRationale()
        } else {
            requestPermissionLauncher.launch(manageExternalStoragePermission)
        }
    }

    private fun showPermissionRationale() {
        AlertDialog.Builder(this)
            .setMessage("Ứng dụng cần quyền truy cập tất cả các tệp để hoạt động.")
            .setCancelable(false)
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                // Yêu cầu quyền
                requestPermissionLauncher.launch(manageExternalStoragePermission)
            }
            .show()
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.grant_access))
            .setMessage(getString(R.string.file_access_denied))
            .setCancelable(false)
            .setPositiveButton(R.string.setting) { _, _ ->
                // Mở màn hình cài đặt ứng dụng để cấp quyền
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->

            }
            .show()
    }

    private fun clickToolbar() {
        toolBar.setBackgroundColor(Color.parseColor("#b30b00"))
        binding.tabLayoutView.setBackgroundColor(Color.parseColor("#b30b00"))
        window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.pdf)

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
                    R.id.toolbarName -> {
                        startActivity(Intent(this, ExcelActivity::class.java))
                        Toast.makeText(this@MainActivity, "" + item.title, Toast.LENGTH_SHORT).show()
                    }
                    R.id.toolbarEdit -> {
                        Toast.makeText(this@MainActivity, "" + item.title, Toast.LENGTH_SHORT).show()
                    }
                    R.id.toolbarSize -> {
                        Toast.makeText(this@MainActivity, "" + item.title, Toast.LENGTH_SHORT).show()
                    }

                }
                true
            })
            popupMenu.show()
        }
    }

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
            startActivity(Intent(this, ConvertPdfActivity::class.java))
            Toast.makeText(this@MainActivity, getString(R.string.image_to_pdf), Toast.LENGTH_LONG).show()
        }

        scanFAB.setOnClickListener {
            startActivity(Intent(this, ConvertPdfActivity::class.java))
            Toast.makeText(this@MainActivity, getString(R.string.scan_document), Toast.LENGTH_LONG).show()
        }
    }

    override fun onBackPressed() {
        if (doubleClickToExit + 1500 > System.currentTimeMillis())  {
            super.onBackPressed()
        } else {
            Toast.makeText(getBaseContext(),
                getString(R.string.double_click_to_exit), Toast.LENGTH_SHORT).show();
        }
        doubleClickToExit = System.currentTimeMillis()
    }
}