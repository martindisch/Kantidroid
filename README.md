Kantidroid
==========

The app for students of the Bündner Kantonsschule Chur.

**Features:**
* Extensive mark management, calculations & predictions
* Absences management
* Notifications for class cancellations
* Class schedules
* Menus for restaurants relevant for students
* Dropbox backup of important informations

Dependencies
==========
For the backwards compatible Holo Theme I'm using [HoloEverywhere] (https://github.com/Prototik/HoloEverywhere), which should be added to the project as an android library.
The backup functionality relies on the [Dropbox Sync API] (https://www.dropbox.com/developers/sync). This library can simply be thrown into the libs folder and is already included.

Credentials
==========
Since this app needs access to third party APIs, there are some credentials such as Dropbox's app secret, which cannot be revealed. They're located in /res/values/creds.xml, which is listed in .gitignore and thus not on Github. In case you want to have these features in your own build, you'll have to get your own API key and appSecret and put it in a new creds.xml.

License
==========

MIT License, see [here] (https://github.com/martindisch/Kantidroid/blob/master/LICENSE.md).