package com.peterlaurence.trekme.ui.maplist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.peterlaurence.trekme.databinding.FragmentMapListBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Show the list of maps.
 *
 * TODO: just like former view-based fragment:
 * * Display an indeterminate progressbar at startup,
 * * Show message and button to create a map when the map list is empty
 */
@AndroidEntryPoint
class MapListFragment : Fragment() {
    private var binding: FragmentMapListBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMapListBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}