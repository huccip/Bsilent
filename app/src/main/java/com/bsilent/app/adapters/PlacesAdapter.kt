package com.bsilent.app.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bsilent.app.database.entities.Place
import com.bsilent.app.databinding.PlaceItemBinding
import com.bumptech.glide.Glide

class PlacesAdapter(
    var places: List<Place> = listOf()
) : RecyclerView.Adapter<PlacesAdapter.PlaceHolder>() {

    inner class PlaceHolder(var binding: PlaceItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceHolder =
        PlaceHolder(
            PlaceItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount(): Int = places.size

    override fun onBindViewHolder(holder: PlaceHolder, position: Int) {
        val place = places[position]
        Log.e("Test","$places")
//        holder.binding.
        holder.binding.apply {
            nameLocItem.text = place.name
            isEnableSw.isChecked = place.isEnabled
            Glide.with(root.context)
                .load(place.img)
                .into(imgMap)
        }
    }


}