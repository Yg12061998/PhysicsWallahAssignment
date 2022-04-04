package com.yogigupta1206.physicswallah.ui.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.yogigupta1206.physicswallah.R
import com.yogigupta1206.physicswallah.databinding.FragmentProfilesBinding
import com.yogigupta1206.physicswallah.ui.adapter.ProfilesAdapter
import com.yogigupta1206.physicswallah.utils.hide
import com.yogigupta1206.physicswallah.utils.show
import com.yogigupta1206.physicswallah.viewmodel.ProfilesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfilesFragment : Fragment() {

    private lateinit var mBinding: FragmentProfilesBinding
    private val viewModel: ProfilesViewModel by viewModels()
    private var profileAdapter: ProfilesAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentProfilesBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        setAdapter()
        setClickListeners()
        viewModel.init()
    }

    private fun setClickListeners() {
        mBinding.toolbar.imgBack.setOnClickListener {
            viewModel.backPressed()
        }
    }

    private fun setAdapter() {
        profileAdapter = ProfilesAdapter(object : ProfilesAdapter.OnItemClickListener{
            override fun onClick(index: Int) {
                viewModel.itemClicked(index)
            }
        })
        mBinding.recyclerView.layoutManager =LinearLayoutManager(requireContext())
        mBinding.recyclerView.adapter = profileAdapter
    }

    private fun setObservers() {
        viewModel.uiState.observe(viewLifecycleOwner){
            when(it){
                is ProfilesViewModel.UiState.Loading -> {
                    mBinding.progress.show()
                }
                is ProfilesViewModel.UiState.LoadingFinish -> {
                    mBinding.progress.hide()
                }
                is ProfilesViewModel.UiState.ErrorState -> {
                    showToast(it.message)
                }
            }
        }

        viewModel.navigateState.observe(viewLifecycleOwner){
            when(it){
                is ProfilesViewModel.NavigateState.NavigateBack -> activity?.supportFragmentManager?.popBackStack()
                is ProfilesViewModel.NavigateState.NavigateToProfile -> showToast(resources.getString(R.string.profile_clicked, it.name))
            }
        }

        viewModel.profiles.observe(viewLifecycleOwner){
            profileAdapter?.submitList(it)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message , Toast.LENGTH_SHORT).show()
    }
}