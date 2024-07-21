# Compiling BCU
###### Last Updated: July 19, 2024
###### NOTE: If you just want to install BCU, stop reading and go to the README!!
###### ANOTHER NOTE: This is still incomplete, and will be worked on in the near future.

## References
- Terminal: The Terminal or Console.
- <kbd>Keybind</kbd>: A key combination. For example, <kbd>Ctrl Shift T</kbd> opens a new tab (on most browsers)!
  - Note that the <kbd>Ctrl</kbd> key is replaced with <kbd>Cmd</kbd> on Mac!

## What you will need:
- [IntelliJ IDEA Community Edition](https://www.jetbrains.com/idea/download/) (as of writing this, you'll want to scroll down a bit for the community edition, unless you already have or want to pay for the "Ultimate" version)
  - While it may be possible to compile BCU using a different IDE, this is what we use, and what we will be using for this guide.
- [JDK for Java 8](https://www.openlogic.com/openjdk-downloads?field_java_parent_version_target_id=416&field_operating_system_target_id=All&field_architecture_target_id=All&field_java_package_target_id=All), get the one that meets your platform specifications

## Strongly Recommended:
- Basic knowledge of the Terminal/Console, Git, and coding (Please be aware that us devs rarely have the time to help go over all the general concepts of these sorts of things! Please refer to resources online if you need help for a certain task before coming to us for assistance.)
  - For simplicity purposes, we will stick with referring to Terminal/Console as "Terminal"
- While we don't use it to code, we use [Visual Studio Code](https://code.visualstudio.com/) as a convenient way to manage Git functions. If you're more familiar with the Terminal, [GitHub Desktop](https://desktop.github.com/), or anything else, please go ahead and use those!

## Set Up Running BCU
### Get the Folder
1. With the Terminal set to your desktop folder (or anywhere comfortable that's not the root folder), run `git clone --recurse-submodules https://github.com/battlecatsultimate/BCU-java-PC.git`
   1. If you have [SSH set up](https://docs.github.com/en/authentication/connecting-to-github-with-ssh), you can run `git clone --recurse-submodules git@github.com:battlecatsultimate/BCU-java-PC.git`. If you don't know what this means (or don't care to), just use the line above.
   2. The `--recurse-submodules` makes sure you get everything in the `common` submodule. In case you didn't do this (meaning the `src/main/java/common` directory is empty), go into the folder and run `git submodule update --init --recursive`
2. From IntelliJ IDEA, open up the new `BCU-java-PC` folder.
### Set Up Settings
1. Open `Project Settings`, either with <kbd>Ctrl ;</kbd>, or through the little gear icon on the top right.
2. In `Project`, set the SDK to `1.8` and the Language Level to `8`
3. Go to `Modules`, and just delete all current Source Folders, Resource Folders, and Excluded Folders.
4. Through the folder tree, set `src/main/java` as the Source Folder, `src/main/resources` as the Resource Folder, and `target` as an Excluded Folder.
5. Close the Project Settings window, and open `Settings`, either with <kbd>Ctrl ,</kbd> or through the little gear icon on the top right.
6. Find the "Java Compiler" tab with the search function, then change `Javac` to `Eclipse` if it's not already `Eclipse`
7. Close the Settings window.
### Run BCU!
1. In the folder tree, find `MainBCU` in `src/main/java/main/`
2. There should be a green arrow on the left of the line `public class MainBCU`. Click on it!
### Troubleshooting!
- Error: `java: cannot find symbol`
  - Make sure the Java Compiler is set to Eclipse!

## Set Up Compiling BCU into a Jar
###### To be written