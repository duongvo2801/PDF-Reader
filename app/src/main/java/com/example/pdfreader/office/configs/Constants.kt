package com.example.pdfreader.office.configs


object Constants {

    const val SYSTEM_SEPARATOR = "/"

    val PROJECT_ROOT_DIRECTORY: String = System.getProperty("user.dir").replace("\\", SYSTEM_SEPARATOR)

//    const val DEFAULT_FOLDER_TMP = "$PROJECT_ROOT_DIRECTORY/tmp"
//    const val DEFAULT_FOLDER_TMP_GENERATE = "$PROJECT_ROOT_DIRECTORY/tmp-generate"
}
