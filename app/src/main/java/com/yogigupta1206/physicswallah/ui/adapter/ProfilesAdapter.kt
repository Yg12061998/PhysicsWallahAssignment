package com.yogigupta1206.physicswallah.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.yogigupta1206.physicswallah.R
import com.yogigupta1206.physicswallah.databinding.ProfileSingleRowBinding
import com.yogigupta1206.physicswallah.network.model.profile.Profile

class ProfilesAdapter(
    var listener: OnItemClickListener
) : RecyclerView.Adapter<ProfilesAdapter.DataViewHolder>() {

    private var list = arrayListOf<Profile>()

    inner class DataViewHolder(var binding: ProfileSingleRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(index: Int, profile: Profile, listener: OnItemClickListener) = binding.apply {
            data = profile
            executePendingBindings()
            clRoot.setOnClickListener {
                listener.onClick(index)
            }
            btnMore.setOnClickListener{
                listener.onClick(index)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder =
        DataViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.profile_single_row, parent, false
            )
        )


    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(position, list[position], listener)
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newList: ArrayList<Profile>){
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onClick(index: Int)
    }
}