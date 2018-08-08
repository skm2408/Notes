package com.example.shubhammishra.notes.Adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class ViewPagerAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {
    var listFragment=ArrayList<Fragment>()
    var listTabTitle=ArrayList<String>()
    override fun getItem(position: Int): Fragment =listFragment[position]

    override fun getCount(): Int =listTabTitle.size
    override fun getPageTitle(position: Int): CharSequence? {
        return listTabTitle[position]
    }
    fun addFragment(fragment:Fragment, title:String)
    {
        listFragment.add(fragment)
        listTabTitle.add(title)

    }
}