package com.example.tiptime

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import com.example.tiptime.ui.theme.TipTimeTheme
import org.junit.Rule
import org.junit.Test
import java.text.NumberFormat
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier

class TipUITests {
    //se debe crear una regla y anotarse con Rule, si no se hace con get, la regla debe ser public
    @get:Rule
    val composeTestRule = createComposeRule()

    //el metodo tambien se debe de anotarse con Test
    //aunque tanto las pruebas locales y de instrumentacion se anotan con Test, el compilador
    //sabe que son pruebas locales si estan en test/java y pruebas de instrumentacion si estan en AndroidTest/java

    @Test
    fun calculate_20_percent_tip(){
        val expectedTip = NumberFormat.getCurrencyInstance().format(20.00)
        //llamamos setContent sobre la composeRule creada y podemos pasar como lambda un codigo
        //similar al que usamos en el onCreate() en la MainActivity de la app
        composeTestRule.setContent {
            TipTimeTheme {
                Surface (modifier = Modifier.fillMaxSize()){
                    TipTimeLayout()
                }
            }
        }
        //una vez que la UI se ha cargado en la composeTestRule, se puede acceder a los elementos de la UI
        //como los TextFields, Text, etc. esos elementos estan en composeTestRule en forma de nodos
        //una forma de acceder a un nodo con un texto en particular es usando onNodeWithText
        //cuando tenemos el nodo que contiene el texto indicado, podemos usar performTextInput para
        //escribir el valor en el nodo que es un TextField
        composeTestRule.onNodeWithText("Monto de la cuenta")
            .performTextInput("100")
        composeTestRule.onNodeWithText("Procentaje de la propina")
            .performTextInput("20")
        //ya que se asignaron los campso de monto y porcentaje, la UI deberia de mostrar el texto
        //con la propina correspondiente
        composeTestRule.onNodeWithText("Monto de la propina: $expectedTip")
            .assertExists("no existe un nodo con este texto")

    }
}