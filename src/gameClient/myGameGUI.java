package gameClient;

import GUI_graph.gui_graph;
import GameUtils.Fruit;
import GameUtils.Robot;
import GameUtils.gameFruits;
import GameUtils.gameRobots;
import Server.Game_Server;
import Server.game_service;
import algorithms.Graph_Algo;
import dataStructure.*;

import oop_dataStructure.OOP_DGraph;
import org.json.JSONException;
import org.json.JSONObject;
import utils.Point3D;

import javax.swing.*;
import java.util.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;

public class myGameGUI extends JFrame
{
    static DGraph dGraph;
    static Timer time;
    static gui_graph guiGraph;
    private static gameFruits game_fruits;
    static gameRobots game_robots;
    static boolean auto_mode;
    int scenario;





    public myGameGUI(int scenario_num){
        game_service game = Game_Server.getServer(scenario_num);
        scenario = scenario_num;
        String g = game.getGraph();
        OOP_DGraph oopdGraph = new OOP_DGraph();
        oopdGraph.init(g);
        dGraph  = new DGraph(oopdGraph);
        //todo: implement input dialog for boolean auto-mode
        game_robots = new gameRobots(dGraph, game);
        game_fruits = new gameFruits(game, dGraph);


    }



    public void drawGraph()
    {
        //// Todo: if have time include intro screen

         guiGraph  = new gui_graph(dGraph, (ArrayList<Fruit>) this.game_fruits.getFruitList(),this.game_robots.Robots());

    }

    public static void update()
    {
        Iterator<Robot> robotsIter = game_robots.RobotsCollection().iterator();

        Robot r;
        while (robotsIter.hasNext())
        {

            r = robotsIter.next();

         if( !r.move_to_dest(dGraph))
         {
           r.setNext_node(3);
         }

        }

       guiGraph.update_frame((ArrayList<Fruit>) game_fruits.getFruitList(),game_robots.Robots());
    }












    private static int nextNode(DGraph g, int src) {
        int ans = -1;
        Collection<edge_data> ee = g.getE(src);
        Iterator<edge_data> itr = ee.iterator();
        int s = ee.size();
        int r = (int)(Math.random()*s);
        int i=0;
        while(i<r) {itr.next();i++;}
        ans = itr.next().getDest();
        return ans;
    }


    private static void moveRobots(game_service game, graph graph, gameFruits fruits, myGameGUI myGame){
        List<String> log = game.move();
        if (log !=null ){
            for(int i = 0 ; i< log.size(); i++){
                String robot_json = log.get(i);
                try {
                    JSONObject line = new JSONObject(robot_json);
                    JSONObject ttt = line.getJSONObject("Robot");
                    int robot_id = ttt.getInt("id");
                    int src = ttt.getInt("src");
                    int dest = ttt.getInt("dest");
                    int speed = ttt.getInt("speed");

                    gameRobots allRobots = new gameRobots(graph, game);
                    fruits = new gameFruits(game, graph);

                    if(dest==-1) {
                       if(auto_mode){
                           if (robot_id == 0){
                               if (speed > 3){
                                   dest = nextNodePriority(game, graph, robot_id, fruits, true, src);
                               } else {
                                   dest = nextNodePriority(game, graph, robot_id, fruits, false, src);
                               }
                           }
                           if (robot_id == 1){
                                dest = nextNode2(game, fruits, graph, robot_id, src);
                           }

                       }
                    }
                }
                catch (JSONException e) {e.printStackTrace();}
            }
        }

    }

    /**
     * This method is similar with to nextNode, the difference is noticing the speed parameter when choosing
     * the closest fruit. If speed is true then it gives better path based on Shortest path.
     * If speed is false, then algorithm based on proximity of points, which is inferior.
     *
     * @param game
     * @param graph
     * @param robot_id
     * @param fruits
     * @param speed
     * @param src
     * @return
     */
    public static int nextNodePriority(game_service game, graph graph, int robot_id, gameFruits fruits, boolean speed, int src){
        gameRobots allRobots = new gameRobots(graph, game);
        Graph_Algo algo = new Graph_Algo(graph);

        Robot robot = allRobots.getRobotByID(robot_id);
        Fruit fru = fruits.getnearFruit(speed, src);
        int fru_id = fru.getId();

        edge_data edge_of_fruit = fruits.edgeOfFruit(fru_id);

        if( edge_of_fruit != null){
            if (edge_of_fruit.getSrc() == src) return edge_of_fruit.getDest();
            if (edge_of_fruit.getDest() == src) return edge_of_fruit.getSrc();
            if (fru.getType() == GameUtils.fruits.BANANA){
                List<node_data> Path = algo.shortestPath(src, edge_of_fruit.getDest());
                return Path.get(1).getKey();
            }else{
                List<node_data> Path = algo.shortestPath(src, edge_of_fruit.getSrc());
                return Path.get(1).getKey();
            }
        }

        return -1;
    }


    /**
     * In case of automatic mode finding the next node based on high value fruit
     * @param game
     * @param dgraph
     * @param robot_id
     * @param node_src
     * @param fruits
     * @return
     */
    private static int autoNextNode(game_service game, graph dgraph, int robot_id, int node_src, gameFruits fruits){
        Graph_Algo algo =  new Graph_Algo(dgraph);
        Fruit maxValue =  fruits.MaxFruit();
        int maxId = maxValue.getId();

        edge_data edge_of_fruit = fruits.edgeOfFruit(maxId);
        if (edge_of_fruit != null){
            if (edge_of_fruit.getDest() == node_src ){
                return edge_of_fruit.getSrc();
            }

            if (edge_of_fruit.getSrc() == node_src){
                return edge_of_fruit.getDest();
            }

            if (maxValue.getType() == GameUtils.fruits.BANANA ){
                List<node_data> Path = algo.shortestPath(node_src, edge_of_fruit.getDest());
                return Path.get(1).getKey();
            }else {
                List<node_data> Path = algo.shortestPath(node_src, edge_of_fruit.getSrc());
                return Path.get(1).getKey();
            }
        }

        return 0;
    }

    /**
     * The General method to find next node key
     * @param game
     * @param fruits
     * @param graph
     * @param robot_id
     * @param node_src
     * @return node key
     */
    private static int nextNode2(game_service game, gameFruits fruits, graph graph, int robot_id, int node_src){
        if (auto_mode){
            return autoNextNode(game, graph, robot_id, node_src, fruits);
        }else{
            /**
             * get X and Y from mouse
             */
            //node_src = clickToNode(x, y);
            return node_src;
        }
    }

    /**
     * this method return a node out of (x,y) coordinates from mouse click
     * @param graph
     * @param x
     * @param y
     * @return node key
     */
    private static int clickToNode(graph graph, double x, double y){
        Iterator<node_data> nodes = graph.getV().iterator();
        double EPS = 0.0005;

        while (nodes.hasNext()){
            Point3D point_node = new Point3D(nodes.next().getLocation());
            if( (Math.abs(point_node.y() - y) < EPS) &&  (Math.abs(point_node.x() - x) <= EPS)   ){
                return nodes.next().getKey();
            }
        }
        return -1;
    }








    public static void main(String[] args) {

        myGameGUI gameGUI = new myGameGUI(19);

        gameGUI.drawGraph();

    while (true) {

       // update();

        //guiGraph.repaint();

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    }


    public int getScenario() {
        return scenario;
    }

    public DGraph getdGraph() {
        return dGraph;
    }
}
