# DSBmobile-API
[![](https://jitpack.io/v/LCLPYT/DSBmobile-API.svg)](https://jitpack.io/#LCLPYT/DSBmobile-API)
[![License][license-image]][license-url]
> Unofficial DSBmobile API for Java.


A simple Java library for DSBmobile.

## Code example

### TimeTable
```java
try {
	DSBMobile dsbMobile = new DSBMobile("username", "password");

	ArrayList<TimeTable> timeTables = dsbMobile.getTimeTables();
	for (TimeTable timeTable : timeTables) {
		String date = timeTable.getDate();
		String groupName = timeTable.getGroupName();
		String id = timeTable.getId();
		String title = timeTable.getTitle();
		String url = timeTable.getUrl();
	}
} catch (IllegalArgumentException e) {
	// Wrong username or password!
}
```
Note: The try-catch block is not necessary.

### News
Note: News are not yet implemented in the new api.

## Implementation
Gradle:

**Step 1.** Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:
```gradle
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
**Step 2.** Add the dependency
```gradle
dependencies {
	implementation 'com.github.LCLPYT:DSBmobile-API:1.6f'
}
```

Maven:

**Step 1.** Add the JitPack repository to your build file
```xml
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```

```xml
	<dependency>
	    <groupId>com.github.LCLPYT</groupId>
	    <artifactId>DSBmobile-API</artifactId>
	    <version>1.6f</version>
	</dependency>
```

## Release History
* 1.5f
    * Now using the app interface.
    * Huge performance update.
* 1.4f
    * removed due to malfunction
* 1.3
    * removed due to malfunction
* 1.2
    * Maven implementation
* 1.1
    * Code cleanup
	* Readme update
* 1.0
    * Initial version

## Dependencies
- [Google Gson](https://github.com/google/gson) ([Apache 2 license](https://github.com/google/gson/blob/master/LICENSE))
- [Google Gson](https://github.com/hsiafan/requests) ([BSD 2-Clause](https://github.com/hsiafan/requests/blob/master/LICENSE))
- [JSoup](https://jsoup.org/) ([MIT license](https://jsoup.org/license))

## Info
Fork by LCLPYT 2019

© Sematre 2018

Distributed under the **MIT License**. See ``LICENSE`` for more information.

[release-image]: https://img.shields.io/github/release/Sematre/DSBmobile-API.svg?style=flat-square
[release-url]: https://github.com/Sematre/DSBmobile-API/releases

[maven-image]: https://img.shields.io/maven-central/v/de.sematre.dsbmobile/DSBmobile-API.svg?style=flat-square
[maven-url]: https://search.maven.org/artifact/de.sematre.dsbmobile/DSBmobile-API/

[travis-image]: https://img.shields.io/travis/com/Sematre/DSBmobile-API.svg?style=flat-square
[travis-url]: https://travis-ci.com/Sematre/DSBmobile-API

[license-image]: https://img.shields.io/github/license/Sematre/DSBmobile-API.svg?style=flat-square
[license-url]: https://github.com/Sematre/DSBmobile-API/blob/master/LICENSE
