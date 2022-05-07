package com.example.myapplication

import android.annotation.SuppressLint
import android.graphics.Color.blue
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class Todolist : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todolist)
        showListData()
    }

    fun showListData() {

        val url = "https://21wsp14pw.course.tamk.cloud/api/v1/task/list"
        //val url = "https://10.0.2.2:3010/todolist"
        val stringRequest = StringRequest(Request.Method.GET, url,
            Response.Listener<String> { response -> handleResponsetodo(response) },
            Response.ErrorListener { handleVolleyErrortodo() });
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest)
    }

    @SuppressLint("ResourceAsColor")
    private fun handleResponsetodo(response: String) {
        //handle response here
        //Toast.makeText(this, response, Toast.LENGTH_LONG).show();

        // Parse the JSON response ( temperature, current condition and wind)
        val todoObject = JSONObject(response);
        val List = todoObject.getJSONArray("task")
        for(i in 0 until 6){
            val todoListItem : JSONObject = List.getJSONObject(i)
            val name: String = todoListItem.getString("name")
            val tag: String = todoListItem.getString("tag")

            //Append that to the text element
            findViewById<TextView>(R.id.todoListTextView).append("Todo name:" +name+"\n"+ "Tag: "+tag+"\n")
            if (i%2==0){
                findViewById<TextView>(R.id.todoListTextView).setBackgroundColor(R.color.yellow)
            }else{
                findViewById<TextView>(R.id.todoListTextView).setBackgroundColor(R.color.blue)
            }
        }


    }

    private fun handleVolleyErrortodo() {
        //Error with request
        Toast.makeText(this, "Network error", Toast.LENGTH_LONG).show();
    }

}