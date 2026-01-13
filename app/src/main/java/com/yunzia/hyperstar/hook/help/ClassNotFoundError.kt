package com.yunzia.hyperstar.hook.help

class ClassNotFoundError : Error {
    /** @hide
     */
    constructor(cause: Throwable?) : super(cause)

    /** @hide
     */
    constructor(detailMessage: String?, cause: Throwable?) : super(detailMessage, cause)

    companion object {
        private val serialVersionUID = -1070936889459514628L
    }
}