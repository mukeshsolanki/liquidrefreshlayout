package com.madapps.example

class Movie {
  var title: String? = null
  var genre: String? = null
  var year: String? = null

  constructor() {}

  constructor(title: String, genre: String, year: String) {
    this.title = title
    this.genre = genre
    this.year = year
  }
}