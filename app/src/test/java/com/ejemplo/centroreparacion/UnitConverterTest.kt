package com.ejemplo.centroreparacion

import com.ejemplo.centroreparacion.util.UnitConverter
import org.junit.Assert.*
import org.junit.Test

class UnitConverterTest {
    private val EPS = 0.0001

    @Test fun `V a mV`() {
        assertEquals(1500.0, UnitConverter.convert(1.5, "V", "mV")!!, EPS)
    }
    @Test fun `kohm a ohm`() {
        assertEquals(4700.0, UnitConverter.convert(4.7, "kΩ", "Ω")!!, EPS)
    }
    @Test fun `W a kW`() {
        assertEquals(1.5, UnitConverter.convert(1500.0, "W", "kW")!!, EPS)
    }
    @Test fun `no permite V a A`() {
        assertNull(UnitConverter.convert(5.0, "V", "A"))
    }
    @Test fun `no permite ohm a W`() {
        assertNull(UnitConverter.convert(5.0, "Ω", "W"))
    }
    @Test fun `no permite W a V`() {
        assertNull(UnitConverter.convert(5.0, "W", "V"))
    }
}
