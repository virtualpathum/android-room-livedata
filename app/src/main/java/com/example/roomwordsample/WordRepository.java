package com.example.roomwordsample;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

class WordRepository {

    private WordDao mWordDao;
    private LiveData<List<Word>> mAllWords;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    WordRepository(Application application) {
        WordRoomDatabase db = WordRoomDatabase.getDatabase(application);
        mWordDao = db.wordDao();
        mAllWords = (LiveData<List<Word>>) mWordDao.getAlphabetizedWords();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    LiveData<List<Word>> getAllWords() {
        return mAllWords;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    void insert(Word word) {
        WordRoomDatabase.databaseWriteExecutor.execute(() -> {
            mWordDao.insert(word);
        });
    }
}
    /*The main takeaways:

        The DAO is passed into the repository constructor as opposed to the whole database. This is because you only need access to the DAO, since it contains all the read/write methods for the database. There's no need to expose the entire database to the repository.
        The getAllWords method returns the LiveData list of words from Room; we can do this because of how we defined the getAlphabetizedWords method to return LiveData in the "The LiveData class" step. Room executes all queries on a separate thread. Then observed LiveData will notify the observer on the main thread when the data has changed.
        We need to not run the insert on the main thread, so we use the ExecutorService we created in the WordRoomDatabase to perform the insert on a background thread.
        Repositories are meant to mediate between different data sources. In this simple example, you only have one data source, so the Repository doesn't do much. See the BasicSample for a more complex implementation.

        */

