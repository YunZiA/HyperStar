package com.yunzia.hyperstar.ui.pagers.dialog

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.BaseButton
import com.yunzia.hyperstar.ui.base.dialog.SuperXDialog
import com.yunzia.hyperstar.ui.base.dialog.SuperXPopupUtil.Companion.dismissXDialog
import com.yunzia.hyperstar.utils.PreferencesUtil
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

@Composable
fun VerDialog(
    show: MutableState<Boolean>,
    navController: NavHostController
) {

    val mContext = navController.context

    SuperXDialog(
        title = stringResource(R.string.os_tips),
        show = show,
        onDismissRequest = {}
    ) {

        Text(
            text = stringResource(R.string.os_description),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp),
            textAlign = TextAlign.Start,
            color = colorScheme.onSurface,
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp,
            style = TextStyle(textIndent = TextIndent(20.sp, 0.sp))

        )
        Text(
            text = stringResource(R.string.os1_link),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .padding(top = 12.dp, bottom = 25.dp)
                .clickable(
                    onClick = {
                        mContext.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://github.com/YunZiA/HyperStar")
                            )
                        )
                    },
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ),
            textDecoration = TextDecoration.Underline,
            textAlign = TextAlign.End,
            color = colorScheme.primary,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp
        )
        BaseButton(
            text = stringResource(R.string.cancel),
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                dismissXDialog(show)
                PreferencesUtil.removePreferences("ver_waring")
            }

        )
        Spacer(Modifier.height(12.dp))
        BaseButton(
            text = stringResource(R.string.no_warning),
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                dismissXDialog(show)
                PreferencesUtil.putBoolean("ver_waring",false)

            }

        )


    }

}