package com.example.blog

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.blog.adapter.AdapterViewpagerHome
import com.example.blog.databinding.ActivityMainBinding
import com.example.blog.fragment.*
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

       /* mAuth = FirebaseAuth.getInstance()
        mAuth.signOut()*/

        val fragmentList =
            arrayListOf<Fragment>(
                HomeFragment(),
//                SearchFragment(),
                CreatePostFragment(),
                NotificationFragment(),
                ProfileFragment()
            )
        val adpterViewpager = AdapterViewpagerHome(fragmentList, supportFragmentManager)
        binding.viewpagerMain.adapter = adpterViewpager
        binding.viewpagerMain.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                binding.bottomNavigation.menu.getItem(position).setChecked(true)
            }

            override fun onPageScrollStateChanged(state: Int) {}

        })
        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    binding.viewpagerMain.currentItem = 0
                    return@setOnNavigationItemSelectedListener true
                }
//                R.id.search -> {
//                    binding.viewpagerMain.currentItem = 1
//                    return@setOnNavigationItemSelectedListener true
//                }
                R.id.createPost -> {
//                    binding.viewpagerMain.currentItem = 2
                    binding.viewpagerMain.currentItem = 1
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.notification -> {
//                    binding.viewpagerMain.currentItem = 3
                    binding.viewpagerMain.currentItem = 2
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.profile -> {
//                    binding.viewpagerMain.currentItem = 4
                    binding.viewpagerMain.currentItem = 3
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }

    }
}