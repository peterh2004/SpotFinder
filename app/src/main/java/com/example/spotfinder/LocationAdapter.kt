package com.example.spotfinder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * RecyclerView adapter that renders the list of stored locations and notifies when one is selected.
 */
class LocationAdapter(
    private val locations: List<Location>,
    private val onLocationSelected: (Location) -> Unit
) : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    /**
     * Inflates the row layout and wraps it in a [LocationViewHolder].
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_location, parent, false)
        return LocationViewHolder(view)
    }

    /**
     * Binds the location at the requested position to the supplied view holder.
     */
    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(locations[position])
    }

    /**
     * Returns the total number of locations available for display.
     */
    override fun getItemCount(): Int = locations.size

    /**
     * View holder that displays a single location row and handles click events.
     */
    inner class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val addressText: TextView = itemView.findViewById(R.id.textAddress)
        private val coordinatesText: TextView = itemView.findViewById(R.id.textCoordinates)

        /**
         * Populates the row views with the supplied location details and wires the click callback.
         */
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
