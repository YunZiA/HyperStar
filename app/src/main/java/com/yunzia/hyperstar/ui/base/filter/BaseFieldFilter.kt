package com.yunzia.hyperstar.ui.base.filter

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

open class BaseFieldFilter() {
    private var inputValue = mutableStateOf(TextFieldValue())

    constructor(value:String) : this() {
        inputValue.value = TextFieldValue(value, TextRange(value.lastIndex+1))
    }

    constructor(value:String,defValue:String) : this() {
        if (value != defValue){
            inputValue.value = TextFieldValue(value, TextRange(value.lastIndex+1))

        }else{
            val newString = StringBuffer()
            newString.append("")
            inputValue.value = TextFieldValue(newString.toString(), TextRange(newString.lastIndex+1))

        }
    }

    protected open fun onFilter(inputTextFieldValue: TextFieldValue, lastTextFieldValue: TextFieldValue): TextFieldValue {
        return TextFieldValue()
    }

    protected open fun computePos(): Int {

        return 0
    }

    protected fun getNewTextRange(
        lastTextFiled: TextFieldValue,
        inputTextFieldValue: TextFieldValue
    ): TextRange? {
        return null
    }

    protected fun getNewText(
        lastTextFiled: TextFieldValue,
        inputTextFieldValue: TextFieldValue
    ): TextRange? {

        return null
    }


    fun setInputValue(value: String) {
        inputValue.value = TextFieldValue(value, TextRange(value.lastIndex+1))
    }

    fun getInputValue(): TextFieldValue {
        return inputValue.value
    }

    fun onValueChange(): (TextFieldValue) -> Unit {
        return {
            inputValue.value = onFilter(it, inputValue.value)
        }
    }
}
