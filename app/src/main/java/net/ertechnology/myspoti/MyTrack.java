package net.ertechnology.myspoti;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Juan on 08/07/2015.
 */
public class MyTrack implements Parcelable {

    public String getName() {
        return mName;
    }

    public String getAlbumName() {
        return mAlbumName;
    }

    public List<String> getImages() {
        return mImages;
    }

    private final String mName;
    private final String mAlbumName;
    private List<String> mImages;

    public MyTrack(String name, String albumName, List<String> images) {
        mName = name;
        mAlbumName = albumName;
        mImages = images;
    }

    private MyTrack(Parcel in) {
        mName = in.readString();
        mAlbumName = in.readString();
        in.readList(mImages, null);
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mAlbumName);
        dest.writeList(mImages);
    }

    public static final Parcelable.Creator<MyTrack> CREATOR =
            new Parcelable.Creator<MyTrack>() {

                /**
                 * Create a new instance of the Parcelable class, instantiating it
                 * from the given Parcel whose data had previously been written by
                 * {@link Parcelable#writeToParcel Parcelable.writeToParcel()}.
                 *
                 * @param source The Parcel to read the object's data from.
                 * @return Returns a new instance of the Parcelable class.
                 */
                @Override
                public MyTrack createFromParcel(Parcel source) {
                    return new MyTrack(source);
                }

                /**
                 * Create a new array of the Parcelable class.
                 *
                 * @param size Size of the array.
                 * @return Returns an array of the Parcelable class, with every entry
                 * initialized to null.
                 */
                @Override
                public MyTrack[] newArray(int size) {
                    return new MyTrack[size];
                }
            };

    public static ArrayList<MyTrack> create(List<Track> tracks) {
        ArrayList<MyTrack> myTracks = new ArrayList<>();

        for (Track item : tracks) {
            List<String> imageUrls = new ArrayList<>();
            for (Image image: item.album.images) {
                imageUrls.add(image.url);
            }
            myTracks.add(new MyTrack(item.name, item.album.name, imageUrls));
        }
        return myTracks;
    }
}
