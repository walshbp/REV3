REV3 - Remote Control of Your EV3
Copyright 2016, Bryan Walsh
All Rights Reserved

Team: BPWALSH

Device Requirements:
--------------------
This application was developed and tested under android marshmallow.

Build and Install Directions: Referenced from the android tutorial:
http://developer.android.com/training/basics/firstapp/running-app.html

Installing Application
----------------------
These directions assume you are installing from a linux/mac based os.

1) Set up your build environment

    Please enure the android sdk platform-tools/ and tools/ directory is in your 
    path, set the ANDROID_HOME environment variable to point to your android SDK.  
    For esample under linux if you are using bash:

    export ANDROID_HOME=/home/bwalsh/Android/Sdk
    export PATH=${PATH}:/home/bwalsh/Android/Sdk/platform-tools/:/home/bwalsh/Android/Sdk/tools/

1) Checkout the REV3 project from svn:

    svn checkout https://subversion.engr.arizona.edu/svn/jms/ece473-573-2016S/tags/bpwalsh/beta bpwalsh_beta

2) cd bpwalsh_beta/REV3
  
3) Build the application 

    ./gradlew assembleDebug 

4a) If installing on an emulator, start the android emulator image

    android avd

    Select the android emulator image you would like to run REV3 on and press 
    start. See above for device image requirements.

4b) If installing to a physical android phone, plug in your phone to the 
    comupter via USB

5) Install REV3

    adb install -r app/build/outputs/apk/app-debug.apk

To Run REV3
-----------

On the phone/emulator browse applications and tap on the REV3 application icon.

To Run Verification Tests
-------------------------
It is assumed that your environment is set up for testing as in:
http://developer.android.com/training/testing/start/index.html#setup

If you are runnning on an emulator you may have to increase your VM Heap size.

The following is from about running the tests from the command line: 
http://developer.android.com/training/testing/start/index.html#run-instrumented-tests

To run your instrumented tests from the command-line via Gradle, call the 
connectedAndroidTest (or cAT) task:

./gradlew cAT

You can find the generated HTML test result reports in the 
app/build/outputs/reports/androidTests/connected/ 
directory, and the corresponding XML files in the 
app/build/outputs/androidTest-results/connected/ 
directory.
