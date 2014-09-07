/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package plugins.searching_series;

import java.awt.Point;
import java.util.List;

/**
 *
 * @author MaciekG
 */
public class DataSampling {
  public List process(List list, int s){
      System.out.print("before: " + list.size());
      for(int i=0; i<list.size()-2; i++){
          Point a =(Point) list.get(i);
          Point b =(Point) list.get(i+1);
          
          double dist = Math.sqrt(Math.pow((b.x-a.x), 2) + Math.pow((b.y-a.y), 2));
          if(dist<=s){
              list.remove(i+1);
              i--;
          }
      }
      System.out.print("after: " + list.size());
      return list;
  }  
  
}
