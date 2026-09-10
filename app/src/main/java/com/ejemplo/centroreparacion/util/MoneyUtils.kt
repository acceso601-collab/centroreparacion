package com.ejemplo.centroreparacion.util

import java.math.BigDecimal
import java.math.RoundingMode

object MoneyUtils {
    private val SCALE = 2

    fun format(amount: Double): String =
        "RD$ ${"%.2f".format(amount)}"

    fun formatShort(amount: Double): String =
        "RD$ ${"%.2f".format(amount)}"

    fun round(amount: Double): Double =
        BigDecimal(amount).setScale(SCALE, RoundingMode.HALF_UP).toDouble()

    fun add(vararg values: Double): Double =
        round(values.sum())

    fun subtract(a: Double, b: Double): Double =
        round(a - b)

    fun multiply(a: Double, b: Double): Double =
        round(a * b)

    fun isNegative(amount: Double) = amount < 0

    fun isValid(amount: Double) = !amount.isNaN() && !amount.isInfinite()
}
