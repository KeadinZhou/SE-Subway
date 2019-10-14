import java.io.*;
import java.util.*;

class Station{
    static int count=0;
    private int id;
    private String name;
    private Set<String> lines;
    private ArrayList<Integer> edges;
    private boolean vis;
    private int lastPoint;

    Station(int _id, String _name){
        this.id = _id;
        this.name = _name;
        this.lines = new HashSet<>();
        this.edges = new ArrayList<>();
        this.vis = false;
        this.lastPoint = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean checkLine(String line) {
        return lines.contains(line);
    }

    public Set<String> getLines() {
        return lines;
    }

    public void addLines(String line) {
        lines.add(line);
    }

    public ArrayList<Integer> getEdges() {
        return edges;
    }

    public void addEdges(int id) {
        edges.add(id);
    }

    public boolean isVis() {
        return vis;
    }

    public void setVis(boolean vis) {
        this.vis = vis;
    }

    public int getLastPoint() {
        return lastPoint;
    }

    public void setLastPoint(int lastPoint) {
        this.lastPoint = lastPoint;
    }
}

public class subway {
    private static final int HASH_BASE = 100000;
    private static int stationCnt = -1;
    private static HashMap<String, Integer> stationMapping=new HashMap<>(), lineStart=new HashMap<>();
    private static ArrayList<Station> stations=new ArrayList<>();
    private static Map<Integer, String> edgeLines=new HashMap<>();
    private static String _o=null;

    private static void println(Object data) throws IOException {
        String msg=data+"\n";
        if(_o!=null){
            BufferedWriter out = new BufferedWriter(new FileWriter(_o,true));
            out.write(msg);
            out.close();
        } else {
            System.out.print(msg);
        }
    }

    private static int getStationId(String name) {
        if(stationMapping.containsKey(name)){
            return stationMapping.get(name);
        }
        stationCnt++;
        stationMapping.put(name, stationCnt);
        stations.add(new Station(stationCnt, name));
        return stationCnt;
    }

    private static int getEdgeHash(int a, int b){
        return a*HASH_BASE+b;
    }

    private static void addEdge(int id1, int id2, String line) {
        stations.get(id1).addEdges(id2);
        stations.get(id2).addEdges(id1);
        edgeLines.put(getEdgeHash(id1,id2),line);
        edgeLines.put(getEdgeHash(id2,id1),line);
    }

    private static void dataInit(String pathname) {
        try(FileReader reader = new FileReader(pathname); BufferedReader br = new BufferedReader(reader) ) {
            String line;
            String lineName="";
            boolean first=false;
            while((line=br.readLine()) != null) {
                String[] data = line.split(" ");
                if(data[0].contains("*")){
                    lineName=data[1];
                    first=true;
                    continue;
                }
                int id1=getStationId(data[0]);
                int id2=getStationId(data[1]);
                if(first){
                    lineStart.put(lineName, id1);
                    first=false;
                    stations.get(id1).addLines(lineName);
                }
                stations.get(id2).addLines(lineName);
                addEdge(id1, id2, lineName);
            }
            Station.count=stationCnt;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getAllStationsInLine(String lineName) throws IOException {
        if(!lineStart.containsKey(lineName)){
            println("错误：没有这条线路");
            return;
        }
        println(lineName);
        int start=lineStart.get(lineName);
        Queue<Integer> q=new LinkedList<>();
        q.offer(start);
        stations.get(start).setVis(true);
        while(!q.isEmpty()){
            int now=q.poll();
            Station nowStation=stations.get(now);
            println(nowStation.getName());
            for(int it:nowStation.getEdges()){
                Station tmp=stations.get(it);
                if(tmp.isVis()) continue;
                if(tmp.checkLine(lineName)){
                    q.offer(it);
                    tmp.setVis(true);
                }
            }
        }
    }

    private static String checkSwitchLine(int id1, int id2, int id3){
        String linea=edgeLines.get(getEdgeHash(id1,id2));
        String lineb=edgeLines.get(getEdgeHash(id2,id3));
        if(linea.equals(lineb)) return null;
        return lineb;
    }

    private static void getShortestPath(String start, String end) throws IOException {
        int startID=getStationId(start);
        if(startID>Station.count){
            println("错误：没有 " + start + " 这个站");
            return;
        }
        int endID=getStationId(end);
        if(endID>Station.count){
            println("错误：没有 " + end + " 这个站");
            return;
        }
        Queue<Integer>q=new LinkedList<>();
        q.offer(startID);
        stations.get(startID).setVis(true);
        while(!q.isEmpty()){
            Station now=stations.get(q.poll());
            for(int it:now.getEdges()){
                Station tmp=stations.get(it);
                if(tmp.isVis()) continue;
                q.offer(it);
                tmp.setVis(true);
                tmp.setLastPoint(now.getId());
                if(it==endID) break;
            }
        }
        Stack<Integer> path=new Stack<>();
        int tip=endID;
        while(tip!=startID){
            path.push(tip);
            tip=stations.get(tip).getLastPoint();
        }

        ArrayList<Integer> res=new ArrayList<>();
        res.add(startID);
        while(!path.empty()){
            int now=path.pop();
            res.add(now);
        }
        println(res.size());
        int tmpa=-1,tmpb=-1;
        for(int it:res){
            if(tmpa!=-1){
                String switchMsg=checkSwitchLine(tmpa,tmpb,it);
                if(switchMsg!=null){
                    println(switchMsg);
                }
            }
            println(stations.get(it).getName());
            tmpa=tmpb;
            tmpb=it;
        }
    }

    public static void main(String[] args) throws IOException {
        String _map=null;
        String _a=null;
        String _b1=null,_b2=null;
        String cmd=null;
        for(int index=0;index<args.length;index++){
            String arg=args[index];
            if(arg.startsWith("-")){
                if (cmd!=null){
                    println("错误：命令错误！");
                    return;
                }
                cmd=arg.substring(1);
            } else {
                if (cmd==null){
                    println("错误：参数错误！");
                    return;
                }
                switch (cmd){
                    case "a":_a=arg;break;
                    case "o":
                        _o=arg;
                        try{
                            new BufferedWriter(new FileWriter(_o));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "map":_map=arg;break;
                    case "b":_b1=arg;index++;_b2=args[index];break;
                    default:println("错误：命令错误！");return;
                }
                cmd=null;
            }
        }
        if(_map==null){
            println("错误：需要提供地图数据文件！");
            return;
        }
        dataInit(_map);
        if(_a!=null){
            getAllStationsInLine(_a);
            return;
        }
        if(_b1!=null&&_b2!=null){
            getShortestPath(_b1,_b2);
            return;
        }
        println("错误：参数错误！");
    }
}
