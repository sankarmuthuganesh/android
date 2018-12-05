package com.tracker.sleep.service.clustering;

import org.apache.commons.math3.ml.clustering.Clusterable;

public class Space implements Clusterable {
   private double[] points;

   private Coordinate location;

   public Space(Coordinate location) {
      this.location = location;
      this.points = new double[] { location.getX()};
   }

   public Coordinate getLocation() {
      return location;
   }

   public double[] getPoint() {
      return points;
   }

}
