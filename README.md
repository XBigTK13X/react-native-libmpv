# THIS IS A PLACEHOLDER AT THE MOMENT. NOT READY FOR USE.

Even when it's ready, I only plan on supporting Android.

iOS support contributions are welcome, but I have no way to test it.

# react-native-libmpv

Libmpv wrapper GUI component

## Installation

```sh
npm install react-native-libmpv
```

## Updating the AAR

Pull down the fork of libmpv-android.

Make the needed changes.

Update the version in the kotlin file.

Run `buildscripts/docker-build.sh`

Login as admin/admin to reposlite.9914.us

Navigate to the main/repository path

Download the previous version's POM. Edit to the new version number.

Rename generated aar to match versioning schema.

Upload POM and AAR to reposlite. Bump the version in the `react-native-libmpv` project.