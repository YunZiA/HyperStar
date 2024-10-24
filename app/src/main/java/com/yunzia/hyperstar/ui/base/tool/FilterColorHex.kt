package com.yunzia.hyperstar.ui.base.tool

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.yunzia.hyperstar.ui.base.filter.BaseFieldFilter


class FilterColorHex(
    value:String,
    private val includeAlpha: Boolean = true
) : BaseFieldFilter(value) {

    override fun onFilter(
        inputTextFieldValue: TextFieldValue,
        lastTextFieldValue: TextFieldValue
    ): TextFieldValue {
        if (inputTextFieldValue.selection.end == 0){
            return inputTextFieldValue.copy(filterInputColorHex(
                inputTextFieldValue.text,
                lastTextFieldValue.text,
                includeAlpha
            ), selection = TextRange(1)
            )
        }
        return inputTextFieldValue.copy(filterInputColorHex(
            inputTextFieldValue.text,
            lastTextFieldValue.text,
            includeAlpha
        ))
    }

    private fun filterInputColorHex(
        inputValue: String,
        lastValue: String,
        includeAlpha: Boolean = true
    ): String {
        val maxIndex = if (includeAlpha) 8 else 6
        val newString = StringBuffer()
        var index = 0
        newString.append('#')
        for (c in inputValue) {
            if (index > maxIndex) break

            if (index == 0) {
//                if (c == '#') {
//                    newString.append(c)
//                    index++
//                }
                index++
            }
            else {
                if (c in '0'..'9' || c.uppercase() in "A".."F" ) {
                    newString.append(c.uppercase())
                    index++
                }
            }
        }

        return newString.toString()
    }
}
