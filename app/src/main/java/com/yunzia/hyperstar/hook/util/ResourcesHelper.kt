package com.yunzia.hyperstar.hook.util

import android.content.res.Resources

interface ResourcesHelper {

    fun Resources.getColor(name:String,defPackage:String):Int{
        val id = this.getIdentifier(name,"color",defPackage);
        return this.getColor(id,this.newTheme())
    }

    fun Resources.getDimension(name:String,defPackage:String): Float {
        val id = this.getIdentifier(name,"dimen",defPackage);
        return this.getDimension(id)
    }

    fun Resources.getDimensionPixelOffset(name:String,defPackage:String):Int{
        val id = this.getIdentifier(name,"color",defPackage);
        return this.getDimensionPixelOffset(id)
    }

    fun Resources.getDimensionPixelSize(name:String,defPackage:String):Int{
        val id = this.getIdentifier(name,"color",defPackage);
        return this.getDimensionPixelSize(id)
    }

}