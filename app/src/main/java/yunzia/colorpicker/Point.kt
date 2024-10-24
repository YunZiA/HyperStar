package yunzia.colorpicker

import androidx.compose.runtime.Stable

@Stable
class Point {
    var row: Int = -1
    var column: Int = -1
    var inside: Boolean

    internal constructor(inside: Boolean) {
        this.inside = inside
    }

    internal constructor(row: Int, column: Int) {
        this.row = row
        this.column = column
        this.inside = true
    }

}