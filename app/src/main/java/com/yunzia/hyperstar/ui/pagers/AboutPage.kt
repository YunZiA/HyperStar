package com.yunzia.hyperstar.ui.pagers

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.yunzia.hyperstar.PagerList
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.SuperIntentArrow
import com.yunzia.hyperstar.ui.base.SuperNavHostArrow
import com.yunzia.hyperstar.ui.base.classes
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.getWindowSize

@Composable
fun ThirdPage(
    activity : ComponentActivity,
    navController: NavController,
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues
) {

    LazyColumn(
        modifier = Modifier.height(getWindowSize().height.dp),
        contentPadding = PaddingValues(top = padding.calculateTopPadding()+16.dp, bottom = padding.calculateBottomPadding()+16.dp),
        topAppBarScrollBehavior = topAppBarScrollBehavior
    ) {
        item {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 60.dp),
                horizontalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "Hyper",
                    fontSize = 40.sp,
                    fontWeight = FontWeight(580),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Star",
                    fontSize = 40.sp,
                    fontWeight = FontWeight(580),
                    textAlign = TextAlign.Center,
                    color = colorResource(R.color.blue),
                )

                Text(
                    modifier=Modifier.padding(start = 10.dp),
                    text = "2.0",
                    fontSize = 40.sp,
                    fontWeight = FontWeight(580),
                    textAlign = TextAlign.Center,
                    color = colorResource(R.color.blue),
                )

            }

            Text(
                text = stringResource(id = R.string.xposed_desc),
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 140.dp),
                fontWeight = FontWeight.Medium,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))
        }
        classes(
            title = R.string.developer_title
        ){
            SuperIntentArrow(
                leftIcon = R.drawable.dd,
                title = "东东说他舍不得",
                summary = "@YunZiA | Hook",
                navController = navController,
                url = "coolmarket://u/8555749"
            )
            SuperNavHostArrow(
                title = stringResource(R.string.translator),
                navController = navController,
                route = PagerList.TRANSLATOR

            )


        }

        classes(
            title = R.string.discussion_title
        ){
            SuperIntentArrow(
                title = stringResource(R.string.qq_group_title),
                navController = navController,
                url = "http://qm.qq.com/cgi-bin/qm/qr?_wv=1027&amp;k=5ONF7LuaoQS6RWEOUYBuA0x4X8ssvHJp&amp;authKey=Pic4VQJxKBJwSjFzsIzbJ50ILs0vAEPjdC8Nat4zmiuJRlftqz9%2FKjrBwZPQTc4I&amp;noverify=0&amp;group_code=810317966"
            )
            SuperIntentArrow(
                title = "Telegram",
                navController = navController,
                url = "https://t.me/+QQWVM0ToHyEyZmRl"
            )

        }

        classes(
            title = R.string.others
        ) {
            SuperNavHostArrow(
                title = stringResource(R.string.references_title),
                navController = navController,
                route = PagerList.REFERENCES

            )
            SuperIntentArrow(
                title = stringResource(R.string.project_address),
                summary = stringResource(R.string.open_source_statement),
                navController = navController,
                url = "https://github.com/YunZiA/HyperStar2.0"
            )
        }


    }
}
