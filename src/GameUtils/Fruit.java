package GameUtils;

import dataStructure.graph;
import dataStructure.node_data;
import org.json.JSONException;
import org.json.JSONObject;
import utils.Point3D;

public class Fruit {
    private fruits type;                //Type of fruit - Banana or Apple
    private double val;                 //Value of fruit
    private Point3D location;           //Location of fruit
    private boolean collected ;          //Whether the fruit was collected by a robot
    public int id;
    public Fruit(){
        ;
    }


    /**
     * Constructor of Fruit
     * @param json_fruit
     * Receives a JSONObject that has the fields : value, type and location
     * double value - points that you can gain
     * String pos - will be parsed from this pattern "x,y,z" - making a Point3D from it
     * int type - apple is 1, while banana is 0
     * @throws JSONException
     */

    public Fruit(JSONObject json_fruit) throws JSONException {
        //Value
        val = json_fruit.getDouble("value");
        int type = json_fruit.getInt("type");

        //Type  - BANANA:-1  , APPLE:1
        if(type == -1){
            this.type = fruits.BANANA;
        }else if (type == 1){
            this.type = fruits.APPLE;
        }

        //Location
        String pos = json_fruit.getString("pos");
        String[] point =pos.split(",");
        double x = Double.parseDouble(point[0]);
        double y = Double.parseDouble(point[1]);
        location = new Point3D(x,y,0);

        collected = false;

    }

    /**
     * This method returns the type of a fruit
     * @return type
     */
    public fruits getType() {
        return type;
    }

    /**
     * This method is used to modify the type of the fruit
     * @param type
     */
    public void setType(fruits type) {
        this.type = type;
    }

    /**
     * This method returns the value of a fruit
     * @return value
     */
    public double getVal() {
        return val;
    }

    /**
     * This method is used to modify the value of the fruit
     * @param val
     */
    public void setVal(double val) {
        this.val = val;
    }

    /**
     * This method returns the location of the fruit
     * @return
     */
    public Point3D getLocation() {
        return location;
    }

    /**
     * This method is used to modify the location of the fruit
     * @param location
     */
    public void setLocation(Point3D location) {
        this.location = location;
    }

    /**
     * This method is used to check if the fruit was collected by robot
     * @return true if collected
     */
    public boolean isCollected(){
        return collected;
    }

//    /**
//     * If fruit is collected, then randomly chooses a new edge for it.
//     * different value
//     */
//    public void backInGame(graph graph){
//        if (this.isCollected()) {
//            this.replaceFruit(graph);
//            this.collected = false;
//        }
//    }

    /**
     * if a fruit is collected then pick a random edge for it from the graph
     * then replace it on a random point on the graph
     * and change it's value to be random number between [0,MaxValueFruit)
     * @param graph
     */
    public void replaceFruit(graph graph){

            int rand_key = (int)(graph.getV().size() * Math.random());
            node_data n1 = graph.getNode(rand_key);
            int rand_key2 = (int) (graph.getE(rand_key).size() * Math.random());//(int)(graph.getV().size() * Math.random());
            node_data n2 = graph.getNode(rand_key2);
            double dist = n2.getLocation().distance2D(n1.getLocation());
            //double y = stickToEdge(n1, n2,  dist*  Math.random());
            double x = dist*  Math.random();
            double y = stickToEdge(n1 , n2, x);
            this.setLocation(new Point3D(x,y,0));
            int maxVAL = 30;
            this.setVal(Math.random() * maxVAL);
            //this.collected = false;

    }


    /**
     * This method is used to collect the fruit
     */
    public void collect(){
        this.collected = true;
    }

    public void setId(int n){
        this.id = n;
    }
    public int getId(){
        return this.id;
    }




    public double stickToEdge(node_data n1, node_data n2, double x){
        double x0 = n1.getLocation().x();
        double y0 = n1.getLocation().y();
        double x1 = n2.getLocation().x();
        double y1 = n2.getLocation().y();

        double m = (y1-y0)/(x1-x0);
        double n = y1 - m*x1;

        return (m*x + n);
    }
}
