### Device Support ###
This app targets Android 5.0+.

### Building and Running ###
This project is built with Android Studio. To run it:
1. Clone the repository.
2. Open Android Studio and select File > New > Import Project...
3. Navigate to the folder you cloned the project into, select it, then click OK.
4. In the top right the "app" build configuration should already be selected, if it is not, select it, then click the play button.
5. The app should be built and run on a supported connected device.

### Running Tests ###
The Android Instrumentation tests can be run by:
1. In the project browser, expand app > src > androidTest > java > com.bubbletastic.android.ping.tests.
2. Right click a test class and select "Run".


### Proto Models ###
Protocol buffers are used to format hosts for persistence. This affords an easy to save string friendly format, while simultaneously granting the power of Protocol Buffer's schema and schema migration support so that changes to the storage format can be handled gracefully.

* Within the resources folder there is a proto-compiler.sh shell script that uses the bundled wire-compiler to compile the Protocol Buffer definitions into java code.
* The proto-compiler.sh script should be run to update the Java classes any time one of the Protocol Buffer schemas change.
* The script can be executed like this:
    ```
    cd project_root/resources
    ./proto-compiler.sh
    ```
* Alternatively you can right click the proto-compiler.sh file from the resources folder in Android Studio and click "Run 'proto-compiler.sh'"
    * If you cannot see the resources folder, change the project perspective the top left to "Project" (it defaults to Android).

### Known Issues ###

* Proper tablet support is a work in progress.