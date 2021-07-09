package com.bsilent.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bsilent.app.R
import com.bsilent.app.database.entities.Place
import com.bsilent.app.databinding.PlaceItemBinding
import com.bsilent.app.viewmodels.PlacesViewModel
import com.bumptech.glide.Glide

class PlacesAdapter(
    val viewModel: PlacesViewModel
) : RecyclerView.Adapter<PlacesAdapter.PlaceHolder>() {

    inner class PlaceHolder(var binding: PlaceItemBinding) : RecyclerView.ViewHolder(binding.root)

    val differ = AsyncListDiffer(
        this,
        object : DiffUtil.ItemCallback<Place>() {

            override fun areItemsTheSame(
                oldItem: Place,
                newItem: Place
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: Place,
                newItem: Place
            ): Boolean =
                oldItem == newItem

        })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceHolder =
        PlaceHolder(
            PlaceItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: PlaceHolder, position: Int) {
        val place = differ.currentList[position]

        holder.binding.apply {
            nameLocItem.text = place.name
            isEnableSw.isChecked = place.isEnabled
            Glide.with(root.context)
                .load(place.img)
                .into(imgMap)

            modeImg.setImageResource(
                if (place.silent) R.drawable.ic_silent else R.drawable.ic_vibration
            )

            selectLayout.visibility =
                if (viewModel.selected.value?.contains(place) == true) View.VISIBLE
                else View.GONE

            //setup listeners
            isEnableSw.setOnCheckedChangeListener { _, b ->
                viewModel.setEnable(root.context, place, b)
            }

            root.setOnClickListener {
                if (viewModel.selected.value.isNullOrEmpty()) {

                    viewModel.showDialogPlace.value = true
                    viewModel.dialogPlace.value = place
                    viewModel.dialogPlaceOld.value = place

                } else {

                    viewModel.addOrRemoveSelected(place)
                    notifyItemChanged(position)
                }
            }

            root.setOnLongClickListener {

                viewModel.addOrRemoveSelected(place)
                notifyItemChanged(position)

                true
            }
        }
    }

    fun submitList(places: List<Place>) = differ.submitList(places)

}