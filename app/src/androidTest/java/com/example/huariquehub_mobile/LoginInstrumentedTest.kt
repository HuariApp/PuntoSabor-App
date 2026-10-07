package com.example.huariquehub_mobile

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pruebas de sistema (instrumentadas) de la app móvil — Autenticación (Escenario 1 del BDD).
 *
 * Se ejecutan sobre el emulador de Android Studio y contra el backend desplegado
 * (ApiClient.BASE_URL), igual que las pruebas E2E del Front.
 *
 * Cuentas de prueba:
 *  - Explorador: qa.pago5@gmail.com / prueba123  (existe, rol Explorer)
 *  - Dueño:      qa.dueno1@gmail.com / prueba123 (crearla antes desde "Regístrate" en la app)
 */
@RunWith(AndroidJUnit4::class)
class LoginInstrumentedTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val explorerEmail = "qa.pago5@gmail.com"
    private val ownerEmail = "qa.dueno1@gmail.com"
    private val password = "prueba123"

    /** Escribe correo y contraseña y pulsa "Ingresar". */
    private fun login(email: String, pass: String) {
        composeTestRule.onNodeWithText("Correo electrónico").performTextInput(email)
        composeTestRule.onNodeWithText("Contraseña").performTextInput(pass)
        // Marca el checkbox de "términos y condiciones" (si no, la app no llama al backend)
        composeTestRule.onNode(isToggleable()).performClick()
        composeTestRule.onNodeWithText("Ingresar").performClick()
    }

    /** Espera (hasta 20 s) a que aparezca alguno de los textos indicados. */
    private fun waitForAnyText(vararg texts: String) {
        try {
            composeTestRule.waitUntil(timeoutMillis = 20_000) {
                texts.any { text ->
                    composeTestRule
                        .onAllNodesWithText(text, substring = true, ignoreCase = true)
                        .fetchSemanticsNodes()
                        .isNotEmpty()
                }
            }
        } catch (e: Throwable) {
            // Diagnóstico: imprime lo que hay en pantalla (Logcat, filtro "LOGIN_TEST")
            composeTestRule.onRoot().printToLog("LOGIN_TEST")
            throw e
        }
    }

    @Test
    fun login_conCamposVacios_muestraMensajeDeValidacion() {
        composeTestRule.onNodeWithText("Ingresar").performClick()

        composeTestRule
            .onNodeWithText("Por favor completa todos los campos")
            .assertIsDisplayed()
    }

    @Test
    fun login_conContrasenaCorta_muestraMensajeDeValidacion() {
        login(explorerEmail, "123")

        composeTestRule
            .onNodeWithText("La contraseña debe tener al menos 6 caracteres")
            .assertIsDisplayed()
    }

    @Test
    fun login_conContrasenaIncorrecta_muestraError() {
        login(explorerEmail, "clave-incorrecta-999")

        waitForAnyText("Credenciales inválidas", "Correo o contraseña incorrectos")
    }

    @Test
    fun login_conCuentaDeExplorador_muestraPantallaSoloParaDuenos() {
        login(explorerEmail, password)

        waitForAnyText("Esta app es para dueños")
        composeTestRule.onNodeWithText("Esta app es para dueños").assertIsDisplayed()
    }

    @Test
    fun login_conCuentaDeDueno_ingresaAlPanel() {
        login(ownerEmail, password)

        waitForAnyText("Mi Panel")
        composeTestRule.onNodeWithText("Mi Panel").assertIsDisplayed()
    }
}