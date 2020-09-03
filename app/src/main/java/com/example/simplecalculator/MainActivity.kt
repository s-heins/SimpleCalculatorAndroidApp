package com.example.simplecalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.ArithmeticException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onDigit(view: View) {
        tvDisplay.append((view as Button).text)
    }

    fun onOperator(view: View) {
        if (textEndsWithDigit(tvDisplay.text)) {
            tvDisplay.append((view as Button).text)
        }
    }

    fun onOperatorMinus(view: View) {
        if (textEndsWithDigitOrOperator(tvDisplay.text) || tvDisplay.text.isBlank()) {
            tvDisplay.append((view as Button).text)
        }
    }

    fun onClear(view: View) {
        tvDisplay.text = ""
    }

    fun onDecimalPoint(view: View) {
        if (!tvDisplay.text.contains(".")) {
            tvDisplay.append(".")
        }
    }

    fun onEqual(view: View) {
        val text = tvDisplay.text.toString()
        if (textEndsWithDigit(text)) {
            try {
                tvDisplay.text = calculateSubtotal(text).toString()
            } catch (e: ArithmeticException) {
                e.printStackTrace()
            }
        }
    }

    private fun calculateSubtotal(text: String): Double {
        if (!textContainsOperator(text)) {
            return getValueFromString(text)
        }
        val equationString = if (text.startsWith("-")) "0$text" else text

        val patternDecimalOrIntOrOperator = "\\d+\\.\\d+|\\d+|[/*+-]"
        val equationElements =
            Regex(patternDecimalOrIntOrOperator).findAll(equationString).map { it.groupValues }
                .flatten().toList()

        if (equationElements.contains("/")) {
            val positionOfOperator = equationElements.indexOf("/")
            val valueOfOperation = getValueFromString(equationElements[positionOfOperator - 1]).div(
                getValueFromString(equationElements[positionOfOperator + 1])
            )
            val equationBeforeOperation =
                equationElements.subList(0, positionOfOperator - 1).joinToString("")
            val equationAfterOperation =
                equationElements.subList(positionOfOperator + 2, equationElements.lastIndex + 1)
                    .joinToString("")
            return calculateSubtotal("$equationBeforeOperation$valueOfOperation$equationAfterOperation")
        }
        if (equationElements.contains("*")) {
            val positionOfOperator = equationElements.indexOf("*")
            val valueOfOperation =
                getValueFromString(equationElements[positionOfOperator - 1]).times(
                    getValueFromString(equationElements[positionOfOperator + 1])
                )
            val equationBeforeOperation =
                equationElements.subList(0, positionOfOperator - 1).joinToString("")
            val equationAfterOperation =
                equationElements.subList(positionOfOperator + 2, equationElements.lastIndex + 1)
                    .joinToString("")
            return calculateSubtotal("$equationBeforeOperation$valueOfOperation$equationAfterOperation")
        }
        if (equationElements.contains("+")) {
            val positionOfOperator = equationElements.indexOf("+")
            val valueOfOperation =
                getValueFromString(equationElements[positionOfOperator - 1]).plus(
                    getValueFromString(equationElements[positionOfOperator + 1])
                )
            val equationBeforeOperation =
                equationElements.subList(0, positionOfOperator - 1).joinToString("")
            val equationAfterOperation =
                equationElements.subList(positionOfOperator + 2, equationElements.lastIndex + 1)
                    .joinToString("")
            return calculateSubtotal("$equationBeforeOperation$valueOfOperation$equationAfterOperation")
        }
        if (equationElements.contains("-")) {
            val positionOfOperator = equationElements.indexOf("-")
            val valueOfOperation =
                getValueFromString(equationElements[positionOfOperator - 1]).minus(
                    getValueFromString(equationElements[positionOfOperator + 1])
                )
            val equationBeforeOperation =
                equationElements.subList(0, positionOfOperator - 1).joinToString("")
            val equationAfterOperation =
                equationElements.subList(positionOfOperator + 2, equationElements.lastIndex + 1)
                    .joinToString("")
            return calculateSubtotal("$equationBeforeOperation$valueOfOperation$equationAfterOperation")
        }
        return 0.0
    }

    private fun getValueFromString(string: String): Double {
        return if (string.isBlank()) 0.0 else string.toDouble()
    }

    private fun textContainsOperator(text: CharSequence): Boolean {
        return text.contains("*") || text.contains("/") || text.contains("+") || text.contains("-")
    }

    private fun textMatchesRegexString(text: CharSequence, regexString: String): Boolean {
        return text.matches(Regex(regexString))
    }

    private fun textEndsWithDigit(text: CharSequence): Boolean {
        return textMatchesRegexString(text, ".*\\d$")
    }

    private fun textEndsWithDigitOrOperator(text: CharSequence): Boolean {
        val stringEndsWithOperator = ".*[/*+-]$"
        val stringEndsWithDigit = ".*\\d$"
        return textMatchesRegexString(text, "$stringEndsWithOperator|$stringEndsWithDigit")
    }
}