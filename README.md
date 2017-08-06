[ ![Download](https://api.bintray.com/packages/mrhabibi/maven/keepchildrenstates/images/download.svg) ](https://bintray.com/mrhabibi/maven/keepchildrenstates/_latestVersion)

# KeepChildrenStates

AndroidAnnotation plugin to keep states of View children of ViewGroup persist when orientation changes.

This AA plugin fix the problem about keeping ViewGroup's children states (explained here: http://trickyandroid.com/saving-android-view-state-correctly) in very simple way, you just need to add an annotation `@KeepChildrenStates` together with `@EViewGroup` and voila, your problem of saving children state inside ViewGroup has been solved automatically inside AA's generated class.

And this plugin also doesn't break `@InstanceState`, both of them can be attached inside ViewGroup together!

## Usage

```
@KeepChildrenStates
@EViewGroup(R.layout.sample_view_group)
public class SampleViewGroup extends LinearLayout {

    @InstanceState
    String status;

    ...
}
```

## Installation

### Gradle

Add this line in your `build.gradle` file:

``` 
apt 'com.mrhabibi:keepchildrenstates-api:1.0.0'
compile 'com.mrhabibi:keepchildrenstates:1.0.0'
```

And also add this line of AndroidAnnotations library in the same file:

```
apt 'org.androidannotations:androidannotations:4.3.1'
compile 'org.androidannotations:androidannotations-api:4.3.1'
```

This library is hosted in the [JCenter repository](https://bintray.com/mrhabibi/maven), so you have to ensure that the repository is included:

```
buildscript {
   repositories {
      jcenter()
   }
}
```

## Contributions

Feel free to create issues and pull requests.

## License

```
Activity Factory library for Android
Copyright (c) 2017 Muhammad Rizky Habibi (http://github.com/mrhabibi).

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```