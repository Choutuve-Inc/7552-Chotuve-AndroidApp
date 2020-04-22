package com.app.chotuve.home

import java.util.*
import kotlin.collections.ArrayList

class DummyVideoDataSource {

    companion object{

        fun createDummyDataSet(): ArrayList<ModelVideo> {
            val list = ArrayList<ModelVideo>()
            list.add(
                ModelVideo(
                    "Title 1",
                    "Jhon",
                    "https://raw.githubusercontent.com/mitchtabian/Blog-Images/master/digital_ocean.png",
                    "Apr 02, 2020"
                )
            )
            list.add(
                ModelVideo(
                    "Title 2",
                    "Mitch",
                    "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/time_to_build_a_kotlin_app.png",
                    "Mar 19, 2020"
                )
            )
            list.add(
                ModelVideo(
                    "Title 3",
                    "Jhon",
                    "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/coding_for_entrepreneurs.png",
                    "Mar 18, 2020"
                )
            )
            list.add(
                ModelVideo(
                    "Title 4",
                    "Jhon",
                    "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/freelance_android_dev_vasiliy_zukanov.png",
                    "Mar 12, 2020"
                )
            )
            list.add(
                ModelVideo(
                    "Title 5",
                    "Steven",
                    "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/freelance_android_dev_donn_felker.png",
                    "Mar 02, 2020"
                )
            )
            list.add(
                ModelVideo(
                    "Title 6",
                    "Tom",
                    "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/work_life_balance.png",
                    "Mar 02, 2020"
                )
            )
            list.add(
                ModelVideo(
                    "Title 7",
                    "Clarence",
                    "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/fullsnack_developer.png",
                    "Mar 02, 2020"
                )
            )
            list.add(
                ModelVideo(
                    "Title 8",
                    "Jessica",
                    "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/javascript_expert_wes_bos.png",
                    "Mar 02, 2020"
                )
            )
            list.add(
                ModelVideo(
                    "Title 9",
                    "Mitch",
                    "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/senior_android_engineer_kaushik_gopal.png",
                    "Mar 02, 2020"
                )
            )
            return list
        }

    }
}