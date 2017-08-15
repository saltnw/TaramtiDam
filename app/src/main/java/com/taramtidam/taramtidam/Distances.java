package com.taramtidam.taramtidam;


import com.taramtidam.taramtidam.model.DistanceIndex;

import java.util.List;

public class Distances {


    /*
    *
    * return the index and distance of the nearest bloodMobile to a specific location
    *
    * myLatitude, myLongitude are the coordinates of the point
    * mobilesList is a List of MDAMobile
    *
    * return - 1 on error
    *
    * */
    public static DistanceIndex findNearestBloodMobile(double myLatitude, double myLongitude, List<MDAMobile> mobilesList){

        int nearestBloodMobileIndex;
        double minDistance = 0;
        double tmpDist;


        if (mobilesList == null){
            DistanceIndex emptyDistIndex = new DistanceIndex(-1, -1);
            return emptyDistIndex;
        }
        if(!(mobilesList.isEmpty())){
            minDistance = distanceBetweenTwoCords(myLatitude, mobilesList.get(0).getLatitude(), myLongitude, mobilesList.get(0).getLongitude(), 0.0, 0.0);
        }
        nearestBloodMobileIndex = 0;

        for (int i=0; i<mobilesList.size(); i++){
            tmpDist = distanceBetweenTwoCords(myLatitude, mobilesList.get(i).getLatitude(), myLongitude, mobilesList.get(i).getLongitude(), 0.0, 0.0);

            if (tmpDist < minDistance){ //if we found a more closer station
                nearestBloodMobileIndex = i;
                minDistance = tmpDist;
            }
        }
        DistanceIndex distIndex = new DistanceIndex(nearestBloodMobileIndex, minDistance);
        return distIndex;
    }





    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
     */
    public static double distanceBetweenTwoCords(double lat1, double lat2, double lon1,
                                                 double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }


}


