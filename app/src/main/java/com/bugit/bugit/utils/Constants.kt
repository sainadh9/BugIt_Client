package com.bugit.bugit.utils

object Constants {



    const val BASE_URL = "https://sheets.googleapis.com/v4/spreadsheets/"
    const val GET_ALL_SHEETDATA = EXCEL_ID
    const val CREATE_SHEET = "${EXCEL_ID}:batchUpdate"
    const val CREATE_ROW = "${EXCEL_ID}/values/"
    const val REFRESH_OAUTH_TOKEN = "https://oauth2.googleapis.com/"
    const val SPLASH_TIME_OUT = 3000L
    const val INSERT_ROWS = "INSERT_ROWS"
    const val USER_ENTERED = "USER_ENTERED"

    //    Shared Preferences Keys
    const val SHARED_PREFS_NAME="BugItdata"
    const val NEW_TAB = "NEW_TAB"
    const val TOKEN = "TOKEN"






}