# README #

These are three Android Studio and one Unity project.

The FieldtripServerService creates an Android  service (with no activity) that starts, stops and controls a Fieltrip buffer server.

The FieldtripClientsService creates an Android service (with no activity) that starts, stops and  controls a number of Fieltrip buffer clients. More clients can be added as classes, extensions of ThreadBase.

Both the services are designed to communicate with another app through Intents.

The FieldtripBufferServicesController is an Android project designed to be a plugin to Unity. It allows communication with the FieldtripServerService and the FieldtripClientsService and exposes a number of functions that can be used to do this communication.

The UnityFieldtripServicesController is a Unity Android project that can be used as a base to create other Untiy Android projects which can control the Fieldtrip Buffer Services. It also incorporates a Fieldtrip Buffer client (written in C#) which allows a Unity project to instantiate its own client and read and write from and to the Fieldtrip Buffer Server running on the device.      


Versions:
Android Studio 1.0.1
Unity 4.6.1
Android API targeted 16, minimum 9


### How do I get set up? ###
Unity Android plugins work only on actual devices so a developing device must be used.

The Fieldtrip Server and Clients Services are self contained Android studio projects. They should be installed to the developing Android device.

The FieldtripServicesController Android studio is meant to create a .jar file that is then added in a Unity project and should not be installed itself on a device. After any changes to its code go with a terminal to the directory that its gradlew file is (Wherever_you_put_the_code/FieldtripBufferServiceController/gradlew) and run gradlew makeJar (in Windows you probably need to run gradlew.bat makeJar). The makeJar function can be found in the project's build.gradle file (in the app module). It generates a jar file of all the .class files of the project and puts it in FieldtripBufferServiceController/app/build/libs. This jar needs to be copied to the Unity project's Assets/Plugins/Android directory, together with the AndroidManifest.xml file and the res directory of the Android Studio project. After any code changes a new jar must be made and copied over, after any manifest changes the AndroidManifest.xml file must be copied over and after any changes in the res files the new res folder should be copied over.

In a Unity project with the required Plugins/Android files setup one needs the FieldtripServicesControlerInterface.cs script which exposes the interface that the plugin provides to the Server and the Clients Services.
The UnityFieldtripBufferServicesController project gives an example of this setup and also provides a simple GUI that can be used to control the Server and the Clients.


* Dependencies
The Services depend on the basic Java  fieldtripbufferserver and fieldtripbufferclient implementations of the fieldtrip buffer.
The FieldtripBufferServicesController has no dependencies. This must be kept like this because it is very difficult to add any extra jar dependencies on an Android Unity plugin. That is the main reason for the current design architecture (native services controlled by a plugin through Intents).

# Prerequisite
- Android Studio or Intellij
- Android Development Kit
- Packages from Android SDK Manager:
	- Android SDK Tools (rev 24.1.2)
	- Android SDK Platform-tools (rev 22)
	- Android SDK Build-tools (rev 21.1.2)
	- SDK Platform (api 16)
	- Android Support Repository (rev 12)
	- Android Support Library (rev 22)

# Dependencies

# Install instructions

## UnityFieldtripBufferServiceController

## FieldTripServerService

## FieldTripClientsService

## FieldtripBufferServiceController