[![](https://jitpack.io/v/Doomsdayrs/shosetsu-services.svg)]
(https://jitpack.io/#Doomsdayrs/shosetsu-services)

# Info

This repository contains the standard library for Shosetsu.

Contained are the classes that define each facet of Shosetsu's internal data and repositories.

Changes to this repository are eventually integrated into a Shosetsu release.

## Testing

kotlin-lib can be used to test Shosetsu extensions.

To do so, currently one must modify `src/test/kotlin/app/shosetsu/lib/Test.kt`.

### Steps

1. Change the DIRECTORY constant to the absolute path of the repository you want to use.
	* It is suggested to use the [default repository]
	  (https://github.com/shosetsuorg/extensions).
		* To clone, execute the following
		  `git clone https://github.com/shosetsuorg/extensions.git`
		  in the directory of your choosing.
2. Add extension you want to test to SOURCES. Such as the following
   `"/home/user/git/extensions/src/en/MyExtension.lua to LuaScript`
3. Execute the Test class
	* Currently only possible using Intellij

## Building

To build kotlin-lib, execute the following command in the project directory.

### Windows

```cmd
./gradle.bat build
```

### Linux

```bash
./gradlew build
```

### Intelij

1. Open gradle tab on the right
2. Tasks > build > build