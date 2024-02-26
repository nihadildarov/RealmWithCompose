package com.example.realmwithcompose

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.realmwithcompose.models.Address
import com.example.realmwithcompose.models.Course
import com.example.realmwithcompose.models.Student
import com.example.realmwithcompose.models.Teacher
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.realmListOf
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel:ViewModel() {

    private val realm = MyApp.realm

    val courses = realm
        .query<Course>(
            "teacher.address.fullName CONTAINS $0",
            "John"

        )
        .asFlow()
        .map {results->
            results.list.toList()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

    var courseDetails: Course? by mutableStateOf(null)
        private set

//    init {
//        createSampleEntries()
//    }


    fun showCourseDetails (course: Course) {
        courseDetails = course
    }


    fun hideCourseDetails (){
        courseDetails = null
    }

    private fun createSampleEntries(){
        viewModelScope.launch(IO) {
            realm.write {
                val address1 = Address().apply {
                    fullName = "John Doe"
                    street = "John Doe Street"
                    houseNumber = 24
                    zipCode = 123123
                    city = "JohnCity"
                }

                val address2 = Address().apply {
                    fullName = "John Doe2"
                    street = "John Doe Street2"
                    houseNumber = 242
                    zipCode = 1231232
                    city = "JohnCity2"
                }

                val course1 = Course().apply {
                    name = "Kotlin programming language"
                }


                val course2 = Course().apply {
                    name = "Android Development"
                }


                val course3 = Course().apply {
                    name = "OOP fundamentals"
                }

                val teacher1 = Teacher().apply {
                    address = address1
                    courses = realmListOf(course1,course2)
                }

                val teacher2 = Teacher().apply {
                    address = address2
                    courses = realmListOf(course3)
                }


                course1.teacher = teacher1
                course2.teacher = teacher1
                course3.teacher = teacher2

                address1.teacher = teacher1
                address2.teacher = teacher2



                val student1 = Student().apply {
                    name = "John Doe1"
                }

                val student2 = Student().apply {
                    name = "John Doe2"
                }


                course1.enrolledStudents.add(student1)
                course2.enrolledStudents.add(student2)
                course3.enrolledStudents.addAll(listOf(student1,student2))



                //ALL demekle varsa update edecek, yoxdusa elave edecek
                copyToRealm(teacher1, updatePolicy = UpdatePolicy.ALL)
                copyToRealm(teacher2, updatePolicy = UpdatePolicy.ALL)

                copyToRealm(course1, updatePolicy = UpdatePolicy.ALL)
                copyToRealm(course2, updatePolicy = UpdatePolicy.ALL)
                copyToRealm(course3, updatePolicy = UpdatePolicy.ALL)

                copyToRealm(student1, updatePolicy = UpdatePolicy.ALL)
                copyToRealm(student2, updatePolicy = UpdatePolicy.ALL)
            }
        }
    }


    fun deleteCourse(){
        viewModelScope.launch {
            realm.write {
                val course = courseDetails ?: return@write
                val latestCourse = findLatest(course) ?: return@write
                delete(latestCourse)
            }
        }
    }
}