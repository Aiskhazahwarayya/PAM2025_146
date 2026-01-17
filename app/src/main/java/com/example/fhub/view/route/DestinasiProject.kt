package com.example.fhub.view.route

import com.example.fhub.R

object DestinasiListProject : DestinasiNavigasi {
    override val route    = "list_project"
    override val titleRes = R.string.nav_projects
}

object DestinasiEntryProject : DestinasiNavigasi {
    override val route    = "entry_project"
    override val titleRes = R.string.btn_save
}

object DestinasiDetailProject : DestinasiNavigasi {
    override val route    = "detail_project"
    override val titleRes = R.string.nav_projects
    const val itemIdArg   = "idProject"
    val routeWithArgs     = "$route/{$itemIdArg}"
}

object DestinasiEditProject : DestinasiNavigasi {
    override val route    = "edit_project"
    override val titleRes = R.string.btn_edit
    const val itemIdArg   = "idProject"
    val routeWithArgs     = "$route/{$itemIdArg}"
}