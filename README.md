JKeyMaster
==========

This is a java library that provides single interface to register Global Hotkeys for several platforms:

  * Windows
  * X11-based systems (in theory, only tested on some Linux distros and PCBSD)
  * Mac OSX

Building
--------

You will need Java 1.6+ and Maven to build

    mvn clean package

Maven
-----

    <dependencies>
        <dependency>
            <groupId>com.github.tulskiy</groupId>
            <artifactId>jkeymaster</artifactId>
            <version>1.1</version>
        </dependency>
    </dependencies>

Testing
-------

To test that the library works on your system, run the jar file

    java -cp jkeymaster-1.1.jar com.tulskiy.keymaster.AWTTest

It will open a simple window. At the top is a field that listens to key presses.
Choose your hotkey and press `Grab` button. Check log output to see if it failed.
If the log is clean, you should be able to press the hotkey anywhere and it should
popup a message.

There's also a `Grab media keys` button that should register media keys
(only Windows and Linux) - Play/Pause, Next Track, Previous Track, Stop.

API Example
-----------

Main class is `Provider`. To get provider for current platform:

    Provider provider = Provider.getCurrentProvider(useSwingEventQueue);
    provider.init();
    
where `useSwingEventQueue` is a boolean parameter specifying whether to fire events
on swing event queue or just simple thread. Usefull when your hotkey listener will 
directly access or modify some swing components.

Provider supports two methods for registering hotkeys - one accepts AWT KeyStroke:

    provider.register(KeyStroke.getKeyStroke("control shift PLUS"), listener);

the other accepts a `MediaKey` parameter:

    provider.register(MediaKey.MEDIA_NEXT_TRACK, listener);

For simplicity reasons, it is currently impossible to unregister some particular
hotkey, but all hotkeys can be reset.

When you're done using the library, you need to reset hotkeys and stop the provider:

    provider.reset();
    provider.stop();
    
Clojure
-------

If you are interested in nicer api from clojure, check out a wrapper library 
developed by Stian Håklev: https://github.com/houshuang/keymaster-clj

Feedback
--------

I would really appreciate it if you could test the library and post some bug reports.
I've tested on Win 7, Win XP, Ubuntu 10.10 and 11.04. I've tested Mac OSX code
only in 32-bit virtual machine, and a bit on a real 64-bit machine. So feedback is greatly
appreciated, especially from people who have real Mac with real Apple keyboard,
BSD users, people with some advanced keyboards, and just anybody who wants to help.
