/*
 * 2020 Zim Launcher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package org.zimmob.zimlx.clock;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Process;

import com.android.launcher3.LauncherAppState;
import com.android.launcher3.icons.LauncherIcons;

import org.zimmob.zimlx.iconpack.AdaptiveIconCompat;

import java.util.Calendar;
import java.util.TimeZone;

public class ClockLayers {
    private final Calendar mCurrentTime;
    public Drawable mDrawable;
    public int mHourIndex;
    public int mMinuteIndex;
    public int mSecondIndex;
    public int mDefaultHour;
    public int mDefaultMinute;
    public int mDefaultSecond;
    public int offset;
    public float scale;
    public Bitmap iconBitmap;
    private LayerDrawable mLayerDrawable;

    ClockLayers() {
        mCurrentTime = Calendar.getInstance();
    }

    @Override
    public ClockLayers clone() {
        ClockLayers ret = null;
        if (mDrawable == null) {
            return null;
        }
        ClockLayers clone = new ClockLayers();
        clone.scale = scale;
        clone.mHourIndex = mHourIndex;
        clone.mMinuteIndex = mMinuteIndex;
        clone.mSecondIndex = mSecondIndex;
        clone.mDefaultHour = mDefaultHour;
        clone.mDefaultMinute = mDefaultMinute;
        clone.mDefaultSecond = mDefaultSecond;
        clone.iconBitmap = iconBitmap;
        clone.offset = offset;
        clone.mDrawable = mDrawable.getConstantState().newDrawable();
        clone.mLayerDrawable = clone.getLayerDrawable();
        if (clone.mLayerDrawable != null) {
            ret = clone;
        }
        return ret;
    }

    void setupBackground(Context context) {
        LauncherIcons launcherIcons = LauncherIcons.obtain(context);
        float[] tmp = new float[1];
        Drawable icon = getBackground().getConstantState().newDrawable();
        if (mDrawable instanceof AdaptiveIconCompat) {
            icon = new AdaptiveIconCompat(icon, null);
        }
        iconBitmap = launcherIcons.createBadgedIconBitmap(icon, Process.myUserHandle(), 26, false, tmp).icon;
        scale = tmp[0];
        offset = (int) Math.ceil(0.0104167f * ((float) LauncherAppState.getInstance(context).getInvariantDeviceProfile().iconBitmapSize));
        launcherIcons.recycle();
    }

    boolean updateAngles() {
        mCurrentTime.setTimeInMillis(System.currentTimeMillis());

        int hour = (mCurrentTime.get(Calendar.HOUR) + (12 - mDefaultHour)) % 12;
        int minute = (mCurrentTime.get(Calendar.MINUTE) + (60 - mDefaultMinute)) % 60;
        int second = (mCurrentTime.get(Calendar.SECOND) + (60 - mDefaultSecond)) % 60;
        int millis = second * 1000 + mCurrentTime.get(Calendar.MILLISECOND);

        boolean hasChanged = false;
        if (mHourIndex != -1 && mLayerDrawable.getDrawable(mHourIndex).setLevel(hour * 60 + mCurrentTime.get(Calendar.MINUTE))) {
            hasChanged = true;
        }
        if (mMinuteIndex != -1 && mLayerDrawable.getDrawable(mMinuteIndex).setLevel(minute + mCurrentTime.get(Calendar.HOUR) * 60)) {
            hasChanged = true;
        }
        if (mSecondIndex != -1 && mLayerDrawable.getDrawable(mSecondIndex).setLevel(millis / 100)) {
            hasChanged = true;
        }
        return hasChanged;
    }

    void setTimeZone(TimeZone timeZone) {
        mCurrentTime.setTimeZone(timeZone);
    }

    LayerDrawable getLayerDrawable() {
        if (mDrawable instanceof LayerDrawable) {
            return (LayerDrawable) mDrawable;
        }
        if (mDrawable instanceof AdaptiveIconCompat) {
            AdaptiveIconCompat adaptiveIconDrawable = (AdaptiveIconCompat) mDrawable;
            if (adaptiveIconDrawable.getForeground() instanceof LayerDrawable) {
                return (LayerDrawable) adaptiveIconDrawable.getForeground();
            }
        }
        return null;
    }

    Drawable getBackground() {
        if (mDrawable instanceof AdaptiveIconCompat) {
            return ((AdaptiveIconCompat) mDrawable).getBackground();
        } else {
            return mDrawable;
        }
    }

    void clipToMask(Canvas canvas) {
        if (mDrawable instanceof AdaptiveIconCompat) {
            canvas.clipPath(((AdaptiveIconCompat) mDrawable).getIconMask());
        }
    }

    void drawForeground(Canvas canvas) {
        if (mDrawable instanceof AdaptiveIconCompat) {
            ((AdaptiveIconCompat) mDrawable).getForeground().draw(canvas);
        } else {
            mDrawable.draw(canvas);
        }
    }
}
