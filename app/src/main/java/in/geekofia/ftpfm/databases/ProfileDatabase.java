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
            profileDao.insert(new Profile("Remote Demo 1", "demo.wftpserver.com", 21, "demo-user", "demo-user"));
            profileDao.insert(new Profile("Remote Demo 2", "ftp.cs.brown.edu", 21, "", ""));
            profileDao.insert(new Profile("Local Demo 1", "192.168.0.101", 2121, "user_name", "your_pass"));
            return null;
        }
    }
}
