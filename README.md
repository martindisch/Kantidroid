# Kantidroid

The unofficial app for students of the [Bündner Kantonsschule Chur](http://www.gr.ch/DE/institutionen/verwaltung/ekud/ahb/bks/uberuns/Seiten/default.aspx).

**Features:**
* Extensive mark management, calculations & predictions
* Absences management
* Notifications for class cancellations
* Class schedules
* Dropbox backup of important information

## Dependencies

Using Gradle for dependency management, this project makes use of the [Support Library](http://developer.android.com/tools/support-library/index.html), using AppCompat, CardView, RecyclerView, Design Support Library and so on.

[Glide](https://github.com/bumptech/glide) is for the graceful loading of images and [ion](https://github.com/koush/ion) for simple downloads.

For a complete list of contributors, attributions, and licenses of the projects used, refer to [ABOUT.md](ABOUT.md).

## Credentials

Since this app needs access to third party APIs, there are some credentials such as Dropbox's app secret, which cannot be revealed. They're located in /res/values/creds.xml, which is listed in .gitignore and thus not on Github.

In case you want to have these features in your own build, you'll have to get your own API key and appSecret and put it in a new creds.xml.

## License

[MIT License](LICENSE.md)
