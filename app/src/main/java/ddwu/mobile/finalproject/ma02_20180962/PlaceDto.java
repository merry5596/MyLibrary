package ddwu.mobile.finalproject.ma02_20180962;

import com.google.android.gms.maps.model.Marker;

public class PlaceDto implements Comparable<PlaceDto> {

    long _id;
    String name;
    String address;
    Double distance;
    Marker marker;

    public PlaceDto(long _id, String name, String address, double distance, Marker marker) {
        this._id = _id;
        this.name = name;
        this.address = address;
        this.distance = distance;
        this.marker = marker;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    @Override
    public int compareTo(PlaceDto markerDto) {
        return this.distance.compareTo(markerDto.distance);
    }
}
