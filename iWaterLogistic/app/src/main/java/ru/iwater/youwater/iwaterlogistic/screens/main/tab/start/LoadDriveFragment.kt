package ru.iwater.youwater.iwaterlogistic.screens.main.tab.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import ru.iwater.youwater.iwaterlogistic.base.App
import ru.iwater.youwater.iwaterlogistic.base.BaseFragment
import ru.iwater.youwater.iwaterlogistic.databinding.LoadDriveFragmentBinding
import ru.iwater.youwater.iwaterlogistic.domain.vm.ProductViewModel
import ru.iwater.youwater.iwaterlogistic.screens.main.adapter.ProductsListAdapter
import javax.inject.Inject

class LoadDriveFragment : BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: ProductViewModel by viewModels { factory }
    private val screenComponent = App().buildScreenComponent()

    private val adapter = ProductsListAdapter()
    private val advancedAdapter = ProductsListAdapter()
    private var binding: LoadDriveFragmentBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LoadDriveFragmentBinding.inflate(inflater)
        binding?.lifecycleOwner = this
        binding?.viewModel = viewModel
        binding?.rvTotalLoad?.adapter = adapter
        binding?.rvAdvancedAdapter?.adapter = advancedAdapter
        viewModel.product.observe(this.viewLifecycleOwner, {
            var count = 0
            it.forEach { product ->
                count += product.count
            }
            binding?.tvTotalLoadCount?.text = count.toString()
            adapter.submitList(it)
        })

        viewModel.advancedProduct.observe(this.viewLifecycleOwner, {
            var count = 0
            it.forEach { product ->
                count += product.count
            }
            binding?.tvAdvancedCount?.text = count.toString()
            advancedAdapter.submitList(it)
        })

        binding?.btnStart?.setOnClickListener {
            val context = this.context
            if (context != null) viewModel.openDriverShift(context)
            this.activity?.finish()
        }

        return binding?.root
    }

    companion object {
        fun newInstance(): LoadDriveFragment = LoadDriveFragment()
    }

}