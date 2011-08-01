JKeyMaster
==========

This is a java library that provides single interface to register Global Hotkeys for several platforms:

  * Windows
  * X11-based systems (in theory, only tested on some Linux distros and PCBSD)
  * Mac OSX

Building
--------

You will need Java 1.5+ and Maven to build

    mvn package

Maven
-----

To use this library as a maven dependency, add the following repository
to your project:

    <repositories>
        <repository>
            <id>tulskiy/jkeymaster</id>
            <url>http://tulskiy.github.com/jkeymaster/maven/</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>com.tulskiy</groupId>
            <artifactId>jkeymaster</artifactId>
            <version>1.0</version>
        </dependency>
    </dependencies>

Testing
-------

To test that the library works on your system, run the jar file

    java -jar jkeymaster-1.0-SNAPSHOT.jar

It will open a simple window. At the top is a field that listens to key presses.
Choose your hotkey and press `Grab` button. Check log output to see if it failed.
If the log is clean, you should be able to press the hotkey anywhere and it should
popup a message.

There's also a `Grab media keys` button that should register media keys
(only Windows and Linux) - Play/Pause, Next Track, Previous Track, Stop.

API Example
-----------

Main class is `Provider`. To get provider for current platform:

    Provider provider = Provider.getCurrentProvider();
    provider.init();

Provider supports two methods for registering hotkeys - one accepts AWT KeyStroke:

    provider.register(KeyStroke.getKeyStroke("control shift PLUS"), listener);

the other accepts a `MediaKey` parameter:

    provider.register(MediaKey.MEDIA_NEXT_TRACK, listener);

For simplicity reasons, it is currently impossible to unregister some particular
hotkey, but all hotkeys can be reset.

When you're done using the library, you need to reset hotkeys and stop the provider:

    provider.reset();
    provider.stop();

Feedback
--------

I would really appreciate it if you could test the library and post some bug reports.
I've tested on Win 7, Win XP, Ubuntu 10.10 and 11.04. I've tested Mac OSX code
only in 32-bit virtual machine, and a bit on a real 64-bit machine. So feedback is greatly
appreciated, especially from people who have real Mac with real Apple keyboard,
BSD users, people with some advanced keyboards, and just anybody who wants to help.