package com.example.pdfreader

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.pdfreader.activities.ChangeLanguageActivity
import com.example.pdfreader.activities.ConvertPdfActivity
import com.example.pdfreader.activities.PremiumActivity
import com.example.pdfreader.activities.SearchActivity
import com.example.pdfreader.adapters.ViewPagerAdapter
import com.example.pdfreader.data.Languages
import com.example.pdfreader.databinding.ActivityMainBinding
import com.example.pdfreader.fragments.PdfFragment
import com.example.pdfreader.fragments.WordFragment
import com.example.pptreader.fragments.ExcelFragment
import com.example.pptreader.fragments.PptFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.karumi.dexter.Dexter
import com.karumi.dexter.DexterBuilder
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

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
    var fabVisible = false

    //
    lateinit var dexter: DexterBuilder

    private var currentNavItem = R.id.documentation


    private val pagerAdapter by lazy {
        ViewPagerAdapter(supportFragmentManager, lifecycle).apply {
            this.addFragments(pdfFragment, wordFragment, excelFragment, pptFragment)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolBar = findViewById(R.id.toolbar)
        clickToolbar()


        if (Environment.isExternalStorageManager()) {
            getPermission()
        } else {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            val uri: Uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
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
        val tabTitles = listOf("PDF", "WORD", "EXCEL", "PPT")

        for (title in tabTitles) {
            binding.tabLayoutView.addTab(binding.tabLayoutView.newTab().setText(title))
        }

        binding.tabLayoutView.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.position?.let {
                    binding.viewpage.currentItem = it
                    when (currentNavItem) {
                        R.id.documentation -> {
                            when (it) {
                                0 -> pdfFragment.loadAllFile()
                                1 -> wordFragment.loadAllFile()
                                2 -> excelFragment.loadAllFile()
                                3 -> pptFragment.loadAllFile()
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

                    updateColorsForTab(it)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }
    private fun updateColorsForTab(position: Int) {
        val colors = when (position) {
            0 -> Pair("#b30b00", R.color.pdf)
            1 -> Pair("#185abd", R.color.word)
            2 -> Pair("#107a40", R.color.excel)
            3 -> Pair("#c43f1d", R.color.ppt)
            else -> Pair("#b30b00", R.color.pdf)
        }

        val (toolbarColor, statusBarColorResId) = colors

        toolBar.setBackgroundColor(Color.parseColor(toolbarColor))
        binding.tabLayoutView.setBackgroundColor(Color.parseColor(toolbarColor))
        window.statusBarColor = ContextCompat.getColor(applicationContext, statusBarColorResId)
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
                        0 -> pdfFragment.loadAllFile()
                        1 -> wordFragment.loadAllFile()
                        2 -> excelFragment.loadAllFile()
                        3 -> pptFragment.loadAllFile()
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

    private fun getPermission() {
        dexter = Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.CAMERA
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    report.let {
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(this@MainActivity, R.string.permissions_granted, Toast.LENGTH_SHORT).show()
                        } else {
                            AlertDialog.Builder(this@MainActivity).apply {
                                setMessage(getString(R.string.please_permissions))
                                    .setCancelable(false)
                                    .setPositiveButton(getString(R.string.setting)) { _, _ ->
                                        val reqIntent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                            .apply {
                                                val uri = Uri.fromParts("package", packageName, null)
                                                data = uri
                                            }
                                        resultLauncher.launch(reqIntent)
                                    }
                                val alert = this.create()
                                alert.show()
                            }
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest?>?, token: PermissionToken?) {
                    token?.continuePermissionRequest()
                }
            }).withErrorListener {
                Toast.makeText(this, it.name, Toast.LENGTH_SHORT).show()
            }
        dexter.check()
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        dexter.check()
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