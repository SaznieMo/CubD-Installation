# ReadMe for CubD

CubD is a Rubiks Cube solving mobile application which incorporates Computer Vision enabling you to scan a Rubik’s Cube using your android device’s camera.

The included CubDInstall.APK file can be installed on any Android device running Android 4 and above. Drag and drop the .APK file into the device’s file storage system, navigate to the location on the device and double tap to download. Enable camera and storage permissions for the application upon installation. 

# Source Code
The full source code for this application is available at https://github.com/SaznieMo/CubD-Installation. Due to the size of the OpenCV library used, the full source code files could not be made available inside this .ZIP folder. Download the folder from Github.

# Build Instructions
The following instructions detail how to build and run the application if required. Android Studio with SDK 28 must be enabled. 

- Open Android Studio and click open existing Android Studio Project.
- Navigate to the downloaded Cube folder’s Build.Gradle file. Open it.
- The file should open and the relevant project files should appear in your project tree.
- You might need to modify the jvm args in the gradle.properties file by changing the memory allocated to Xmx1024m.
- Once modified, go to file -> Sync Project with Gradle files to sync project.
- The run configuration should now appear. Connect an android device with “USB Debugging” enabled and click the run app project to launch the application on the device.
- If a .APK file is required, go to Build -> Generate .APK file. The generated .APK file will appear in the Build folder of your project. 
