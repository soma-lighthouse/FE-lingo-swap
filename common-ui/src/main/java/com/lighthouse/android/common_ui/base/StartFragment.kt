package com.lighthouse.android.common_ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lighthouse.android.common_ui.R
import com.lighthouse.navigation.NavigationFlow
import com.lighthouse.navigation.ToFlowNavigatable

class StartFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //decide where to go on the first app launch, check auth token if login needed
        (requireActivity() as ToFlowNavigatable).navigateToFlow(NavigationFlow.BoardFlow)
    }

}