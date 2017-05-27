public class Ambulance{
  String type;
  int capacity;
  Vertex node;

  public Ambulance(String type){
    this.type=type;
    if (type.equals("Public"))
      capacity=1;
    else
      capacity=2;
  }
}
