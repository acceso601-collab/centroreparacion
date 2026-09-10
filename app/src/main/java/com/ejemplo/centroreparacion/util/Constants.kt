package com.ejemplo.centroreparacion.util

object Constants {
    // Estados de reparación
    object RepairStatus {
        const val RECIBIDO = "RECIBIDO"
        const val DIAGNOSTICANDO = "DIAGNOSTICANDO"
        const val ESPERANDO_PIEZAS = "ESPERANDO_PIEZAS"
        const val EN_REPARACION = "EN_REPARACION"
        const val EN_PRUEBAS = "EN_PRUEBAS"
        const val REPARADO = "REPARADO"
        const val ENTREGADO = "ENTREGADO"
        const val CANCELADO = "CANCELADO"

        fun all() = listOf(RECIBIDO, DIAGNOSTICANDO, ESPERANDO_PIEZAS,
            EN_REPARACION, EN_PRUEBAS, REPARADO, ENTREGADO, CANCELADO)

        fun label(code: String): String = when (code) {
            RECIBIDO -> "Recibido"
            DIAGNOSTICANDO -> "Diagnosticando"
            ESPERANDO_PIEZAS -> "Esperando piezas"
            EN_REPARACION -> "En reparación"
            EN_PRUEBAS -> "En pruebas"
            REPARADO -> "Reparado"
            ENTREGADO -> "Entregado"
            CANCELADO -> "Cancelado"
            else -> code
        }
    }

    // Estados del checklist
    object Checklist {
        const val OK = "OK"
        const val FAILED = "FAILED"
        const val NOT_TESTED = "NOT_TESTED"

        fun next(current: String) = when (current) {
            NOT_TESTED -> OK
            OK -> FAILED
            else -> NOT_TESTED
        }
    }

    // Categorías de inventario
    object Categories {
        const val PANTALLAS = "Pantallas"
        const val BATERIAS = "Baterías"
        const val CAMARAS = "Cámaras"
        const val ALTAVOCES = "Altavoces"
        const val MICROFONOS = "Micrófonos"
        const val FLEX = "Flex"
        const val CONECTORES = "Conectores"
        const val TORNILLOS = "Tornillos"
        const val IC = "IC/Componentes"
        const val CABLES = "Cables"
        const val HERRAMIENTAS = "Herramientas"
        const val OTROS = "Otros"

        fun all() = listOf(PANTALLAS, BATERIAS, CAMARAS, ALTAVOCES, MICROFONOS,
            FLEX, CONECTORES, TORNILLOS, IC, CABLES, HERRAMIENTAS, OTROS)
    }

    // Unidades eléctricas
    object Units {
        const val V = "V"; const val MV = "mV"
        const val A = "A"; const val MA = "mA"
        const val OHM = "Ω"; const val KOHM = "kΩ"; const val MOHM = "MΩ"
        const val W = "W"; const val KW = "kW"

        fun voltageUnits() = listOf(V, MV)
        fun currentUnits() = listOf(A, MA)
        fun resistanceUnits() = listOf(OHM, KOHM, MOHM)
        fun powerUnits() = listOf(W, KW)
    }

    // Preferencias
    const val PREFS = "centro_reparacion_prefs"
    const val FILE_PROVIDER = ".fileprovider"
}
