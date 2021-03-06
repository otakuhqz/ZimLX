/*
 *     This file is part of Lawnchair Launcher.
 *
 *     Lawnchair Launcher is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Lawnchair Launcher is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Lawnchair Launcher.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.zimmob.zimlx.iconpack

import android.content.Context
import android.content.pm.LauncherActivityInfo
import android.content.pm.ShortcutInfo
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi
import com.android.launcher3.*
import com.android.launcher3.shortcuts.ShortcutInfoCompat
import com.google.android.apps.nexuslauncher.DynamicIconProvider

class CustomIconProvider(context: Context) : DynamicIconProvider(context) {

    private val iconPackManager by lazy { IconPackManager.getInstance(context) }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getIcon(launcherActivityInfo: LauncherActivityInfo, iconDpi: Int, flattenDrawable: Boolean): Drawable {
        return iconPackManager.getIcon(launcherActivityInfo, iconDpi, flattenDrawable, null, this)//.assertNotAdaptiveIconDrawable(launcherActivityInfo)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getIcon(launcherActivityInfo: LauncherActivityInfo, itemInfo: ItemInfo, iconDpi: Int, flattenDrawable: Boolean): Drawable {
        return iconPackManager.getIcon(launcherActivityInfo, iconDpi, flattenDrawable, itemInfo, this).assertNotAdaptiveIconDrawable(launcherActivityInfo)
    }

    fun getIcon(shortcutInfo: ShortcutInfo, iconDpi: Int, flattenDrawable: Boolean): Drawable? {
        return iconPackManager.getIcon(shortcutInfo, iconDpi).assertNotAdaptiveIconDrawable(shortcutInfo)
    }

    fun getIcon(shortcutInfo: ShortcutInfoCompat, iconDpi: Int): Drawable? {
        return iconPackManager.getIcon(shortcutInfo, iconDpi).assertNotAdaptiveIconDrawable(shortcutInfo)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDynamicIcon(launcherActivityInfo: LauncherActivityInfo?, iconDpi: Int, flattenDrawable: Boolean): Drawable {
        return super.getIcon(launcherActivityInfo, iconDpi, flattenDrawable)//.assertNotAdaptiveIconDrawable(launcherActivityInfo)
    }

    private fun <T> T.assertNotAdaptiveIconDrawable(info: Any?): T {
        if (Utilities.ATLEAST_OREO && this is AdaptiveIconDrawable) {
            error("unwrapped AdaptiveIconDrawable for ${
            if (info is LauncherActivityInfo) info.applicationInfo else info
            }")
        }
        return this
    }

    companion object {

        @JvmStatic
        fun getAdaptiveIconDrawableWrapper(context: Context): AdaptiveIconCompat {
            return AdaptiveIconCompat.wrap(context.getDrawable(
                    R.drawable.adaptive_icon_drawable_wrapper)!!.mutate()) as AdaptiveIconCompat
        }
    }
}
