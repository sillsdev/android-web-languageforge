# Building and signing the Language Forge Android app

1. Download [Android Studio](https://developer.android.com/studio)
2. In Android Studio, in 'File' > 'New' > 'Project from Version Control...', paste `https://github.com/sillsdev/android-web-languageforge.git` for 'URL', and click 'Clone'.
3. Verify that the version code and version name say what you want them to in build.gradle.
4. In 'Build' > 'Select Build Variant...', under 'Active Build Variant', click 'debug' (if visible) to open a dropdown, and select 'release'.
5. Build an unsigned APK by going to 'Build' > 'Build Bundles/APK(s)' > 'Build APK(s)'.
6. Upload the newly built release APK to the Google Play Store, and have it signed in the developer account by the keystore corresponding to the SHA fingerprints in the LanguageForge.org [assetlinks file](https://languageforge.org/.well-known/assetlinks.json).