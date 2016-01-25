# Yet Another Messaging App ("Yama")
Android apps do not exist in a vacuum: they sit alongside a variety of other apps and functionality provided by the phone, as well as interaction elements that are (ideally) standardized by the platform itself. For this assignment, you will build an app that harnesses this ecosystem and interfaces with other app components. Specifically, as the majority of Android devices are phones and have the telephony capabilities as such, you'll build a messaging app&mdash;that is, an application for sending and receiving SMS/MMS messages. To try and separate your app from all the others out there, yours will include the secret sauce of **automated replies**, allowing the user to specify a message that is automatically sent back on receipt of a message (cause no other app can do that right? _RIGHT?_). And to make your app more "modern" and usable, you'll include some addition UI widgets provided by the Android platform.

Because messaging is a form of communication and can be easier to test with two phones, you can complete this assignment **either individually or in pair**. Note that this assignment will seem a bit larger than the last (lots of smaller pieces, which means lots more to learn and debug), so I highly recommend you work with a partner!

### Objectives
By completing this challenge you will practice and master the following skills:
* Using intents to communicate between activities and applications
* Responding to implicit intents and broadcasts
* Harnessing Android telephony classes to send, receive, and display SMS messages
* Displaying notifications
* Allowing users to specify settings and preferences
* _Learning_ and using Android UI widgets and patterns

## User Stories
The user stories for this Yet Another Messaging App are:
* As a user, I want to be able to send a text message
* As a user, I want to be able to send a message to one of my contacts
* As a user, I want to be notified when I receive a text message
* As a user, I want to view a list of messages I have received
* As a user, I want to be able to _automatically_ send a message in reply to one received.
* As a user, I want to customize the automatic reply in the application settings
* As a user, I want to interact with my application using Android-specific UI widgets

## Working with a Partner
For this assignment you are welcome to work either individually or in pairs. If you work in pairs, I highly recommend [_pair programming_](http://guide.agilealliance.org/guide/pairing.html), with two people writing code on one computer. You can then collaborate on figuring out solutions to problems, and use both people's phones to test messaging. (And that way both people learn the material better!) If you do decide to split the work, you should split the work by _feature_ (or by user story). **Do not** try to have one person do the views/user interface and one person do the controllers/Java. The integration process will not be pleasant.

You will need to work off of the same GitHub repository. You should be familiar with [resolving merge conflicts](https://help.github.com/articles/resolving-a-merge-conflict-from-the-command-line/), because they will occur.  

## Implementation Details
There are lots of pieces to this assignment. Get started early!

### Fork and Create
As with all assignments, you should start by **forking** and **cloning** this repository. It includes any starter code plus the `SUBMISSION.md` file you will need to complete.

You will need to create a new project through Android Studio. Make sure to name your project **yama**, so that the package is `edu.uw.uwnetid.yama`. _You will need to save the project inside your cloned repo!_ (i.e., in the top-level directory).

For this assignment, you should target **API 19 (4.4 KitKat)** as your minimum SDK. Android's messaging API [changed dramatically](http://android-developers.blogspot.com/2013/10/getting-your-sms-apps-ready-for-kitkat.html) in KitKat, including the addition of methods that make messaging much easier to deal with. While you would want to support older devices (Jelly Bean is still on [more than 30% of devices](http://developer.android.com/about/dashboards/)) for a full messaging application, for our educational purposes just supporting KitKat is more appropriate.

Your application will need to contain _multiple_ Activities; you can include multiple fragments as well. You can start with a single empty Activity, or if you want Android to provide you a fragment by default you can start with a _blank_ Activity (be sure and look through the generated code to trace how the resources are being used). Once you've created the app, go into the `build.gradle` file (Module level) and set the target SDK to be **22 or lower** (this is important so that we don't have to deal with asking for permissions at runtime yet! We'll talk about doing this more later, but for now we'll stick with pre-Marshmellow interactions).


### Overall User Interface
Your application should have at least two different activities: one for _reading messages_ and one for _composing messages_ (either of these can include fragments). You will also need to have a [Settings Activity](http://developer.android.com/guide/topics/ui/settings.html) (see below for details).

The _reading messages_ Activity should allow the user to see a list of messages received (all messages---not just the ones sent from this app). For each message, the user should be able to see the _author_ of the message, the _body_ of the message, and the _date_ (and time) when the message was sent.
- SMS messages are short enough that it is possible to include this information for each item in a `ListView`. Alternatively, you could make a "detail" fragment or use a pop-up dialog to show additional information. If you do use a dialog or fragment, make sure that the "back" button still works!

The _composing messages_ Activity should allow the user to specify the recipient (phone number) and write out a message to send, as well as actually send it! After sending a message, the inputs should be "cleared" so that the user can write a new message. You are _not_ required to include a list of sent messages (though you can if you want--it's basically the same as the list of received messages!)
- Note that the user will also need to be able to "search" for a recipient by opening the contacts list. A [simple button](http://developer.android.com/reference/android/widget/ImageButton.html) with a [search icon](http://androiddrawables.com/Menu.html) next to the phone number field is a nice way to do this.


### Working with SMS
SMS (Short Messaging Service) is one of the most widely used forms of data communication. In Android (since API 19), SMS capabilities are provided through an [API](http://developer.android.com/reference/android/provider/Telephony.html). This API specifies a number of _Intents_ (think: event messages) that occur as part of sending and receiving messages: see [Sms Intents](http://developer.android.com/reference/android/provider/Telephony.Sms.Intents.html) and the API linked for details.

Android systems have a _default SMS app_ defined by the user: this app has control over the `SMS Provider`, which basically keeps track of messages that are coming in and out of the system. Only the default app can respond to "delivered" intent when a message arrive and manage the list of messages sent and received (e.g., delete them). Note that the goal of this assignment is not to create a full app capable of effectively serving as the default app; however, it will be able to mirror a number of features. Other apps are still able to respond to the "received" intent (primarily to handle things like data transmission over SMS), and can use the default app to send messages (essentially, you have the default app send it on your behalf). You can access the default app as an [`SmsManager`](http://developer.android.com/reference/android/telephony/SmsManager.html) object.

The default app `SmsManager` has methods capable of sending SMS messages, making the process fairly straightforward. You should specify a [`PendingIntent`](http://developer.android.com/reference/android/app/PendingIntent.html) that notified _your_ app when the message has been sent---especially so that you can get let the user know (e.g., via a UI element) if there was an error sending the message. You can check the status of the message by calling `getResultCode()` on the `BroadcastReceiver` that receives the `PendingIntent`. See [Using a PendingIntent](http://developer.android.com/guide/components/intents-filters.html#PendingIntent).

- Note that to send an SMS your application will need the `SEND_SMS` permission. This is a _dangerous_ permission, so in Marshmellow you would need to confirm permission at runtime.


Receiving an SMS is a bit more complicated. You will need to create and specify a [`BroadcastReceiver`](http://developer.android.com/reference/android/content/BroadcastReceiver.html), which will let you respond to "broadcasted" Intents (like the one that occurs when a message is _received_). You should subclass `BroadcastReceiver`, and declare your `<receiver>` in the manifest (along with the proper [Intent Filter](http://developer.android.com/guide/components/intents-filters.html#Receiving) to receive the implicit intent). In this receiver, because you're using API 19 or later, you can use the <a href="http://developer.android.com/reference/android/provider/Telephony.Sms.Intents.html#getMessagesFromIntent(android.content.Intent)">getMessagesFromIntent</a> to easily fetch a list of [`SmsMessage`](http://developer.android.com/reference/android/telephony/SmsMessage.html) objects from the Intent. This will allow you to process "received" messages!

- Older versions of Android make this much more painful, and involves a lot of typecasting.

- You will need the `RECEIVE_SMS` permission to receive SMS in your application, and `READ_SMS` to read the content of the message. Again, these are a _dangerous_ permissions, so in Marshmellow you would need to confirm them at runtime.


Finally, you can get a list of SMS messages that are on the phone by querying the
[`ContentProvider`](http://developer.android.com/guide/topics/providers/content-providers.html) provided by the system that "provides" the "content" of the [SMS Inbox](http://developer.android.com/reference/android/provider/Telephony.Sms.Inbox.html).

- Note that a `ContentProvider` would be the _correct_ way of allowing access to an SQL database. We'll talk about how to actually create these in the future, but for now you can at least use one!

- A `ContentProvider` is basically a "data source" that has a Uri (Universal Resource Indicator: think URL but not necessarily on the Internet) that you can query, similar in concept to how you get data from a website by sending a query. In particular, these Uri's specify the `content://` protocol, allowing you to access the data as content (rather than, say, HTTP). The Uri for the Inbox is specified as a constant in the [Inbox](http://developer.android.com/reference/android/provider/Telephony.Sms.Inbox.html) class.

- You can access the data in a particular `ContentProvider` by first fetching the <a href="http://developer.android.com/reference/android/content/Context.html#getContentResolver()">`ContentResolver`</a> for the current `Context`, and then using that resolver's <a href="http://developer.android.com/reference/android/content/ContentResolver.html#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)">`query`</a> method to query data from the `ContentProvider`. This method returns a `Cursor`---basically working exactly like how we queried an `SQLiteDatabase`!


#### Testing SMS
The easiest way to test that your SMS functionality work is by sending and receiving messages with another phone (why partners are useful).

The emulator is **not** capable of sending or receiving external SMS messages. However, the emulator **is** capable of sending and receiving messages _to other emulators running on the same computer_. This makes it possible to test SMS capabilities, if a little awkward.

To test sending a message, you can have the emulator send a message to _itself_. The emulator's "phone number" is simply the 4-digit port number of the emulator. You can find this number by looking at the `Run` tab of the the output window at the bottom of Android Studio (where you normally find Logcat): when you first launch an emulator, you should see a message like:
```
Device connected: emulator-5554
```
This says that the emulator is running on port `5554` (the default). If you send a text message _from_ the emulator to this port number, it should be also received by the emulator. You can find this message in the inbox (you can go to the built-in Messaging app to see a list of messages received). If your SMS sent successfully, it should be there!


Testing how your emulator _receives_ messages is a bit simpler. The trick is that you can use `telnet` to connect to your emulator and send commands directly to it! `telnet` is a simple application that lets you make very basic network connections. `telnet` is available by default on Macs and Linux machines; you may need to [enable it in Windows](http://windows.microsoft.com/en-us/windows/telnet-faq#1TC=windows-7). You can then use `telnet` to connect to the (running) emulator via:
```
telnet localhost <port>
```
where `<port>` is the port number described above (e.g., `5554`). Once you are connected to the emulator, you can send it an SMS message with:
```
sms send <from-number> "<message>"
```
e.g., `sms send 5555 "hello world!"`. To send a `hello world` message "from" number `5555`. This will cause the emulator to receive a message, which your app can then react to!


You can also send messages to the emulator by using `Emulator Control`, found at `Tools > Android > Android Device Monitor` in Android Studio (click on the `Emulator Control` tab). This basically gives you a GUI interface for the `telnet` command. Finally, you can also run two emulator instances simultaneously (they will have different port numbers); however, you need to make sure you have enough memory (and have told HAXM to use appropriate memory) for this to work.

### Looking up Contacts
Your application will need to allow the user to select one of their contacts to send a message to (because who remembers phone numbers). While it is possible to [access and search the Contacts data directly](http://developer.android.com/training/contacts-provider/index.html) a cleaner method that fits with the goal of practicing with Intents is to instead ask the _Contacts_ or _People_ app to open and let the user select a contact. See [Getting Results from an Activity](http://developer.android.com/training/basics/intents/result.html) for a detailed walkthrough.
- While most of this code is provided for you, I recommend you re-type it out on your own using your own 10 fingers. It's a good way to make sure you understand what code is being called and what all the pieces do!

In summary, you'll need to create an `Intent` to `PICK` a contact from the "contacts" content provider (this is a `ContentProvider` in the exact same way that the SMS messages list is one; it has a Uri with protocol `content://` that you can access). You can then start a new Activity with this Intent, but using the `startActivityForResult()` method in order to get a response _back_ from that Activity!

When the requested Activity is finished, the `onActivityResult()` callback in _your_ Activity (the Activity that sent off the Intent) will be executed. In this method you can then query the Contacts `ContentProvider`, again similar to how you did with SMS messages. You can get the Uri of the _individual contact_ (think a webpage on an specific web server) by calling `getData()` on the passed in `data`, then use the `ContentResolver` again to query that particular content. The `Cursor` returned will let you get information about the contact selected (you're interested in the [`Phone.NUMBER`](http://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.Phone.html#NUMBER), which is actually buried in the `ContactsContract.CommonDataKinds` package).

The overall interaction should be that the user hits a "search" button, the "Contacts" app opens. The user selects a contact, and then is taken back to your messaging activity to find the correct phone number entered so that the message is ready to send!

Note that if you're working on the emulator, you'll need to add some Contacts for this to make any sense! You can do this through the "People" app (the little blue book with a people icon on the front dock).


### Notifications
When a message is received, you will need to create a [Notification](http://developer.android.com/training/notify-user/build-notification.html) so that the user knows it arrived. Your notification doesn't need to be complex: it should include the `author` and `body` of the message (and a small icon would be nice). The user should be able to click on the notification and be taken to the _reading messages_ screen (or the detail screen for that message).

- As extra credit, you could add a ["Big View"](http://developer.android.com/training/notify-user/expanded.html) style that supports both navigation to the _reading messages_ screen AND navigation to the _compose message_ screen (e.g., a "reply" button).

- If the user jumps to a particular fragment from the notification, you should [preserve the navigation](http://developer.android.com/guide/topics/ui/notifiers/notifications.html#NotificationResponse) by specifying the back stack!

The notification should be shown on the [lock screen](http://developer.android.com/guide/topics/ui/notifiers/notifications.html#lockscreenNotification). But since messages are usually private, you should use the API 21's visibility settings to keep it `PRIVATE`.

To create a notification, you should use the [`NotificationCompat.Builder`](http://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html) class. This will work similar to the `AlertDialog.Builder` class, in that you can call various setters on it to specify what kind of notification is built. As part of this, you'll need to specify another `PendingIntent` for what to do when the notification is clicked. Finally, you can issue the notification though the [`NotificationManager`](http://developer.android.com/training/notify-user/build-notification.html#notify).
- You are not required to ["update"](http://developer.android.com/training/notify-user/managing.html#Updating) the notification if more messages arrive later, though that's a nice touch!

See the [Notifications API Guide](http://developer.android.com/guide/topics/ui/notifiers/notifications.html) for further details.


### Settings
For this messaging app, the user should be able to specify an "auto-reply" message, as well as whether automatic replies should occur. The user should be able to choose this behavior by using a [Settings](http://developer.android.com/guide/topics/ui/settings.html) menu.

You should provide this settings menu as a [`PreferenceFragment`](http://developer.android.com/reference/android/preference/Preference.html) class, which can be shown by selection through the [App Bar](http://developer.android.com/training/appbar/index.html) (making this a `never` shown menu is a nice way to go).

Your Preferences need to include two elements: whether the user has turned on auto-reply (a [`Switch`](http://developer.android.com/reference/android/preference/SwitchPreference.html) is nice for this), and what message they have specified to auto-reply with (e.g., through an [`EditText`](http://developer.android.com/reference/android/preference/EditTextPreference.html)). The layout for these preferences should be defined as an XML resource (e.g., as `res/xml/preferences.xml`)

By using a `Preference` object, the values specified should be automatically saved inside your application's [`SharedPreferences`](http://developer.android.com/training/basics/data-storage/shared-preferences.html), which you can then access to determine whether to auto-reply when a message is received.
- In case it isn't clear, you can auto-reply by having your receiver send an SMS back to the number it heard from!

Even though there are only two items, your Settings should still follow the [Android Design Guidelines](https://www.google.com/design/spec/patterns/settings.html#).


### UI Widgets
Lastly, in order to make your app feel more "modern", you should include at least **one (1)** UI widget or interaction pattern that is provided in the latest Android APIs. Some potential elements you may use include:
- A [Snackbar](http://developer.android.com/training/snackbar/showing.html) for displaying in-app notifications (that are actionable!)
- A [floating action button](http://developer.android.com/reference/android/support/design/widget/FloatingActionButton.html) for launching new activities
- [Swipe to refresh](http://developer.android.com/training/swipe/index.html) capabilities for your message list
- A [context menu](http://developer.android.com/guide/topics/ui/menus.html#context-menu), such as for interacting with messages in your list view
- An [action bar search input](http://developer.android.com/training/search/setup.html), such as for "filtering" messages in a list view
- A [progress bar or indicator](http://developer.android.com/reference/android/widget/ProgressBar.html) (e.g., if it takes time to send a message)
- [Tabs](http://developer.android.com/training/implementing-navigation/lateral.html) for navigating between fragments
- A slide-out [naigation-drawer](http://developer.android.com/training/implementing-navigation/nav-drawer.html) for navigating between fragments

Which one(s) you include are up to you (**note**: the first two are almost trivial to add). The goal here is two-fold:
1. You to be _aware_ of some of the options available to you. At least look at all of the links to get a sense for what kind of user interaction they support, even if you don't actually implement them.
2. Practice using the documentation to add features on your own.

The Android platform changes rapidly, with new interaction paradigm and support widgets added in each new release. In order to do real-world Android development, you need to be able to understand these APIs and figure out how they fit into an overall application. This is a chance for you to do that once (or twice!) when you have resources to ask for help if needed.



## Submit Your Solution
In order to submit programming assignments in this class, you will need to both `push` your completed program to your GitHub repository (the one in the cloud that you created by forking), and submit a link to your repository to [Canvas](https://canvas.uw.edu/) (so that we know where to find your work)!

- If you are working with a partner, you only need a single GitHub repository. Only ___one___ person needs to submit a link to Canvas, but make sure you include both partner's names in the submission.

Before you submit your assignment, double-check the following:

* Test that your app builds (from `gradle`!), installs, and works without errors. It should fulfill all the user stories.
* Fill out the `SUBMISSION.md`included in the assignment directory, answering the questions.
* Commit the final version of your work, and push your code to your GitHub repository.

Submit a a link to your GitHub repository via [this canvas page](https://canvas.uw.edu/courses/1023396/assignments/3082085).

The assignment is due on **Tue Feb 02 at 6:00 AM**.

### Grading Rubric
See the assignment page on Canvas for the grading rubric.
