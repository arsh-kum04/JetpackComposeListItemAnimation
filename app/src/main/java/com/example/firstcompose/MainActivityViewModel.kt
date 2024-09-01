package com.example.firstcompose

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class MainActivityViewModel:ViewModel() {
    val suggestedTags= mutableStateListOf<String>()
    val selectedTags=mutableStateListOf<String>()
    fun setSuggestedTagList(){
        suggestedTags.add("Android")
        suggestedTags.add("Website")
        suggestedTags.add("AI/ML")
        suggestedTags.add("Cyber Security")


    }
    fun addSelectedTag(tag:String){
        selectedTags.add(tag)
        suggestedTags.remove(tag)
    }
    fun removeSelectedTag(tag:String){
        selectedTags.remove(tag)
        suggestedTags.add(tag)
    }
}
