package com.example.pdfreader

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.pdfreader.activities.ChangeLanguageActivity
import com.example.pdfreader.activities.SearchActivity
import com.example.pdfreader.adapters.ViewPagerAdapter
import com.example.pdfreader.databinding.ActivityMainBinding
import com.example.pdfreader.fragments.PdfFragment
import com.example.pdfreader.fragments.WordFragment
import com.example.pptreader.fragments.ExcelFragment
import com.example.pptreader.fragments.PptFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
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
    private lateinit var dialog : BottomSheetDialog

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val toolBar = findViewById<Toolbar>(R.id.myToolBar)
        setSupportActionBar(toolBar)
        checkPermission()
        setFAB()


        binding.tabLayoutView.addTab(binding.tabLayoutView.newTab().setText("PDF"))
        binding.tabLayoutView.addTab(binding.tabLayoutView.newTab().setText("WORD"))
        binding.tabLayoutView.addTab(binding.tabLayoutView.newTab().setText("EXCEL"))
        binding.tabLayoutView.addTab(binding.tabLayoutView.newTab().setText("PPT"))

        toolBar.setBackgroundColor(Color.RED)
        binding.tabLayoutView.setBackgroundColor(Color.RED)
        window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.red)

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

                            toolBar.setBackgroundColor(Color.RED)
                            binding.tabLayoutView.setBackgroundColor(Color.RED)
                            window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.red)
                        }
                        1 -> {
                            toolBar.setBackgroundColor(Color.BLUE)
                            binding.tabLayoutView.setBackgroundColor(Color.BLUE)
                            window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.blue)
                        }
                        2 -> {
                            toolBar.setBackgroundColor(Color.GREEN)
                            binding.tabLayoutView.setBackgroundColor(Color.GREEN)
                            window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.green)
                        }
                        3 -> {
                            toolBar.setBackgroundColor(Color.parseColor("#FF9800"))
                            binding.tabLayoutView.setBackgroundColor(Color.parseColor("#FF9800"))
                            window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.orange)

                        }
                        else -> {
                            toolBar.setBackgroundColor(Color.RED)
                            binding.tabLayoutView.setBackgroundColor(Color.RED)
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
                        }
                        1 -> {
                            wordFragment.loadAllFileWord()
                        }
                        2 -> {
                            excelFragment.loadAllFileExcel()
                        }
                        3 -> {
                            pptFragment.loadAllFilePPT()
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
                        }
                        3 -> {
                            //ppt
                            pptFragment.loadFavoriteFilePpt()
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


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_top, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.sort -> {

            }
            R.id.search -> {
                startActivity(Intent(this, SearchActivity::class.java))
            }
            R.id.change_language -> {
                intent = Intent(this, ChangeLanguageActivity::class.java)
                startActivity(intent)
            }
            R.id.get_premium -> {
                Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show()

            }
        }
        return super.onOptionsItemSelected(item)
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
            Toast.makeText(this@MainActivity, "Image to PDF", Toast.LENGTH_LONG).show()
        }

        scanFAB.setOnClickListener {
            Toast.makeText(this@MainActivity, "Scan document", Toast.LENGTH_LONG).show()
        }
    }

}