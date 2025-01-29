/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.tiptime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tiptime.ui.theme.TipTimeTheme
import java.text.NumberFormat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            TipTimeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    TipTimeLayout()
                }
            }
        }
    }
}

@Composable
fun TipTimeLayout() {
    //siempre que se modifique amountInput va a disparar una recomposicion
    var amountInput by remember { mutableStateOf("") }
    val amount = amountInput.toDoubleOrNull() ?: 0.0
    val tip = calculateTip(amount)
    //se puede usar la lambda quese definio dentro de EditNumberField ya que esya solo le pasa
    //el nuevo valor a amountInput, lo que desencadena la recomposición
    val onAmountChange: (String) -> Unit = {
            newAmount -> amountInput = newAmount
    }
    Column(
        modifier = Modifier
            .statusBarsPadding()
            .padding(horizontal = 40.dp)
            .safeDrawingPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.calculate_tip),
            modifier = Modifier
                .padding(bottom = 16.dp, top = 40.dp)
                .align(alignment = Alignment.Start)
        )
        //campo de texto
        EditNumberField(
            value = amountInput,
            //usando la lambda
            //onValueChange = onAmountChange,
            onValueChange = {amountInput = it},
            modifier = Modifier.padding(bottom = 32.dp).fillMaxWidth()
        )
        Text(
            text = stringResource(R.string.tip_amount, tip),
            style = MaterialTheme.typography.displaySmall
        )
        Spacer(modifier = Modifier.height(150.dp))
    }
}

//para hacer hoisting al state de un composable se deben de agregar los parametros
//value: T y onValueChange: (T) -> Unit
//despues del hoisting este composable ya no tiene logica de estado y toda la logica
//para convertir y calcular la propina se extrae para que sea accesible a otros composables
//fuera de este contexto
@Composable
fun EditNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
){
    //mutableStateOf() retorna un valor que es considerado estado de la app
    //es mutable y es observable, por lo que cualquier cambio va a disparar una recomposition
    //la siguiente manera de usar un motableStateOf() marca el arbol de cambios como dirty
    //y dispara una recomposicion, pero no conserva el valor entre recomposiciones
    //var amountInput: MutableState<String> = mutableStateOf("0")
    //con remember, cada que se modifica el estado mutableStateOf
    //por lo que se dispara una nueva recomposition
    //se delega el getter y setter default a remember, por lo que ya no es necesario acceder directamente
    //a la propiedad value de MutableState
    //cuando se hace hoisting la siguiente linea ya no tiene uso en este contexto
    //var amountInput by remember { mutableStateOf("") }
    //convertimos el input un valor double y se alamacena en amount
    //en caso de ser null (la conversion no es posible) mediante el operador Elvis pasamos el valor de 0.0
    //las siguientes variables tampoco tienen mas uso en este contexto
    /*
    val amount = amountInput.toDoubleOrNull() ?: 0.0
    val tip = calculateTip(amount)
    */
    //creamos una lambda que se va a pasar a onValueChange
    //posterior al hoisting esta lambda se saca de este composable
    /*
    val CalculateAndDisplayTip: (String) -> Unit = {
            newAmount -> amountInput = newAmount
    }
    */
    //se puede obtener el valor mediante la propiedad value cuando se usa mutableStateOf()
    TextField(
        //se agrega una etiqueta dentro del textField
        label = {
            Text(
                text = stringResource(R.string.bill_amount)
            )
        },
        //Compose mantiene un registro de todos los composables que leen el value de los observables de estado
        //cada que estos value cambian, Compose ejecuta una recomposicion en sus composables donde se lee el value
        //value = amountInput.value
        //cuando se delega amountInput a remember, este es ya no es directamente un mutableStateOf
        //por lo que no se usa la propiedad value
        //value = amountInput,
        //posterior al hoisting, value se va a encadenar con el parametro que tambien se llama value
        value = value,
        //esta asignacion dispara la recomposición de este composable ya que el value de amountInput se modifica
        //y aqui es donde se lee el valor de amountImput cuando se asigna al value de TextField
        //onValueChange = CalculateAndDisplayTip,
        //posterior al hoisting, tambien se va a encadenar el cambio de valor con la funcion externa
        //esto se debe a que el estado ya no es interno a este Composable
        //es decir ya no existe una variable amountInput que mantenga el estado, se maneja externamente
        //mediante value y onValueChange
        //al no tener estado, en este conxto no hay nada que actualizar, se reliza desde el exterior con la lambda
        onValueChange = onValueChange,
        modifier = modifier,
        //condensa el TextField en una sola linea, horizontally scrollable
        singleLine = true,
        //indica que se usara un teclado numerico en este textField
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        )
    )
}

/**
 * Calculates the tip based on the user input and format the tip amount
 * according to the local currency.
 * Example would be "$10.00".
 */
private fun calculateTip(amount: Double, tipPercent: Double = 15.0): String {
    val tip = tipPercent / 100 * amount
    return NumberFormat.getCurrencyInstance().format(tip)
}

@Preview(showBackground = true)
@Composable
fun TipTimeLayoutPreview() {
    TipTimeTheme {
        TipTimeLayout()
    }
}
