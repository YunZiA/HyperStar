package com.yunzia.hyperstar.ui.base.filter

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import kotlin.math.abs

class FilterNumber(
    private val minValue: Double = -Double.MAX_VALUE,
    private val maxValue: Double = Double.MAX_VALUE,
    private val decimalNumber: Int = -1
) : BaseFieldFilter("") {

    override fun onFilter(
        inputTextFieldValue: TextFieldValue,
        lastTextFieldValue: TextFieldValue
    ): TextFieldValue {
        return filterInputNumber(inputTextFieldValue, lastTextFieldValue, minValue, maxValue, decimalNumber)
    }

    private fun filterInputNumber(
        inputTextFieldValue: TextFieldValue,
        lastInputTextFieldValue: TextFieldValue,
        minValue: Double = -Double.MAX_VALUE,
        maxValue: Double = Double.MAX_VALUE,
        decimalNumber: Int = -1,
    ): TextFieldValue {
        val inputString = inputTextFieldValue.text
        val lastString = lastInputTextFieldValue.text

        val newString = StringBuffer()
        val supportNegative = minValue < 0
        var dotIndex = -1
        var isNegative = false

        if (supportNegative && inputString.isNotEmpty() && inputString.first() == '-') {
            isNegative = true
            newString.append('-')
        }

        for (c in inputString) {
            when (c) {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                    newString.append(c)
                    val tempValue = newString.toString().toDouble()
                    if (tempValue > maxValue) newString.deleteCharAt(newString.lastIndex)
                    if (tempValue < minValue) newString.deleteCharAt(newString.lastIndex) // 需要改进 （例如限制最小值为 100000000，则将无法输入东西）

                    if (dotIndex != -1) {
                        if (decimalNumber != -1) {
                            val decimalCount = (newString.length - dotIndex - 1).coerceAtLeast(0)
                            if (decimalCount > decimalNumber) newString.deleteCharAt(newString.lastIndex)
                        }
                    }
                }
                '.' -> {
                    if (decimalNumber != 0) {
                        if (dotIndex == -1) {
                            if (newString.isEmpty()) {
                                if (abs(minValue) < 1) {
                                    newString.append("0.")
                                    dotIndex = newString.lastIndex
                                }
                            } else {
                                newString.append(c)
                                dotIndex = newString.lastIndex
                            }

                            if (newString.isNotEmpty() && newString.toString().toDouble() == maxValue) {
                                dotIndex = -1
                                newString.deleteCharAt(newString.lastIndex)
                            }
                        }
                    }
                }
            }
        }

        val textRange: TextRange
        if (inputTextFieldValue.selection.collapsed) { // 表示的是光标范围
            if (inputTextFieldValue.selection.end != inputTextFieldValue.text.length) { // 光标没有指向末尾
                var newPosition = inputTextFieldValue.selection.end + (newString.length - inputString.length)
                if (newPosition < 0) {
                    newPosition = inputTextFieldValue.selection.end
                }
                textRange = TextRange(newPosition)
            }
            else { // 光标指向了末尾
                textRange = TextRange(newString.length)
            }
        }
        else {
            textRange = TextRange(newString.length)
        }

        return lastInputTextFieldValue.copy(
            text = newString.toString(),
            selection = textRange
        )
    }
}
