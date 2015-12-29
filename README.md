# Warmup
_This repository contains code for some simple warm-up exercises for an Android Development [course](https://canvas.uw.edu/courses/1023396) at the UW iSchool_

In this assignment, you will get set up with a development environment for the class, as well as practice and review some basic Java concepts to make sure you're ready. There are a lot of small steps to this assignment but with a lot of detail (making these instructions much longer than normal), so lets get started!

### Objectives
By completing this challenge you will practice and master the following skills:

* Using git and GitHub to track code changes
* Using Gradle to automate building
* Writing and debugging Java code
* Creating and running Android Apps


## 1. Git and GitHub
If you can see this page, this you've already completed the first step and joined the course GitHub organization. Next you'll need to [install](http://git-scm.com/downloads) and [configure](https://help.github.com/articles/set-up-git/) `git` so that you can use it from the command-line. While Android Studio does have a git plugin, it's a good idea to make sure everything works from the command-line as a fallback, and you'll be doing command-line work for this assignment (though the rest should be using the IDE).

(I've included a lot of details about using git in this section for your review).

Once you have git installed, you'll need to `fork` this repository to your own account on GitHub (by clicking the "Fork" button at the top right"), and then `clone` it down to your local machine using the url found in the clone url box:

![clone url](./img/clone.png)

You can clone with the command
    
    $ git clone paste-your-url-here

Which will create a new `warmup/` folder which you can `cd` into.

As you complete the assignment, you can modify the code, `git add` and `git commit` your changes (and `git push` them back out to GitHub). For more details, see the e.g., [this tutorial](https://www.atlassian.com/git/tutorials/).

#### .gitignore
For practice with this "coding cycle", you should add a single file called `.gitignore` to your git repository. This file includes a list of files or directories that you do _not_ want git to keep track of. For example, a lot of Android files are auto-generated, and so every time you built the application you'd need to merge in these changes!So we can include those files in the `.gitignore` file to automatically exclude them from our everyday git operations.

Using a text editor (e.g., [Sublime](http://www.sublimetext.com/)), create a new file called `.gitignore` inside the root of your repository (in the `warmup/` folder). Add any files you want to exclude to this file; a good starting list is available [here](https://github.com/github/gitignore/blob/master/Android.gitignore) or [here](https://gist.github.com/keyboardsurfer/3240022). It's a good idea to create a .gitignore file for every repository you create for this class!

#### Commit mesages
After you've created this file, use `git add .` to add it (and everything else that has been added or modified) to the _staging area_. Then you can commit the files using

    $ git commit -m "add .gitignore"

If you omit the `-m "message"` part of the command, git will put you into a command-line text editor so that you can compose a message. If you haven't done any other configuration, you might be dropped into the **_vi_** editor. Type `:q` (colon q) and hit enter to flee from this horrid place and try again, remembering the `-m` tag!

Make sure you include a short message on each commit. The message should say what changes that commit makes to the files, written in the **imperative mode** (_"Add gitignore"_, not _"adds gitignore"_). You don't need to list every line change, just a single-sentence overview of what this commit will do to the code. Your message should complete the sentence _"If applied, this commit will {your message}"_.

Finally, you can push your changes back to the GitHub servers using

    $ git push origin master


## 2. Gradle
The Android Studio IDE using **Gradle**, a build system popular in the Java world. Gradle effectively lets you automate complex build processes (e.g., when you need to do more than just compile). Since we'll be using Gradle to test your code in this course, this assignment will make sure you're at least familiar with using the system.

You'll need to install Gradle in order to use it from the command-line for this assignment. Visit the [download page](http://gradle.org/gradle-download/) for information on installing it on your machine of choice. Note that if you are on a Mac, the easiest way to install Gradle (or any other command-line software) is to install [Homebrew](http://brew.sh/), and then use `brew install gradle`.

Note you'll need to have the [Java JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html) (not just the JRE) installed on your computer for this assignment.

Once Gradle is installed, on the command-line you should `cd` into the `java/` folder in your cloned repo. You can then use

    $ gradle build

to build the provided code, or

    $ gradle test

to build the provided code _and_ run the provided tests. Note that the provided code will **not** build in its initial state!

If you _really_ need a GUI, you can get one with `gradle --gui`.


## 3. Java Debugging
Now that you're all set up, the bulk of this assignment involves taking the provided Java code and making it work---that is, making it so that all the unit tests pass. Clearly you should do this by modifying the source code (in the `java/src/main` folder); you should **not** change the existing tests in `java/src/test`, though you will be required to add a few new tests as well.

Note that once Gradle is able to build the code, if you run the tests they will generate a nicely formatted set of results in HTML at `java/build/reports/tests`. It will show you a summary of the test run, with breakdowns either by Packages or by Classes.

In order to get the code to work, you will need to do the following (in no particular order):

* Create the required getters and setters for the `Person` class

* Ensure that `.setAge()` throws an `IllegalArgumentException` when passed a value less than zero

* Ensure that `.setName()` throws an `IllegalArgumentException` when passed a `null` String

* Ensure that `.equals()` returns `true` if two `Person` instances have the same name and age (salary doesn't factor into equality comparison). Make sure no exceptions are thrown from this method--anything "weird" should just return `false`. Although it's good practice, you don't need to also override `.hashCode()`.

* Make `Person` implement the [`Comparable` interface](https://docs.oracle.com/javase/8/docs/api/java/lang/Comparable.html), such that when two `Person` objects are compare two Persons, they arrange themselves by age **in reverse order** (i.e., age 25 comes before age 15---old people first!). In case of a tie, sort alphabetically by name.

* Implement an `SalaryComparator` class (which implements the [Comparator](https://docs.oracle.com/javase/8/docs/api/java/util/Comparator.html) interface) that compares two `Person` objects and arranges them by salary (i.e., salary 10,000 comes before salary 100,000). This Comparator **MUST BE** a [nested class](https://docs.oracle.com/javase/tutorial/java/javaOO/nested.html) inside of the `Person` class; `Person` fields must remain private.

* Create a class (`static`) method `Person.createFamily()` that returns an `ArrayList<Person>` consisting of four `Person` objects: Anakin, age 46, salary 75000; Padme, age 41, salary 1000000; Luke, age 19, salary 0; and Leia, age 19, salary $10000.

* Lastly, you will need to implement a final test, marked in comments in the `TestPerson` class, which will register a `PropertyChangeListener` instance. This `PropertyChangeListener` will need to call the three `assertEquals()` calls (as described in the comments) in order to test that the property did change. You shouldn't change anything above or below the comment lines beyond that. Note that this will likely require you to look over the [documentation](https://docs.oracle.com/javase/8/docs/api/java/beans/PropertyChangeListener.html) for the Listener class--reading JavaDoc is something you need to be able to do!

Once all your tests pass, you've finished this step! Make sure you `add` and `commit` your code using git (_"Finish java debugging"_ is a good commit message).


## 4. Android Setup
The next step for this assignment is to make sure that you have an Android development environment prepared, so that there aren't any problems in the future. While we've been over making a new Android App in class, you'll need to repeat the steps for this homework.

Start by installing [Android Studio](http://developer.android.com/sdk/index.html), along with the SDK tools. You'll want to make sure that all the command-line tools (i.e., `adb`) are installed as well, though they should come with the Android Studio package.

Create a new Android Project in Android Studio (or using the command-line tools `android create` if you want... but the IDE is easier). Your project should have the following properties:

* Name the application **Hello**
* Your company name should look like `uwnetid.uw.edu` (using your uwnetid), so that the package name ends up being `edu.uw.uwnetid.hello`.
* Make sure to save the project inside your `warmup` code repo!

Make sure to target **API 19: Android 4.4 (KitKat)**. While we'll talk about features of Lollipop and Marshmallow, we're going to target a slightly older release to make sure we run on more devices (and to give us at least a taste of supporting older hardware). _If your Android device is older than this, please target that instead and let us know so we can adjust our lower bound!_
 
You'll want to start with an **Empty Activity**. Then modify the code (e.g., the `TextView` instead of `activity_main`) so that instead of "Hello World!", the screen shows a warm, personal message for your instructor and TA. Something polite; let's get started on the right foot. You're welcome to make any other changes you wish, but you only need to worry about changing the message.

You should also add a **custom icon** for your application. I've provided an okay `hello_icon.png` inside the `img/` folder of the code repo that you can use, or you can design your own. You can specify the icon easily in Android Studio by selecting the `app` folder in the project browser, and then selecting `File > New > Image Asset` and specifying a new **Launcher Icon**. You can instead specify it manually by coping the image into the `src/main/res/drawable` folder of Android project, and modifying the `manifest.xml` file so that the `android:icon` attribute of the `<application>` tag points at `drawable/hello_icon`. For better results, copy each of the resolution-specific images into their appropriate folders; Android will then pick the appropriate resolution based on the device!

#### Run in Emulator
You should test your application inside an **emulated android device**, to make sure that is set up. 

You will need to create a new _Virtual Device_ if you haven't before. Open up the **AVD Manager** (`Tools > Android > AVD Manager` in Android Studio, or `android avd` from the command line). Then create a new virtual device to use. We'll be testing against a virtual **Nexus 5**, so I recommend you use that as well.

You can then run your application in the emulator by clicking on the green "Run" button at the top of Android Studio.

Once you have the app working in the emulator, take a **Screenshot** of it running. You can do this easily by hitting the camera icon in the _Android Monitor_ pane of Android Studio (where you see the logs).

![screen capture icon](./img/screenshot_icon.png)

Save this image in the `screenshots/` folder provided.

#### Run on a Device
You should also test to make sure that you can test your app on an actual device. This will make your life **_MUCH_** more pleasant. See [this link](http://developer.android.com/tools/device.html) for instructions. If you don't have access to a device, check with us about other options.

You'll need to make sure that your device has developer options enabled (you need to go to `About phone > Build Number` and tap that 7 times to get the options. Seriously). When the device is plugged into your computer, you'll need to confirm it is detected by `adb` (using `adb devices` should give you its serial number).

You should then be able to deploy to the device by using the green "Run" button in Android Studio.

Once you've deployed to a device, take a picture or screenshot of it running. Put this file inside the `screenshots/` folder as well.

#### Running via Gradle
You'll also want to double-check that you can build and install your app via Gradle; this is how we'll test and run your apps, so it's a good idea to make sure it works!

Make sure that you have a device connected and running---either physical or virtual (i.e., that the emulator is running). From inside the project folder, you can then build and install your app with

    $ gradle installDebug

(You can also use `./gradlew installDebug` to use the "local", Studio-specific version of gradle).

**Pro Tip:** you can also launch the app from the command-line with the command

    $ adb shell am start -n package.name/.ActivityName

You can run both these commands in sequence by connecting them with an `&&` (which short-circuits, so it will only launch if the build was successful). This will let you install and launch without needing to interact with the emulator GUI!

Remember to `add` and `commit` your working code and screenshots!


## 5. `SUBMISSION.md`
The last step of all assignments is to fill in the `SUBMISSION.md` file included in the starter code repository with details about your program (the README is being use for instructions). You can open this file in your favorite text editor (e.g., [Sublime](http://www.sublimetext.com/)) or Android Studio. This text is in [Markdown](https://en.wikipedia.org/wiki/Markdown) format, which is a very simplified markup language, often used for explanatory files. You should fill in answers to the questions in the spots provided to help us in evaluating your assignment (and improving them in the future!).

Remember to `add` and `commit` any changes to your `SUBMISSION.md` file!


## Submit Your Solution
In order to submit programming assignments in this class, you will need to both `push` your completed program to your GitHub repository (the one in the cloud that you created by forking), and submit a link to your repository to [Canvas](https://canvas.uw.edu/) (so that we know where to find your work)!

Before you submit your assignment, double-check the following:

* Test that your app builds, installs, and works without errors.
* Fill out the `SUBMISSION.md`included in the assignment directory, answering the questions.
* Commit the final version of your work, and push your code to your GitHub repository.

Submit a a link to your GitHub repository via [this canvas page](https://canvas.uw.edu/courses/1023396/assignments/3082079).

The assignment is due on **Tue Jan 12 at 6:00 AM**.

### Grading Rubric
See the assignment page on Canvas for the grading rubric.

_Adapted from assignment by Ted Neward_