#! /bin/bash

cd android
./gradlew clean
cd ..
cd example/android
./gradlew clean
cd ../..