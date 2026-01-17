package com.example.fhub.view.route

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.fhub.R

object DestinasiListKlien : DestinasiNavigasi {
    override val route    = "list_klien"
    override val titleRes = R.string.nav_clients
}

object DestinasiEntryKlien : DestinasiNavigasi {
    override val route    = "entry_klien"
    override val titleRes = R.string.btn_save
}

object DestinasiDetailKlien : DestinasiNavigasi {
    override val route    = "detail_klien"
    override val titleRes = R.string.nav_clients
    const val itemIdArg   = "idKlien"
    val routeWithArgs     = "$route/{$itemIdArg}"
    val arguments = listOf(
        navArgument(itemIdArg) { type = NavType.IntType }
    )
}

object DestinasiEditKlien : DestinasiNavigasi {
    override val route    = "edit_klien"
    override val titleRes = R.string.btn_edit
    const val itemIdArg   = "idKlien"
    val routeWithArgs     = "$route/{$itemIdArg}"
    val arguments = listOf(
        navArgument(itemIdArg) { type = NavType.IntType }
    )
}