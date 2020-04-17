package com.nivelais.attestationsortie.presentation.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.tabs.TabLayoutMediator
import com.nivelais.attestationsortie.R
import com.nivelais.attestationsortie.databinding.ActivityHomeBinding
import com.nivelais.attestationsortie.presentation.ui.create.CreateAttestationFragment
import com.nivelais.attestationsortie.presentation.ui.list.AttestationsFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


/**
 * Home activity (Create and access generated attestation)
 */
class HomeActivity : AppCompatActivity() {

    /**
     * Import the view model
     */
    private val viewModel: HomeViewModel by viewModel()

    /**
     * Badge for the attestations counts
     */
    private lateinit var attestationsCountBadge: BadgeDrawable

    /**
     * Import the view binding
     */
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewPager()
        initObserver()
    }

    /**
     * Init the view pager that will be used to list our attestations
     */
    private fun initViewPager() {
        // Setting up an adapter on the view pager
        binding.viewPager.adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)

        // Bind the tabs layout to the view pager
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            // Find the title and icon for the tab
            when (position) {
                0 -> {
                    tab.setText(R.string.lbl_create)
                    tab.setIcon(R.drawable.ic_write)
                }
                else -> {
                    tab.setText(R.string.lbl_my_attestations)
                    tab.setIcon(R.drawable.ic_folder)
                    attestationsCountBadge = tab.orCreateBadge
                }
            }
            binding.viewPager.setCurrentItem(tab.position, true)
        }.attach()
    }

    /**
     * Init all the observer for the current view
     */
    private fun initObserver() {
        // Observer for the attestations count
        viewModel.liveAttestationsCount.observe(this, Observer { attestationsCount ->
            run {
                if (attestationsCount.first > 0) {
                    // Refresh attestaions tabs badge
                    attestationsCountBadge.isVisible = true
                    attestationsCountBadge.number = attestationsCount.first
                } else {
                    attestationsCountBadge.isVisible = false
                }
            }
        })

        // Listen to change on the back stack, to update the text that count attestations
        supportFragmentManager.addOnBackStackChangedListener {
            viewModel.refreshAttestationsCount()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshAttestationsCount()
    }

    /**
     * Adapter for our view pager
     */
    class ViewPagerAdapter(
        fragmentManager: FragmentManager,
        lifecycle: Lifecycle
    ) : FragmentStateAdapter(fragmentManager, lifecycle) {

        /**
         * The fragments we want to display
         */
        private val fragments = ArrayList<Fragment>()

        /**
         * Return the number of available fragments
         */
        override fun getItemCount(): Int = fragments.size

        /**
         * Create base fragment
         */
        init {
            fragments.apply {
                add(CreateAttestationFragment())
                add(AttestationsFragment())
            }
        }

        /**
         * Create the fragment for our positions
         */
        override fun createFragment(position: Int): Fragment {
            // Return the matching fragment
            return fragments[position]
        }
    }
}