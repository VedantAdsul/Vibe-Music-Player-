# Vibe Music Player 🎧

**A music player Android app built with Java.**

Vibe is an Android music-player project featuring a curated music library, playback controls, favourites, search, and account/profile screens. It combines a custom XML-based interface with Firebase-backed account features.

## Features

- **Music playback** — play songs from the app's bundled audio resources.
- **Playback controls** — play/pause, next/previous, repeat, shuffle, and seek through a track.
- **Track progress** — view current playback time and track duration.
- **Music library** — browse a curated list of tracks with cover art.
- **Search** — find tracks by title or artist, including speech-to-text search using Android speech recognition.
- **Favourites** — mark tracks as favourites and view them in a separate screen.
- **User account screens** — registration, login, and profile management interfaces.
- **QR codes** — generate a QR code for a signed-in user's username and scan QR codes.
- **Firebase integration** — uses Firebase Authentication and Realtime Database, with Firebase Storage included as a dependency.


## Tech stack

- **Language:** Java
- **Platform:** Android
- **UI:** XML layouts, AndroidX, Material Components, View Binding
- **Build system:** Gradle (Kotlin DSL)
- **Authentication and cloud data:** Firebase Authentication and Realtime Database
- **Media playback:** Android `MediaPlayer`
- **Images:** Glide
- **Other libraries:** RecyclerView, ImageSlideshow

## Getting started

### Prerequisites

- Android Studio
- JDK 17 for the Android Gradle Plugin build
- Android SDK for **API 35**
- Android device or emulator running **Android 7.0 (API 24)** or later
- A Firebase project configured for this app if you want to test authentication or cloud-backed profile features

### Setup

1. Clone the repository:

   ```bash
   git clone https://github.com/VedantAdsul/Vibe-Music-Player-.git
   ```

2. Open the cloned project folder in Android Studio.
3. If you plan to use Firebase features, configure the project with your own Firebase Android app settings and place the corresponding `google-services.json` in the `app/` directory. Do not commit credentials or private configuration.
4. Sync the project with Gradle.
5. Build and run the `app` configuration on a compatible device or emulator.

You can also build the debug APK using the Gradle wrapper from the repository root:

**Windows**
```powershell
.\gradlew.bat assembleDebug
```

**macOS / Linux**
```bash
./gradlew assembleDebug
```

## Notes

- The current demo music library is defined in the source code and includes bundled audio resources. Update the song list and associated resources together when adding or removing tracks.
- Speech search depends on speech-recognition support being available on the device.
- Firebase-related screens and features require the correct Firebase project configuration and database security rules.
- Ensure you have the rights to distribute any audio tracks and artwork included in a build you share publicly.

## Security

Firebase client configuration is not a substitute for access control. Configure Firebase Authentication and secure Realtime Database and Storage rules so users cannot access or modify data they are not authorized to use. Never store plaintext passwords or private credentials in source control.

## Contributing

Suggestions and bug reports are welcome. Include the Android version, device/emulator details, steps to reproduce, and relevant logs when reporting a problem. Do not include private user data or credentials.

## License

No license has been specified yet. Unless a license is added to this repository, the source code is not explicitly offered for reuse, modification, or redistribution.
