package com.ankhrom.base.networking;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ankhrom.base.Base;

/**
 * Download file by url
 * Downloader has two modes - SYNC and ASYNC (SYNC as default)
 * SYNC works on system backround thread and all urls are downloaded one by one. Result is executed on UI Thread
 * ASYNC creates for every url new Thread and downloads all in once
 */
@Deprecated
public class FileDownloader {

    public enum Type {DOWNLOADER_SYNC, DOWNLOADER_ASYNC}

    private final List<Downloader> downloaderList;
    private OnResultListener listener;
    private float currProgress;
    private int doneCount;
    private int count;
    private Type type;

    public FileDownloader() {

        downloaderList = new ArrayList<>();
        resetProgress();
        type = Type.DOWNLOADER_SYNC;
    }

    /**
     * initialize downloader
     *
     * @param type Type.DOWNLOADER_SYNC/ASYNC
     */
    public FileDownloader(Type type) {
        this();

        setType(type);
    }

    /**
     * sets downloader type - SYNC, ASYNC
     *
     * @param type Type.DOWNLOADER_...
     */
    public void setType(Type type) {

        this.type = type;
    }

    /**
     * sets downloader listener to handle with byte result
     *
     * @param listener handler
     */
    public void setListener(OnResultListener listener) {

        this.listener = listener;
    }

    /**
     * invoke listener
     */
    private void fileDownloadDone(int dIndex, byte[] result) {

        if (listener == null) {
            throw new NoClassDefFoundError("Missing FileDownloader.OnResultListener");
        }

        updateProgress();
        listener.onDownloadDone(dIndex, result);
    }

    /**
     * invoke to download data, downloader index is set to 0
     *
     * @param urls URLs to download
     */
    public void download(String... urls) {

        download(type, 0, urls);
    }

    /**
     * invoke to download data, downloader index is set to 0
     *
     * @param urls URLs to download
     */
    public void download(URL... urls) {

        download(type, 0, urls);
    }

    /**
     * invoke to download data
     *
     * @param dIndex downloader index
     * @param urls   URLs to download
     */
    public void download(int dIndex, String... urls) {

        download(type, dIndex, urls);
    }

    /**
     * invoke to download data
     *
     * @param dIndex downloader index
     * @param urls   URLs to download
     */
    public void download(int dIndex, URL... urls) {

        download(type, dIndex, urls);
    }

    /**
     * invoke to download data, convert strings to URLs
     *
     * @param type   Type.DOWNLOADER_...
     * @param dIndex downloader index
     * @param urls   URLs to download
     */
    public void download(Type type, int dIndex, String... urls) {

        URL[] toDownload = new URL[urls.length];
        for (int i = 0; i < toDownload.length; i++) {
            try {
                toDownload[i] = new URL(urls[i]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        download(type, dIndex, toDownload);
    }

    /**
     * invoke to download data
     *
     * @param type   Type.DOWNLOADER_...
     * @param dIndex downloader index
     * @param urls   URLs to download
     */
    public void download(Type type, int dIndex, URL... urls) {

        count += urls.length;
        switch (type) {
            case DOWNLOADER_SYNC:
                downloaderList.add(new AsyncDownloader(dIndex, urls));
                break;
            case DOWNLOADER_ASYNC:
                downloaderList.add(new ThreadDownloader(dIndex, urls));
                break;
        }
    }

    /**
     * invoked when all files are downloaded
     */
    private void downloaderDone(Downloader downloader) {

        downloaderList.remove(downloader);
    }

    private void updateProgress() {

        if (++doneCount == count) {
            currProgress = 1.0f;
        } else {
            currProgress = (float) doneCount / (float) count;
        }
    }

    /**
     * get current progress
     */
    public float getProgress() {

        return currProgress;
    }

    /**
     * reset current progress
     */
    public void resetProgress() { //todo reset when progress

        currProgress = 0.0f;
        doneCount = 0;
        count = 0;
    }

    /**
     * cencel all downloads
     */
    public void cencel() {

        for (Downloader downloader : downloaderList) {
            downloader.cancel();
        }

        downloaderList.clear();
    }

    /**
     * cencel all downloads by downloader glid
     */
    public void cencel(int dIndex) {

        Iterator<Downloader> iterator = downloaderList.iterator();
        while (iterator.hasNext()) {
            Downloader downloader = iterator.next();
            if (dIndex == downloader.getId()) {
                downloader.cancel();
                iterator.remove();
            }
        }
    }

    /**
     * pause all current downloads
     */
    public void pause() {

        for (Downloader downloader : downloaderList) {
            downloader.pause();
        }
    }

    /**
     * pause all downloads by downloader glid
     */
    public void pause(int dIndex) {

        for (Downloader downloader : downloaderList) {
            if (dIndex == downloader.getId()) {
                downloader.pause();
            }
        }
    }

    /**
     * resume all paused downloads
     */
    public void resume() {

        for (Downloader downloader : downloaderList) {
            downloader.resume();
        }
    }

    /**
     * resume all paused downloads by downloader glid
     */
    public void resume(int dIndex) {

        for (Downloader downloader : downloaderList) {
            if (dIndex == downloader.getId()) {
                downloader.resume();
            }
        }
    }

    /**
     * stop handling downloads and cencel all running downloads
     */
    public void destroy() {

        listener = null;
        cencel();
    }

    private byte[] getByteResult(URL url) throws IOException {

        BufferedInputStream is = new BufferedInputStream(url.openConnection().getInputStream());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        int b;
        byte[] bar = new byte[4096];
        while ((b = is.read(bar)) != -1) {
            bos.write(bar, 0, b);
        }

        bos.flush();

        byte[] result = bos.toByteArray();

        is.close();

        return result;
    }

    /**
     * downloader based on AsyncTask - tasks are executed on a single background thread
     */
    private class AsyncDownloader extends AsyncTask<URL, Float, Void> implements Downloader {

        private final int dIndex;

        protected AsyncDownloader(int dIndex, URL... urls) {

            this.dIndex = dIndex;
            execute(urls);
        }

        protected Void doInBackground(URL... urls) {

            for (URL url : urls) {
                try {
                    if (isCancelled()) break;

                    fileDownloadDone(dIndex, getByteResult(url));

                } catch (IOException e) {
                    Base.logE("FileDownloader: " + url + e.getMessage());
                }
            }
            return null;
        }

        protected void onPostExecute(Void result) {

            downloaderDone(this);
        }

        @Override
        public int getId() {

            return dIndex;
        }

        @Override
        public void cancel() {
            cancel(true);
        }

        @Override
        public void pause() {

        }

        @Override
        public void resume() {

        }
    }

    /**
     * dowloader based on Thread, every task has own thread
     */
    private class ThreadDownloader implements Downloader {

        private Thread thread;
        private final int dIndex;

        protected ThreadDownloader(int dIndex, final URL... urls) {

            this.dIndex = dIndex;

            thread = new Thread("FileDownloader: " + dIndex) {
                @Override
                public void run() {
                    startdownloding(urls);
                }
            };
            thread.start();
        }

        private void startdownloding(URL... urls) {

            for (URL url : urls) {
                try {

                    fileDownloadDone(dIndex, getByteResult(url));

                } catch (IOException e) {
                    Base.logE("FileDownloader: " + url + e.getMessage());
                }

            }

            downloaderDone(this);
        }

        @Override
        public int getId() {

            return dIndex;
        }

        @Override
        public void cancel() {

            if (thread.isAlive()) {
                thread.interrupt();
                thread = null;
            }
        }

        @Override
        public void pause() {

        }

        @Override
        public void resume() {

        }
    }

    public interface OnResultListener {

        void onDownloadDone(int dIndex, byte[] result);
    }


    private interface Downloader {

        int getId();

        void cancel();

        void pause();

        void resume();
    }

}
