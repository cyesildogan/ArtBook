package com.example.udemyartbooktesting.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.udemyartbooktesting.R
import com.example.udemyartbooktesting.adapter.ImageRecyclerAdapter
import com.example.udemyartbooktesting.databinding.FragmentImageApiBinding
import com.example.udemyartbooktesting.util.Status
import com.example.udemyartbooktesting.viewmodel.ArtViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class ImageApiFragment @Inject constructor(
    private val imageRecyclerAdapter: ImageRecyclerAdapter
): Fragment(R.layout.fragment_image_api) {

    lateinit var viewModel : ArtViewModel
    private var fragmentBinding : FragmentImageApiBinding?= null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[ArtViewModel::class.java]

        val binding = FragmentImageApiBinding.bind(view)
        fragmentBinding = binding

        var job : Job? = null

        binding.searchText.addTextChangedListener {
            job?.cancel()
            job = lifecycleScope.launch {
                delay(1000)
                it?.let {
                    if (it.toString().isNotEmpty()){
                        viewModel.searchForImage(it.toString())
                    }
                }
            }
        }

        subscribeToObservers()

        binding.imageRecyclerView.adapter = imageRecyclerAdapter
        binding.imageRecyclerView.layoutManager = GridLayoutManager(requireContext(),3)
        imageRecyclerAdapter.setOnItemClickListener {clickedUrl ->
            findNavController().popBackStack()
            viewModel.setSelectedImage(clickedUrl)
        }
    }

    private fun subscribeToObservers(){
        viewModel.imageList.observe(viewLifecycleOwner, Observer {resource->
            when(resource.status){
                Status.SUCCESS ->{
                    val urls = resource.data?.hits?.map {imageResult ->
                        imageResult.previewURL
                    }

                    imageRecyclerAdapter.images = urls ?: listOf()

                    fragmentBinding?.apiProgressBar?.visibility = View.GONE
                }
                Status.ERROR ->{
                    Toast.makeText(requireContext(),resource.message ?: "Error",Toast.LENGTH_LONG).show()
                    fragmentBinding?.apiProgressBar?.visibility = View.GONE
                }
                Status.LOADING ->{
                    fragmentBinding?.apiProgressBar?.visibility = View.VISIBLE
                }
            }
        })
    }

}