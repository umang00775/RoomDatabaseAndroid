package com.roomdatabase.android

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity()
data class Contact(
    @PrimaryKey(autoGenerate = true) // Room will automatically set id as primary key
    val  id: Int = 0, // Room will generate random unique ID

    val firstName: String,
    val lastName: String,
    val phoneNumber: String
)
