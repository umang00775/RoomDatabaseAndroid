package com.roomdatabase.android

sealed interface ContactEvent{
    // Contact event is nothing but a event (User event or click)

    object SaveContact: ContactEvent // Save the contact event

    // Take value from the fields
    data class SetFirstName(val firstName: String): ContactEvent
    data class SetLastName(val lastName: String): ContactEvent
    data class SetPhoneNumber(val phoneNumber: String): ContactEvent

    object ShowDialog: ContactEvent // Event --> Show he dialog
    object HideDialog: ContactEvent // Event --> Hide the dialog

    data class SortContacts(val sortType: SortType): ContactEvent // To short the contacts
    data class deleteContact(val contact: Contact): ContactEvent
}
