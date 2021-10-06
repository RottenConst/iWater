package ru.iwater.youwater.iwaterlogistic.screens.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.iwater.youwater.iwaterlogistic.databinding.ItemProductLoadBinding
import ru.iwater.youwater.iwaterlogistic.domain.Product

class ProductsListAdapter : ListAdapter<Product, ProductsListAdapter.ProductsListHolder>(ProductDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsListHolder {
        return ProductsListHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ProductsListHolder, position: Int) {
        val item = getItem(position)
        holder.bindProduct(item)
    }

    class ProductsListHolder(val binding: ItemProductLoadBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindProduct(product: Product) {
            binding.product = product
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup) : ProductsListHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemProductLoadBinding.inflate(layoutInflater, parent, false)
                return ProductsListHolder(binding)
            }
        }
    }

    companion object ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}