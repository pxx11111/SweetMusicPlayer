package com.huwei.sweetmusicplayer;


import com.huwei.sweetmusicplayer.contains.IContain;
import com.huwei.sweetmusicplayer.datamanager.MusicManager;
import com.huwei.sweetmusicplayer.fragments.MainFragment;
import com.huwei.sweetmusicplayer.abstracts.AbstractMusic;
import com.huwei.sweetmusicplayer.interfaces.IMusicControl;
import com.huwei.sweetmusicplayer.services.MusicControlerService;
import com.huwei.sweetmusicplayer.fragments.PlayingFragment;
import com.huwei.sweetmusicplayer.ui.widgets.SlidingPanel;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.*;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.PopupWindow;


import java.util.List;


public class MainActivity extends BaseActivity implements IMusicControl,IContain {
    private SlidingPanel mSlidingPanel;
    private PopupWindow pop;

    private IMusicControlerService musicControler;
    private boolean isServiceBinding;


    private FragmentManager manager;

    private PlayingFragment playing_fragment;
    private MainFragment mainFragment;


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            isServiceBinding = true;
            musicControler = IMusicControlerService.Stub.asInterface(iBinder);

            //todo
//            if (MusicManager.getInstance().isForeground()) {
//
//                Intent intent = new Intent(PLAYBAR_UPDATE);
//                sendBroadcast(intent);
//            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isServiceBinding = false;
            musicControler = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MusicManager.getInstance().bindProxyedObject(this);

        manager=getSupportFragmentManager();

        setContentView(R.layout.activity_main);
        playing_fragment= (PlayingFragment) manager.findFragmentById(R.id.playing_fragment);
        mainFragment = (MainFragment) manager.findFragmentById(R.id.main);

        initView();
        initReciever();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!isServiceBinding) {
            Intent intent = new Intent("com.huwei.sweetmusicplayer.services.MusicControlerService");
            startService(intent);

            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        if (mSlidingPanel.isExpanded()) {
            if(playing_fragment.isDrawerOpen()){
                playing_fragment.closeDrawer();
            }else {
                mSlidingPanel.close();
            }
        }else if(manager.getBackStackEntryCount()!=0){
            manager.popBackStack();
        }
        else {
            moveTaskToBack(true);

            //finish();
        }
    }


    @Override
    public void play() {
        try {
            musicControler.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pause() {
        try {
            musicControler.pause();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        try {
            musicControler.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void seekTo(int progress) {
        try {
            musicControler.seekTo(progress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void preparePlayingList(int index, List list) {
        try {
            musicControler.preparePlayingList(index, list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isPlaying() {
        if (musicControler == null) return false;

        try {
            return musicControler.isPlaying();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public AbstractMusic getNowPlayingSong() {
        try {
            return musicControler.getNowPlayingSong();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isForeground() {
        if (musicControler == null) return false;

        try {
            return musicControler.isForeground();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int getNowPlayingIndex() {
        if (musicControler == null) return -1;

        try {
            return musicControler.getPlayingSongIndex();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void nextSong() {
        try {
            musicControler.nextSong();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void preSong() {
        try {
            musicControler.preSong();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void randomSong() {
        try {
            musicControler.randomSong();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateMusicQueue() {
        Intent intent = new Intent(UPTATE_MUISC_QUEUE);
        sendBroadcast(intent);
    }

    public void stopPlayingService() {
        if (isServiceBinding) {
            unbindService(mConnection);

            Intent intent = new Intent(this, MusicControlerService.class);
            stopService(intent);
        }
    }

    private void initView() {
        mSlidingPanel = (SlidingPanel) findViewById(R.id.sp_main);
//        mToolbar = (Toolbar) findViewById(R.id.toolbar);
//
//        setSupportActionBar(mToolbar);
    }

    private void initReciever() {
        //ExitPlayProReciever reciever = new ExitPlayProReciever();
        //IntentFilter filter1 = new IntentFilter(SweetMPContains.PLAYPRO_EXIT);
        //registerReceiver(reciever, filter1);
    }

    public void songscan(View v) {

        Intent intent = new Intent();
        intent.setClass(this, SongScanActivity.class);
        startActivity(intent);
    }

    /**
     * ???content*
     *
     * @param view
     */
    public void closeContent(View view) {
        mSlidingPanel.close();
    }
}
