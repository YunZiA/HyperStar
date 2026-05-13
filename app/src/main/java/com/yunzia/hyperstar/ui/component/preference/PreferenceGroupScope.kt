package com.yunzia.hyperstar.ui.component.preference

@DslMarker
annotation class PreferenceGroupScopeMarker

@PreferenceGroupScopeMarker
interface PreferenceGroupScope

internal class PreferenceGroupScopeInstance : PreferenceGroupScope
