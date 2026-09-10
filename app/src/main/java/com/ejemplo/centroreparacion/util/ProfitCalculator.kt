package com.ejemplo.centroreparacion.util

/**
 * Cálculo de ganancias centralizado. Todas las pantallas deben usar esto
 * para evitar inconsistencias.
 *
 * Fórmula:
 *   costPieces  = suma de (precioUnitario × cantidad) de todas las piezas usadas
 *   totalCost   = costPieces + otherCosts + laborCost
 *   profit      = chargedPrice - totalCost
 */
object ProfitCalculator {
    data class Result(
        val piecesCost: Double,
        val otherCosts: Double,
        val laborCost: Double,
        val totalCost: Double,
        val chargedPrice: Double,
        val profit: Double
    )

    fun calculate(
        piecesCost: Double,
        otherCosts: Double,
        laborCost: Double,
        chargedPrice: Double
    ): Result {
        val total = MoneyUtils.add(piecesCost, otherCosts, laborCost)
        val profit = MoneyUtils.subtract(chargedPrice, total)
        return Result(piecesCost, otherCosts, laborCost, total, chargedPrice, profit)
    }
}
