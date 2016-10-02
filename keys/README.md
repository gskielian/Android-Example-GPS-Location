## Using "./keys" directory

request any api keys from greg -at- foodrev -dot- org
then place into this folder

check the app level build.gradle for examples for how to include

in the keys text file, place:

`key_name = <API KEY HERE>`

on new lines to add properties


afterwards place this in the app level build.gradle:

`buildConfigField "String", "MY_API_KEY", getMyApiKey("key_name")`

and place this in your app whereever you need the key:

`BuildConfig.MY_API_KEY`
