# How to generate a Language Forge TWA using Bubblewrap

1. Install Bubblewrap

`npm i -g @bubblewrap/cli`

Go through its prompts for configuring the JDK and Android Studio.

2. Initialize it as an Android Studio project with the following command:

`bubblewrap init --manifest https://languageforge.org/appManifest/languageforge.webmanifest`

3. Open the project in Android Studio.
"Build > Generate Signed Bundle / APK ..." and create a signed Bundle or APK. The keystore should be the same one as the one whose SHA256 fingerprints are in the languageforge.org/.well-known/assetlinks.json file.