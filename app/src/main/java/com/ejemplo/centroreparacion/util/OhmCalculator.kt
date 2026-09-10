package com.ejemplo.centroreparacion.util

object OhmCalculator {
    data class Result(
        val formula: String,
        val substitution: String,
        val result: Double,
        val unit: String,
        val error: String? = null
    )

    fun calculateVoltage(current: Double, resistance: Double): Result {
        if (current < 0 || resistance < 0)
            return Result("V = I × R", "", 0.0, "V", "Los valores no pueden ser negativos")
        val v = current * resistance
        return Result("V = I × R", "$current × $resistance", v, "V")
    }

    fun calculateCurrent(voltage: Double, resistance: Double): Result {
        if (resistance == 0.0)
            return Result("I = V ÷ R", "", 0.0, "A", "División entre cero")
        if (voltage < 0 || resistance < 0)
            return Result("I = V ÷ R", "", 0.0, "A", "Los valores no pueden ser negativos")
        val i = voltage / resistance
        return Result("I = V ÷ R", "$voltage ÷ $resistance", i, "A")
    }

    fun calculateResistance(voltage: Double, current: Double): Result {
        if (current == 0.0)
            return Result("R = V ÷ I", "", 0.0, "Ω", "División entre cero")
        if (voltage < 0 || current < 0)
            return Result("R = V ÷ I", "", 0.0, "Ω", "Los valores no pueden ser negativos")
        val r = voltage / current
        return Result("R = V ÷ I", "$voltage ÷ $current", r, "Ω")
    }

    fun calculatePowerVI(voltage: Double, current: Double): Result {
        if (voltage < 0 || current < 0)
            return Result("P = V × I", "", 0.0, "W", "Los valores no pueden ser negativos")
        return Result("P = V × I", "$voltage × $current", voltage * current, "W")
    }

    fun calculatePowerI2R(current: Double, resistance: Double): Result {
        if (current < 0 || resistance < 0)
            return Result("P = I² × R", "", 0.0, "W", "Los valores no pueden ser negativos")
        return Result("P = I² × R", "$current² × $resistance", current * current * resistance, "W")
    }

    fun calculatePowerV2R(voltage: Double, resistance: Double): Result {
        if (resistance == 0.0)
            return Result("P = V² ÷ R", "", 0.0, "W", "División entre cero")
        if (voltage < 0 || resistance < 0)
            return Result("P = V² ÷ R", "", 0.0, "W", "Los valores no pueden ser negativos")
        return Result("P = V² ÷ R", "$voltage² ÷ $resistance", voltage * voltage / resistance, "W")
    }
}
