package com.example.udemyartbooktesting.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.example.udemyartbooktesting.R
import com.example.udemyartbooktesting.databinding.FragmentArtDetailsBinding
import com.example.udemyartbooktesting.util.Status
import com.example.udemyartbooktesting.viewmodel.ArtViewModel
import javax.inject.Inject

class ArtDetailsFragment @Inject constructor(
    val glide : RequestManager
): Fragment(R.layout.fragment_art_details) {

    lateinit var viewModel : ArtViewModel
    private var fragmentBinding : FragmentArtDetailsBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[ArtViewModel::class.java]
        val binding = FragmentArtDetailsBinding.bind(view)
        fragmentBinding = binding
        subscribeToObservers()

        binding.artImageView.setOnClickListener {
            findNavController().navigate(ArtDetailsFragmentDirections.actionArtDetailsFragmentToImageApiFragment())
        }

        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)

        binding.saveButton.setOnClickListener {
            viewModel.makeArt(binding.artNameText.text.toString(),
                binding.artArtistText.text.toString(),
                binding.artYearText.text.toString())
        }

    }

    private fun subscribeToObservers(){
        viewModel.selectedImageUrl.observe(viewLifecycleOwner, Observer {url->
            fragmentBinding?.let {binding->
                glide.load(url).into(binding.artImageView)
            }
        })
        viewModel.insertArtMessage.observe(viewLifecycleOwner, Observer {resource->
            when(resource.status){
                Status.SUCCESS ->{
                    Toast.makeText(requireContext(),"Success",Toast.LENGTH_LONG).show()
                    findNavController().popBackStack()
                    viewModel.resetInsertArtMsg()
                }
                Status.ERROR ->{
                    Toast.makeText(requireContext(),resource.message?: "Error",Toast.LENGTH_LONG).show()
                }
                Status.LOADING -> {

                }
            }
        })
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }

}