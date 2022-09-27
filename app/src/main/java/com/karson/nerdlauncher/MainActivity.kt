package com.karson.nerdlauncher

import android.content.Intent
import android.content.pm.ResolveInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.karson.nerdlauncher.databinding.ActivityMainBinding
import com.karson.nerdlauncher.databinding.ItemAppBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rv.apply {
            layoutManager = LinearLayoutManager(context)
        }

        setAdapter()
    }

    private fun setAdapter() {
        val intent = Intent().apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val activities = packageManager.queryIntentActivities(intent, 0)
        activities.sortWith { a, b ->
            String.CASE_INSENSITIVE_ORDER.compare(
                a.loadLabel(packageManager).toString(),
                b.loadLabel(packageManager).toString()
            )
        }
        Log.e("=====", "Found ${activities.size} activities")
        binding.rv.adapter = MyAdapter(activities)
    }

    private inner class MyViewHolder(private val itemBinding: ItemAppBinding):
        RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {
        private lateinit var activity: ResolveInfo

        init {
            itemBinding.root.setOnClickListener(this)
        }

        fun bind(activity: ResolveInfo) {
            this.activity = activity
            val label = activity.loadLabel(packageManager).toString()
            val icon = activity.loadIcon(packageManager)
            itemBinding.tvName.text = label
            itemBinding.ivLogo.setImageDrawable(icon)
        }

        override fun onClick(v: View?) {
            val intent = Intent(Intent.ACTION_MAIN)
            val info = activity.activityInfo
            intent.setClassName(info.packageName, info.name)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private inner class MyAdapter(private val activities: List<ResolveInfo>): RecyclerView.Adapter<MyViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val binding = ItemAppBinding.inflate(layoutInflater, parent, false)
            return MyViewHolder(binding)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bind(activities[position])
        }

        override fun getItemCount() = activities.size
    }
}