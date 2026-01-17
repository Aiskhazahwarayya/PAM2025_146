package com.example.fhub.view.route

import com.example.fhub.R

object DestinasiSplash : DestinasiNavigasi {
    override val route = "splash"
    override val titleRes = R.string.app_name
}

object DestinasiLogin : DestinasiNavigasi {
    override val route = "login"
    override val titleRes = R.string.btn_login
}

object DestinasiRegister : DestinasiNavigasi {
    override val route = "register"
    override val titleRes = R.string.btn_register
}
