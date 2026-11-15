plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.androidLibrary) apply false

    id("com.google.devtools.ksp") version "2.3.14" apply false
    id("com.google.dagger.hilt.android") version "2.60.1" apply false
}
