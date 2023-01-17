package com.example.roomdatabase

import android.app.Application
import android.content.Context

import androidx.room.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var textView:TextView
    lateinit var button:Button
    var db:AppDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = AppDatabase.getInstance(applicationContext)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.textView)
        button = findViewById(R.id.button)


        button.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch{ getUser() }
        }

    }

    private suspend fun getUser(){

            db?.let { it.userDao().insertUser(User("first Entry" , "123456789")) }
            db?.let { it.userDao().insertUser(User("second Entry" , "123456789"))}
            val text = db?.let{ it.userDao().getAllUser()[1].userId.toString()}
        CoroutineScope(Dispatchers.Main).launch{
            textView.text = text
        }
    }

}

@Entity(tableName = "user_table")
data class User(
    val userName:String,
    val password:String ){
    @PrimaryKey(autoGenerate = true)
    var userId:Int = 0

}


@Dao
interface UserDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user:User)

    @Update
    suspend fun updateUser(user:User)

    @Delete
    suspend fun deleteUser(user:User)

    @Query("SELECT * FROM USER_TABLE")
    suspend fun getAllUser():List<User>

}

@Database(entities = [User::class], version = 2)
abstract class AppDatabase:RoomDatabase(){

    abstract fun userDao():UserDao

    companion object{
        private var instance:AppDatabase? = null
        fun getInstance(applicationContext:Context):AppDatabase?{
            instance?:let{ instance = Room.databaseBuilder(applicationContext,AppDatabase::class.java,"SampleRoomDatabase").fallbackToDestructiveMigration().build() }
            return instance
        }
    }

}