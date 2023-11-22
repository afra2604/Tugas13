package com.example.note

import android.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.tugas13.databinding.ActivityMainBinding
import java.util.concurrent.AbstractExecutorService
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var mNotesDao: NoteDao
    private lateinit var  executorService: ExecutorService
    private var updateId : Int = 0
    private lateinit var  binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        executorService = Executors.newSingleThreadExecutor()
        val db = NoteRoomDatabase.getDatabase(this)
        mNotesDao = db!!.noteDao()!!


        with(binding) {
            btnAdd.setOnClickListener   {
                insert(
                    Note (title = edtMakanan.text.toString(),
                        description = edtHarga.text.toString()))
                setEmptyField()
            }
            listData.setOnItemClickListener{
                    adapterView, _, i, _ ->
                val item = adapterView.adapter.getItem(i)as Note
                updateId = item.id
                edtMakanan.setText(item.title)
                edtHarga.setText(item.description)
            }
            btnUpdate.setOnClickListener{
                update(Note(
                    id=updateId,
                    title = edtMakanan.text.toString(),
                    description = edtHarga.text.toString()
                ))
                updateId = 0
                setEmptyField()
            }
            listData.onItemLongClickListener=
                AdapterView.OnItemLongClickListener{
                        adapterview, view, i, l ->
                    val item = adapterview.adapter.getItem(i) as Note
                    delete(item)
                    true
                }
        }
    }

    private fun setEmptyField(){
        with(binding){
            edtMakanan.setText("")
            edtHarga.setText("")
        }
    }

    private fun getAllNotes(){
        mNotesDao.allNote.observe(this){
                notes ->
            val adapter : ArrayAdapter<Note> =
                ArrayAdapter<Note>(this,
                    R.layout.simple_list_item_1,
                    notes)
            binding.listData.adapter = adapter
        }
    }

    override fun onResume() {
        super.onResume()
        getAllNotes()
    }

    private fun insert(note: Note){
        executorService.execute{
            mNotesDao.insert(note)
        }
    }

    private fun update(note: Note){
        executorService.execute{
            mNotesDao.update(note)
        }
    }

    private fun delete(note: Note){
        executorService.execute{
            mNotesDao.delete(note)
        }
    }


}