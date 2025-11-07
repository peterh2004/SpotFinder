package com.example.spotfinder

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class LocationDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_LOCATIONS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ADDRESS TEXT NOT NULL,
                $COLUMN_LATITUDE REAL NOT NULL,
                $COLUMN_LONGITUDE REAL NOT NULL
            )
            """.trimIndent()
        )
        prepopulate(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LOCATIONS")
        onCreate(db)
    }

    fun insertLocation(address: String, lat: Double, lng: Double): Long {
        val values = ContentValues().apply {
            put(COLUMN_ADDRESS, address)
            put(COLUMN_LATITUDE, lat)
            put(COLUMN_LONGITUDE, lng)
        }
        return writableDatabase.insert(TABLE_LOCATIONS, null, values)
    }

    fun updateLocation(id: Long, address: String, lat: Double, lng: Double): Int {
        val values = ContentValues().apply {
            put(COLUMN_ADDRESS, address)
            put(COLUMN_LATITUDE, lat)
            put(COLUMN_LONGITUDE, lng)
        }
        return writableDatabase.update(
            TABLE_LOCATIONS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
    }

    fun deleteLocation(id: Long): Int {
        return writableDatabase.delete(
            TABLE_LOCATIONS,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
    }

    fun getLocationByAddress(address: String): Location? {
        val selection = "$COLUMN_ADDRESS LIKE ? COLLATE NOCASE"
        val selectionArgs = arrayOf("%$address%")
        readableDatabase.query(
            TABLE_LOCATIONS,
            null,
            selection,
            selectionArgs,
            null,
            null,
            "$COLUMN_ID ASC",
            "1"
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                return cursor.toLocation()
            }
        }
        return null
    }

    fun getAllLocations(): List<Location> {
        val locations = mutableListOf<Location>()
        readableDatabase.query(
            TABLE_LOCATIONS,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_ADDRESS COLLATE NOCASE ASC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                locations.add(cursor.toLocation())
            }
        }
        return locations
    }

    private fun Cursor.toLocation(): Location {
        val idIndex = getColumnIndexOrThrow(COLUMN_ID)
        val addressIndex = getColumnIndexOrThrow(COLUMN_ADDRESS)
        val latitudeIndex = getColumnIndexOrThrow(COLUMN_LATITUDE)
        val longitudeIndex = getColumnIndexOrThrow(COLUMN_LONGITUDE)
        return Location(
            id = getLong(idIndex),
            address = getString(addressIndex),
            latitude = getDouble(latitudeIndex),
            longitude = getDouble(longitudeIndex)
        )
    }

    private fun prepopulate(db: SQLiteDatabase) {
        val existingCount = DatabaseUtils.queryNumEntries(db, TABLE_LOCATIONS)
        if (existingCount > 0) return

        val seeds = listOf(
            Seed("Oshawa Centre, Oshawa, ON", 43.8965, -78.8656),
            Seed("Durham College, Oshawa, ON", 43.9453, -78.8956),
            Seed("Lakeview Park, Oshawa, ON", 43.8584, -78.8051),
            Seed("Whitby Civic Recreation Complex, Whitby, ON", 43.8846, -78.9427),
            Seed("Iroquois Park Sports Centre, Whitby, ON", 43.8669, -78.9447),
            Seed("Ajax Community Centre, Ajax, ON", 43.8509, -79.0284),
            Seed("Rotary Park, Ajax, ON", 43.8226, -79.0411),
            Seed("Ajax GO Station, Ajax, ON", 43.8501, -79.0204),
            Seed("Pickering Town Centre, Pickering, ON", 43.8353, -79.0892),
            Seed("Frenchman's Bay Marina, Pickering, ON", 43.8067, -79.0823),
            Seed("Pickering GO Station, Pickering, ON", 43.8346, -79.0875),
            Seed("Scarborough Town Centre, Scarborough, ON", 43.7767, -79.2578),
            Seed("Toronto Zoo, Scarborough, ON", 43.8177, -79.1859),
            Seed("Centennial College Progress Campus, Scarborough, ON", 43.7841, -79.2263),
            Seed("Scarborough Bluffs Park, Scarborough, ON", 43.7050, -79.2318),
            Seed("Guild Park and Gardens, Scarborough, ON", 43.7397, -79.1964),
            Seed("North York Civic Centre, North York, ON", 43.7695, -79.4123),
            Seed("Yorkdale Shopping Centre, North York, ON", 43.7250, -79.4521),
            Seed("Mel Lastman Square, North York, ON", 43.7684, -79.4125),
            Seed("Downsview Park, North York, ON", 43.7390, -79.4690),
            Seed("Fairview Mall, North York, ON", 43.7787, -79.3456),
            Seed("Ontario Science Centre, North York, ON", 43.7162, -79.3380),
            Seed("Sunnybrook Park, North York, ON", 43.7255, -79.3520),
            Seed("Toronto Eaton Centre, Toronto, ON", 43.6544, -79.3807),
            Seed("CN Tower, Toronto, ON", 43.6426, -79.3871),
            Seed("Rogers Centre, Toronto, ON", 43.6414, -79.3894),
            Seed("Roy Thomson Hall, Toronto, ON", 43.6465, -79.3854),
            Seed("Nathan Phillips Square, Toronto, ON", 43.6525, -79.3832),
            Seed("University of Toronto St. George Campus, Toronto, ON", 43.6629, -79.3957),
            Seed("St. Lawrence Market, Toronto, ON", 43.6487, -79.3716),
            Seed("Harbourfront Centre, Toronto, ON", 43.6387, -79.3817),
            Seed("Exhibition Place, Toronto, ON", 43.6333, -79.4141),
            Seed("High Park, Toronto, ON", 43.6465, -79.4637),
            Seed("Trinity Bellwoods Park, Toronto, ON", 43.6469, -79.4134),
            Seed("Kensington Market, Toronto, ON", 43.6540, -79.4024),
            Seed("Distillery Historic District, Toronto, ON", 43.6503, -79.3596),
            Seed("Art Gallery of Ontario, Toronto, ON", 43.6536, -79.3925),
            Seed("Royal Ontario Museum, Toronto, ON", 43.6677, -79.3948),
            Seed("Scotiabank Arena, Toronto, ON", 43.6435, -79.3791),
            Seed("Billy Bishop Toronto City Airport, Toronto, ON", 43.6280, -79.3962),
            Seed("Liberty Village, Toronto, ON", 43.6382, -79.4209),
            Seed("Fort York National Historic Site, Toronto, ON", 43.6374, -79.4022),
            Seed("Bloor-Yorkville, Toronto, ON", 43.6705, -79.3947),
            Seed("Casa Loma, Toronto, ON", 43.6780, -79.4094),
            Seed("Evergreen Brick Works, Toronto, ON", 43.6840, -79.3641),
            Seed("Riverdale Park East, Toronto, ON", 43.6678, -79.3524),
            Seed("Toronto Islands Ferry Terminal, Toronto, ON", 43.6417, -79.3762),
            Seed("Woodbine Beach, Toronto, ON", 43.6613, -79.3118),
            Seed("Leslieville, Toronto, ON", 43.6646, -79.3301),
            Seed("The Beaches Library, Toronto, ON", 43.6726, -79.2966),
            Seed("Greektown on the Danforth, Toronto, ON", 43.6775, -79.3520),
            Seed("Rosedale Park, Toronto, ON", 43.6829, -79.3796),
            Seed("Chinatown West, Toronto, ON", 43.6514, -79.3997),
            Seed("Queen's Park, Toronto, ON", 43.6622, -79.3930),
            Seed("Toronto City Hall, Toronto, ON", 43.6526, -79.3832),
            Seed("Union Station, Toronto, ON", 43.6453, -79.3807),
            Seed("Spadina Museum, Toronto, ON", 43.6800, -79.4099),
            Seed("Evergreen Brick Works Trails, Toronto, ON", 43.6845, -79.3651),
            Seed("Etobicoke Civic Centre, Etobicoke, ON", 43.6431, -79.5803),
            Seed("Sherway Gardens, Etobicoke, ON", 43.6108, -79.5572),
            Seed("Centennial Park Conservatory, Etobicoke, ON", 43.6561, -79.5832),
            Seed("Humber Bay Park East, Etobicoke, ON", 43.6204, -79.4759),
            Seed("Kipling GO Station, Etobicoke, ON", 43.6375, -79.5354),
            Seed("Long Branch Park, Etobicoke, ON", 43.5896, -79.5424),
            Seed("Mississauga Civic Centre, Mississauga, ON", 43.5890, -79.6441),
            Seed("Square One Shopping Centre, Mississauga, ON", 43.5934, -79.6427),
            Seed("Port Credit Lighthouse, Mississauga, ON", 43.5476, -79.5876),
            Seed("University of Toronto Mississauga, Mississauga, ON", 43.5471, -79.6625),
            Seed("Lakefront Promenade Park, Mississauga, ON", 43.5654, -79.5628),
            Seed("Meadowvale Town Centre, Mississauga, ON", 43.5942, -79.7510),
            Seed("Streetsville Memorial Park, Mississauga, ON", 43.5826, -79.7131),
            Seed("Erin Mills Town Centre, Mississauga, ON", 43.5618, -79.7487),
            Seed("Dixie Outlet Mall, Mississauga, ON", 43.5903, -79.5667),
            Seed("Jack Darling Memorial Park, Mississauga, ON", 43.5205, -79.6277),
            Seed("Brampton City Hall, Brampton, ON", 43.6845, -79.7595),
            Seed("Gage Park, Brampton, ON", 43.6833, -79.7590),
            Seed("Bramalea City Centre, Brampton, ON", 43.7175, -79.7217),
            Seed("Professor's Lake Recreation Centre, Brampton, ON", 43.7356, -79.7125),
            Seed("Chinguacousy Park, Brampton, ON", 43.7243, -79.7288),
            Seed("Eldorado Park, Brampton, ON", 43.6587, -79.8223),
            Seed("Rose Theatre, Brampton, ON", 43.6841, -79.7606),
            Seed("Mount Pleasant GO Station, Brampton, ON", 43.7032, -79.8361),
            Seed("Markham Civic Centre, Markham, ON", 43.8565, -79.3370),
            Seed("Markville Shopping Centre, Markham, ON", 43.8663, -79.2824),
            Seed("Unionville Main Street, Markham, ON", 43.8694, -79.3098),
            Seed("Milne Dam Conservation Park, Markham, ON", 43.8512, -79.2577),
            Seed("Pacific Mall, Markham, ON", 43.8273, -79.3023),
            Seed("Angus Glen Golf Club, Markham, ON", 43.9000, -79.3275),
            Seed("Cornell Community Centre, Markham, ON", 43.8775, -79.2307),
            Seed("Richmond Green Sports Centre, Richmond Hill, ON", 43.9184, -79.4055),
            Seed("Hillcrest Mall, Richmond Hill, ON", 43.8623, -79.4328),
            Seed("David Dunlap Observatory Park, Richmond Hill, ON", 43.8580, -79.4184),
            Seed("Richmond Hill Centre, Richmond Hill, ON", 43.8404, -79.4204),
            Seed("Aurora Cultural Centre, Aurora, ON", 44.0062, -79.4509),
            Seed("Aurora Town Park, Aurora, ON", 44.0061, -79.4501),
            Seed("Newmarket Riverwalk Commons, Newmarket, ON", 44.0545, -79.4542),
            Seed("Upper Canada Mall, Newmarket, ON", 44.0506, -79.4636),
            Seed("Vaughan City Hall, Vaughan, ON", 43.8375, -79.5281),
            Seed("Canada's Wonderland, Vaughan, ON", 43.8430, -79.5410),
            Seed("Vaughan Mills, Vaughan, ON", 43.8256, -79.5390),
            Seed("Kortright Centre for Conservation, Vaughan, ON", 43.8531, -79.5955),
            Seed("Woodbridge Memorial Tower, Vaughan, ON", 43.7885, -79.5952),
            Seed("Maple Community Centre, Vaughan, ON", 43.8560, -79.5289),
            Seed("Bolton Arena, Caledon, ON", 43.8801, -79.7359),
            Seed("Caledon Centre for Recreation, Caledon, ON", 43.8736, -79.7643),
            Seed("King City Arena, King City, ON", 43.9291, -79.5287),
            Seed("Stouffville GO Station, Whitchurch-Stouffville, ON", 43.9706, -79.2451),
            Seed("Brooklin Community Centre, Whitby, ON", 43.9639, -78.9457),
            Seed("Uxbridge Arena, Uxbridge, ON", 44.1029, -79.1272),
            Seed("Georgina Ice Palace, Georgina, ON", 44.2402, -79.4554),
            Seed("Pefferlaw Lions Community Hall, Georgina, ON", 44.3174, -79.1980),
            Seed("Lake Wilcox Park, Richmond Hill, ON", 43.9553, -79.4369),
            Seed("Aurora GO Station, Aurora, ON", 44.0059, -79.4502),
            Seed("Oshawa GO Station, Oshawa, ON", 43.8975, -78.8659),
            Seed("Whitby GO Station, Whitby, ON", 43.8572, -78.9433)
        )

        val insertSql = "INSERT INTO $TABLE_LOCATIONS ($COLUMN_ADDRESS, $COLUMN_LATITUDE, $COLUMN_LONGITUDE) VALUES (?, ?, ?)"
        val statement = db.compileStatement(insertSql)
        db.beginTransaction()
        try {
            for (seed in seeds) {
                statement.clearBindings()
                statement.bindString(1, seed.address)
                statement.bindDouble(2, seed.latitude)
                statement.bindDouble(3, seed.longitude)
                statement.executeInsert()
            }
            db.setTransactionSuccessful()
        } finally {
            statement.close()
            db.endTransaction()
        }
    }

    private data class Seed(
        val address: String,
        val latitude: Double,
        val longitude: Double
    )

    companion object {
        private const val DATABASE_NAME = "spotfinder.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_LOCATIONS = "locations"
        private const val COLUMN_ID = "id"
        private const val COLUMN_ADDRESS = "address"
        private const val COLUMN_LATITUDE = "latitude"
        private const val COLUMN_LONGITUDE = "longitude"
    }
}
