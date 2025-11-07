package com.example.spotfinder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LocationAdapter(
    private val locations: List<Location>,
    private val onLocationSelected: (Location) -> Unit
) : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_location, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(locations[position])
    }

    override fun getItemCount(): Int = locations.size

    inner class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val addressText: TextView = itemView.findViewById(R.id.textAddress)
        private val coordinatesText: TextView = itemView.findViewById(R.id.textCoordinates)

        fun bind(location: Location) {
            addressText.text = location.address
            coordinatesText.text = itemView.context.getString(
                R.string.coordinates_format,
                location.latitude,
                location.longitude
            )
            itemView.setOnClickListener { onLocationSelected(location) }
        }
    }
}
