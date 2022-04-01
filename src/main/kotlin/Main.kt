import controllers.NoteAPI
import models.Note
import mu.KotlinLogging
import persistence.JSONSerializer
import utils.ScannerInput.readNextInt
import utils.ScannerInput.readNextLine
import java.io.File
import java.lang.System.exit

private val logger = KotlinLogging.logger {}
//private val noteAPI = NoteAPI(XMLSerializer(File("notes.xml")))
private val noteAPI = NoteAPI(JSONSerializer(File("notes.json")))

fun main(args: Array<String>) {
    runMenu()
}

fun runMenu() {
    do {
        val option = mainMenu()
        when (option) {
            1 -> addNote()
            2 -> listNotes()
            3 -> listActiveNotesByCategory()
            4 -> updateNote()
            5 -> deleteNote()
            6 -> archiveNote()
            7 -> searchNotes()
            20 -> save()
            21 -> load()
            0 -> exitApp()
            else -> System.out.println("Invalid option selected: ${option}")
        }
    } while (true)
}

fun mainMenu(): Int {
    return readNextInt(
        """ 
         > --------------------------------------
         > |        NOTE KEEPER APP             |
         > --------------------------------------
         > | NOTE MENU                          |
         > |   1) Add a note                    |
         > |   2) List notes                    |
         > |   3) List Active Notes By Category |
         > |   4) Update a note                 |
         > |   5) Delete a note                 |
         > |   6) Archive a note                |
         > |   7) Search notes                  |
         > --------------------------------------
         > |   20) Save notes                   |
         > |   21) Load notes                   |
         > |   0) Exit                          |
         > --------------------------------------
         > ==>> """.trimMargin(">")
    )
}

fun addNote(){
    //logger.info { "addNote() function invoked" }
    val noteTitle = readNextLine("Enter a title for the note: ")
    val notePriority = readNextInt("Enter a priority (1-low, 2, 3, 4, 5-high): ")
    val noteCategory = readNextLine("Enter a category for the note: ")
    val isAdded = noteAPI.add(Note(noteTitle, notePriority, noteCategory, false))

    if (isAdded) {
        println("Added Successfully")
    } else {
        println("Add Failed")
    }
}

fun listNotes() {
    if (noteAPI.numberOfNotes() > 0) {
        val option = readNextInt(
            """
                  > --------------------------------
                  > |   1) View ALL notes          |
                  > |   2) View ACTIVE notes       |
                  > |   3) View ARCHIVED notes     |
                  > --------------------------------
         > ==>> """.trimMargin(">"))

        when (option) {
            1 -> listAllNotes();
            2 -> listActiveNotes();
            3 -> listArchivedNotes();
          //  4 -> noteContents();
          //  5 -> noteStatus();
          //  6 -> listEvenNotes();
          //  7 -> listOddNotes();
            else -> println("Invalid option entered: " + option);
        }
    } else {
        println("Option Invalid - No notes stored");
    }
}

fun listActiveNotesByCategory() {
    if (noteAPI.numberOfNotes() > 0) {
        val option = readNextInt(
            """
                  > --------------------------------
                  > |   1) List Low priority       |
                  > |   2) List Mid Priority       |
                  > |   3) List High Priority      |
                  > --------------------------------
         > ==>> """.trimMargin(">"))

        when (option) {
            1 -> listActiveLowPriorityNotes();
            2 -> listActiveMidPriorityNotes();
            3 -> listActiveHighPriorityNotes();
            else -> println("Invalid option entered: " + option);
        }
    }
    else {
        println("Option Invalid - No active notes stored");
    }
}

fun updateNote() {
    //logger.info { "updateNotes() function invoked" }
    listNotes()
    if (noteAPI.numberOfNotes() > 0) {
        //only ask the user to choose the note if notes exist
        val indexToUpdate = readNextInt("Enter the index of the note to update: ")
        if (noteAPI.isValidIndex(indexToUpdate)) {
            val noteTitle = readNextLine("Enter a title for the note: ")
            val notePriority = readNextInt("Enter a priority (1-low, 2, 3, 4, 5-high): ")
            val noteCategory = readNextLine("Enter a category for the note: ")

            //pass the index of the note and the new note details to NoteAPI for updating and check for success.
            if (noteAPI.updateNote(indexToUpdate, Note(noteTitle, notePriority, noteCategory, false))){
                println("Update Successful")
            } else {
                println("Update Failed")
            }
        } else {
            println("There are no notes for this index number")
        }
    }
}

fun deleteNote(){
    //logger.info { "deleteNotes() function invoked" }
    listNotes()
    if (noteAPI.numberOfNotes() > 0) {
        //only ask the user to choose the note to delete if notes exist
        val indexToDelete = readNextInt("Enter the index of the note to delete: ")
        //pass the index of the note to NoteAPI for deleting and check for success.
        val noteToDelete = noteAPI.deleteNote(indexToDelete)
        if (noteToDelete != null) {
            println("Delete Successful! Deleted note: ${noteToDelete.noteTitle}")
        } else {
            println("Delete NOT Successful")
        }
    }
}

fun save() {
    try {
        noteAPI.store()
    } catch (e: Exception) {
        System.err.println("Error writing to file: $e")
    }
}

fun load() {
    try {
        noteAPI.load()
    } catch (e: Exception) {
        System.err.println("Error reading from file: $e")
    }
}

fun listActiveNotes() {
    println(noteAPI.listActiveNotes())
}

fun archiveNote() {
    listActiveNotes()
    if (noteAPI.numberOfActiveNotes() > 0) {
        //only ask the user to choose the note to archive if active notes exist
        val indexToArchive = readNextInt("Enter the index of the note to archive: ")
        //pass the index of the note to NoteAPI for archiving and check for success.
        if (noteAPI.archiveNote(indexToArchive)) {
            println("Archive Successful!")
        } else {
            println("Archive NOT Successful")
        }
    }
}

fun listAllNotes() {
    println(noteAPI.listAllNotes())
}

fun listArchivedNotes() {
    println(noteAPI.listArchivedNotes())
}

fun searchNotes() {
    val searchTitle = readNextLine("Enter the description to search by: ")
    val searchResults = noteAPI.searchByTitle(searchTitle)
    if (searchResults.isEmpty()) {
        println("No notes found")
    } else {
        println(searchResults)
    }
}



/*made an attempt on the idea that was given to me.
The code is running without any errors whatsoever, but its not printing the list to the console.
 */

fun listActiveLowPriorityNotes() {
    val searchPriority = readNextInt("enter the number between 1 and 2: \n")
    val searchResult = noteAPI.searchByLowPriority(searchPriority)
    if (searchResult.equals(null)) {
        println(searchPriority)
    } else {
        println(searchPriority)
    }
}


fun listActiveMidPriorityNotes() {
    val searchPriority = readNextInt("enter the number 3: \n")
    val searchResult = noteAPI.searchByMidPriority(searchPriority)
    if (searchResult.equals(null)) {
        println(searchPriority)
    } else {
        println(searchPriority)
    }
}

fun listActiveHighPriorityNotes() {
    val searchPriority = readNextInt("enter the number between 4 and 5: \n")
    val searchResult = noteAPI.searchByHighPriority(searchPriority)
    if (searchResult.equals(null)) {
        println(searchPriority)
    } else {
        println(searchPriority)
    }
}








//extra work week 8
/*
fun noteContents() {
    val array = intArrayOf(1, 2, 3, 4, 5); for (element in array) { println(element) }
}

fun noteStatus() {
    if (note is String) {
        print(note.length)
    }
}


//extra work week 7

//Source: https://www.knowledgefactory.net/2021/12/kotlin-print-odd-even-numbers-from-array-list-set.html

fun listEvenNotes() {
    val numbers = listOf(1, 4, 8, 40, 11, 22, 33, 99)
    val evenNumbers = numbers.stream().filter { o: Int -> o % 2 == 0 }.collect(Collectors.toList())
    println(evenNumbers)
}



fun listOddNotes() {
        val numbers = listOf(1, 4, 8, 40, 11, 22, 33, 99)
        val oddNumbers = numbers.stream().filter { o: Int -> o % 2 != 0 }.collect(Collectors.toList())
        println(oddNumbers)
    }
 */

fun exitApp(){
    println("Exiting...bye")
    exit(0)
}

