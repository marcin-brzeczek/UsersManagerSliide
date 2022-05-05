package com.mbitsystem.sliide.usersmanager.data.model

sealed class GenderType {
    object Male : GenderType()
    object Female : GenderType()
}
