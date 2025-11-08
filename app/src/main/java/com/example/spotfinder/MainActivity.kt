package com.example.spotfinder

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * Main entry point that wires together the map, list, and CRUD interactions for locations.
 */
class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var databaseHelper: LocationDatabaseHelper
    private var googleMap: GoogleMap? = null

    private lateinit var searchInput: EditText
    private lateinit var idInput: EditText
    private lateinit var addressInput: EditText
    private lateinit var latitudeInput: EditText
    private lateinit var longitudeInput: EditText

    private lateinit var locationAdapter: LocationAdapter
    private val locations = mutableListOf<Location>()

    /**
     * Initializes the user interface, database helper, map fragment, and list of locations.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        databaseHelper = LocationDatabaseHelper(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        searchInput = findViewById(R.id.editSearchAddress)
        idInput = findViewById(R.id.editLocationId)
        addressInput = findViewById(R.id.editAddress)
        latitudeInput = findViewById(R.id.editLatitude)
        longitudeInput = findViewById(R.id.editLongitude)

        findViewById<Button>(R.id.buttonFind).setOnClickListener { handleSearch() }
        findViewById<Button>(R.id.buttonAdd).setOnClickListener { handleAdd() }
        findViewById<Button>(R.id.buttonUpdate).setOnClickListener { handleUpdate() }
        findViewById<Button>(R.id.buttonDelete).setOnClickListener { handleDelete() }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerLocations)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        locationAdapter = LocationAdapter(locations) { location ->
            populateFields(location)
            displayLocationOnMap(location)
        }
        recyclerView.adapter = locationAdapter

        loadLocations()
    }

    /**
     * Receives the GoogleMap instance and configures the default camera and gestures.
     */
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isZoomGesturesEnabled = true
        map.uiSettings.isScrollGesturesEnabled = true

        val gta = LatLng(43.7250, -79.3400)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(gta, 9f))
    }

    /**
     * Attempts to find a location matching the search query and displays it if found.
     */
    private fun handleSearch() {
        val query = searchInput.text.toString().trim()
        if (query.isEmpty()) {
            showToast(R.string.error_empty_search)
            return
        }
        val location = databaseHelper.getLocationByAddress(query)
        if (location != null) {
            populateFields(location)
            displayLocationOnMap(location)
        } else {
            showToast(R.string.error_address_not_found)
        }
    }

    /**
     * Validates input and inserts a new location into the database.
     */
    private fun handleAdd() {
        val address = addressInput.text.toString().trim()
        val lat = latitudeInput.text.toString().trim().toDoubleOrNull()
        val lng = longitudeInput.text.toString().trim().toDoubleOrNull()

        if (address.isEmpty()) {
            showToast(R.string.error_address_required)
            return
        }
        if (lat == null || lng == null) {
            showToast(R.string.error_invalid_coordinates)
            return
        }

        val result = databaseHelper.insertLocation(address, lat, lng)
        if (result != -1L) {
            showToast(R.string.message_insert_success)
            clearCrudInputs()
            loadLocations()
        } else {
            showToast(R.string.message_insert_failure)
        }
    }

    /**
     * Validates the provided id and coordinates before updating an existing location.
     */
    private fun handleUpdate() {
        val id = idInput.text.toString().trim().toLongOrNull()
        val address = addressInput.text.toString().trim()
        val lat = latitudeInput.text.toString().trim().toDoubleOrNull()
        val lng = longitudeInput.text.toString().trim().toDoubleOrNull()

        if (id == null) {
            showToast(R.string.error_id_required)
            return
        }
        if (address.isEmpty()) {
            showToast(R.string.error_address_required)
            return
        }
        if (lat == null || lng == null) {
            showToast(R.string.error_invalid_coordinates)
            return
        }

        val rows = databaseHelper.updateLocation(id, address, lat, lng)
        if (rows > 0) {
            showToast(R.string.message_update_success)
            loadLocations()
        } else {
            showToast(R.string.message_update_failure)
        }
    }

    /**
     * Deletes a location using either the supplied id or address.
     */
    private fun handleDelete() {
        val id = idInput.text.toString().trim().toLongOrNull()
        val address = addressInput.text.toString().trim()

        val rows = when {
            id != null -> databaseHelper.deleteLocation(id)
            address.isNotEmpty() -> {
                val location = databaseHelper.getLocationByAddress(address)
                if (location != null) databaseHelper.deleteLocation(location.id) else 0
            }
            else -> {
                showToast(R.string.error_delete_missing)
                return
            }
        }

        if (rows > 0) {
            showToast(R.string.message_delete_success)
            clearCrudInputs()
            loadLocations()
        } else {
            showToast(R.string.message_delete_failure)
        }
    }

    /**
     * Clears the current markers and focuses the map on the specified location.
     */
    private fun displayLocationOnMap(location: Location) {
        val map = googleMap ?: return
        val latLng = LatLng(location.latitude, location.longitude)
        map.clear()
        map.addMarker(MarkerOptions().position(latLng).title(location.address))
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))
    }

    /**
     * Copies location details into the CRUD input fields for easy editing.
     */
    private fun populateFields(location: Location) {
        idInput.setText(location.id.toString())
        addressInput.setText(location.address)
        latitudeInput.setText(location.latitude.toString())
        longitudeInput.setText(location.longitude.toString())
    }

    /**
     * Refreshes the in-memory list of locations from the database and updates the adapter.
     */
    private fun loadLocations() {
        locations.clear()
        locations.addAll(databaseHelper.getAllLocations())
        locationAdapter.notifyDataSetChanged()
    }

    /**
     * Clears the CRUD input fields after an operation succeeds.
     */
    private fun clearCrudInputs() {
        idInput.text?.clear()
        addressInput.text?.clear()
        latitudeInput.text?.clear()
        longitudeInput.text?.clear()
    }

    /**
     * Displays a short user-facing message for validation and CRUD feedback.
     */
    private fun showToast(messageResId: Int) {
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }
}
