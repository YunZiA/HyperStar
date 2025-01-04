package com.yunzia.hyperstar.ui.base.nav

import android.os.Bundle
import android.util.Log
import androidx.navigation.NavHostController


fun NavHostController.goBackRouteWithParams(
    route: String,
    autoPop: Boolean = true,
    callback: (Bundle.() -> Unit)? = null,
) {
    getBackStackEntry(route).arguments?.let {
        callback?.invoke(it)
    }
    if (autoPop) {
        popBackStack()
    }
}

fun NavHostController.goBackWithParams(
    autoPop: Boolean = true,
    callback: (Bundle.() -> Unit)? = null,
) {
    previousBackStackEntry?.arguments?.let {
        callback?.invoke(it)
    }
    if (autoPop) {
        popBackStack()
    }
}

fun  NavHostController.nav(
    route: String
){
    val currentRoute = this.currentDestination?.route
    if (currentRoute == route){
        Log.d("NavHostController", "nav repeat: $currentRoute")
        return
    }
    this.navigate(route)

}