package com.yunzia.hyperstar.ui.pagers.dialog

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.yunzia.hyperstar.PagerList
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.BaseButton
import com.yunzia.hyperstar.ui.base.dialog.SuperCTDialogDefaults
import com.yunzia.hyperstar.ui.base.dialog.SuperXDialog
import com.yunzia.hyperstar.ui.base.dialog.SuperXPopupUtil.Companion.dismissXDialog
import com.yunzia.hyperstar.utils.PreferencesUtil
import top.yukonga.miuix.kmp.basic.Text

@Composable
fun RootDialog(
    show: MutableState<Boolean>,
    navController: NavHostController
) {

    SuperXDialog(
        title = stringResource(R.string.tips),
        show = show,
        onDismissRequest = {}
    ) {

        Text(
            stringResource(R.string.no_root_description),
            Modifier
                .padding(horizontal = 5.dp)
                .padding(top = 8.dp, bottom = 24.dp)
                .fillMaxWidth(),
            color = SuperCTDialogDefaults.summaryColor(),
            textAlign = TextAlign.Center,
            fontSize = 16.sp
        )
        BaseButton(
            text = stringResource(R.string.cancel),
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                dismissXDialog(show)
                PreferencesUtil.removePreferences("no_root_waring")
            }

        )
        Spacer(Modifier.height(12.dp))
        BaseButton(
            text = stringResource(R.string.no_warning),
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                dismissXDialog(show)
                PreferencesUtil.putBoolean("no_root_waring",false)

            }

        )
        Spacer(Modifier.height(12.dp))
        BaseButton(
            text = stringResource(R.string.quick_authorization),
            modifier = Modifier.fillMaxWidth(),
            submit = true,
            onClick = {
                dismissXDialog(show)
                navController.navigate(PagerList.GO_ROOT)

            }

        )

    }
}