package com.example.fhub.view.route

import com.example.fhub.R

object DestinasiListInvoice : DestinasiNavigasi {
    override val route    = "list_invoice"
    override val titleRes = R.string.nav_invoices
}

object DestinasiEntryInvoice : DestinasiNavigasi {
    override val route    = "entry_invoice"
    override val titleRes = R.string.btn_save
}

object DestinasiDetailInvoice : DestinasiNavigasi {
    override val route    = "detail_invoice"
    override val titleRes = R.string.nav_invoices
    const val itemIdArg   = "idInvoice"
    val routeWithArgs     = "$route/{$itemIdArg}"
}

object DestinasiEditInvoice : DestinasiNavigasi {
    override val route    = "edit_invoice"
    override val titleRes = R.string.btn_edit
    const val itemIdArg   = "idInvoice"
    val routeWithArgs     = "$route/{$itemIdArg}"
}

object DestinasiEntryInvoiceItem : DestinasiNavigasi {
    override val route    = "entry_invoice_item"
    override val titleRes = R.string.btn_add_item // "+ Tambah Item"
    const val invoiceIdArg = "idInvoice" // Kita butuh ID Invoice biar tau item ini masuk ke mana
    val routeWithArgs     = "$route/{$invoiceIdArg}"
}