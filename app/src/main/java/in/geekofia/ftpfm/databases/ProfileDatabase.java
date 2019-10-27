package in.geekofia.ftpfm.databases;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import in.geekofia.ftpfm.interfaces.ProfileDao;
import in.geekofia.ftpfm.models.Profile;

@Database(entities = {Profile.class}, version = 1)
public abstract class ProfileDatabase extends RoomDatabase {

    private static ProfileDatabase instance;

    public abstract ProfileDao profileDao();

    public static synchronized ProfileDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    ProfileDatabase.class, "profiles_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {

        private ProfileDao profileDao;

        private PopulateDbAsyncTask(ProfileDatabase db) {
            this.profileDao = db.profileDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            profileDao.insert(new Profile("Demo Connection", "ftp.mit.edu", 21, "", ""));
            profileDao.insert(new Profile("Demo Connection 2", "ftp.geekofia.in", 2121, "geekofia-tester", "12345678"));
            return null;
        }
    }
}
