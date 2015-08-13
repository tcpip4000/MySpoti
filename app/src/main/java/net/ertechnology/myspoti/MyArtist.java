package net.ertechnology.myspoti;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by Juan on 12/08/2015.
 */
public class MyArtist implements Parcelable{

    public String getId() {
        return mId;
    }
    public String getName() {
        return mName;
    }


    public List<String> getImages() {
        return mImages;
    }

    private final String mId;
    private final String mName;
    private List<String> mImages;

    public MyArtist(String id, String name, List<String> images) {
        mId = id;
        mName = name;
        mImages = images;
    }

    public MyArtist(Parcel in) {
        mId = in.readString();
        mName = in.readString();
        in.readList(mImages, null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mName);
        dest.writeList(mImages);
    }

    public static final Parcelable.Creator<MyArtist> CREATOR =
            new Parcelable.ClassLoaderCreator<MyArtist>() {

                @Override
                public MyArtist createFromParcel(Parcel source) {
                    return new MyArtist(source);
                }

                @Override
                public MyArtist[] newArray(int size) {
                    return new MyArtist[size];
                }

                @Override
                public MyArtist createFromParcel(Parcel source, ClassLoader loader) {
                    return null;
                }
            };

    public static ArrayList<MyArtist> create(List<Artist> artists) {
        ArrayList<MyArtist> myArtists = new ArrayList<>();

        for (Artist item : artists) {
            List<String> imageUrls = new ArrayList<>();
            for (Image image: item.images) {
                imageUrls.add(image.url);
            }
            myArtists.add(new MyArtist(item.id, item.name, imageUrls));
        }
        return myArtists;
    }
}
