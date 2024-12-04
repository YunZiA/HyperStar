package yunzia.colorpicker

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

const val mRow = 7
const val mColumn = 13

data class Size(val width:Float,val height:Float)


@Composable
fun ColorGrid(
    modifier: Modifier = Modifier,
    color: Color,
    onColorChange: (HsvColor) -> Unit
) {

    val colorList = getGridColors()
    var totalWidth = 0f
    var mSize  = 0f
    var mSizes : Size = Size(0F,0F)
    var realWidth = 0f

    var extraSpace = 0f// 计算剩余空间

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { offset ->
                        val point = getSelectPoint(center = offset, mSize, extraSpace)
                        if (!point.inside) {
                            return@detectTapGestures
                        }
                        val row = point.row
                        val column = point.column
                        val index = (row) * mColumn + column

                        onColorChange(HsvColor.from(getGridColors()[index]))

                    }
                )

            }
    ) {

        totalWidth = size.width
        Log.d("ggc", "ColorGrid: $totalWidth")
        mSize = (totalWidth / mColumn).toInt().toFloat()
        realWidth = mSize * mColumn
        extraSpace = totalWidth - realWidth

        drawColors(colorList,mSize,extraSpace)


        drawSelector(mSize,extraSpace,color)



    }

}


internal fun DrawScope.drawColors(
    colorList: List<Color>,
    mSize: Float,
    extraSpace: Float
) {
    val mRadius = (mSize/3).toInt().toFloat()
    colorList.forEachIndexed { index, color ->
        val row = index / mColumn
        val col = index % mColumn
        val x = col * mSize + (extraSpace / 2).toInt()
        val y = row.toFloat() * mSize

        when {
            row == 0 && col == 0 -> drawTopLeftCorner(x, y, color, mSize, mRadius)
            row == 0 && col == 12 -> drawTopRightCorner(x, y, color, mSize, mRadius)
            row == 6 && col == 0 -> drawBottomLeftCorner(x, y, color, mSize, mRadius)
            row == 6 && col == 12 -> drawBottomRightCorner(x, y, color, mSize, mRadius)
            else -> drawNormalRectangle(x, y, color, mSize)
        }
    }
}

internal fun DrawScope.drawSelector(
    selectionSize: Float,
    extraSpace: Float,
    colorPickerValueState: Color,
) {

    val stroke = selectionSize/3
    getGridColors().forEachIndexed { index, color ->
        val row = index / mColumn
        val col = index % mColumn
        if (color == colorPickerValueState){
            drawCircle(
                color = if (row < 3) Color(0xFFE4E4E4) else Color.White,
                radius = selectionSize/2-stroke/2,
                center = getSelectPointCenter(Point(row, col),selectionSize,extraSpace),
                style = Stroke(stroke)
            )
        }
    }


}

private fun getSelectPoint(center: Offset, size: Float, extraSpace: Float): Point {
    var x = center.x
    val y = center.y
    val maxSize = Offset(size * mColumn,size * mRow)
    if (x <= extraSpace/2 || x >= (maxSize.x+extraSpace/2) || y >= maxSize.y){
        Log.d("ggc", "getSelectPoint: outside")
        return Point(false)
    }
    x -= (extraSpace/2)

    val row = (y / size).toInt()
    val colum = (x / size).toInt()

    return Point(row,colum)

}

private fun getSelectPointCenter(
    center : Point,
    size: Float,
    extraSpace: Float
): Offset {

    val row = center.row
    val colum = center.column

    // 返回调整后的中心点
    return Offset(colum*size+size/2+extraSpace/2, row*size+size/2)

}

private fun DrawScope.drawTopLeftCorner(
    x: Float,
    y: Float,
    color: Color,
    mSize: Float,
    mRadius: Float
) {
    val space = mSize - mRadius
    val rSize = mRadius * 2
    drawArc(
        color = color,
        startAngle = 180f,
        sweepAngle = 90f,
        useCenter = true,
        topLeft = Offset(x, y),
        size = Size(rSize, rSize)
    )
    drawRect(
        color = color,
        topLeft = Offset(x, y + mRadius),
        size = Size(mSize, space)
    )
    drawRect(
        color = color,
        topLeft = Offset(x + mRadius, y),
        size = Size(space, mSize)
    )
}

private fun DrawScope.drawTopRightCorner(
    x: Float,
    y: Float,
    color: Color,
    mSize: Float,
    mRadius: Float
) {
    val space = mSize - mRadius
    val rSize = mRadius * 2
    val rSpace = mSize - mRadius * 2
    drawArc(
        color = color,
        startAngle = 270f,
        sweepAngle = 90f,
        useCenter = true,
        topLeft = Offset(x + rSpace, y),
        size = Size(rSize, rSize)
    )
    drawRect(
        color = color,
        topLeft = Offset(x, y + mRadius),
        size = Size(mSize, space)
    )
    drawRect(
        color = color,
        topLeft = Offset(x, y),
        size = Size(space, mSize)
    )
}

private fun DrawScope.drawBottomLeftCorner(
    x: Float,
    y: Float,
    color: Color,
    mSize: Float,
    mRadius: Float
) {
    val space = mSize - mRadius
    val rSize = mRadius * 2
    val rSpace = mSize - mRadius * 2
    drawArc(
        color = color,
        startAngle = 90f,
        sweepAngle = 90f,
        useCenter = true,
        topLeft = Offset(x, y + rSpace),
        size = Size(rSize, rSize)
    )
    drawRect(
        color = color,
        topLeft = Offset(x, y),
        size = Size(mSize, space)
    )
    drawRect(
        color = color,
        topLeft = Offset(x + mRadius, y),
        size = Size(space, mSize)
    )
}

private fun DrawScope.drawBottomRightCorner(
    x: Float,
    y: Float,
    color: Color,
    mSize: Float,
    mRadius: Float
) {
    val space = mSize - mRadius
    val rSize = mRadius * 2
    val rSpace = mSize - mRadius * 2
    drawArc(
        color = color,
        startAngle = 0f,
        sweepAngle = 90f,
        useCenter = true,
        topLeft = Offset(x + rSpace, y + rSpace),
        size = Size(rSize, rSize)
    )
    drawRect(
        color = color,
        topLeft = Offset(x, y),
        size = Size(mSize, space)
    )
    drawRect(
        color = color,
        topLeft = Offset(x, y),
        size = Size(space, mSize)
    )
}

private fun DrawScope.drawNormalRectangle(x: Float, y: Float, color: Color, mSize: Float) {
    drawRect(
        color = color,
        topLeft = Offset(x, y),
        size = Size(mSize, mSize)
    )
}



private fun getGridColors(): List<Color> {
    return listOf(
        Color(0xfffff1f0),
        Color(0xfffff2e8),
        Color(0xfffff7e6),
        Color(0xfffffbe6),
        Color(0xfffeffe6),
        Color(0xfffcffe6),
        Color(0xfff6ffed),
        Color(0xffe6fffb),
        Color(0xffe6f7ff),
        Color(0xfff0f5ff),
        Color(0xfff9f0ff),
        Color(0xfffff0f6),
        Color(0xffffffff),
        Color(0xffffb7b8),
        Color(0xffffd8bf),
        Color(0xffffe7ba),
        Color(0xfffff1b8),
        Color(0xffffffb8),
        Color(0xfff4ffb8),
        Color(0xffd9f7be),
        Color(0xffb5f5ec),
        Color(0xffbae7ff),
        Color(0xffd6e4ff),
        Color(0xffefdbff),
        Color(0xffffd6e7),
        Color(0xfffafafa),
        Color(0xffffa39e),
        Color(0xffffbb96),
        Color(0xffffd591),
        Color(0xffffe58f),
        Color(0xfffffb8f),
        Color(0xffeaff8f),
        Color(0xffb7ff95),
        Color(0xff87e8de),
        Color(0xff91d5ff),
        Color(0xffadc6ff),
        Color(0xffd3adf7),
        Color(0xffffadd2),
        Color(0xfff5f5f5),
        Color(0xffff4d4f),
        Color(0xffff7a45),
        Color(0xfffbb45d),
        Color(0xffffc53d),
        Color(0xffffec3d),
        Color(0xffbae637),
        Color(0xff73d13d),
        Color(0xff36cfc9),
        Color(0xff40a9ff),
        Color(0xff597ef7),
        Color(0xff9254de),
        Color(0xfff759ab),
        Color(0xffbfbfbf),
        Color(0xffcf1322),
        Color(0xffd4380d),
        Color(0xffd46b08),
        Color(0xffd48806),
        Color(0xffd4b106),
        Color(0xff7cb305),
        Color(0xff389e0d),
        Color(0xff08979c),
        Color(0xff096dd9),
        Color(0xff1d39c4),
        Color(0xff531dab),
        Color(0xffc41d7f),
        Color(0xff434343),
        Color(0xff820014),
        Color(0xff871400),
        Color(0xff873800),
        Color(0xff874d00),
        Color(0xff876800),
        Color(0xff3f6600),
        Color(0xff135200),
        Color(0xff00474f),
        Color(0xff003a8c),
        Color(0xff061178),
        Color(0xff22075e),
        Color(0xff780650),
        Color(0xff1f1f1f),
        Color(0xff5c0011),
        Color(0xff610b00),
        Color(0xff612500),
        Color(0xff613400),
        Color(0xff614700),
        Color(0xff254000),
        Color(0xff092b00),
        Color(0xff002329),
        Color(0xff002766),
        Color(0xff030852),
        Color(0xff120338),
        Color(0xff520339),
        Color(0xff000000),
    )
}
