package com.ejemplo.centroreparacion.util

object UnitConverter {
    enum class Magnitude { VOLTAGE, CURRENT, RESISTANCE, POWER }

    fun magnitudeOf(unit: String): Magnitude? = when (unit) {
        Constants.Units.V, Constants.Units.MV -> Magnitude.VOLTAGE
        Constants.Units.A, Constants.Units.MA -> Magnitude.CURRENT
        Constants.Units.OHM, Constants.Units.KOHM, Constants.Units.MOHM -> Magnitude.RESISTANCE
        Constants.Units.W, Constants.Units.KW -> Magnitude.POWER
        else -> null
    }

    fun canConvert(from: String, to: String): Boolean {
        val m1 = magnitudeOf(from) ?: return false
        val m2 = magnitudeOf(to) ?: return false
        return m1 == m2
    }

    /** Devuelve el resultado o null si la conversión no es posible. */
    fun convert(value: Double, from: String, to: String): Double? {
        if (!canConvert(from, to)) return null
        val base = toBase(value, from) ?: return null
        return fromBase(base, to)
    }

    private fun toBase(v: Double, unit: String): Double? = when (unit) {
        Constants.Units.V -> v
        Constants.Units.MV -> v / 1000.0
        Constants.Units.A -> v
        Constants.Units.MA -> v / 1000.0
        Constants.Units.OHM -> v
        Constants.Units.KOHM -> v * 1000.0
        Constants.Units.MOHM -> v * 1_000_000.0
        Constants.Units.W -> v
        Constants.Units.KW -> v * 1000.0
        else -> null
    }

    private fun fromBase(base: Double, unit: String): Double? = when (unit) {
        Constants.Units.V -> base
        Constants.Units.MV -> base * 1000.0
        Constants.Units.A -> base
        Constants.Units.MA -> base * 1000.0
        Constants.Units.OHM -> base
        Constants.Units.KOHM -> base / 1000.0
        Constants.Units.MOHM -> base / 1_000_000.0
        Constants.Units.W -> base
        Constants.Units.KW -> base / 1000.0
        else -> null
    }
}
