package com.madapps.example

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MoviesAdapter(private val moviesList: List<Movie>) :
  RecyclerView.Adapter<MoviesAdapter.MyViewHolder>() {

  inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var title: TextView = view.findViewById<View>(R.id.title) as TextView
    var year: TextView = view.findViewById<View>(R.id.year) as TextView
    var genre: TextView = view.findViewById<View>(R.id.genre) as TextView
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
    val itemView = LayoutInflater.from(parent.context)
      .inflate(R.layout.movie_list_row, parent, false)

    return MyViewHolder(itemView)
  }

  override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
    val movie = moviesList[position]
    holder.title.text = movie.title
    holder.genre.text = movie.genre
    holder.year.text = movie.year
  }

  override fun getItemCount(): Int {
    return moviesList.size
  }
}