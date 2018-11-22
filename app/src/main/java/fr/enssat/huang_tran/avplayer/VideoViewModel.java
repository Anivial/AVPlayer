package fr.enssat.huang_tran.avplayer;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class VideoViewModel extends ViewModel {
    private MutableLiveData mpos;

    public LiveData<Integer> getPosition() {
        if (mpos == null) {
            mpos = new MutableLiveData();
            setPosition(0);
        }
        return mpos;
    }

    public void setPosition(int pos) {
        mpos.setValue(pos);
    }

}
