package com.yunzia.hyperstar.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import java.util.Locale


class LanguageHelper {

    companion object{
        private fun setSystemLanguage(activity: Activity) {

            val context = activity.baseContext

            val config = Resources.getSystem().configuration;
            val locale = config.locales[0]

            val resources = context.resources
            val configuration = resources.configuration
            val lastLocale = configuration.locales[0]
            Locale.setDefault(locale)
            configuration.setLocale(locale)
            //resources.updateConfiguration(configuration, resources.displayMetrics)
            activity.recreate()

        }

        private fun setLanguage(activity: Activity, language: String?) {

            val context = activity.baseContext
            val resources = context.resources
            val configuration = resources.configuration
            val locale = Locale(language)
            val lastLocale = configuration.locales[0]
            Locale.setDefault(locale)
            configuration.setLocale(locale)
            //resources.updateConfiguration(configuration, resources.displayMetrics)
            activity.recreate()

        }

        private fun setLanguage(activity: Activity, language: String?, country: String?) {
            val context = activity.baseContext
            val resources = context.resources
            val configuration = resources.configuration
            val locale = Locale(language, country)
            val lastLocale = configuration.locales[0]

            Locale.setDefault(locale)
            configuration.setLocale(locale)
            //resources.updateConfiguration(configuration, resources.displayMetrics)
            activity.recreate()

        }

        private fun setLanguage(context: Context, locale: Locale?) {
            val resources = context.resources
            val configuration = resources.configuration
            Locale.setDefault(locale)
            configuration.setLocale(locale)
            resources.updateConfiguration(configuration, resources.displayMetrics)
        }

        fun setIndexLanguage(activity: Activity, index: Int) {
            when (index) {
                0 -> {
                    setSystemLanguage(activity)

                }

                1 -> {
                    setLanguage(activity, "zh", "CN")

                }

                2 -> {
                    setLanguage(activity, "en")

                }


            }
        }

        private fun getSystemLanguage():Locale {
            val config = Resources.getSystem().configuration;
            return config.locales[0]
        }

        private fun getLanguage(language: String?):Locale {
            return Locale(language)

        }

        private fun getLanguage(language: String?, country: String?):Locale {

            return Locale(language, country)
        }

        fun getIndexLanguage(index: Int):Locale {
            when (index) {
                0 -> {
                    return getSystemLanguage()

                }

                1 -> {
                    return getLanguage("zh", "CN")

                }

                2 -> {
                    return getLanguage("en")

                }
                3-> {
                    return getLanguage("ru")

                }

                4-> {
                    return getLanguage("vi")

                }


            }
            return getSystemLanguage()
        }

        fun wrap( context:Context): ContextWrapper {

            val res = context.resources;
            val configuration = res.configuration;
            val index = PreferencesUtil.getInt("app_language",0)

            configuration.setLocale(getIndexLanguage(index))

            val contexts = context.createConfigurationContext(configuration)

            return ContextWrapper(contexts);
        }


    }


}