# react-native-libmpv

React Native component that wraps libmpv.

# Usage

I only plan on supporting Android.

iOS support contributions are welcome, but I have no way to test it.

It uses the legacy Native Module style. No Turbo support planned at this time.

The component will display video. Controls are handled by the app, not this library.

Take a look at https://github.com/XBigTK13X/snowstream for a real app using the library.

## Installation

```sh
npm install react-native-libmpv
```

## Updating the AAR

Pull down the fork of libmpv-android.

Make the needed changes.

Update the version in the kotlin file.

Run `buildscripts/docker-build.sh`

Run `buildscripts/prep-reposlite.sh VERSION`

Copy the versioned aar and pom to android/lib

Update the version in gradle.build

# Credits

I built this wrapper. But the library that drives the interactions with mpv comes from https://github.com/jarnedemeulemeester/libmpv-android.

That repo is the baseline, I merged in a PR that handle multi instance support and tweaked some things to my liking.