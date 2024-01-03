package com.roomdatabase.android

// The state of the contacts
data class ContactState(
    val contacts: List<Contact> = emptyList(),
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val isAddingContact: Boolean = false, // If user currently adding a new contact
    val sortType: SortType = SortType.FIRST_NAME // Sort on the basis of firstName by default
)
