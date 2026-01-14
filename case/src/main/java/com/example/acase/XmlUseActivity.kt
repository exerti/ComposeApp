package com.example.acase

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class XmlUseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 创建简单布局，避免复杂的Navigation和资源命名空间问题
        setContentView(R.layout.activity_xml_use_simple)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        
        // 设置返回按钮
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "XML Use Activity"

        // FAB点击事件
        findViewById<FloatingActionButton>(R.id.fab)?.setOnClickListener { view ->
            Snackbar.make(view, "This is XML-based Activity with ViewBinding", Snackbar.LENGTH_LONG)
                .setAction("OK", null)
                .show()
        }
        
        // 显示传递的数据
        val extraValue = intent.getStringExtra("key") ?: "No data"
        findViewById<TextView>(R.id.textView)?.text = "Received data: $extraValue\n\nThis Activity demonstrates:\n• XML layouts\n• Material Design components\n• Activity navigation from Compose"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}