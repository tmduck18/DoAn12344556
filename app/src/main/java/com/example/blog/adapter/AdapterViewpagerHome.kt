package com.example.blog.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class AdapterViewpagerHome(list: ArrayList<Fragment>, fm: FragmentManager) :
    FragmentStatePagerAdapter(fm,FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val fragmentList : ArrayList<Fragment> = list

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }
}