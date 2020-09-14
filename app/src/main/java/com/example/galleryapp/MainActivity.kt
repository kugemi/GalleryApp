package com.example.galleryapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.galleryapp.R


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val isFragmentContainerEmpty = savedInstanceState == null  //добавление фрагмента в контейнер, если его там нет
                                                                   //если Bundle null, то это новый запуск activity, знчит еще фрагментов не было
        if (isFragmentContainerEmpty)
        {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentContainer, GalleryAppFragment.newInstance())
                .commit()
        }
    }

    companion object{
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}
