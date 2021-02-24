package com.bsilent.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bsilent.app.database.entities.Place
import com.bsilent.app.databinding.PlaceItemBinding

class PlacesAdapter(
    var places: List<Place>
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
//        holder.binding.
        holder.binding.apply {
            nameLocItem.text = place.name
            
        }
    }


}