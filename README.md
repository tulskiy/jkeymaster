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

    Provider provider = Provider.createProvider();

Provider supports two methods for registering hotkeys - one accepts AWT KeyStroke:

    provider.register(KeyStroke.getKeyStroke("control shift PLUS"), listener);

the other accepts a `MediaKey` parameter:

    provider.register(MediaKey.MEDIA_NEXT_TRACK, listener);

For simplicity reasons, it is currently impossible to unregister some particular
hotkey, but all hotkeys can be reset.

When you're done using the library, you need to reset hotkeys and stop the provider:

    provider.stop();

If you need just to reset all key mapping use:

	provider.reset();

Restrictions:

On Mac OS current implementation replaces existed hotkey mapping with own. This means that previous mapping doesn't
work while binding alive and all applications can't receive any messages about pressed keys.
(source: http://stackoverflow.com/questions/6186317/why-copy-and-paste-in-finder-doesnt-work-when-i-use-registereventhotkey-cocoa)
Also since you can bind keys without modifiers (like 'y' or '0') that can break your system totally and
makes keyboard unreachable for whole system.

Feedback
--------

I would really appreciate it if you could test the library and post some bug reports.
I've tested on Win 7, Win XP, Ubuntu 10.10 and 11.04. I've tested Mac OSX code
only in 32-bit virtual machine, and a bit on a real 64-bit machine. So feedback is greatly
appreciated, especially from people who have real Mac with real Apple keyboard,
BSD users, people with some advanced keyboards, and just anybody who wants to help.