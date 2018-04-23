<h1 align="center">Liquid Refresh Layout - Android</h1>
<p align="center">
  <a href="https://jitpack.io/#mukeshsolanki/liquidrefreshlayout"><img src="https://jitpack.io/v/mukeshsolanki/liquidrefreshlayout/month.svg"/></a>
  <a href="https://android-arsenal.com/api?level=16"> <img src="https://img.shields.io/badge/API-16%2B-blue.svg?style=flat" /></a>
  <a href="https://jitpack.io/#mukeshsolanki/liquidrefreshlayout"> <img src="https://jitpack.io/v/mukeshsolanki/liquidrefreshlayout.svg" /></a>
  <a href="https://travis-ci.org/mukeshsolanki/liquidrefreshlayout"> <img src="https://travis-ci.org/mukeshsolanki/liquidrefreshlayout.svg?branch=master" /></a>
  <a href="https://opensource.org/licenses/Apache-2.0"><img src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
  <a href="https://www.paypal.me/mukeshsolanki"> <img src="https://img.shields.io/badge/paypal-donate-yellow.svg" /></a>
  <br /><br />
  Liquid Refresh Layout is a simple SwipeToRefresh library that helps you easily integrate SwipeToRefresh and performs simple clean liquid animation.
</p>

<p align="center">
  <img src="https://github.com/mukeshsolanki/liquidrefreshlayout/blob/master/demo.gif" width="300px" />
</p>

## Getting started
Its really simple to integrate *Liquid Refresh Layout* in android. All you need to do make the following change to you build gradle.

Step 1. Add the JitPack repository to your build file. Add it in your root build.gradle at the end of repositories:

```java
allprojects {
  repositories {
    ...
    maven { url "https://jitpack.io" }
  }
}
```
Step 2. Add the dependency
```java
dependencies {
    implementation 'com.github.mukeshsolanki:liquidrefreshlayout:<latest-version>'
}
```
## How to use Liquid Refresh Layout

Its fairly simple and straight forward to use *Liquid Refresh Layout* in you application. Just add the following in your layout where you want to display the Liquid Refresh Layout.

```XML
<com.madapps.liquid.LiquidRefreshLayout
    android:id="@+id/refreshLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:AniBackColor="@color/colorAccent"
    app:AniForeColor="@color/colorBackground"
    app:CircleSmaller="6"
    >
  <!--Add your views here for example we are using recyclerview-->
  <android.support.v7.widget.RecyclerView
      android:id="@+id/recyclerView"
      android:layout_width="match_parent"
      android:layout_height="wrap_content"
      android:scrollbars="vertical"
      />
</com.madapps.liquid.LiquidRefreshLayout>
```

and implement `LiquidRefreshLayout.OnRefreshListener` it in your activity/fragment and assign the Liquid Refresh Layout like wise.
```Java
refreshLayout.setOnRefreshListener(object : LiquidRefreshLayout.OnRefreshListener {
      override fun completeRefresh() {
         //Called when you call refreshLayout.finishRefreshing()
      }

      override fun refreshing() {
        //TODO make api call here
      }
    })
```

to stop refreshing call `refreshLayout.finishRefreshing()`

## Author
Maintained by [Mukesh Solanki](https://www.github.com/mukeshsolanki)

## Contribution
[![GitHub contributors](https://img.shields.io/github/contributors/mukeshsolanki/liquidrefreshlayout.svg)](https://github.com/mukeshsolanki/liquidrefreshlayout/graphs/contributors)

* Bug reports and pull requests are welcome.
* Make sure you use [square/java-code-styles](https://github.com/square/java-code-styles) to format your code.

## License
```
Copyright 2018 Mukesh Solanki

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