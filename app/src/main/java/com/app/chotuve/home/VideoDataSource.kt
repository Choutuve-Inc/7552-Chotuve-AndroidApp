package com.app.chotuve.home

class VideoDataSource {

    companion object {

        fun createDataSet(): ArrayList<ModelVideo> {
            val list = ArrayList<ModelVideo>()
            list.add(
                ModelVideo(
                    "Title 1",
                    "Jhon",
                    "https://raw.githubusercontent.com/mitchtabian/Blog-Images/master/digital_ocean.png",
                    "Apr 02, 2020"
                )
            )
            return list
        }

    }
}
