# Motion Game
Mobile devices such as phone and tablets include a number of sensors and interfaces (e.g., touch gestures, motion controls, etc.) that enable novel forms of user experiences. For this assignment, you will build an app that utilizes these capabilities in the form a mobile, motion-based graphical game.

Note that the word "game" here may be a bit misleading&mdash;your app does _not_ need to have specific "win" or "lose" conditions, keep track of points, or have an identifiable story. Rather, I'm using "game" to refer generically to [_play_](https://en.wikipedia.org/wiki/Man,_Play_and_Games)-enabling artifacts or structures: in order words, an app that provides a "non-serious" user experience. This can be a traditional game (with points and other trappings), but it can also be closer to interactive [generative art](https://en.wikipedia.org/wiki/Generative_art) or an abstract visualization&mdash;just so long as it utilizes motion and gestures to control a graphical display in a purposeful way.

- For example, a "game" might be a [Metaball](https://en.wikipedia.org/wiki/Metaballs) visualization, in which the user can use touch gestures to create or split the blobby objects, and tilt and shake the phone to move them around. (I almost made _that_ the assignment, but decided to go more open-ended).

- Other options might be an interactive painting app, a [boid](https://en.wikipedia.org/wiki/Boids) simulation, or some other kind of [abstract game](http://store.steampowered.com/tag/en/Abstract/). The [Demo Scene](https://en.wikipedia.org/wiki/Demoscene) is a good source of inspiration, as are HTML5 demos (like [these](http://hakim.se/experiments)).

  - You can almost think of this as an "all-ages" game: if you make something that enthralls a two-year old, you _will_ get extra credit!

- It's also perfectly fine to make a more traditional game: motion-controlled [Snake](https://en.wikipedia.org/wiki/Snake_(video_game)) would be perfectly doable, as would a [Ball Maze](https://en.wikipedia.org/wiki/Ball-in-a-maze_puzzle) puzzle.

You can create whatever kind of game/experience you wish, so long as it meets the basic requirements. This is a chance for you to be creative, to draw on your artistic side and integrate that with the more logical process of coding! The overall requirements for your experience are:
1. It draws and shows dynamic, graphical output
2. It can be controlled via _motion_ (e.g., by moving or shaking the device). This motion should be a _primary_ form of interaction, not just an add-on!
3. It can be also controlled via touch gestures. These controls should _supplement_ the motion controls.
4. It plays sound along with drawing pictures

Since this assignment will require physical hardware to test and the best experiences come from collaboration, you can complete this assignment either individually or in a **pair** (not more than 2 people, even though you're starting into your projects). Note that if you work in a pair, we'll have slightly higher expectations for the "creativity" portion of the assignment; it should be more impressive because there are two of you!

  - See the [YAMA assignment](https://github.com/info498d-w16/yama) for notes about working with a partner!


### Objectives
By completing this assignment you will practice and master the following skills:
* Generating dynamic and animated graphical output
* Identifying and responding to touch gestures
* Harnessing hardware sensors (e.g., for motion)
* Loading and playing media files


## User Stories
The user stories for the Motion Game are a bit more vague, since the exact content is entirely up to you. However, it should fulfill the below requirements:
* As a player, I want to see a visual display that responds to my purposeful input.
* As a player, I want the visual display to be accompanied by audio.
* As a player, I want the displayed output to react to my touch gestures _and_ device motion.


## Implementation Details
This assignment is _much_ more open-ended than the previous ones, though I'll include some general guidance below.

### Fork and Create
As with all assignments, you should start by **forking** and **cloning** this repository. It includes any starter code plus the `SUBMISSION.md` file you will need to complete.

You will need to create a new project through Android Studio. Make sure to name your project **motiongame**, so that the package is `edu.uw.uwnetid.motiongame` (even if you have a more specific name; the package names help us with grading). _You will need to save the project inside your cloned repo!_ (i.e., in the top-level directory).

For this assignment, you should once again target **API 15 (4.0.3 Ice Cream Sandwich)** as your minimum SDK.

Your application will probably just need a single _blank_ Activity (though you can include more or use multiple fragments if your game demands). In order to programmatically generate graphics, your Activity will probably want to include a [Custom View](http://developer.android.com/guide/topics/ui/custom-components.html). I have included a small sample `View` you can use as a starting point.

Once you've created the app, go into the `build.gradle` file (Module level) and set the target SDK to be **21 or lower** for testing.


### Drawing Graphics
Your game will need to include dynamically generated graphical content&mdash;that is, you'll need to utilize an Android [Canvas](http://developer.android.com/guide/topics/graphics/2d-graphics.html) to draw the graphics that should be displayed. The [`Canvas`](http://developer.android.com/reference/android/graphics/Canvas.html) class provides methods to draw lines, shapes, and even pictures (`Bitmaps`). Be sure and check out the documentation for a list of methods you can use!

While it's possible to draw directly onto a blank `View`, you'll get better results by drawing onto a custom component and overriding the `onDraw()` method. Even better: if you use a [`SurfaceView`](http://developer.android.com/guide/topics/graphics/2d-graphics.html#on-surfaceview), you can make that drawing service available to a separate, background thread so you can do graphic work without tying up the main UI thread. Check out the provided sample code, the [documentation](http://developer.android.com/guide/topics/graphics/2d-graphics.html#on-surfaceview), and Google's own [LunarLander](https://android.googlesource.com/platform/development/+/master/samples/LunarLander) sample to see how it works.

  - In order to include your custom view in a layout resource, you need to declare a `<view>` element with a `class` attribute equal to the package and classname of your view. For example:
  ```xml
  <view class="edu.uw.package.DrawingSurfaceView"
      android:id="@+id/surfaceView"
      android:layout_width="match_parent"
      android:layout_height="match_parent" />
  ```

I'd also recommend using a separate `Bitmap` to use to draw individual pixels (this requires more computation and so is slower, but you can get much more detailed results!). You can use the `setPixel()` method to color a single pixel.

- Remember that the coordinate system for drawing has a "reversed y"&mdash; the "y" axis gets bigger as you go _down_ the device.

In order to produce animation, you need to cause your `render()` method to be called repeatedly _by the Android system_. This will allow the system to produce and show your drawing, then produce and show the next, then produce and show the next... and doing this as fast as it can will produce animation! When using a regular subclassed `View` you can cause the `onDraw()` method to be executed by calling `invalidate()` on your `View` (thus forcing Android to redraw it), but with a `SurfaceView` you can have your background thread repeatedly fetch and post the `Canvas` for rendering. Voila, animation!

- Again, check the documentation for further example, such as if you want to simply load and draw a `drawable` (e.g., a `.png` picture you've made elsewhere). It's also possible to use [Drawable Animation](http://developer.android.com/guide/topics/graphics/drawable-animation.html) in addition to (or even instead of!) Canvas drawing, though you'll need to read up on how that works.

  - If you utilize any third-party images, make sure that you a _license_ to use them, and cite your sources in the `SUBMISSION.md` file!

You might want to make your app [fullscreen](http://developer.android.com/training/system-ui/immersive.html), to produce a more immersive experience


### Motion Controls
A significant form of interaction in your game needs to be through [motion controls](http://developer.android.com/guide/topics/sensors/sensors_motion.html): tilting, shaking, rotating, or swinging the phone should allow the player to include the visual display. For example, maybe items in the game sink to the sides when the user tilts the phone, or the user can spin the phone around to produce other effects. This should not just be a bonus interface, but a core part of the experience!

- The [Accelerometer](http://developer.android.com/guide/topics/sensors/sensors_motion.html#sensors-motion-accel) and [Rotation Vector Sensor](http://developer.android.com/guide/topics/sensors/sensors_motion.html#sensors-motion-rotate) are good sensors to access for these kinds of interactions (and are commonly found on mobile devices), though you are welcome to use other sensors as well.

In order to indicate that your app uses a particular sensors, you should include an appropriate [`<uses-feature>`](http://developer.android.com/guide/topics/sensors/sensors_overview.html#sensors-configs) tag in your `Manifest`. You'll then need to fetch the device's [`SensorManager`](http://developer.android.com/reference/android/hardware/SensorManager.html) object, and then `register` the particular sensor you want to listen to. Then you can implement the `SensorEventListener` interface in order to respond to sensor readings. You can get the readings from the `SensorEvent` passed into the `onSensorChanged()` method.

- Be sure and `unregister` the sensor when your app pauses so you don't waste battery!

Sensor data (particularly from the accelerometer) tends to be in a "raw" format, requiring processing to turn it into a meaningful value that you can actually use. You may need to play around with (and log out!) these values to get a feel for what magnitudes you want to respond to, or check out the demos provided by Google for examples (e.g., [here](https://github.com/android/platform_development/blob/master/samples/AccelerometerPlay/src/com/example/android/accelerometerplay/AccelerometerPlayActivity.java) or [here](https://android.googlesource.com/platform/development/+/master/samples/ApiDemos/src/com/example/android/apis/os/RotationVectorDemo.java)).


### Touch Gestures
Your game will need to respond to [touch gestures](http://developer.android.com/training/gestures/index.html) in some way. While this can be basic tapping, it could also be more complex: you might use drags, flings or [multi-touch](http://developer.android.com/training/gestures/multi.html) to create an engaging interactive experience. Having the game depend on [tracked velocity](http://developer.android.com/training/gestures/movement.html#velocity) would be a nice _touch_ (haha).

The basic step in supporting touch gestures is to have your `Activity` provide the `onTouchEvent()` callback, and then [determine](http://developer.android.com/training/gestures/detector.html) _which_ gesture was used and respond appropriately. You can also figure out _where_ the event occurred by using the `.getX()` and `.getY()` methods (passing a `pointerIndex` if using [multitouch](http://developer.android.com/training/gestures/multi.html)).

- Note that you can also implement this callback in your custom `View`; however, keeping the View separate from the Model and Controller (e.g., the Activity) is generally good practice! You can use direct method calls to communicate between these classes: use `findViewById` to access the View, and then call its specialized methods to adjust what is being drawn.


### Including Sound
Finally, to make your experience more engaging (and engrossing for a two-year-old), it should also play audio to accompany the graphical output. Indeed, having an appropriate soundtrack is often what separates simple demos from memorable experiences. Your game's audio should be more than just "background music": the sounds should change in reaction to the game (e.g., it "beeps" depending on what input is given).

- Note that you should be careful to make sure that you have a _license_ to use whatever audio assets you include in your game, and cite your sources in the `SUBMISSION.md` file. There are lots of sound-effects available online; a quick Google Search should help you find some!

- Audio assets are normally kept in the `raw` resource folder. This is a folder for resources that are not processed (compiled) by Android, but are still assigned `ids` (based on the filename) so you can access them easily from the code.

[Playing sound](http://developer.android.com/guide/topics/media/mediaplayer.html) relies on two main classes: [`MediaPlayer`](http://developer.android.com/reference/android/media/MediaPlayer.html) and [`AudioManager`](http://developer.android.com/reference/android/media/AudioManager.html). The `MediaPlayer` is the primary point of access for playing media: you can simply load in the sound file you want to play and then call `.start()` to begin playing it!

- Be sure and `release()` and _nullify_ your `MediaPlayer` when you are done playing audio, so that you don't hold on to system resources (memory, etc) that you aren't using!

While the `MediaPlayer` works fine for basic audio, in order to be more responsive, interactive, and to potentially play _multiple sounds at once_, you'll want to instead (or additionally) use the [`SoundPool`](http://developer.android.com/reference/android/media/SoundPool.html) class. This class can be used to `.load()` and set up a "list" of sounds to play, and then `.play()` or `.pause()` them on demand&mdash;including while other sounds are playing!

- You can create a `SoundPool` object using the [`SoundPool.Builder`](http://developer.android.com/reference/android/media/SoundPool.Builder.html) class, similar to what we've used to create notifications, a `GoogleApiClient`, etc. You'll probably want to specify that your `SoundPool` uses the `USAGE_GAME` `AudioAttribute`, as well as indicate the number of maximum streams you want to be playable at once (less than 7 is good; some people have reported memory issues with too many or too large sounds).

- You can then <a href="http://developer.android.com/reference/android/media/SoundPool.html#load(android.content.Context, int, int)">`load()`</a> different sound resources into your pool, referring to each by their resource id (use the method signature that takes a `Context`). You'll also need to specify an `OnLoadCompleteListener` that can respond to the "event" of when a sound is done being loaded into memory. You'll only want to start your game once everything is loaded.

- Finally, as your game is playing, you can <a href="http://developer.android.com/reference/android/media/SoundPool.html#play(int, float, float, int, int, float)">`.play()`</a> a sound from your `SoundPool` without needing to reload it! Note that you can also specify lots of other properties: like the volume level, playback speed, etc. (I recommend using defaults as much as possible until you've got things working!)

- You can also `pause()` and `resume()` clips if needed.

- Remember to `release()` all the sound clips when you're done!

This should allow you to get basic sound effects. Note that the `SoundPool` _should_ be able to be used alongside the `MediaPlayer`, but this is less tested than it should be. As always, we'll go over these pieces in class, and I will update the assignment with better instructions/details if I can!



## Submit Your Solution
In order to submit your assignment, you will need to both `push` your completed program to your GitHub repository (the one in the cloud that you created by forking), and submit a link to your repository to [Canvas](https://canvas.uw.edu/) (so that we know where to find your work)!

Before you submit your assignment, double-check the following:

* Test that your app builds (from `gradle`!), installs, and works without errors. It should fulfill all the user stories.
* Fill out the `SUBMISSION.md`included in the assignment directory, answering the questions.
* Commit the final version of your work, and push your code to your GitHub repository.

Submit a a link to your GitHub repository via [this canvas page](https://canvas.uw.edu/courses/1023396/assignments/3082154).

The assignment is due on **Wed Feb 17 at 6:00 AM**.

### Grading Rubric
See the assignment page on Canvas for the grading rubric.
