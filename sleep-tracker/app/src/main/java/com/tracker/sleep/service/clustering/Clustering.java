package com.tracker.sleep.service.clustering;

import com.tracker.sleep.database.LifestyleLog;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Clustering {
   public void clusterAndGuessLifeStyle (List<LifestyleLog> lifestyleLogs){
        int clusterSize = HumanRoutine.values().length;

       Coordinate lone = new Coordinate(1);
       Coordinate ltwo = new Coordinate(2);
       Coordinate lthree = new Coordinate(3);
       Coordinate lfour = new Coordinate(7);
       Coordinate lfive = new Coordinate(77);
       Coordinate lseven = new Coordinate(79);
      List<Coordinate> locations = Arrays.asList(lone, ltwo, lthree, lfour, lfive, lseven);
      List<Space> clusterInput = new ArrayList<Space>(locations.size());
      for (Coordinate location : locations)
         clusterInput.add(new Space(location));

       KMeansPlusPlusClusterer<Space> clusterer = new KMeansPlusPlusClusterer<Space>(clusterSize, 1000);
       List<CentroidCluster<Space>> clusterResults = clusterer.cluster(clusterInput);

//     output the clusters
       for (int i=0; i<clusterResults.size(); i++) {
       System.out.println("Cluster " + i);
       for (Space locationWrapper : clusterResults.get(i).getPoints())
       System.out.println(locationWrapper.getLocation());
       System.out.println();
       }
   }
}
