package com.example.spotfinder

/**
 * Represents a saved map location that can be displayed, searched, and edited.
 *
 * @property id Unique database identifier for the location.
 * @property address Human readable description of the location.
 * @property latitude Latitude coordinate used to position the marker on the map.
 * @property longitude Longitude coordinate used to position the marker on the map.
 */
data class Location(
    val id: Long,
    val address: String,
    val latitude: Double,
    val longitude: Double
)
