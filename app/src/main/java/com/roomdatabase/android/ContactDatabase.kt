package com.roomdatabase.android

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Contact::class],
    version = 1 // To migrate database: Old --> New :)
)
abstract class ContactDatabase: RoomDatabase() {

    abstract val dao: ContactDao

}