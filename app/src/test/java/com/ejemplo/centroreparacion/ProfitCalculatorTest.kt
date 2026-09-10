package com.ejemplo.centroreparacion

import com.ejemplo.centroreparacion.util.ProfitCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class ProfitCalculatorTest {
    private val EPS = 0.001

    @Test fun `ejemplo del requerimiento`() {
        // pantalla 1000 + batería 500 = piezas 1500
        // mano 700 + otros 100 = 800
        // cobrado 3000
        val r = ProfitCalculator.calculate(
            piecesCost = 1500.0, otherCosts = 100.0, laborCost = 700.0, chargedPrice = 3000.0)
        assertEquals(1500.0, r.piecesCost, EPS)
        assertEquals(2300.0, r.totalCost, EPS)
        assertEquals(700.0, r.profit, EPS)
    }

    @Test fun `sin piezas`() {
        val r = ProfitCalculator.calculate(0.0, 0.0, 500.0, 800.0)
        assertEquals(500.0, r.totalCost, EPS)
        assertEquals(300.0, r.profit, EPS)
    }

    @Test fun `ganancia negativa si cobra menos`() {
        val r = ProfitCalculator.calculate(1000.0, 0.0, 500.0, 1200.0)
        assertEquals(1500.0, r.totalCost, EPS)
        assertEquals(-300.0, r.profit, EPS)
    }
}
