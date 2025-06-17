package com.yunzia.hyperstar.ui.component.nav

import android.util.Log
import androidx.navigation.NavController


fun NavController.backParentPager(){
    val parentRoute = this.currentDestination?.route!!.substringBeforeLast("/")
    Log.d("ggc", "backParentPager: ${this.currentDestination?.route!!}\n$parentRoute")
    this.popBackStack(parentRoute,false)
}

fun NavController.backParentPager(parentRoute: String){
    this.popBackStack(parentRoute,false)
}

fun NavController.nav(
    route: String
){
    val currentRoute = this.currentDestination?.route
    if (currentRoute == route){
        Log.d("NavHostController", "nav repeat: $currentRoute")
        return
    }
    this.navigate(route)

}