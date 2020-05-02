/*
 * Copyright (c) 2020 Zim Launcher
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

package org.zimmob.zimlx.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.launcher3.LauncherFiles;
import com.android.launcher3.R;

import org.zimmob.zimlx.model.AppCountInfo;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_HOME = LauncherFiles.LAUNCHER_DB2;
    private static final String TABLE_APP_COUNT = "app_count";
    private static final String TABLE_DASH_ITEMS = "dash_items";

    /*CREAR TABLA PARA CONTAR APPS*/
    private static final String COLUMN_PACKAGE_NAME = "package_name";
    private static final String COLUMN_PACKAGE_COUNT = "package_count";
    private static final String COLUMN_PACKAGE_ID = "count_id";
    private static final String SQL_CREATE_COUNT =
            "CREATE TABLE " + TABLE_APP_COUNT + " ("
                    + COLUMN_PACKAGE_ID + " INTEGER PRIMARY KEY,"
                    + COLUMN_PACKAGE_NAME + " VARCHAR, "
                    + COLUMN_PACKAGE_COUNT + " INTEGER)";

    /*CREAR TABLA ITEMS*/
    /*private static final String COLUMN_DASH_ID = "dash_id";
    private static final String COLUMN_DASH_NAME = "dash_label";
    private static final String COLUMN_DASH_ICON = "dash_icon";
    private static final String COLUMN_DASH_ACTION = "dash_action";
    private static final String COLUMN_DASH_TYPE = "dash_type";
    private static final String COLUMN_DASH_VISIBLE = "dash_visible";
    private static final String SQL_CREATE_DASH =
            "CREATE TABLE " + TABLE_APP_COUNT + " ("
                    + COLUMN_DASH_ID + " INTEGER PRIMARY KEY,"
                    + COLUMN_DASH_NAME + " INTEGER, "
                    + COLUMN_DASH_ICON + " INTEGER, "
                    + COLUMN_DASH_ACTION + " VARCHAR, "
                    + COLUMN_DASH_TYPE + " INTEGER,"
                    + COLUMN_DASH_VISIBLE + " INTEGER)";*/

    private static final String SQL_DELETE = "DROP TABLE IF EXISTS ";

    private SQLiteDatabase db;

    public DbHelper(Context c) {
        super(c, DATABASE_HOME, null, 1);
        db = getWritableDatabase();
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_COUNT);
        //db.execSQL(SQL_CREATE_DASH);
        //loadDashItems();
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // discard the data and start over
        db.execSQL(SQL_DELETE + TABLE_APP_COUNT);
        db.execSQL(SQL_DELETE + TABLE_DASH_ITEMS);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /*public int getAppCount(String packageName) {
        String SQL_QUERY = "SELECT package_count FROM app_count WHERE package_name='" + packageName + "';";
        Cursor cursor = db.rawQuery(SQL_QUERY, null);
        int appCount = 0;
        if (cursor.moveToFirst()) {
            appCount = cursor.getInt(0);
        }
        cursor.close();
        Log.i("APP COUNT " + packageName, String.valueOf(appCount));
        return appCount;
    }*/
    public List<AppCountInfo> getAppsCount() {
        List<AppCountInfo> apps = new ArrayList<>();
        String SQL_QUERY = "SELECT package_name, package_count FROM app_count;";
        Cursor cursor = db.rawQuery(SQL_QUERY, null);
        if (!cursor.moveToFirst()) {
            return apps;
        }
        do {

            String name = cursor.getString(0);
            int count = cursor.getInt(1);
            apps.add(new AppCountInfo(name, count));
        }
        while (cursor.moveToNext());
        cursor.close();
        return apps;
    }

    public void updateAppCount(String packageName) {
        String SQL_QUERY = "SELECT package_count FROM app_count WHERE package_name='" + packageName + "';";
        Cursor cursor = db.rawQuery(SQL_QUERY, null);
        int appCount = 0;
        if (cursor.moveToFirst()) {
            appCount = cursor.getInt(0);
        } else {
            saveAppCount(packageName);
        }

        cursor.close();
        appCount++;
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PACKAGE_COUNT, appCount);
        db.update(TABLE_APP_COUNT, cv, "package_name='" + packageName + "'", null);
    }

    private void saveAppCount(String packageName) {
        ContentValues itemValues = new ContentValues();
        itemValues.put(COLUMN_PACKAGE_NAME, packageName);
        itemValues.put(COLUMN_PACKAGE_COUNT, 0);
        db.insert(TABLE_APP_COUNT, null, itemValues);
    }

    public void deleteApp(String packageName) {
        db.delete(TABLE_APP_COUNT, "package_name='" + packageName + "'", null);
    }

    /*private void loadDashItems(){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_DASH_ID, 1);
        cv.put(COLUMN_DASH_NAME, R.string.minibar_8);
        cv.put(COLUMN_DASH_ICON, 1);
        cv.put(COLUMN_DASH_ACTION, 1);
        cv.put(COLUMN_DASH_TYPE, 1);
        cv.put(COLUMN_DASH_VISIBLE, 1);

        saveDashItem(cv);
    }

    private void saveDashItem(ContentValues values){
        db.insert(TABLE_DASH_ITEMS, null, values);
    }*/
}