package com.example.rorders.kitchen

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rorders.R
import com.example.rorders.kitchen.adapter.KitchenTableAdapter
import com.example.rorders.kitchen.model.TableListModel
import com.example.rorders.login.LoginActivity
import com.example.rorders.staff.adapter.StaffTableAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class KitchenMainActivity : AppCompatActivity() {
    lateinit var nContext: Context
    private lateinit var auth: FirebaseAuth
    lateinit var heading: TextView
    lateinit var signOutBtn: Button
    lateinit var nxtBtn: Button
    lateinit var menuBtn: Button
    lateinit var detailTableRec: RecyclerView
    var backPressedTime: Long = 0
    var tableList: ArrayList<TableListModel> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kitchen_main)
        nContext=this
        auth = Firebase.auth
        init()
        orderdetail()
        Toast.makeText(this, "Kitchen", Toast.LENGTH_SHORT).show()
    }
    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
            finish()
        } else {
            Toast.makeText(this, "Press back again to exit.", Toast.LENGTH_LONG).show()
        }
        backPressedTime = System.currentTimeMillis()
    }
    private fun init(){
        signOutBtn=findViewById(R.id.signout_btn)
        nxtBtn=findViewById(R.id.next_btn)
        menuBtn=findViewById(R.id.menu_btn)
        heading=findViewById(R.id.heading)
        heading.text="Order Details"
        detailTableRec=findViewById(R.id.detail_table_rec)
        signOutBtn.setOnClickListener {
            auth.signOut()
            var intent= Intent(nContext, LoginActivity::class.java)
            startActivity(intent)

        }
        menuBtn.setOnClickListener {
            var intent= Intent(nContext, KitchenMenuActivity::class.java)
            startActivity(intent)
        }
        nxtBtn.setOnClickListener {
            var intent= Intent(nContext, KitchenOrdersActivity::class.java)
            startActivity(intent)
        }
    }
    private fun orderdetail(){
        var firebaseDatabase = FirebaseDatabase.getInstance().getReference("OrderDetails")
        //var databaseReference = firebaseDatabase.getReference("TodaysMenu");
        firebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                /*val value: String? = dataSnapshot.getValue(String::class.java)
                Log.d("TAG", value.toString())*/
                Log.e("datchange","true")
                tableList = ArrayList()
                for (ds in dataSnapshot.children) {
                    var menuItem = ds.key
                    var tableNum:String=""
                    var status=ds.child("status").value.toString()
                    if (status.toString()=="0"){
                        var nmodel=TableListModel(menuItem.toString(),status)
                        tableList.add(nmodel)
                    }else{
                        Log.e("status","not0")
                    }



                    Log.e("TAG", menuItem.toString())
                }

                Log.e("listchange",tableList.size.toString())
                detailTableRec.layoutManager= LinearLayoutManager(nContext)
                val menuAdapter= KitchenTableAdapter(nContext,tableList)
                detailTableRec.adapter=menuAdapter
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("cancelled","true")
            }

        })
    }
}