package com.ejemplo.centroreparacion

import com.ejemplo.centroreparacion.util.OhmCalculator
import org.junit.Assert.*
import org.junit.Test

class OhmCalculatorTest {
    private val EPS = 0.0001

    @Test fun `voltage 6A x 5ohm = 30V`() {
        val r = OhmCalculator.calculateVoltage(6.0, 5.0)
        assertEquals(30.0, r.result, EPS)
        assertNull(r.error)
    }
    @Test fun `current 30V / 5ohm = 6A`() {
        val r = OhmCalculator.calculateCurrent(30.0, 5.0)
        assertEquals(6.0, r.result, EPS)
    }
    @Test fun `resistance 30V / 6A = 5ohm`() {
        val r = OhmCalculator.calculateResistance(30.0, 6.0)
        assertEquals(5.0, r.result, EPS)
    }
    @Test fun `power 30V x 6A = 180W`() {
        val r = OhmCalculator.calculatePowerVI(30.0, 6.0)
        assertEquals(180.0, r.result, EPS)
    }
    @Test fun `power 6A² x 5ohm = 180W`() {
        val r = OhmCalculator.calculatePowerI2R(6.0, 5.0)
        assertEquals(180.0, r.result, EPS)
    }
    @Test fun `power 30V² / 5ohm = 180W`() {
        val r = OhmCalculator.calculatePowerV2R(30.0, 5.0)
        assertEquals(180.0, r.result, EPS)
    }
    @Test fun `division by zero returns error`() {
        val r = OhmCalculator.calculateCurrent(30.0, 0.0)
        assertNotNull(r.error)
    }
    @Test fun `negative values return error`() {
        val r = OhmCalculator.calculateVoltage(-1.0, 5.0)
        assertNotNull(r.error)
    }
}
