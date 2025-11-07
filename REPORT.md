# SpotFinder Implementation Report

## SQLite Database
- `LocationDatabaseHelper` extends `SQLiteOpenHelper` to manage a single `locations` table with columns for ID, address, latitude, and longitude. Table creation happens in `onCreate`, and `onUpgrade` recreates it when the schema changes.
- CRUD helper methods (`insertLocation`, `updateLocation`, `deleteLocation`, `getLocationByAddress`, and `getAllLocations`) encapsulate SQL operations with parameter binding to prevent SQL injection and ensure reliable transactions.
- During the first launch, `prepopulate` loads 100+ Greater Toronto Area records using a compiled insert statement executed within a transaction so the seed data is written efficiently and atomically.

## Google Maps Integration
- `MainActivity` hosts a `SupportMapFragment` defined in `activity_main.xml`. Once Google Play services initializes the map, `onMapReady` configures camera defaults and enables zoom and gesture controls over the GTA region.
- User interactions (searching or selecting a list item) call `displayLocationOnMap`, which clears previous markers, adds a marker for the chosen location, and animates the camera to a zoomed view of the coordinates.
- The RecyclerView list and search UI stay synchronized with map state by loading locations from SQLite, populating form fields, and reusing the same helper for all CRUD actions.

GitHub repository: <link to be added>
