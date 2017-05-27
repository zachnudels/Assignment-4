public class Hospital{
  String type;
  String name;
  Vertex node;
  int ambus;
  int beds = 13;
  boolean full;
  boolean priva=false;


  public Hospital(String type, int num){
    this.type=type;
    this.name=type+Integer.toString(num);
    if (type.equals("Life")|| type.equals("MediClinic")||type.equals("NetCare"))
      priva=true;
    if(priva){
      if (type.equals("NetCare"))
        ambus=3;
      else
        ambus=2;
    }
    else{
      ambus=2;

    }
  }
}
