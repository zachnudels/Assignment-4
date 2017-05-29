import java.util.*;
public class SimulatorTwo{
  public static void main(String[] args){
    GraphGenerator g = new GraphGenerator(100);
    int seconds=0;
    ArrayList<Hospital> hospitals = g.hospitals;
    ArrayList<Ambulance> ambulances = g.ambulances;
    ArrayList<Trip> trips = new ArrayList<Trip>();
    ArrayList<Victim> allVics = new ArrayList<Victim>();
    ArrayList<Trip> vicPaths = new ArrayList<Trip>();
    ArrayList<Trip> hosPaths = new ArrayList<Trip>();


    // One day simulation
    while (seconds<10){
      System.out.println("Seconds: "+seconds);
      allVics.clear();
/*
// Update ambulances in action
  // 1. Update time and if reached destination
  // 2. If reached victim, update
  // 3. If reached hospital, update
*/
      for(Ambulance am: ambulances){
  // 1. If ambulance is unavailable (because driving to next node)
        if(!am.available){
          am.currentTime++; //increment timer
        //if reached next node
          if(am.currentTime==am.timeToNext){
            // am.available=true; //Only if two patients
            if(am.nodesInPath.size()>0){ //If more nodes, may not have reached victim yet
              am.node=am.nodesInPath.remove(0); //remove node from amb's nodePath save it as its current nodePosition
  // 2. Update ambulance relative to victim
              if(am.gotVic==false){
                if(am.node==null){
                  throw new GraphException("Ambulance is null");
                }
                if (am.vic1==null){
                  throw new GraphException("Ambulance's victim's is null. Second: "+ seconds);
                }
                if(am.node.equals(am.vic1.node)){ //If ambulance reaches victim
                  Victim viccur = am.vic1;
                  viccur.node.vic=null; //remove victim from node
                  viccur.node.victim=false; //remove victim(boolean) from node
                  viccur.node=null; //remove node from victim
                  am.gotVic=true; //Mark ambulance as having gotten it's patient
                }
              }
              am.onwards();
            }else{ //If hospital has no more nodes in path, must have reached hospital
  // 3. Update ambulance relative to hospital
              if(!am.node.equals(am.hosp.node))
                throw new GraphException("Ambulance didn't reach hospital!");
              Hospital currhosp = am.hosp;
              am.vic1.saved=true;
              currhosp.beds++; //Increment hospital beds
              currhosp.update(); //Update full or not
              am.reset();
            }
          }
        }
      }


      // New victim if:
      if(seconds==0 || seconds==1){///*((seconds%208==0)*/&&(seconds>207))){
        allVics.add(g.addVictim());
      }

      ArrayList<Victim> vicNames = g.checkVictims(); //list of all victims still around
      if(vicNames.size()==0){
        seconds++;
        continue; //If no victims in need, increment time
      }

        // Finding cheapest paths from ambulance to victim to hospital
        for(Victim v: vicNames){
          if (v.dead)
            continue;
          hosPaths.clear();
          vicPaths.clear();
          trips.clear();
          v.time++; //This is to track time from birth to saviour
          // System.out.println(v.name);
          if (v.inAmbu){ //Don't find new path for vics already in ambulance
            continue;
          }

// How vic will be saved
  // 1. Find ambulances
          Ambulance bestAmb=null;
          Trip bestVicTrip=null;
    // Iterate through ambulances to save shortest paths from each to victim
          for (int i=0;i<ambulances.size();i++){  //iterate through all the ambulances
            Ambulance ambi = ambulances.get(i);

          // Ambulance conditionals
            if(ambi.full && !ambi.available) //Check ambulance not full and positioned at a node
              continue;
            if(!ambi.type.equals(v.insurance)) //Check ambulance from same provider as victim insurance
              continue;


          // Find shortest path from ambulance to victim
            g.dijkstra(ambi.node.name);
            if(v.node.dist==g.INFINITY) //If dist is infinity after dijkstra, no path exists from ambi to v
              continue;
            if(ambi.node.equals(v.node)){ //This means ambulance and victim are in same place
               bestAmb=ambi;
               bestVicTrip=new Trip(v,0,ambi);
               break;
            }
            int path = v.node.dist; //cost to vic
            Trip t = new Trip(v, path, ambi);
            vicPaths.add(t);
          }
          if(bestAmb==null){
    // Iterate through ambulances with paths to find the best one
          int bestVicCost=g.INFINITY;
            for(Trip trip: vicPaths){
              if (trip.costToVic<bestVicCost)
                bestVicCost=trip.costToVic;
            }
            for(Trip trip: vicPaths){
              if(trip.costToVic==bestVicCost)
                bestVicTrip=trip;
                bestAmb=trip.amb;
            }
          }

    // Found best hosTrip but can't assign it to the victim because there may be no path to the hospital and then we've wasted an ambulance. Soz vic

// 2. Find best hospital to take victim to
          Trip bestHosTrip=null;
          Hospital bestHosp=null;
          for(Hospital h : hospitals){

    // Hospital Conditionals
            if(h.full==true) //check hospital not full
              continue; //Go to next hospital
            if(!h.type.equals(v.insurance))//check vic insurance is same as hosp's
              continue;  //Go to next hospital


            g.dijkstra(v.node.name); //cost to hospital from victim
            if(h.node.dist==g.INFINITY){
              continue;} //If unreachable, go to next hospital
            int path = h.node.dist;
            Trip t = new Trip(v,path,h);
            hosPaths.add(t);
          }
          int bestHosCost=g.INFINITY;
          for(Trip trip: hosPaths){
            if (trip.costToHosp<bestHosCost)
              bestHosCost=trip.costToHosp;
          }
          for(Trip trip: hosPaths){
            if(trip.costToHosp==bestHosCost)
              bestHosTrip=trip;
              bestHosp=trip.hosp;
          }
          if (bestVicTrip==null || bestHosTrip==null){
          v.dead=true;
          }else{
            bestAmb.hosp=bestHosp;
            bestAmb.full=true;
            bestAmb.patients++;
            bestAmb.vic1=v;
            ArrayList<Vertex> nodesToVic = g.getPath(bestAmb.node,v.node);
            if(nodesToVic==null){
              v.node.vic=null; //remove victim from node
              v.node.victim=false; //remove victim(boolean) from node
              v.node=null; //remove node from victim
              bestAmb.gotVic=true; //Mark ambulance as having gotten it's patient
            }
            else if(nodesToVic.size()==1){
              bestAmb.nodesInPath.add(nodesToVic.get(0));
            }
            else{
              bestAmb.nodesInPath.addAll(nodesToVic);
            }
            ArrayList<Vertex> nodesToHosp=null;
            if(v.node==null){
              nodesToHosp=g.getPath(bestAmb.node, bestHosp.node);
            }else{
              nodesToHosp = g.getPath(v.node,bestHosp.node);
            }
            if(nodesToHosp==null){
              throw new GraphException("Hosp and Vic in Same Place or getPath is broken. Victim: "+v.node.name+" Hospital: "+bestHosp.node.name);
            }
            else if(nodesToHosp.size()==1){
              bestAmb.nodesInPath.add(nodesToHosp.get(0));
            }
            else{
              bestAmb.nodesInPath.addAll(nodesToHosp);
            }
            bestAmb.startTimer();
          }
        }
    seconds++;
    }
    /*
    // Get averages of all victim times by insurance
    int publc =0;
    int numpub=0;
    // double life =0;
    // int numlife =0;
    // double mediclinc=0;
    // int mediclinnum=0;
    int netcare=0;
    int numnetcare=0;
    for(Victim v: allVics){
      if(v.saved){
        if(v.insurance.equals("Public")){
          numpub++;
          publc=publc+v.time;
        // }else if(v.insurance.equals("Life")){
        //   numlife++;
        //   life=life+v.time;
        // }else if(v.insurance.equals("MediClinic")){
        //   mediclinnum++;
        //   mediclinc=mediclinc+v.time;
        }else if(v.insurance.equals("NetCare")){
          numnetcare++;
          netcare = netcare+v.time;
        }else
          System.out.println("Error, insurance does not exist: "+v.insurance);
      }
    }
    System.out.println("Public: Average: "+(publc/numpub)+" Patients: "+numpub);
    // System.out.println("Life: Average: "+(life/numlife)+" Patients: "+numlife);
    // System.out.println("MediClinic: Average: "+(mediclinc/mediclinnum)+" Patients: "+mediclinnum);
    System.out.println("Private: Average: "+(netcare/numnetcare)+" Patients: "+numnetcare);
*/
  }
}
