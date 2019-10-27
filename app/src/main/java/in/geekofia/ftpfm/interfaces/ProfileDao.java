package in.geekofia.ftpfm.interfaces;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import in.geekofia.ftpfm.models.Profile;

@Dao
public interface ProfileDao {

    @Insert
    void insert(Profile profile);

    @Update
    void update(Profile profile);

    @Delete
    void delete(Profile profile);

    @Query("DELETE FROM profiles_table")
    void deleteAllProfiles();

    @Query("SELECT * FROM profiles_table ORDER BY id ASC")
    LiveData<List<Profile>> getAllProfiles();
}
