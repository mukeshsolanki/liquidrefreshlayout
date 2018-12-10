<h1 align="center">Liquid Refresh Layout - Android</h1>
<p align="center">
  <a class="badge-align" href="https://www.codacy.com/app/mukeshsolanki/liquidrefreshlayout?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=mukeshsolanki/liquidrefreshlayout&amp;utm_campaign=Badge_Grade"><img src="https://api.codacy.com/project/badge/Grade/8507823b5c2e4890b47c7cec49d23f4e"/></a>
  <a href="https://jitpack.io/#mukeshsolanki/liquidrefreshlayout"><img src="https://jitpack.io/v/mukeshsolanki/liquidrefreshlayout/month.svg"/></a>
  <a href="https://jitpack.io/#mukeshsolanki/liquidrefreshlayout"> <img src="https://jitpack.io/v/mukeshsolanki/liquidrefreshlayout.svg" /></a>
  <a href="https://circleci.com/gh/mukeshsolanki/liquidrefreshlayout/tree/master"> <img src="https://circleci.com/gh/mukeshsolanki/liquidrefreshlayout/tree/master.svg?style=shield" /></a>
  <a href="https://opensource.org/licenses/MIT"><img src="https://img.shields.io/badge/License-MIT-blue.svg"/></a>
  <br /><br />
  Liquid Refresh Layout is a simple SwipeToRefresh library that helps you easily integrate SwipeToRefresh and performs simple clean liquid animation.
</p>

<p align="center">
  <img src="https://github.com/mukeshsolanki/liquidrefreshlayout/blob/master/demo.gif" width="300px" />
</p>

# Supporting Liquid Refresh Layout

Liquid Refresh Layout is an independent project with ongoing development and support made possible thanks to donations made by [these awesome backers](BACKERS.md#sponsors). If you'd like to join them, please consider:

- [Become a backer or sponsor on Patreon](https://www.patreon.com/mukeshsolanki).
- [One-time donation via PayPal](https://www.paypal.me/mukeshsolanki)

<a href="https://www.patreon.com/bePatron?c=935498" alt="Become a Patron"><img src="https://c5.patreon.com/external/logo/become_a_patron_button.png" /></a>

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
Copyright (c) 2018 Mukesh Solanki

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
