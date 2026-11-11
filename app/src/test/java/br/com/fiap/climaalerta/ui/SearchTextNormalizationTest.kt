package br.com.fiap.climaalerta.ui

import br.com.fiap.climaalerta.ui.screens.home.normalizeForSearch
import org.junit.Assert.assertEquals
import org.junit.Test

class SearchTextNormalizationTest {

    @Test
    fun `remove acentos, espacos e caixa alta`() {
        assertEquals("sao paulo", "  São Paulo ".normalizeForSearch())
        assertEquals("cuiaba", "CUIABÁ".normalizeForSearch())
    }
}
