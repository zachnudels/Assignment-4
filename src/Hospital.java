import java.util.*;
public class Hospital{
  String type;
  String name;
  Vertex node;
  int capacity = 13;
  int beds;
  boolean full=false;
  boolean priva=false;

  public Hospital(String type){
    this.type=type;
    if (type.equals("Private"))
      priva=true;
  }

  public void update(){
    if(beds==capacity)
      full=true;
  }
}
