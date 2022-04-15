package com.example.testapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.testapp.R
import com.example.testapp.fragments.DraftFragment
import com.example.testapp.fragments.NoteFragment
import com.example.testapp.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    private lateinit var bottomNav: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        bottomNav = findViewById(R.id.bottom_nav)
        val notesFragment = NoteFragment()
        val draftFragment = DraftFragment()
        val profileFragment = ProfileFragment()

        setCurrentFragment(notesFragment)
        bottomNav.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.notes_nav_item ->setCurrentFragment(notesFragment)
                R.id.draft_nav_item ->setCurrentFragment(draftFragment)
                R.id.profile_nav_item ->setCurrentFragment(profileFragment)
        }
            true
        }

    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply{
            replace(R.id.fragments_view, fragment)
            commit()
    }

}