package controllers

import models.Note
import persistence.Serializer
import persistence.XMLSerializer
import java.io.File



class NoteAPI(serializerType: Serializer){

    private var serializer: Serializer = serializerType


    private var notes = ArrayList<Note>()

    fun add(note: models.Note): Boolean {
        return notes.add(note)
    }

    fun numberOfNotes(): Int {
        return notes.size
    }

    fun findNote(index: Int): Note? {
        return if (isValidListIndex(index, notes)) {
            notes[index]
        } else null
    }

    //utility method to determine if an index is valid in a list.
    fun isValidListIndex(index: Int, list: List<Any>): Boolean {
        return (index >= 0 && index < list.size)
    }

    fun listNotesBySelectedPriority(priority: Int): String {
        return if (notes.isEmpty()) {
            "No notes stored"
        } else {
            var listOfNotes = ""
            for (i in notes.indices) {
                if (notes[i].notePriority == priority) {
                    listOfNotes +=
                        """$i: ${notes[i]}
                        """.trimIndent()
                }
            }
            if (listOfNotes.equals("")) {
                "No notes with priority: $priority"
            } else {
                "${numberOfNotesByPriority(priority)} notes with priority $priority: $listOfNotes"
            }
        }
    }
/*
    fun listActiveNotesByCategory(category: Int): String {
        return if (notes.isEmpty()) {
            "No Active Notes Stored"
        } else {
            var listOfNotes = ""
            for (i in notes.indices) {
                if (notes[i].notePriority == category) {
                    listOfNotes +=
                        """$i: ${notes[i]}
                            """.trimIndent()
                }
            }
            if(listOfNotes.equals("")) {
                "No notes within this category: $category"
            }
            else {
                "${numberOfNotes()} notes with category $category: $listOfNotes"
            }
        }
    }

 */

    fun deleteNote(indexToDelete: Int): Note? {
        return if (isValidListIndex(indexToDelete, notes)) {
            notes.removeAt(indexToDelete)
        }
        else null
    }


    @Throws(Exception::class)
    fun load() {
        notes = serializer.read() as ArrayList<Note>
    }

    @Throws(Exception::class)
    fun store() {
        serializer.write(notes)
    }


    fun isValidIndex(index: Int) :Boolean{
        return isValidListIndex(index, notes);
    }

    fun updateNote(indexToUpdate: Int, note: Note?): Boolean {
        //find the note object by the index number
        val foundNote = findNote(indexToUpdate)

        //if the note exists, use the note details passed as parameters to update the found note in the ArrayList.
        if ((foundNote != null) && (note != null)) {
            foundNote.noteTitle = note.noteTitle
            foundNote.notePriority = note.notePriority
            foundNote.noteCategory = note.noteCategory
            return true
        }

        //if the note was not found, return false, indicating that the update was not successful
        return false
    }

    fun archiveNote(indexToArchive: Int): Boolean {
        if (isValidIndex(indexToArchive)) {
            val noteToArchive = notes[indexToArchive]
            if (!noteToArchive.isNoteArchived) {
                noteToArchive.isNoteArchived = true
                return true
            }
        }
        return false
    }


    fun numberOfActiveNotes(): Int = notes.count { note: Note -> note.isNoteArchived }

    fun numberOfArchivedNotes(): Int = notes.count { note: Note -> note.isNoteArchived }

    fun numberOfNotesByPriority(priority: Int): Int = notes.count { note : Note -> note.isNoteArchived }


    fun searchByTitle (searchString : String) =
        formatListString(
            notes.filter { note -> note.noteTitle.contains(searchString, ignoreCase = true) })




    fun searchByLowPriority (intRange: Int) =
        (
            notes.indexOf(Note(noteTitle = "Test1", notePriority = 2, noteCategory = "Test", isNoteArchived = true))

                )

    fun searchByMidPriority (intRange: Int) =
        (
            notes.indexOf(Note(noteTitle = "Test1", notePriority = 3, noteCategory = "Test", isNoteArchived = true))

                )


    fun searchByHighPriority (intRange: Int) =
        (
            notes.indexOf(Note(noteTitle = "Test1", notePriority = 5, noteCategory = "Test", isNoteArchived = true))

                )





    fun formatListString(notesToFormat : List<Note>) : String =
        notesToFormat
            .joinToString (separator = "\n") { note ->
                notes.indexOf(note).toString() + ": " + note.toString() }

    fun listAllNotes(): String =
        if  (notes.isEmpty()) "No notes stored"
        else formatListString(notes)

    fun listActiveNotes(): String =
        if  (numberOfActiveNotes() == 0)  "No active notes stored"
        else formatListString(notes.filter { note -> !note.isNoteArchived})

    fun listArchivedNotes(): String =
        if  (numberOfArchivedNotes() == 0) "No archived notes stored"
        else formatListString(notes.filter { note -> note.isNoteArchived})


}