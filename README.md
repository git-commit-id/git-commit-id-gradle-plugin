# git-commit-id-gradle-plugin
git-commit-id-gradle-plugin is a plugin quite similar to [git-commit-id-maven-plugin](https://github.com/git-commit-id/git-commit-id-maven-plugin/) but for gradle!
For those who don't know the plugin, it basically helps you with the following tasks and answers related questions
* Which version had the bug? Is that deployed already?
* Make your distributed deployment aware of versions
* Validate if properties are set as expected

Getting the plugin
==================
The plugin **unreleased**!

Using the plugin
==================
To make use of the plugin you must apply the plugin in your `build.gradle` as mentioned in the [gradle documentation](https://docs.gradle.org/current/userguide/plugins.html#sec:plugins_block):
```groovy
plugins {
    id 'java'
    id 'io.github.git-commit-id.git-commit-id-gradle-plugin'
}
```
or if you want to use a specific version of the plugin via:
```groovy
plugins {
    id 'java'
    id 'io.github.git-commit-id.git-commit-id-gradle-plugin' version 'MAJOR.MINOR.PATCH'
}
```
This plugin follows the [Semantic Versioning](https://semver.org/) that roughly can be 
summarized as a version number `MAJOR.MINOR.PATCH`, increment the:
- `MAJOR` version when you make incompatible API changes
- `MINOR` version when you add functionality in a backwards compatible manner
- `PATCH` version when you make backwards compatible bug fixes


Configure the plugin
==================
This plugin comes with a sensible set of default of configurations and settings.
However, there might be cases where a default doesn't fit your project needs.
Gradle allows user to configure with [Modeling DSL-like APIs](https://docs.gradle.org/current/userguide/implementing_gradle_plugins.html#modeling_dsl_like_apis).
Perhaps one additional item you need to know about configuration is that the plugin makes
use of gradle's [Lazy Configuration](https://docs.gradle.org/current/userguide/lazy_configuration.html) which is gradle's
way to manage growing build complexity.

In short the plugin can be configured by adding a block like this to your `build.gradle`:
```groovy
gitCommitId {
    skip.set(false)
}
```

Refer to **TODO** for all the settings and in-depth explanations what the individual configurations are there for.

Versions
--------
The current version is **unreleased**


Plugin compatibility with Gradle
-----------------------------
This project requires *at least java 11* and will rely on gradle's convention for configuration.
You also will need *at least a gradle 5.3** installation to be able to use this plugin.

In case you are interested here are more details about the specifics for the version requirements:
- The [git-commit-id-plugin-core](https://github.com/git-commit-id/git-commit-id-plugin-core)
  which provides the core functions for gathering the git information relies on [JGit](https://wiki.eclipse.org/JGit)
  that is a pure Java library implementing the Git version control file access routines.
  With version [6.0](https://wiki.eclipse.org/JGit/New_and_Noteworthy/6.0) this library requires Java 11 to run.
- With [Gradle's Compatibility Matrix](https://docs.gradle.org/current/userguide/compatibility.html) the
  minimum supported gradle version is 5.0.
- This plugin relies on conventions that had been introduced as part of the [Lazy Configuration](https://docs.gradle.org/current/userguide/lazy_configuration.html).
  Such conventions had been made available with [gradle 5.1](https://docs.gradle.org/5.1/release-notes.html)
- The GitCommitIdPluginExtension is made abstract and uses an `Injection` annotation that only
  works with gradle 5.3 and onwards. For more details refer to https://github.com/gradle/gradle/issues/24947.


Maintainers
===========
This project is currently maintained thanks to: @TheSnoozer


Notable contributions
=====================
* @ktoso (founder) for the initial idea of the git-commit-id-maven-plugin
* @jbellmann for starting the initiative to make the maven plugin available for gradle ([see here](https://github.com/git-commit-id/git-commit-id-maven-plugin/pull/92))

License
=======
this plugin is released under the **GNU Lesser General Public License 3.0**.

You're free to use it as you wish, the full license text is attached in the [LICENSE](https://github.com/git-commit-id/git-commit-id-gradle-plugin/blob/main/LICENSE) file.

Feature requests
================
The best way to ask for features / improvements is [via the Issues section on GitHub](https://github.com/git-commit-id/git-commit-id-gradle-plugin/issues/new/choose)
